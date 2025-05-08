package musicanalyzer;
import java.io.IOException;
import java.util.List;

public class Cs214Project {
 

    public static void main(String[] args) {
   
    try{
        
        if (
            args.length > 0 && args[0].equals("-i")){  // Interactive mode first if initialized via terminal
            Processor processor = ProcessorFactory.createInteractiveProcessor(); 
            processor.process(null); 
            return; 
        }
      
        String inputFile = args[0];
        String outputFile = args[1];


        FileValidator.validate(args);

        DataManager dataManager = new DataManager(); 
        List<String[]> songStatsList = FileProcessor.processInputFile(inputFile, dataManager);  

        dataManager.calculateAllUserStats();
        dataManager.findMissingSongs();

        Processor processor = ProcessorFactory.createProcessor(args,dataManager); 

        String mode = processor.getMode();
        System.out.println("Starting with mode: " + mode);
        dataManager.checkCooperativeUsers(mode);

       
        List<String[]> results = processor.process(songStatsList);
     
        
    
        OutputProcessor.writeOutputFile(outputFile, results, mode);
    
    }catch(IllegalArgumentException | IOException | CustomException e)
    {

    System.err.println("Error: " + e.getMessage());
    System.exit(0); 
    
    }catch (Exception e)
    { 
        System.err.println("Error: " + e.getMessage());
        System.exit(0);}
    } 
    }