import java.util.HashSet;

public class SimilarityPairs {
    private String message;
    private String response;
    private double similarity;

    SimilarityPairs(String message, String response){
        this.message = message;
        this.response = response;
    }

    //this method finds the similarity between this message and input message
    public void stringSim(String inputMessage){
        if(message.length() > 2){
            this.message = this.message.substring(3,message.length());
        }
        HashSet<String> set1 = new HashSet<>();
        String[] words =  inputMessage.split(" ");
        for(int i = 0; i < words.length; i++){
            set1.add(words[i]);
        }
        HashSet<String> set2 = new HashSet<>();
        words = message.split(" ");
        for(int i = 0; i < words.length; i++){
            set2.add(words[i]);
        }
        set1.retainAll(set2);
        similarity = (double) (set1.size()) / (double)((inputMessage.split(" ").length + this.message.split(" ").length)/2);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public double getSimilarity() {
        return similarity;
    }

}
