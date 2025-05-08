package musicanalyzer;

public class SongStats{
    private String songName;
    private Statistics stats;
    
    public SongStats(String songName){ 
        this.songName = songName;
        this.stats = new Statistics();
    }

    public void addRating(int rating){
        stats.updateStats(rating);
    }

    public Statistics getStats(){
        return stats;
    }


    public String getSongName(){
        return songName;
    }
    
}