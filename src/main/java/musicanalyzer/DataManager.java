package musicanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataManager{
    private Map<String, Users> users;
    private Map<String, SongStats> songsStats;

    public DataManager(){ 
       users = new HashMap<>();
       songsStats = new HashMap<>();
    }



    // Users-----------------
   public void addUser(String userName) {
        users.putIfAbsent(userName, new Users(userName));
    }
    
    public Users getUser(String userName) {
        return users.get(userName);
    }
    
    public Map<String, Users> getAllUsers() {
        return users;
    }


    
    // Song------------
    public void addSong(String songName) {
        songsStats.putIfAbsent(songName, new SongStats(songName));
    }
    
    public SongStats getSongStats(String songName) {
        return songsStats.get(songName);
    }
    
    public Map<String, SongStats> getAllSongs() {
        return songsStats;
    }




    // Rating-------------
     public void recordRating(String userName, String songName, int rating) {   // former addRating, same name in statistics. Delete comment if renamming doesn't mess it up
        addUser(userName);
        addSong(songName);

        Users user = users.get(userName); // updating song's ratings
        user.rateSong(songName, rating);

        SongStats song = songsStats.get(songName);  // updating song's statistics
        song.addRating(rating);
     
     }

     public void checkCooperativeUsers(String mode) throws CustomException{

        if (mode.equals("similarity") || mode.equals("predictions") || mode.equals("recommendations"))
        {
            calculateAllUserStats();
            if(!hasEnoughCooperativeUsers())
            {
                throw new CustomException("Error: need at least two cooperative users for similarity mode.");
            }
        }
     }

    public boolean hasEnoughCooperativeUsers() {
    int cooperativeCount = 0;
    for (Users user : users.values()) {
        if (user.isCooperative()) {
            cooperativeCount++;
            if (cooperativeCount >= 2) {
                return true;
            }
        }
    }
    return false;
    }

    public void calculateAllUserStats() {                    // basic version, no conflict with Similarities and recommendations
        for (Users user : users.values()) {
            user.calcUserSongStatistics();
        }
    }

    // helper for processing
    public void calculateAllUserStats(boolean forStatsDisplay) {         // to allow reset, for Stats
       /*for (Users user : users.values()) {
            user.calcUserSongStatistics();
        }*/
        if (forStatsDisplay) {
     
            for (SongStats song : songsStats.values()){
                song.getStats().resetStats();
            }

            for (Map.Entry<String, SongStats> entry : songsStats.entrySet()){
                SongStats song = entry.getValue();

                for (Users user: users.values()){
                    Integer rating = user.getSongRatings().get(entry.getKey());
                    if(rating != null){
                        song.addRating(rating);
                }
            }
        }
    } else { 
            calculateAllUserStats();

            }
    }

    public void findMissingSongs() {          
        Set<String> allSongs = songsStats.keySet();
        for (Users user : users.values()) {
            List<String> missingSongs = new ArrayList<>();
            for (String song : allSongs) {
                if (!user.getSongRatings().containsKey(song)) {
                    missingSongs.add(song);
                }
            }
            user.setMissingSongs(missingSongs);
        }
    }


    public List<String[]> prepareSongStatsPrint() {     // bundle for output processor. Don't do this in outputProcessor   

        calculateAllUserStats(true);
        
        List<String[]> outputRows = new ArrayList<>();
        for (SongStats stats : songsStats.values()) {
            Statistics stat = stats.getStats();
            String[] outputRow = {
                stats.getSongName(),
                Integer.toString(stat.getNumberOfRatings()),
                Double.toString(stat.getMean()),
                Double.toString(stat.getStandardDeviation())
            };
            outputRows.add(outputRow);
        }
        return outputRows;
}
}