import java.util.*;

public class DataCleaner {


    //re-orders messages from backwards to forwards
    static Message[] sortMessages(Message[] thread){
        for(int i = 0; i < thread.length/2; i++){
            Message temp = thread[i];
            thread[i] = thread[thread.length-1-i];
            thread[thread.length-1-i] = temp;
        }
        return thread;
    }

    //takes a message and removes punctuation, repeated letters and puts it to lower case.
    static String sanitiseString(String content){
        if (content != null){
            content = content.replaceAll("[^a-zA-Z ]", "").toLowerCase();
            content = elongatedWords(content);
        }else{
            content = "";
        }
        return content;
    }

    //replaces 3 or more consecutive letters with two.
    static String elongatedWords(String content){
        String currentLetter;
        HashMap<String, String> segments = new HashMap<>();
        for(int i = 0; i < content.length(); i++){
            currentLetter = content.substring(i, i+1);
            int count = 1;
            try {
                while (content.substring(i + 1, i + 2).equals(currentLetter)) {
                    count++;
                    i++;
                }
            }catch(StringIndexOutOfBoundsException e){/*ignore*/}

            //record replacements
            if(count > 2){
                String replacement = currentLetter + currentLetter;
                String segment = concatenator(currentLetter, count, 0, "");
                segments.put(segment, replacement);
            }
        }
        //loop through and perform replacements
        Iterator it = segments.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            content = content.replaceAll((String) pair.getKey(), (String) pair.getValue());
        }
        return content;
    }

    //creates replacement string for elongated words
    static String concatenator(String letter, int length, int count, String segment){
        if (count < length){
            segment = segment + letter;
            count+=1;
            segment = concatenator(letter, length, count, segment);
        }
        return segment;
    }

    //remove any links
    static boolean webAddress(String word){
        if(word.contains("http")){
            return true;
        } else{
            return false;
        }
    }

    //standardise laughter reactions
    static String replaceLaughter(String word){
        if(word.contains("ha") && !word.equals("ha")){
            word = "ahaha";
        }
        return word;
    }

    //standardise xo's
    static String replaceXO(String word){
        if(word.contains("xo")){
            word = "xo";
        }
        return word;
    }

    //find the number of unique characters in the string
    static int uniqueLetters(String word){
        int i = 0;
        ArrayList<String> characters = new ArrayList<>();
        while(characters.size() < 3 && i < word.length()){
            String letter = word.substring(i,i+1);
            if(!characters.contains(letter)) characters.add(letter);
            i++;
        }
        return characters.size();
    }
}
