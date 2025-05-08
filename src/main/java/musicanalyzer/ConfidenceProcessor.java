package musicanalyzer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfidenceProcessor implements Processor {
    
    private Map<String, Users> users;
    private Map<String, Double> confidenceScores = new HashMap<>();

    public ConfidenceProcessor(Map<String, Users> users){
        this.users = users;
    }

    @Override
    public String getMode(){
        return "confidence";
    }


   @Override
    public List<String[]> process(List<String[]> data) throws CustomException {
     
        PredictionProcessor predictionProcessor = new PredictionProcessor(users);
        List<String[]> predictions = predictionProcessor.process(data);
        
    
        calculateConfidenceScores(predictions);
   
        return enhancePredictionsWithConfidence(predictions);
    }
    
    private void calculateConfidenceScores(List<String[]> predictions) {
     
        confidenceScores.clear();    // clear past scores / reset
        

        for (String[] prediction : predictions) {         // calculate confidence for each prediction
            String song = prediction[0];
            String user = prediction[1];
            String songUser = song + ":" + user;
            

            double confidence = calculateConfidenceForPrediction(prediction);           
            
     
            confidenceScores.put(songUser, confidence);        // store  confidence score
        }
    }
    
    private double calculateConfidenceForPrediction(String[] prediction) {
        String song = prediction[0];
        String user = prediction[1];
        
     
        int ratingCount = countRatings(song);   //# of users who rated the song
        

        double ratingVariance = calculateRatingVariance(song);         // rating variance (consistency)
        

        int similarUsers = countSimilarUsersWhoRatedSong(user, song);         // # of similar users who rated this song
        
        // calculate confidence score
        // high rating count, low variance, more similar users = high confidence
        double confidenceScore = (ratingCount * 0.4) + ((1.0 / (ratingVariance + 0.1)) * 0.3) + (similarUsers * 0.3);

        return Math.min(1.0, confidenceScore / 10.0);         // normalize to 0-1 range
    }
    

    private int countRatings(String song) {
        int count = 0;
        for (Users user : users.values()) {
            if (user.getSongRatings().containsKey(song)) {
                count++;
            }
        }
        return count;
    }
    

    private double calculateRatingVariance(String song) {
        List<Integer> ratings = new ArrayList<>();
        
        for (Users user : users.values()) {   // collect all ratings for this song
            Integer rating = user.getSongRatings().get(song);
            if (rating != null) {
                ratings.add(rating);
            }
        }
        
        // handle case with no ratings
        if (ratings.isEmpty()) {
            return 5.0; // high variance (low confidence) if no ratings
        }
        
        //  mean    // call the original version later
        double sum = 0.0;
        for (Integer rating : ratings) {
            sum += rating;
        }
        double mean = sum / ratings.size();
        
        //  variance       
        double sumSquaredDifferences = 0.0;
        for (Integer rating : ratings) {
            double diff = rating - mean;
            sumSquaredDifferences += diff * diff;
        }
        
        return sumSquaredDifferences / ratings.size();
    }
    
    // count similar users who have rated a song
    private int countSimilarUsersWhoRatedSong(String userName, String song) {
        Users targetUser = users.get(userName);
        if (targetUser == null) {
            return 0;
        }
        
        int count = 0;
        double similarityThreshold = 2.0; // low values means more similar
        
        for (Users otherUser : users.values()) {
            if (!otherUser.getUserName().equals(userName) && 
                otherUser.getSongRatings().containsKey(song)) {
                
                // call calculate similarity
                try {
                    double similarity = Similarity.calcEuclidDistance(targetUser, otherUser);
                    
                    if (similarity <= similarityThreshold) {
                        count++;
                    }
                } catch (Exception e) { // skips user if error calculating similarity
                }
            }
        }
        
        return count;
    }
    
    private List<String[]> enhancePredictionsWithConfidence(List<String[]> predictions) {
        List<String[]> enhancedPredictions = new ArrayList<>();
        
        for (String[] prediction : predictions) {
            String song = prediction[0];
            String user = prediction[1];
            String rating = prediction[2];
            
          
            //double confidence = confidenceScores.getOrDefault(songUser, 0.0);
    
            String[] enhancedPrediction = new String[4];
            enhancedPrediction[0] = prediction[0]; // song
            enhancedPrediction[1] = prediction[1]; // user
            enhancedPrediction[2] = prediction[2]; // predicted rating
            //enhancedPrediction[3] = getConfidenceLevel(confidence); // confidence level

            if ("NaN".equals(rating)) {
                enhancedPrediction[3] = "NaN";
            }else{
                String songUser = song + ":" + user;
                double confidence = confidenceScores.getOrDefault(songUser, 0.0);
                enhancedPrediction[3] = getConfidenceLevel(confidence);
            }
            
            enhancedPredictions.add(enhancedPrediction);
        }
        
        return enhancedPredictions;
    }
    

    private String getConfidenceLevel(double confidence) {
        if (confidence >= 0.7) {
            return "High";
        } else if (confidence >= 0.4) {
            return "Medium";
        } else {
            return "Low";
        }
    }
 
    public double getConfidenceScore(String song, String user) {
        return confidenceScores.getOrDefault(song + ":" + user, 0.0);
    }
}