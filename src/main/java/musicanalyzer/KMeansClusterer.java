package musicanalyzer;
import java.util.*;
import org.apache.commons.math4.legacy.ml.distance.EuclideanDistance;

public class KMeansClusterer { // in conjunction with RecommendationsProcessor
    private List<double[]> centroids;
    private List<List<String>> clusters;
    private Map<String, double[]> normalizedRatings;
    private Set<String> allSongsForCluster;
    
    public KMeansClusterer(Map<String, double[]> normalizedRatings, List<String> centroidSongs) {
        this.normalizedRatings = normalizedRatings;
        this.allSongsForCluster = new HashSet<>(normalizedRatings.keySet());
        this.clusters = new ArrayList<>();
        
        for (int i = 0; i < centroidSongs.size(); i++) {
            clusters.add(new ArrayList<>());
        }
        initializeCentroids(centroidSongs);
    }
    
    public List<List<String>> getClusters() {
        return clusters;
    }
    
    public void cluster(int iterations) {
        for (int iter = 0; iter < iterations; iter++) {
            for (List<String> cluster : clusters) {  // Clear past clusters
                cluster.clear();  
            }
            
            assignToClusters();
            recalculateCentroids();
        }
    }
    
    private void initializeCentroids(List<String> centroidSongs) {
        centroids = new ArrayList<>();
        for (String song : centroidSongs) { 
            double[] normalizedVector = normalizedRatings.get(song);
            if (normalizedVector == null) {
                throw new IllegalArgumentException("Selected song not found or removed: " + song);
            }
            centroids.add(normalizedVector);
        }
    }
    
    private void assignToClusters() {
        EuclideanDistance distance = new EuclideanDistance();
        for (String song : allSongsForCluster) {
            double[] songVector = normalizedRatings.get(song);
            double minDistance = Double.MAX_VALUE;
            int minIndex = 0;
            
            for (int k = 0; k < centroids.size(); k++) {
                double dist = distance.compute(songVector, centroids.get(k));
                if (dist < minDistance) {
                    minDistance = dist;
                    minIndex = k;
                }
            }
            clusters.get(minIndex).add(song);
        }
    }
    
    private void recalculateCentroids() {
        int vectorLength = centroids.get(0).length;
        for (int i = 0; i < clusters.size(); i++) {
            List<String> cluster = clusters.get(i);
            if (cluster.isEmpty()) {
                continue;
            }
            
            double[] newCentroid = new double[vectorLength];
            for (String song : cluster) {
                double[] songVector = normalizedRatings.get(song);
                for (int j = 0; j < vectorLength; j++) {
                    newCentroid[j] += songVector[j];
                }
            }
            
            for (int j = 0; j < vectorLength; j++) {
                newCentroid[j] /= cluster.size();
            }
            
            centroids.set(i, newCentroid);
        }
    }
}