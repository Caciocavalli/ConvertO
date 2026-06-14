package com.lcavalli.csvtoxlsxconverter;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;

public class CsvToXlsxConverter {

    public static void main(String[] args) {
        // Applica lo stile grafico del sistema operativo per finestre moderne
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorato
        }

        // 1. Selezione del file CSV di origine
        JFileChooser csvChooser = new JFileChooser();
        csvChooser.setDialogTitle("Seleziona il file CSV da convertire");
        csvChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("File CSV (*.csv)", "csv"));
        
        if (csvChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            System.out.println("Conversione annullata dall'utente.");
            return;
        }
        File csvFile = csvChooser.getSelectedFile();

        // 2. Selezione di dove salvare il file XLSX di destinazione
        JFileChooser xlsxChooser = new JFileChooser();
        xlsxChooser.setDialogTitle("Salva il file Excel generato");
        xlsxChooser.setSelectedFile(new File(csvFile.getParent(), dividiEstensione(csvFile.getName())[0] + ".xlsx"));
        xlsxChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("File Excel (*.xlsx)", "xlsx"));

        if (xlsxChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            System.out.println("Salvataggio annullato dall'utente.");
            return;
        }
        File xlsxFile = xlsxChooser.getSelectedFile();
        
        // Assicura l'estensione corretta .xlsx
        if (!xlsxFile.getName().toLowerCase().endsWith(".xlsx")) {
            xlsxFile = new File(xlsxFile.getAbsolutePath() + ".xlsx");
        }

        // 3. Esecuzione della conversione
        try {
            convertiCsvInXlsx(csvFile, xlsxFile);
            JOptionPane.showMessageDialog(null, "Conversione completata con successo!\nFile creato: " + xlsxFile.getName(), "Successo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Errore durante la conversione:\n" + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private static void convertiCsvInXlsx(File csvSource, File xlsxTarget) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(csvSource));
             OutputStream os = new FileOutputStream(xlsxTarget);
             Workbook wb = new Workbook(os, "CsvConverter", "1.0")) {

            Worksheet ws = wb.newWorksheet("Dati Convertiti");
            
            String linea;
            int rigaCorrente = 0;

            while ((linea = br.readLine()) != null) {
                // Riconosce il punto e virgola ";" tipico dei CSV italiani
                String[] colonne = linea.split(";");
                
                for (int colonnaCorrente = 0; colonnaCorrente < colonne.length; colonnaCorrente++) {
                    String dato = colonne[colonnaCorrente].trim();
                    
                    // Identifica se è un numero intero (es: PTR) e lo scrive come valore numerico reale
                    if (dato.matches("\\d+")) {
                        ws.value(rigaCorrente, colonnaCorrente, Long.parseLong(dato));
                    } else {
                        ws.value(rigaCorrente, colonnaCorrente, dato);
                    }
                }
                rigaCorrente++;
            }
        }
    }

    private static String[] dividiEstensione(String nomeFile) {
        int targetIdx = nomeFile.lastIndexOf('.');
        if (targetIdx == -1) {
            return new String[]{nomeFile, ""};
        }
        return new String[]{nomeFile.substring(0, targetIdx), nomeFile.substring(targetIdx)};
    }
}