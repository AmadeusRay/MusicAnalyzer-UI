package musicanalyzer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimilarityProcessor implements Processor{
    private Map<String, Users> users;

    public SimilarityProcessor(Map<String, Users> users){
        this.users = users;
    }

    @Override
    public List<String[]> process(List<String[]> data) throws CustomException {
           for (Users user : users.values()) {
            user.calcUserSongStatistics();
        }

        return calculateSimilarities(users);
    }

    @Override
    public String getMode(){
        return "similarity";
    }

    /// keep everything below on the surface 

    public static List<String[]> calculateSimilarities(Map<String, Users> users) throws CustomException {
        List<String> userList = new ArrayList<>(users.keySet());
        List<String[]> similarityResults = new ArrayList<>();
        boolean twoCooperative = false;

        for (int i = 0; i < userList.size(); i++) {
            for (int j = i + 1; j < userList.size(); j++) {
               Users user1 = users.get(userList.get(i));
               Users user2 = users.get(userList.get(j));
               if (processUserPair(user1, user2, similarityResults)){
                twoCooperative = true;
               }
            }
        }

        if (!twoCooperative){
            throw new CustomException("must have at least two cooperative users for user similarity");
        }
        return similarityResults;
    }

        private static boolean processUserPair(Users user1, Users user2, List<String[]> similarityResults) {
            if (areCooperative(user1, user2)){
                double similarity = Similarity.calcEuclidDistance(user1, user2);
                String userName1 = user1.getUserName();
                String userName2 = user2.getUserName();

                similarityResults.add(new String[] {userName1, userName2, Double.toString(similarity)});   
                return true;
            }
            return false; 
        }

        private static boolean areCooperative(Users user1, Users user2){     
            return user1.isCooperative() && user2.isCooperative();
        }
}
