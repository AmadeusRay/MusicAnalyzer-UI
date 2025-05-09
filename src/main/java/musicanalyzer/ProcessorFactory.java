package musicanalyzer;
import java.util.Arrays;

public class ProcessorFactory {
    public static Processor createInteractiveProcessor(){
        return new InteractiveProcessor();  // interactive processor to isolate, yet be able to acess the other modes / processors. The most special case of them all.
    }

    public static Processor createProcessor(String[] args, DataManager dataManager)
   {
    String mode = selectedMode(args);

    switch (mode){
        case "stats":
            return new StatsProcessor();
        case "similarity":
            if (dataManager.getAllUsers().isEmpty()){
                throw new IllegalArgumentException("Error: Cannot calculate similarities: no user data");
            }
            return new SimilarityProcessor(dataManager.getAllUsers());

        case "predictions":
            return new PredictionProcessor(dataManager.getAllUsers());
        
        case "recommendations":
            return new RecommendationProcessor(dataManager.getAllUsers(), 
            Arrays.asList(Arrays.copyOfRange(args, 3, args.length)));
            
        case "confidence":
            return new ConfidenceProcessor(dataManager.getAllUsers());
       case "diversity":
     
            if (args.length > 3) {                      // check for a specific user is provided for diversity mode
                String targetUser = args[3];
                return new DiversityProcessor(dataManager.getAllUsers(), targetUser);
            } else {
                return new DiversityProcessor(dataManager.getAllUsers());
            }
        default:
            throw new IllegalArgumentException("Unsupported mode " + mode);

        }
   }

    private static String selectedMode(String[] args){ 
    if (args.length == 2) {
        return "stats";
        }

    validateArguementCount(args);

    String flag = args[2];
    switch (flag){
        case "-r":
            if (args.length <= 3){
                throw new IllegalArgumentException("must select at least one song for recommendations");
            } 
            return "recommendations"; 
        case "-u":
            return "similarity";
        case "-p":
            return "predictions";
        case "-c":
            return "confidence";
        case "-d":
            return "diversity";
        default:
            throw new IllegalArgumentException("Error: '" + args[2] + "'. Only '-u', '-r', '-p', '-c', and '-d' are allowed.");
        }
    }

    private static void validateArguementCount(String[] args)
    {
        if (args.length < 3) {
            throw new IllegalArgumentException("Error: Incorrect amount of arguments.");
        }
    }
}