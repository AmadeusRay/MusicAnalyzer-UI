package musicanalyzer;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class FileProcessor {

    public static List<String[]> processInputFile(String inputfile, DataManager dataManager)  
    throws IOException, CustomException{ 
    

        try (Reader reader = new FileReader(inputfile)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
       
            populateDataManager(records, dataManager);
           
            dataManager.findMissingSongs();

            dataManager.calculateAllUserStats();

            return dataManager.prepareSongStatsPrint();
        }
    }

   
    private static void populateDataManager(Iterable<CSVRecord> records, DataManager dataManager) throws CustomException
    {
        if (dataManager == null) {
        throw new IllegalArgumentException("DataManager cannot be null");  
    }


        for (CSVRecord record : records)
        {
            try{
                int rating = DataValidation.validateRecord(record);
                String userName = record.get(1);
                String song = record.get(0);

                dataManager.recordRating(userName, song, rating);
            } catch (NumberFormatException e) {
                System.err.println("Error: Rating '" + record.get(2) + "' not an Int.'");
                System.exit(0);
            } catch (CustomException e) {
                System.err.println(e.getMessage());
                System.exit(0);
            }
        }
    }
}

