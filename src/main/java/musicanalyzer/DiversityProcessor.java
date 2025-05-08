package musicanalyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DiversityProcessor implements Processor {
    private Map<String, Users> users;

    public DiversityProcessor(Map<String, Users> users){
        this.users = users;
    }

    @Override
    public String getMode(){
        return "diversity";
    }

    @Override
    public List<String[]> process(List<String[]> data) throws CustomException{

        for (Users user : users.values()){
            user.calcUserSongStatistics();
            
        }

        List<String[]> results = new ArrayList<>();

        for (Users user : users.values()){
            Set<String> allSongs = getAllSongs(); // make later

            Set<String> unratedSongs = getUnratedSongs(user, allSongs); 

            for (String song : unratedSongs) {

                double similarity = calculateUserSongSimilarity(user, song);
                String recommendationType = categorizeRecommendation(similarity);
                int expectedRating = predictExpectedRating(user, song);
          


            String[] recommendation = new String[4];
            recommendation[0] = user.getUserName();
            recommendation[1] = song;
            recommendation[2] = recommendationType;
            recommendation[3] = String.valueOf(expectedRating);

            results.add(recommendation);
         }
        }

        return results;
    }

    private Set<String> getAllSongs(){
        Set<String> allSongs = new HashSet<>();
        for (Users user : users.values()){
            allSongs.addAll(user.getSongRatings().keySet());
        }
        return allSongs;
    }

    private Set<String> getUnratedSongs(Users user, Set<String> allSongs){
        Set<String> unratedSongs = new HashSet<>(allSongs);
        unratedSongs.removeAll(user.getSongRatings().keySet());
        return unratedSongs;
    }

    private double calculateUserSongSimilarity(Users user, String song){
        double totalSimilarity = 0.0;
        int count = 0;

        for (Users otherUser : users.values()){
            if (!otherUser.getUserName().equals(user.getUserName()) &&
            otherUser.getSongRatings().containsKey(song)){      
                try {
                double similarity = Similarity.calcEuclidDistance(user, otherUser);
                totalSimilarity += similarity;
                count ++;
                } catch (Exception e){
                    continue;
                }
            }
        }
       
    return count > 0 ? totalSimilarity / count : 0.0;
}

private String categorizeRecommendation(double similarity){

    if (similarity <= 1.3){
        return "safe_bet";
    } else if (similarity <= 2.0){
        return "balanced";
    } else if (similarity <= 3.0){
        return "exploratory";
    } else {
        return "discovery";
    }
}

private int predictExpectedRating(Users user, String song) 
{
    try{
       SimilarityProcessor similarityProcessor = new SimilarityProcessor(users);
        List<String[]> similarities = similarityProcessor.process(new ArrayList<>());

    Predictions prediction = new Predictions(users, similarities);
    String[] predictEntry = prediction.predictRating(user, song);

    if ("NaN".equals(predictEntry[2])){

        double sum = 0.0;
        int count = 0;

        for (Users otherUsers : users.values()){
            Integer rating = otherUsers.getSongRatings().get(song);
            if (rating != null){
                sum += rating;
                count++;
            }
        }
        return count > 0 ? (int) Math.round(sum / count) : 3;
    } else{
        return Integer.parseInt(predictEntry[2]);
    }
} catch (Exception e) {

        double sum = 0.0;
        int count = 0;

        for (Users otherUsers : users.values()){
            Integer rating = otherUsers.getSongRatings().get(song);
            if (rating != null){
                sum += rating;
                count++;
            }
        }
        return count > 0 ? (int) Math.round(sum / count) : 3;
    }
}






}