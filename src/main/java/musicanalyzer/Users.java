package musicanalyzer;

import java.util.*;

public class Users{
    private String userName;
    private Map<String, Integer> userSongRatings; 
    private Statistics stats;
    private boolean isCooperative; // if Standard Deviation is 0, then fail. 
    private List<String> missingSongs = new ArrayList<>(); 
 
    public Users(String userName){
        this.userName = userName;
        this.userSongRatings = new HashMap<>();
        this.stats = new Statistics();
    }

    public void rateSong(String song, int rating){
        userSongRatings.put(song, rating);
    }

    public void setMissingSongs(List<String> missing){
        this.missingSongs = missing;
    }

    public List<String> getMissingSongs(){   
        return missingSongs;
    }

    public void calcUserSongStatistics(){
        if (userSongRatings.isEmpty()){   
            isCooperative = false;
            return;
        }

        stats.computeStatsFromRatings(getRatingsAsList());

        isCooperative = stats.getStandardDeviation() != 0;
    }

    public String getUserName(){
        return userName;
    }

    public Map<String, Integer> getSongRatings(){
        return userSongRatings;
    }

    public Statistics getStats(){
        return stats;
    }

    public boolean isCooperative()
    {
        return isCooperative;
    }

    public Map<String, Double> getNormalizedRatings() {
        Map<String, Double> normalizedRatings = new HashMap<>();

        for (Map.Entry<String, Integer> entry : userSongRatings.entrySet()) {
            String song = entry.getKey();
            double rating = entry.getValue();
    
            double normalizedRating = stats.normalizeRating(rating);
            normalizedRatings.put(song, normalizedRating);
        }
        return normalizedRatings;
    }

    public List<Double> getRatingsAsList() {
        List<Double> ratingsList = new ArrayList<>();
        for (Integer rating : userSongRatings.values()) {
            ratingsList.add(rating.doubleValue()); 
        }
        return ratingsList;
    }
}