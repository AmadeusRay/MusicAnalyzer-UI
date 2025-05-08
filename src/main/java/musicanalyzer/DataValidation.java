package musicanalyzer;

import org.apache.commons.csv.CSVRecord;

public class DataValidation {
    static int validateRecord(CSVRecord record) throws CustomException {
        validateFields(record.get(0), record.get(1), record.get(2));
        return validatingRating(record.get(2));
    }

    private static void validateFields(String songSizeCheck, String userSizeCheck, String ratingsSizeCheck) throws CustomException {
        validateFieldsAmount(3);
        if (songSizeCheck.isEmpty() || userSizeCheck.isEmpty() || ratingsSizeCheck.isEmpty()) {
            throw new CustomException("Error: Entry is missing fields (song, user, rating)");
        }
    }

    private static void validateFieldsAmount(int fieldsAmount) throws CustomException {
        if (fieldsAmount != 3) {
            throw new CustomException("Error: Entry has extra fields (song, user, rating)");
        }
    }

    private static int validatingRating(String ratingsRange) throws CustomException {
        int rating = Integer.parseInt(ratingsRange);
        if (rating < 1 || rating > 5) {
            throw new CustomException("Error: Ratings must be between 1 and 5");
        }
        return rating;
    }
}