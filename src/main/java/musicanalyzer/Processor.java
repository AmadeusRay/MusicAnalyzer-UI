package musicanalyzer;

import java.io.IOException;
import java.util.List;

public interface Processor {
    List<String []> process(List<String[]> data) throws CustomException, IOException;
    String getMode();
}
