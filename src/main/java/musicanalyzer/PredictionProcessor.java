package musicanalyzer;
import java.util.List;
import java.util.Map;


public class PredictionProcessor implements Processor {
    private Map<String, Users> users;
    
    public PredictionProcessor(Map<String, Users> users) {
        this.users = users;
    }

    @Override
    public String getMode(){
        return "predictions";
    }

    @Override
    public List<String[]> process(List<String[]> data) throws CustomException {
        SimilarityProcessor similarityProcessor = new SimilarityProcessor(users);
        List<String[]> similarities = similarityProcessor.process(data);  
                                                             
        Predictions prediction = new Predictions(users, similarities);  
        List<String[]> predictions = prediction.generatePredictions();
        
        if (predictions.isEmpty()) {
            throw new CustomException("Error: no songs to recommend. Songs may have been removed. Try with a larger file or fewer selections.");
        }
        return predictions;
    }

    
}
