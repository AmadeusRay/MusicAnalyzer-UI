package musicanalyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;

public class DiversityProcessor implements Processor {
    private Map<String, Users> users;
    private String targetUser; // single user, from the arg
    private static final int MAX_RECOMMENDATIONS = 50; // cap of 10 recommendations, better performance and also no one wants to see low recommendations

    public DiversityProcessor(Map<String, Users> users){
        this.users = users;
        this.targetUser = null;
    }

    public DiversityProcessor(Map<String, Users> users, String targetUser) {
        this.users = users;
        this.targetUser = targetUser;
        System.out.println("DiversityProcessor created. Users map size: " + users.size() + ", targetUser: " + targetUser);
    }

    @Override
    public String getMode(){
        return "diversity";
    }

    @Override
    public List<String[]> process(List<String[]> data) throws CustomException{
        System.out.println("Processing diversity for users: " + users.keySet());
        System.out.println("Target user: " + targetUser);
        System.out.println("MAX_RECOMMENDATIONS: " + MAX_RECOMMENDATIONS);

        for (Users user : users.values()){
            user.calcUserSongStatistics();
        }

        List<String[]> results = new ArrayList<>();

        if (targetUser != null) {
            Users user = users.get(targetUser);
            if (user != null) {
                processUserRecommendations(user, results);
            } else {
                throw new CustomException("Error: User '" + targetUser + "' not found");
            }
        } else {
            for (Users user : users.values()) {
                System.out.println("Processing recommendations for user: " + user.getUserName());
                Set<String> allSongs = getAllSongs();
                Set<String> unratedSongs = getUnratedSongs(user, allSongs);
                System.out.println("Unrated songs for user: " + unratedSongs.size());
                int count = 0;
                for (String song : unratedSongs) {
                    if (count >= MAX_RECOMMENDATIONS) break;
                    System.out.println("Processing song: " + song + " for user: " + user.getUserName());
                    double similarity = calculateUserSongSimilarity(user, song);
                    String recommendationType = categorizeRecommendation(similarity);
                    int expectedRating = predictExpectedRating(user, song);
                    String[] recommendation = new String[4];
                    recommendation[0] = user.getUserName();
                    recommendation[1] = song;
                    recommendation[2] = recommendationType;
                    recommendation[3] = String.valueOf(expectedRating);
                    results.add(recommendation);
                    count++;
                }
                System.out.println("Total recommendations for user: " + count);
            }
        }
        return results;
    }

    private void processUserRecommendations(Users user, List<String[]> results) {
        Set<String> allSongs = getAllSongs();
        Set<String> unratedSongs = getUnratedSongs(user, allSongs);
        
        List<String[]> userRecommendations = new ArrayList<>();
    
        for (String song : unratedSongs) {
            double similarity = calculateUserSongSimilarity(user, song);
            String recommendationType = categorizeRecommendation(similarity);
            int expectedRating = predictExpectedRating(user, song);
    
            String[] recommendation = new String[4];
            recommendation[0] = user.getUserName();
            recommendation[1] = song;
            recommendation[2] = recommendationType;
            recommendation[3] = String.valueOf(expectedRating);
    
            userRecommendations.add(recommendation);
        }
    
        if (targetUser != null) {
            Collections.sort(userRecommendations, this::compareByRatingDescending); 
        
            int count = Math.min(userRecommendations.size(), MAX_RECOMMENDATIONS);
            for (int i = 0; i < count; i++) {
                results.add(userRecommendations.get(i));
            }
        } else {
           
            results.addAll(userRecommendations);
        }
    }
    
    private int compareByRatingDescending(String[] rec1, String[] rec2) {
        int rating1 = Integer.parseInt(rec1[3]);
        int rating2 = Integer.parseInt(rec2[3]);
     
        return Integer.compare(rating2, rating1);    // Compare rating2 to rating1 for descending order, check if this was done before. Check all the sort methods
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