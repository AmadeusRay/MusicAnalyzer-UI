package musicanalyzer;
import java.io.IOException;
import java.util.List;

public class StatsProcessor implements Processor{
    @Override

    public List<String[]> process(List<String[]> songStatsList) throws CustomException, IOException {
        return songStatsList;
    }

    @Override
    public String getMode(){
        return "stats";
    }
}
