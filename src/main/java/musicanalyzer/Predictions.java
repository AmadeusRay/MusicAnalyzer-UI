package musicanalyzer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Predictions {
    private Map<String, Users> users;
    private List<String[]> similarities;

    public Predictions(Map<String, Users> users, List<String[]> similarities) throws CustomException {
        this.users = users;
        this.similarities = similarities;
    }

    public List<String[]> generatePredictions(){
        List<String[]> predictions = new ArrayList<>();

        for (Users user : users.values()){
            if (!user.getMissingSongs().isEmpty()){
                for (String songToPredict : user.getMissingSongs()){
                    String [] prediction = predictRating(user, songToPredict);
                    predictions.add(prediction);
                }
            }
        }

        predictions.sort((a, b) -> {                 // will be separate
            int songCompare = a[0].compareTo(b[0]);
            if (songCompare != 0)
            {
                return songCompare;
            }
            return a[1].compareTo(b[1]);
         });

        return predictions;
    }

    protected String[] predictRating(Users user, String songToPredict) {
        Users bestMatch = findSimilarity(user, songToPredict);
        String[] predictEntry = new String[3];
        predictEntry[0] = songToPredict;
        predictEntry[1] = user.getUserName();

        if (bestMatch == null) {
            predictEntry[2] = "NaN";
        } else {
            int predictedRating = findBestMatch(user, bestMatch, songToPredict);
            predictEntry[2] = String.valueOf(predictedRating);
        }
        
        return predictEntry;
    }

    public Users findSimilarity(Users user, String songToPredict) {
        double minSimilarity = Double.MAX_VALUE;
        Users bestMatch = null;

        for (String[] userSimilarityPair : similarities)  
                {
                    Users otherUser = getOtherUser(user, userSimilarityPair);
                    if (userSimilarityPair[0].equals(user.getUserName()) || userSimilarityPair[1].equals(user.getUserName()))
                    {
                       
                        String[] similarityBundle = {userSimilarityPair[2], String.valueOf(minSimilarity)};   //clarify notes better / clean it up, messy
                        Users potentialMatch = findCloserMatch(otherUser, similarityBundle, songToPredict);
                        if (potentialMatch != null){
                            bestMatch = potentialMatch;
                            minSimilarity = Double.parseDouble(userSimilarityPair[2]);
                        }
                        
                    }
                }             
                return bestMatch;   
    }

    private Users getOtherUser(Users user, String[] userSimilarirtyPair)
    {
        if (userSimilarirtyPair[0].equals(user.getUserName()))            
        {
                return users.get(userSimilarirtyPair[1]);
        }
        else if (userSimilarirtyPair[1].equals(user.getUserName()))
        {
                return users.get(userSimilarirtyPair[0]);
        }
        return null;
    }

    private Users findCloserMatch(Users otherUser, String[] similirarityBundle, String songToPredict)
    {
        if (otherUser.getSongRatings().containsKey(songToPredict))
        {
            double similarityScore = Double.parseDouble(similirarityBundle[0]); // userSimilarityPair[2]  
            double minSimilarity = Double.parseDouble(similirarityBundle[1]); //minSimilarity           
            if (similarityScore < minSimilarity)
            {
                return otherUser;
            }
        }
        return null;
    }


    public int findBestMatch(Users user, Users bestMatch, String songToPredict) {
        Double bestMatchNormalizedRating = bestMatch.getNormalizedRatings().get(songToPredict);
        int predictedRating = (int) Math.round(bestMatchNormalizedRating * user.getStats().getStandardDeviation() 
                                                                                + user.getStats().getMean());
        if (predictedRating > 5)
        {
            predictedRating = 5;
        }
        else if (predictedRating < 1) {
            predictedRating = 1;
        }  
        return predictedRating;
    }
    
}
