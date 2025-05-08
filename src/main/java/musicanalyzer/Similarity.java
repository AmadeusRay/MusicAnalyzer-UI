package musicanalyzer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.legacy.ml.distance.EuclideanDistance;

public class Similarity {

    public static double calcEuclidDistance(Users user1, Users user2){
        EuclideanDistance distance = new EuclideanDistance();

        List<Double> ratings1 = new ArrayList<>();
        List<Double> ratings2 = new ArrayList<>();


        List<String> allSongs = new ArrayList<>(user1.getSongRatings().keySet());
        for (String song : user2.getSongRatings().keySet()) {
            if (!allSongs.contains(song)) {
                allSongs.add(song);
            }
        }

        for (String song : allSongs) {
            Double rating1 = user1.getNormalizedRatings().getOrDefault(song, Double.NEGATIVE_INFINITY);
            Double rating2 = user2.getNormalizedRatings().getOrDefault(song, Double.NEGATIVE_INFINITY);

            if (!rating1.isInfinite() && !rating2.isInfinite()) { 
                ratings1.add(rating1);
                ratings2.add(rating2);
            }
        }

        if (ratings1.isEmpty()) {
            return Double.NaN;
        }

        double[] array1 = ratings1.stream().mapToDouble(Double::doubleValue).toArray();
        double[] array2 = ratings2.stream().mapToDouble(Double::doubleValue).toArray();

        return distance.compute(array1, array2);
        
    }

    
}
