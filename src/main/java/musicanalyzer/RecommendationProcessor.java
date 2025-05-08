package musicanalyzer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// connected with KMeansClustrer 

public class RecommendationProcessor implements Processor {
    private Map<String, Users> users;
    private List<String> recommendSongs;
    private Map<String, double[]> normalizedRatings;
    private Set<String> allSongsForCluster;
    
    public RecommendationProcessor(Map<String, Users> users, List<String> recommendSongs) {
        Set<String> songSet = new HashSet<>(recommendSongs);
        if (songSet.size() < recommendSongs.size()) {
            throw new IllegalArgumentException("Error: Duplicated songs inside recommendation list");
        }
        this.users = users;
        this.recommendSongs = recommendSongs;
        this.allSongsForCluster = new HashSet<>();
    }

    @Override
    public String getMode(){
        return "recommendations";
    }
    
    @Override
    public List<String[]> process(List<String[]> data) throws CustomException {

        if (users.isEmpty()) {
            throw new CustomException("Error: Cannot calculate recommendations: no user data");
        }
        
        // Get all predictions first
        PredictionProcessor predictionProcessor = new PredictionProcessor(users);
        List<String[]> predictions = predictionProcessor.process(data);
        
        // Fill in missing ratings
        fillUsersWithPredictions(predictions);
        ensureAllRatingsFilled();
        
        // Prep data forcluster
        List<Users> userList = new ArrayList<>(users.values());
        collectAllSongs(userList);
        removeSongsWithIdenticalRatings(userList);
        
        normalizedRatings = computeNormalizedRatings(userList);

        
        // kmean clustering
        KMeansClusterer clusterer = new KMeansClusterer(normalizedRatings, recommendSongs);
        clusterer.cluster(10); // 10 iterations that p5 mentioned
        
        
        return compileRecommendations(clusterer.getClusters());
    }
    
    private void collectAllSongs(List<Users> userList) {
        allSongsForCluster.clear();
        for (Users user : userList) {
            allSongsForCluster.addAll(user.getSongRatings().keySet());
        }
    }
    
    private void removeSongsWithIdenticalRatings(List<Users> userList) {
        Set<String> songsToRemove = getSongsToRemove(userList);
        allSongsForCluster.removeAll(songsToRemove);
    }
    
    private void fillUsersWithPredictions(List<String[]> predictions) {
        for (String[] prediction : predictions) {
            String song = prediction[0];         //ew
            String userName = prediction[1];
            String ratingStr = prediction[2];     
            Users user = users.get(userName);
            int rating = 0;
            
            if ("NaN".equals(ratingStr)) {
                Statistics songStats = getSongStatistics(song);
                rating = songStats.getWeightedAverageRating(user.getStats());
            } else {
                rating = Integer.parseInt(ratingStr);
            }
            
            user.rateSong(song, rating);
            user.getMissingSongs().remove(song);
        }
    }
    
    private Statistics getSongStatistics(String song) {
        Statistics songStats = new Statistics();
        for (Users user : users.values()) {
            Integer rating = user.getSongRatings().get(song);
            if (rating != null) {
                songStats.updateStats(rating);
            }
        }
        return songStats;
    }
    
    private void ensureAllRatingsFilled() {
        for (Users user : users.values()) {
            for (String song : allSongsForCluster) {
                if (!user.getSongRatings().containsKey(song)) {
                    Statistics songStats = getSongStatistics(song); 
                    int rating = songStats.getWeightedAverageRating(user.getStats());  //too nested
                    user.rateSong(song, rating);
                    user.calcUserSongStatistics();
                }
            }
        }
    }
    
    private Set<String> getSongsToRemove(List<Users> userList) {
        Set<String> songsToRemove = new HashSet<>();
        for (String song : allSongsForCluster) {
            Set<Integer> uniqueRatings = new HashSet<>();
            for (Users user : userList) {
                uniqueRatings.add(user.getSongRatings().get(song));
            }
            if (uniqueRatings.size() == 1) {
                songsToRemove.add(song);
            }
        }
        return songsToRemove;
    }
    
    private Map<String, double[]> computeNormalizedRatings(List<Users> userList) {
        Map<String, double[]> normalizedRatings = new HashMap<>();
        for (String song : allSongsForCluster) {
            Statistics stats = new Statistics();
            for (Users user : userList) {
                stats.updateStats(user.getSongRatings().get(song));
            }
            double[] normalized = new double[userList.size()];
            for (int i = 0; i < userList.size(); i++) {
                normalized[i] = stats.normalizeRating(userList.get(i).getSongRatings().get(song));
            }
            normalizedRatings.put(song, normalized);
        }
        return normalizedRatings;
    }
    
    private List<String[]> compileRecommendations(List<List<String>> clusters) {
        Map<String, List<String>> recommendationsMap = new HashMap<>();
        for (int i = 0; i < recommendSongs.size(); i++) {
            String userChoice = recommendSongs.get(i);
            List<String> clusterSongs = new ArrayList<>(clusters.get(i));
            List<String> recs = new ArrayList<>();
            for (String song : clusterSongs) {
                if (!recommendSongs.contains(song)) {
                    recs.add(song);
                }
            }
            if (!recs.isEmpty()) { // Only include if there are recommendations
                recommendationsMap.put(userChoice, recs);
            }
        }
        
        List<String> sortedUserChoices = new ArrayList<>(recommendationsMap.keySet());
        sortedUserChoices.sort(String::compareTo);

        List<String[]> results = new ArrayList<>();
        for (String userChoice : sortedUserChoices) {
            List<String> recs = recommendationsMap.get(userChoice);
            recs.sort(String::compareTo);
            for (String rec : recs) {
                results.add(new String[]{userChoice, rec});
            }
        }
        return results;
    }
}
