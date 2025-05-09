/*import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



public class TestCs214Project {

    Cs214Project project;
    File inputFile;
    private SongStats songStats;
    File outputFile;   

    @BeforeEach 
    public void setUpProject() throws IOException
    {
        //project = new Cs214Project();
        songStats = new SongStats("TestSong");
        outputFile = new File("output2.csv");
    }

    @Test
    public void testPredictionsMode() throws IOException {
        inputFile = new File("test_input.csv");
        FileWriter writer = new FileWriter(inputFile);
        writer.write("song1,User1,3\n" +
                     "song2,User1,4\n" +
                     "song3,User1,2\n" +
                     "song1,User2,5\n" +
                     "song2,User2,1\n" +
                     "song3,User2,4\n" +
                     "song1,User3,2\n" +
                     "song3,User3,5\n");
        writer.close();

        String[] args = {"test_input.csv", "test_output.csv", "-p"};
        Cs214Project.main(args);

        outputFile = new File("test_output.csv");
        assertTrue(outputFile.exists());
        List<String> lines = Files.readAllLines(outputFile.toPath());
        assertEquals("song,user,predicted rating", lines.get(0));
        assertEquals(2, lines.size());
        String[] prediction = lines.get(1).split(",");
        assertEquals("song2", prediction[0]);
        assertEquals("User3", prediction[1]);
        assertTrue(Integer.parseInt(prediction[2]) >= 1 && Integer.parseInt(prediction[2]) <= 5);
    }
 
    @Test
    void testMeanCalculation() {
        songStats.addRating(3);
        songStats.addRating(4);
        songStats.addRating(5);

        assertEquals(4.0, songStats.getStats().getMean(), 9e-7);
    }

    @Test
    void testStandardDeviationWithValue(){
    songStats.addRating(3);
    songStats.addRating(4);
    songStats.addRating(5);
    double expectedStdDev = Math.sqrt(2.0 / 3.0); 

    assertEquals(expectedStdDev, songStats.getStats().getStandardDeviation(), 9e-7);
    }

    @Test  
    void testGetNumberOfRatings(){
    songStats.addRating(4);
    songStats.addRating(5);

    assertEquals(2, songStats.getStats().getNumberOfRatings());
    }

 @Test
    public void testSimilarityMode() throws IOException {
        inputFile = new File("test_similarity.csv");
        FileWriter writer = new FileWriter(inputFile);
        writer.write("Song1,User1,3\n" +
                     "Song2,User1,4\n" +
                     "Song1,User2,3\n" +
                     "Song2,User2,5\n");
        writer.close();

        String[] args = {"test_similarity.csv", "test_similarity_output.csv", "-u"};
        Cs214Project.main(args);

        outputFile = new File("test_similarity_output.csv");
        assertTrue(outputFile.exists());
        List<String> lines = Files.readAllLines(outputFile.toPath());
   
        assertEquals(2, lines.size()); // header and result.
    }

    @Test
    public void testRecommendationsMode() throws IOException {
        inputFile = new File("test_recommends.csv");
        FileWriter writer = new FileWriter(inputFile);
        writer.write("Song1,User1,3\n" +
                     "Song2,User1,4\n" +
                     "Song1,User2,5\n" +
                     "Song2,User2,1\n" +
                     "Song3,User3,2\n");
        writer.close();

        String[] args = {"test_recommends.csv", "test_recommends_output.csv", "-r", "Song1"};
        try {
            Cs214Project.main(args);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        outputFile = new File("test_recommends_output.csv");
        assertTrue(outputFile.exists());
        List<String> lines = Files.readAllLines(outputFile.toPath());

        assertEquals(3, lines.size()); // header and 2 results
    }

    @Test
    public void testCustomException() {
        String errorMessage = "Test error message";
        CustomException exception = new CustomException(errorMessage);
    
        assertEquals(errorMessage, exception.getMessage(), 
                    "CustomException should store the error message");
    }
    



/// Interactions         // I just started to directly bypass main to test since -i is handled differently.
@Test
public void testDisplaySplash() throws Exception {

    ByteArrayOutputStream outCapture = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    
    try {
        System.setOut(new PrintStream(outCapture));
        
        InteractiveProcessor processor = new InteractiveProcessor();
        processor.displaySplash();
        
        String output = outCapture.toString();
        assertTrue(output.contains("Music Analyzer"), "Splash screen should program title");
        assertTrue(output.contains("v1.0"), "Splash screen should show version number");
    } finally {
        System.setOut(originalOut);
    }
}


@Test
public void testGetMenuChoiceWithInvalidInput() throws Exception {
    String input = "abc\n5\n2\n";
    InputStream originalIn = System.in;
    ByteArrayOutputStream outCapture = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;

    try {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setOut(new PrintStream(outCapture));

       
        InteractiveProcessor processor = new InteractiveProcessor();
        String[] options = {"Option 1", "Option 2"};
        int choice = processor.getMenuChoice(options, 2);

        assertEquals(2, choice, "Method should return valid options have rejecting input");
        String output = outCapture.toString();
        assertTrue(output.contains("Error: invalid option"), "Should show error for letters input");

    } finally {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }
}



@Test
public void testAccessFolderWithEmptyFolder() throws Exception {
    // create an empty folder for the test
    File emptyFolder = new File("emptyTestFolder");
    emptyFolder.mkdirs();
    
    // test with folder that's empty
    String input = "emptyTestFolder\n";
    InputStream originalIn = System.in;
    ByteArrayOutputStream outCapture = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;

    try {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setOut(new PrintStream(outCapture));

        InteractiveProcessor processor = new InteractiveProcessor();
        processor.accessFolder();

        String output = outCapture.toString();
        assertTrue(output.contains("no files in folder"), "should display error for folder with no CSV files");

    } finally {
        System.setIn(originalIn);
        System.setOut(originalOut);
        emptyFolder.delete();
    }
}

@Test
public void testInteractiveClass() {
    String input = "1\n" +                 // Choose option 1 - Song Stats   // bypassing the main menu (with interactive class), directly to the action menu
                   "output.csv\n" +        
                   "5\n" +                 // Return to menu
                   "2\n" +                 // Choose option 2 - User Similarity 
                   "similarity.csv\n" +    
                   "5\n" +                 // Return to menu
                   "3\n" +                 // Choose option 3 - User Prediction
                   "prediction.csv\n" +    
                   "5\n" +                 // Return to menu
                   "4\n" +                 // Choose option 4 - User Recommendation
                   "recommendation.csv\n" + // Enter output path
                   "1\n" +            
                   "5\n" +                  
                   "2\n"+
                   "2\n";
    
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    

    Interactive interactive = new Interactive(scanner); // by pass many of the menus
    
    // test file
    File testFile = null;
    try {
        testFile = File.createTempFile("test", ".csv");
        FileWriter writer = new FileWriter(testFile);
        writer.write("Song1,User1,3\n" +
                     "Song2,User1,4\n" +
                     "Song1,User2,5\n" +
                     "Song2,User2,1\n");
        writer.close();
        
        ByteArrayOutputStream outCapture = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outCapture));
        
        try {
            interactive.handleActionMenu(testFile);
            
            String output = outCapture.toString();
            assertTrue(output.contains("Song Stats") || 
                       output.contains("User Similarity") || 
                       output.contains("User Prediction") || 
                       output.contains("User Recommendation"), 
                       "Action menu options should be displayed");
            
        } finally {
            System.setOut(originalOut);
        }
    } catch (IOException e) {
        fail("Failed to create test file: " + e.getMessage());
    } finally {
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }
        new File("output.csv").delete();
        new File("similarity.csv").delete();
        new File("prediction.csv").delete();
        new File("recommendation.csv").delete();
    }
}

@Test
public void testStatsProcessorCompletely() throws Exception {
    StatsProcessor processor = new StatsProcessor();
    assertEquals("stats", processor.getMode(), "Mode should be 'stats'");

    List<String[]> testData = new ArrayList<>();
    testData.add(new String[]{"Song1", "3", "0.5"});
    testData.add(new String[]{"Song2", "4", "0.7"});
    
    List<String[]> result = processor.process(testData);
    assertNotNull(result);
    assertEquals(testData, result, "Should return the same data");
    assertEquals(2, result.size(), "Should return same number of rows");
}


@Test
public void testGetRecommendationArgs() throws Exception {
    String input = "1,2\n"; // selecting songs 1 and 2
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    Interactive interactive = new Interactive(scanner);
    
    // creating a test context
    File testFile = File.createTempFile("test", ".csv");
    try (FileWriter writer = new FileWriter(testFile)) {
        writer.write("Song1,User1,3\nSong2,User1,4\nSong3,User1,5\n");
    }
    
    // create DataManager with song data
    DataManager dataManager = new DataManager();
    dataManager.addSong("Song1");
    dataManager.addSong("Song2");
    dataManager.addSong("Song3");
    
    ByteArrayOutputStream outCapture = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outCapture));
    
    try {
        String[] args = interactive.testGetRecommendationArgs(testFile, "output.csv", dataManager);
        
        assertNotNull(args);
        assertEquals(5, args.length);
        assertEquals("-r", args[2]);
        
    } finally {
        System.setOut(originalOut);
        testFile.delete();
    }
}

// confidence mode 

@Test
public void testConfidenceScoreCalculation() throws IOException {
    File inputFile = new File("test_confidence_score.csv");
    FileWriter writer = new FileWriter(inputFile);
    writer.write("song1,user1,5\n" +
                 "song1,user2,5\n" +
                 "song1,user3,5\n" +
                 "song1,user4,5\n" +  
                 "song2,user1,1\n" +
                 "song2,user2,1\n" +
                 "song2,user3,1\n" +  
                 "song3,user1,3\n" +
                 "song3,user2,3\n" +
                 "song3,user3,4\n" +
                 "song3,user4,3\n" +  
                 "song4,user2,2\n");
    writer.close();

    String[] args = {"test_confidence_score.csv", "test_confidence_output.csv", "-c"};
    Cs214Project.main(args);

    outputFile = new File("test_confidence_output.csv");
    assertTrue(outputFile.exists(), "");

    List<String> lines = Files.readAllLines(outputFile.toPath());
    
    System.out.println("\n--- Confidence Output ----");
    for (String line : lines) {
        System.out.println(line);
    }
    
    int highCount = 0, mediumCount = 0, lowCount = 0;
    int nanCount = 0;
    
    for (int i = 1; i < lines.size(); i++) {
        String[] prediction = lines.get(i).split(",");
        String confidence = prediction[3];
        
        if (confidence.equals("High")) highCount++;
        else if (confidence.equals("Medium")) mediumCount++;
        else if (confidence.equals("Low")) lowCount++;
        
        if (prediction[2].equals("NaN")) nanCount++;
    }
    
  
    System.out.println("High: " + highCount);
    System.out.println("Medium: " + mediumCount);
    System.out.println("Low: " + lowCount);
    System.out.println("NaN predictions: " + nanCount);
    
}

@AfterEach
public void deleteTestFiles(){
    if(outputFile.exists())
    {outputFile.delete();
    }
    new File("test_input.csv").delete();  
    new File("test_similarity.csv").delete(); 
    new File("test_recommends.csv").delete();
    new File("test_confidence_score.csv").delete();  
    new File("test_confidence_output.csv").delete();
}

}*/