package musicanalyzer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
public class FileUploadController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Spring Boot works.");
    }

    /* 
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("mode") String mode) {
        try {
            // Save the uploaded file temporarily
            Path tempFile = Files.createTempFile("upload-", ".csv");
            file.transferTo(tempFile.toFile());

            // Process the file using FileProcessor
            DataManager dataManager = new DataManager();
            List<String[]> processedData = FileProcessor.processInputFile(tempFile.toString(), dataManager);

            // Generate output using OutputProcessor
            String outputFile = "output.csv";
            OutputProcessor.writeOutputFile(outputFile, processedData, mode);

            // Clean up the temporary file
            Files.delete(tempFile);

            return ResponseEntity.ok("File processed successfully. Output saved to " + outputFile);
        } catch (IOException | CustomException e) {
            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
        }
    }*/
} 