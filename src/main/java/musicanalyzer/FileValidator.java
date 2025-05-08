package musicanalyzer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileValidator{
    
    public static void validate(String[] args) throws IOException{
        checkArgCount(args);
        validateFilePaths(args);
    }

    private static void checkArgCount(String[] args){
            if (args.length < 2) {
            throw new IllegalArgumentException("Error: Must include <inputFile> and <outputFile> (optional: -u, -p)");
        }
    }

    private static void validateFilePaths(String[] args) throws IOException { 
        validateFileExtensions(args);
        validateFileExistence(args[0]);
    }

    private static void validateFileExistence(String intputFile) throws IOException {
        File file = new File(intputFile); 
        if (!file.exists()) {
            throw new FileNotFoundException("Error: The input file doesn't exist.");
        }
        if (file.length() == 0) {
            throw new IOException("Error: The file is empty.");
        }
    }

    private static void validateFileExtensions(String[] args) throws IOException {  
        //GUIDE: args[0] == inputFile, args[1] == outputFile
        if (!args[0].toLowerCase().endsWith(".csv")) {
            throw new IOException("Error: input paths must have `.csv` extension.");
        }
        if (!args[1].toLowerCase().endsWith(".csv")) {
            throw new IOException("Error: output paths must have `.csv` extension.");
        }
    }
}