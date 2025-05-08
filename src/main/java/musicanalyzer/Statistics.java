package musicanalyzer;

import java.util.List;


public class Statistics {
    private int count = 0;
    private double mean = 0.0;
    private double sumSquaredDeviations = 0.0; 
    public double getMean() { return mean; }
    public int getCount() { return count; }

    public void updateStats(int rating){  
        count++;
        double delta = rating - mean;
        mean += delta / count;
        sumSquaredDeviations += delta * (rating - mean);
    }

    public void computeStatsFromRatings(List<Double> ratings){
        count = ratings.size();
        if(count==0){
            mean = 0.0;
            sumSquaredDeviations = 0.0;
            return;
        }
        computeMean(ratings);
        computeSumSquaredDeviations(ratings);
    }

    private void computeMean(List<Double> ratings){
        double sum = 0.0;
        for (Double rating : ratings){
            sum+=rating;
        }
        mean = sum / count;
    }

    private void computeSumSquaredDeviations(List<Double> ratings){
        sumSquaredDeviations = 0.0;
        for (Double rating : ratings) {
            double delta = rating - mean;
            sumSquaredDeviations += delta * delta;
        }

    }

    public double getStandardDeviation(){
        return count > 1 ? Math.sqrt(sumSquaredDeviations / count) : 0.0;
    }

    public int getNumberOfRatings(){
        return count;
    }

    public double normalizeRating(double rating){
        double stdDev = getStandardDeviation();
        double mean = getMean();
        return stdDev != 0 ? (rating - mean) / stdDev : 0.0; 
    }

    private int calcWeightedAverage(Statistics userStats) {
        double songMean = this.getMean();
        int numSongRatings = this.getNumberOfRatings();
        double userMean = userStats.getMean();
        int numUserRatings = userStats.getNumberOfRatings();
        if (numSongRatings == 0 || numUserRatings == 0) {
            return 0;
        }
        
        return (int) Math.round((songMean * numSongRatings + userMean * numUserRatings) / (numSongRatings + numUserRatings));
    }
   

    public int getWeightedAverageRating(Statistics userStats) {
        return calcWeightedAverage(userStats);
        }

    public void resetStats(){

        count = 0;
        mean = 0.;

        sumSquaredDeviations = 0.0;
    }
        
   
}
