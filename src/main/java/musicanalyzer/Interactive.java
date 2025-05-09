package musicanalyzer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Interactive {
    private final Scanner scanner;

    public Interactive(Scanner scanner) {
        this.scanner = scanner;
    }

    // Manages the action menu for processing a selected file. 
    public void handleActionMenu(File selectedFile) {
        boolean isAnalyzing = true;
        while (isAnalyzing) {
            displayActionMenu();
            int choice = getValidInput(1, 7);
    
            if (choice == 7) {
                isAnalyzing = false;
                continue;
            }
    
            String outputPath = getValidOutputPath();
            if (outputPath == null) {
                continue;
            }
    
            try {
                DataManager songData = new DataManager();

               FileProcessor.processInputFile(selectedFile.getAbsolutePath(), songData);  // file paths
               ProcessContext context = new ProcessContext(selectedFile, outputPath);
                ProcessResult result = processChoice(choice, context, songData);
                OutputProcessor.writeOutputFile(outputPath, result.results, result.mode);
                System.out.println("Output written to: " + outputPath + "\n");
            } catch (IOException | CustomException e) {
                System.out.println("Error: " + e.getMessage() + "\n");
            }
        }
    }

    public String getValidOutputPath() {
        System.out.print("Enter output path: ");
         String outputPath = scanner.nextLine().trim();
         System.out.println();

         if (!outputPath.endsWith(".csv")) {
             System.out.println("Error: only `.csv` files are supported\n");
             return null;
         }
         return outputPath;

 }

 private ProcessResult processChoice(int choice, ProcessContext context, DataManager songData) throws IOException, CustomException {
    Processor processor;
    String mode;

    if (choice == 1) {
        processor = new StatsProcessor();
        mode = "stats";
    } else {
        String[] args;
        if (choice == 4) {
        args = getRecommendationArgs(context, songData);
    } else if (choice == 6) { // Diversity mode
        args = getDiversityArgs(context, songData);
    } else {
        args = getProcessorArgs(choice, context);
    }
        processor = ProcessorFactory.createProcessor(args, songData);
        mode = processor.getMode();
    }

    List<String[]> results = processor.process(FileProcessor.processInputFile(context.selectedFile.getAbsolutePath(), songData));
    return new ProcessResult(results, mode);
}

private String[] getProcessorArgs(int choice, ProcessContext context) {
        String flag;
    if (choice == 2) {
        flag = "-u";
    } else if (choice == 3) {
        flag = "-p";
    } else if (choice == 5)
    {
        flag = "-c";
    } else if (choice == 6){
        flag = "-d";
    }else{
        throw new IllegalArgumentException("Invalid choice " + choice);
    }
    return new String[]{context.selectedFile.getAbsolutePath(), context.outputPath, flag};
}

private String[] getRecommendationArgs(ProcessContext context, DataManager songData) {
    Set<String> uniqueSongs = new HashSet<>(songData.getAllSongs().keySet());
    List<String> sortedSongs = new ArrayList<>(uniqueSongs);
    Collections.sort(sortedSongs);

    for (int i = 0; i < sortedSongs.size(); i++) {
        System.out.println((i + 1) + " - " + sortedSongs.get(i));
    }
    System.out.println();
    System.out.print("Enter selections (e.g. 2,5,7): ");
    String selectionInput = scanner.nextLine().trim();
    System.out.println();

    List<String> selectedSongs = new ArrayList<>();
    String[] selections = selectionInput.split(",");
    for (String selection : selections) {
        try {
            int index = Integer.parseInt(selection.trim()) - 1;
            if (index >= 0 && index < sortedSongs.size()) {
                selectedSongs.add(sortedSongs.get(index));
            } else {
                System.out.println("Error: invalid selection\n");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: invalid selection\n");
        }
    }

    String[] args = new String[selectedSongs.size() + 3];
    args[0] = context.selectedFile.getAbsolutePath();
    args[1] = context.outputPath;
    args[2] = "-r";
    for (int i = 0; i < selectedSongs.size(); i++) {
        args[3 + i] = selectedSongs.get(i);
    }
    return args;
    }

    private String[] getDiversityArgs(ProcessContext context, DataManager songData) {
        System.out.print("Username: ");
        
        String username = scanner.nextLine().trim();
        System.out.println();
        
  
        if (username.isEmpty()) {
            System.out.println("Error: username cannot be empty\n");
            return getDiversityArgs(context, songData); // Recursively ask again
        }
        
        // verify that username exists in the data from file
        if (!songData.getAllUsers().containsKey(username)) {
            System.out.println("Error: User '" + username + "' not found\n");
            return getDiversityArgs(context, songData); // Recursively ask again
        }
        
        String[] args = {context.selectedFile.getAbsolutePath(), context.outputPath, "-d"};
        
        // return if username was entered and validated
        return new String[]{args[0], args[1], args[2], username};
    }

    // display action menu 
    private void displayActionMenu() {
        System.out.println("1 - Song Stats");
        System.out.println("2 - User Similarity");
        System.out.println("3 - User Prediction");
        System.out.println("4 - User Recommendation");
        System.out.println("5 - Confidence Rating");
        System.out.println("6 - Diversity Recommendations"); 
        System.out.println("7 - Return\n");
        System.out.print("Select an option: ");
    }

    // valid input within range of options
    private int getValidInput(int min, int max) {
        boolean validInput = false;
        int choice = -1;

        while (!validInput) { 
            try {
                String input = scanner.nextLine().trim();
                System.out.println();
                choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    validInput = true;
                } else {
                    System.out.println("Error: invalid option\n");
                    displayActionMenu(); // Redisplay menu after error
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: invalid option\n");
                displayActionMenu(); // Redisplay menu after error
            }
        }
        return choice;
    }

    public static class ProcessContext {
    private File selectedFile;
    private String outputPath;
    
    public ProcessContext(File selectedFile, String outputPath) {
        this.selectedFile = selectedFile;
        this.outputPath = outputPath;
    }
}

    public static class ProcessResult {
        private List<String[]> results;
        private String mode;
    
        public ProcessResult(List<String[]> results, String mode) {
            this.results = results;
            this.mode = mode;
        }
    }


    // just for junit test
    public String[] testGetRecommendationArgs(File file, String outputPath, DataManager dataManager) {
        ProcessContext context = new ProcessContext(file, outputPath);
        return getRecommendationArgs(context, dataManager);
    }
    }