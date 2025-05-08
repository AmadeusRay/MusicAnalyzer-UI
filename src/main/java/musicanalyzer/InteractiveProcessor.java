package musicanalyzer;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class InteractiveProcessor implements Processor {
    private Scanner scanner;
    private Interactive interactive;

    public InteractiveProcessor() {
        this.scanner = new Scanner(System.in);
        this.interactive = new Interactive(scanner);
    }

    @Override
    public List<String[]> process(List<String[]> data) throws CustomException {
        displaySplash();  
        handleMainMenu(); // the main menu loop
        return null;     
    }

    public void displaySplash() {
        System.out.println("===========================================================");
        System.out.println("(         __________________________                      )");
        System.out.println(")        ||                        ||                     (");
        System.out.println("(        ||                        ||        (  ) )       )");
        System.out.println(")        ||     Music Analyzer     ||         )( (        (");
        System.out.println("(        ||          v1.0          ||        (  ) )       )");
        System.out.println(")        ||                        ||       _________     (");
        System.out.println("(        ||                        ||    .-'---------|    )");
        System.out.println(")        *__________________________*   ( c  Java 20 |    (");
        System.out.println("(       / ==__oooo__==___ooooo-+ o //    `-.         |    )");
        System.out.println(")      /  oooo   ______  ooooo    //       '_________'    (");
        System.out.println("(     /         /_____/          /'         `-------'     )");
        System.out.println(")     `-------------------------'                         (");
        System.out.println("(                                                         )");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n");
    }

    //main menu loop and handles exit
    private void handleMainMenu() {
        boolean running = true;
        while (running) {
            int choice = showFirstMenu(); // Display menu and get user's choice
            if (choice == 1) {
                accessFolder(); // Handle "Load Folder" option
            } else if (choice == 2) {
                System.exit(0);
            }else {
            System.out.println("Invalid choice. Please try again.\n");
            }
        }
    }

    //main menu display, first menu options
    private int showFirstMenu() {
        String[] options = {
            "1 - Load Folder",
            "2 - Exit\n"
        };
        return getMenuChoice(options, 2);
    }

   //getting menu choice, checking if valid
    public int getMenuChoice(String[] menuOptions, int maxChoice) {
        while (true) {
            for (String menuOption : menuOptions) {
                System.out.println(menuOption);
            }
            System.out.print("Select an option: ");

            try {
                String input = scanner.nextLine().trim();
                System.out.println();
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= maxChoice) {
                    return choice;
                } else {
                    System.out.println("Error: invalid option\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: invalid option\n");
            }
        }
    }

    // Load Folder option, prompting a folder path and processing files. 
    protected void accessFolder() {             
        System.out.print("Enter folder path: ");
        String folderPath = scanner.nextLine().trim();
        System.out.println();

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Error: folder not found\n");
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files == null || files.length == 0) {
            System.out.println("Error: no files in folder\n");
            return;
        }

        handleSecondMenu(files);
    }

    // display secondary menu for file selection or returning to the main menu. 
    private void handleSecondMenu(File[] files) {
        boolean isSelectingFile = true;
        while (isSelectingFile) {
            String[] secondMenuOptions = {
                "1 - Select File",
                "2 - Return\n"
            };
            for (String option : secondMenuOptions) {
                System.out.println(option);
            }
            System.out.print("Select an option: ");

            try {
                String input = scanner.nextLine().trim();
                System.out.println();
                int choice = Integer.parseInt(input);
                if (choice == 1) {
                    handleFileSelection(files);
                } else if (choice == 2) {
                    isSelectingFile = false;  // Return to main menu
                } else {
                    System.out.println("Error: invalid option\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: invalid option\n");
            }
        }
    }

    //handle file selection from the list of CSV files
    private void handleFileSelection(File[] dataBaseFiles) {
        List<File> sortedFiles = new ArrayList<>(Arrays.asList(dataBaseFiles));
        sortedFiles.sort((f1, f2) -> f1.getName().compareTo(f2.getName()));

        for (int i = 0; i < sortedFiles.size(); i++) {
            System.out.println((i + 1) + " - " + sortedFiles.get(i).getName());
        }
        System.out.println();
        System.out.print("Select file number: ");
        String input = scanner.nextLine().trim();
        System.out.println();

        try {
            int fileChoice = Integer.parseInt(input);
            if (fileChoice >= 1 && fileChoice <= sortedFiles.size()) {
                File selectedFile = sortedFiles.get(fileChoice - 1);
                interactive.handleActionMenu(selectedFile); 
            } else {
                System.out.println("Error: invalid option\n");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: invalid option\n");
        }
    }

    @Override
    public String getMode() {
        return "interactive";
    }

}