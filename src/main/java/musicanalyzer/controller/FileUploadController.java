package musicanalyzer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import musicanalyzer.*;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Spring Boot is working! Music Analyzer API is ready.");
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/diversity")
    public ResponseEntity<String> getDiversity(@RequestParam String user) {
        try {
            String filePath = "database/files/large_dataset.csv";
            String outputPath = "output.csv";
            String[] args = {filePath, outputPath, "-d", user};

            // validate and process input
            DataManager dataManager = new DataManager();
            FileValidator.validate(args);
            List<String[]> songStatsList = FileProcessor.processInputFile(filePath, dataManager);

            Processor processor = ProcessorFactory.createProcessor(args, dataManager);

            // Process and get results ( sorted by DiversityProcessor)
            List<String[]> results = processor.process(songStatsList);

            // Do NOT sort results here. Just format and return.
            StringBuilder response = new StringBuilder();
            response.append("<pre>");
            response.append("🎵 Diversity Analysis Results 🎵\n\n");
            response.append("Total Recommendations: ").append(results.size()).append("\n\n");
            for (String[] result : results) {
                response.append(String.format("%-10s %-10s %-15s %-5s\n",
                    result[0], result[1], result[2], result[3]
                ));
            }
            response.append("</pre>");

            return ResponseEntity.ok().header("Content-Type", "text/html").body(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}