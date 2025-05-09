package musicanalyzer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import musicanalyzer.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Spring Boot is working! Music Analyzer API is ready.");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("mode") String mode, @RequestParam(value = "songs", required = false) String[] songs) {
        try {
            // Create DataManager
            DataManager dataManager = new DataManager();

            // Process the large dataset file
            List<String[]> results = FileProcessor.processInputFile("large_dataset.csv", dataManager);

            // Create appropriate processor using your factory
            String[] args;
            if (mode.equalsIgnoreCase("recommendations")) {
                if (songs == null || songs.length == 0) {
                    return ResponseEntity.badRequest().body("Error: At least one song is required for recommendations");
                }
                // Combine mode flag with songs for recommendations
                args = new String[]{"large_dataset.csv", "output.csv", "-r"};
                args = Arrays.copyOf(args, args.length + songs.length);
                System.arraycopy(songs, 0, args, 3, songs.length);
            } else {
                args = new String[]{"large_dataset.csv", "output.csv", getModeFlag(mode)};
            }
            
            Processor processor = ProcessorFactory.createProcessor(args, dataManager);

            // Process the data
            List<String[]> processedResults = processor.process(results);

            return ResponseEntity.ok("Large dataset processed successfully. Results: " + processedResults.size() + " records processed.");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /* Future file upload feature, sticking with large_dataset
    @PostMapping("/upload-file")
    public ResponseEntity<String> uploadCustomFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("mode") String mode) {
        try {

            Path tempFile = Files.createTempFile("upload-", ".csv");             // save the uploaded file temporarily
            file.transferTo(tempFile.toFile());

            DataManager dataManager = new DataManager();

            List<String[]> results = FileProcessor.processInputFile(tempFile.toString(), dataManager);


            String[] args = {"input.csv", "output.csv", getModeFlag(mode)};
            Processor processor = ProcessorFactory.createProcessor(args, dataManager);


            List<String[]> processedResults = processor.process(results);

            Files.delete(tempFile);                   // Clean up temp file

            return ResponseEntity.ok("File processed successfully. Results: " + processedResults.size() + " records processed.");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    */

    private String getModeFlag(String mode) {
        switch (mode.toLowerCase()) {
           // case "similarity": return "-u";
           // case "predictions": return "-p";
           // case "confidence": return "-c";
           // case "diversity": return "-d";
           // case "recommendations": return "-r";
            default: throw new IllegalArgumentException("Unsupported mode: " + mode);
        }
    }

    @GetMapping("/modes")
    public ResponseEntity<String[]> getAvailableModes() {
        return ResponseEntity.ok(new String[]{"similarity", "diversity", "confidence", "predictions", "recommendations"});
    }
} 