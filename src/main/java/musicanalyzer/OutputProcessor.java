package musicanalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class OutputProcessor {
    public static void writeOutputFile(String outputFile, List<String[]> processedData, String mode) throws IOException {

        checkOutputFolderExists(outputFile);
        
        try (Writer writer = new FileWriter(outputFile);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            List<String[]> outputRows = new ArrayList<>();
            selectHeader(printer, mode);
            outputRows.addAll(processedData);
            for (String[] outputRow : outputRows){
                printer.printRecord((Object[]) outputRow);
            }
        }
    }

    private static void checkOutputFolderExists(String outputFile) {
        File output = new File(outputFile);
        try {
            File outputDir = output.getParentFile();
            if (outputDir != null && !outputDir.exists()) {
                outputDir.mkdirs();
            }
        } catch (Exception e) {
            System.err.println("Error: creating output directory: " + e.getMessage());
            System.exit(0);
        }
    }

    private static void selectHeader(CSVPrinter printer, String mode) throws IOException {
        if (mode.equals("stats")) {                                   
            printer.printRecord("song", "number of ratings", "mean", "standard deviation");   
        }else if (mode.equals("similarity")) {                                   
            printer.printRecord("name1", "name2", "similarity");
        } else if (mode.equals("predictions")) {                                    
            printer.printRecord("song", "user", "predicted rating");
        } else if (mode.equals("recommendations")) {                                    
            printer.printRecord("user choice", "recommendation");
        } else if (mode.equals("confidence")) { 
            printer.printRecord("song", "user", "predicted rating", "confidence");
        } else if (mode.equals("diversity")){
             printer.printRecord("user", "song", "recommendation_type", "expected_rating");
        }
        
    }
}