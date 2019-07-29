import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args){

        File directory = new File("inbox");

        /*the following commented lines are only necessary for other version of chatbot*/

        // Get all files from a directory.
        File[] fList = directory.listFiles();
        Message[][] threads = new Message[fList.length][];
        for(int i = 0; i < fList.length; i++){
            threads[i] = getMessageThread(fList[i].toString());
        }
//
//        //write all to a file so it can be analysed
//        ArrayList<String> corpus = new ArrayList<String>();
//        corpus = createCorpus(corpus, threads);
//        //create a set of words used
//        writeToFile(corpus);
        writeMessages(threads);
//
//        // create a list of words for co-occurrence vectors
//        HashMap<String, WordCount> vectors = new HashMap<>();
//        for(int i =  0; i < corpus.size(); i++){
//            vectors.put(corpus.get(i),new WordCount(corpus.get(i)));
//        }

        //read files as a list of mpairs
        File file = new File("messages.txt");
        ArrayList<SimilarityPairs> mPairs = new ArrayList<>();
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()){
                String message = sc.nextLine();
                if(message.contains("M:")){
                    SimilarityPairs pair = new SimilarityPairs(message, sc.nextLine());
                    //if no response don't add it
                    if(!pair.getResponse().equals("END")){
                        mPairs.add(pair);
                    }
                }
        }
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }

       libBot( mPairs);
    }

    //this chat bot uses string similarity and the messages as a library
    public static void libBot(ArrayList<SimilarityPairs> mPairs ) {
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Start a conversation");

        while(true != false){
            String sentence = myObj.nextLine();  // Read user input
            System.out.println(bestResponse(mPairs, sentence));
        }
    }

    //this chatbot uses probability and co-occurrence vectors
    public static void seq2seq( HashMap<String, WordCount> vectors, ArrayList<SimilarityPairs> mPairs ){
        //get counts
        vectors = populateVectors(vectors, mPairs);
        //convert to probabilities
        Iterator it = vectors.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            vectors.get(pair.getKey()).convert2Probability();
        }
        HashMap<String,Double> sentenceVectors =inputProbabilities("im a twat",vectors);
        //create response
        //loop
        System.out.println(sentenceVectors.entrySet());
        System.out.println(getHighest(sentenceVectors));
        System.out.println(vectors.size());
    }

    public static String getHighest(HashMap<String,Double> sentenceVectors){
        Iterator it = sentenceVectors.entrySet().iterator();
        String word = "";
        Double highest = 0.0;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if((double) pair.getValue() > highest){
                    word = (String) pair.getKey();
                    highest = (double) pair.getValue();
            }
        }
        return word;
    }

    public static HashMap<String,Double> inputProbabilities(String sentence, HashMap<String, WordCount> vectors){
        String[] words = sentence.split(" ");
        HashMap<String, Double> sentenceVectors = new HashMap<>();
        for(int i = 0; i < words.length; i++){
            if(!words[i].equals("")){
                HashMap<String, Double> currentWord = vectors.get(words[i]).getOccurenceMap();
                Iterator it = currentWord.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry pair = (Map.Entry)it.next();
                    Double val = sentenceVectors.get(pair.getKey());
                    if(val == null){
                        sentenceVectors.put((String)pair.getKey(), (Double) pair.getValue());
                    }else{
                        Double newVal = (Double)pair.getValue() + val;
                        sentenceVectors.replace((String)pair.getKey(),newVal);
                    }
                }
            }
        }
        return sentenceVectors;
    }

    //create co-occurence vectors
    public static HashMap<String, WordCount> populateVectors(HashMap<String, WordCount> vectors, ArrayList<SimilarityPairs> mPairs){
        for(int i = 0; i < mPairs.size(); i ++){
            addToVec( mPairs.get(i).getMessage(), vectors);
            addToVec( mPairs.get(i).getResponse(), vectors);
        }
        return vectors;
    }

    //add co-occurences within a string to relevant vectors
    public static void addToVec(String message, HashMap<String, WordCount> vectors){
        String[] words = message.split(" ");
        for(int i = 0; i < words.length; i++){
            WordCount currentVec = vectors.get(words[i]);
            for(int j = i - 2; j < i + 2; j++){
                if( j > 0 && j < words.length && words[j] != null && currentVec != null){
                    if(!(words[j].equals("") || words[j] == null)){
                        currentVec.addCount(words[j]);
                    }
                }
            }
            vectors.replace(words[i], currentVec);
        }
    }

    //takes a message from the user and returns the most fitting response from the library
    public static String bestResponse(ArrayList<SimilarityPairs> mPairs, String sentence){
        for(int k = 0; k < mPairs.size(); k++){
            mPairs.get(k).stringSim(sentence);
        }
        SimilarityPairs best = mPairs.get(0);
        for(int k = 1; k < mPairs.size(); k++){
            if(mPairs.get(k).getSimilarity() >= best.getSimilarity() ){
                best = mPairs.get(k);
            }
        }
        return  best.getResponse();
    }

    //this method writes all messages and responses to a file
    public static void writeMessages(Message[][] threads){
        //write messages to a file
        Writer writer = null;
        try{
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("messages.txt"), "utf-8"));
            for(int i = 0; i < threads.length; i++){
                //skip group chats
                if (threads[i] != null && threads[i].length > 5) {
                    String previousSender = threads[i][0].getSender();
                    for (int j = 0; j < threads[i].length; j++) {
                        /*concatenate consecutive messages*/
                        threads[i][j].setContent(cleanMessage(threads[i][j].getContent()));
                        if(threads[i][j].getSender().equals(previousSender) && j > 0){
                            writer.write(" " + threads[i][j].getContent());
                        }else if(j==0){
                            printSender(threads[i][j].getSender(), writer);
                            writer.write(threads[i][j].getContent());
                        }else{
                            writer.write("\n");
                            printSender(threads[i][j].getSender(), writer);
                            writer.write(threads[i][j].getContent());
                            previousSender = threads[i][j].getSender();
                        }
                    }
                    /*make space between conversations*/
                    writer.write("\nEND\n");
                }
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }finally {
            try {writer.close();} catch (Exception e) {/*ignore*/}
        }

    }

    //this method prints the start of a new message
    private static void printSender(String sender, Writer writer){
        try{
            if(!sender.equals("Sandy Johnstone")){
                writer.write("M: ");
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    //this method gets an array of messages from a single conversation thread
    private static Message[] getMessageThread(String filename){
        // concatenate string with json file name
        filename =  filename + "\\" + "message_1.json";
        try{
            Object obj = new JSONParser().parse(new FileReader(filename));
            // typecasting obj to JSONObject
            JSONObject jo = (JSONObject) obj;
            // getting message recipient
            JSONArray participants = (JSONArray) jo.get("participants");
            //make sure it's not a group chat
            if(participants.size() == 2){
                Message[] message_thread  = messageThread(participants,jo);
                //put messages in order
                message_thread = DataCleaner.sortMessages(message_thread);
                return message_thread;
            }
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }catch(org.json.simple.parser.ParseException e){
            System.out.println(e.getMessage());
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    //create each message node and return as an array
    public static Message[] messageThread(JSONArray participants, JSONObject jo){

        JSONArray messages = (JSONArray) jo.get("messages");
        Message[] message_thread = new Message[messages.size()];
        //create message nodes
        for(int i = 0; i < messages.size(); i++){
            String sender = (String) ((JSONObject) messages.get(i)).get("sender_name");
            String content = (String) ((JSONObject) messages.get(i)).get("content");
            content = DataCleaner.sanitiseString(content);
            long time = (long) ((JSONObject) messages.get(i)).get("timestamp_ms");
            message_thread[i] = new Message(sender, time, content);
        }
        return message_thread;
    }

    //create completed corpus
    public static ArrayList<String> createCorpus(ArrayList<String> corpus, Message[][] threads){
        for(int i = 0; i < threads.length; i++) {
            if (threads[i] != null) {
                for (int j = 0; j < threads[i].length; j++) {
                    String[] words;
                    words = threads[i][j].getContent().split(" ");
                    for(int k = 0; k < words.length; k++){
                       corpus = addToCorpus(corpus, words[k]);
                    }

                }
            }
        }
        return corpus;
    }

    //add a word to a corpus
    public static ArrayList<String> addToCorpus(ArrayList<String> corpus, String word){
            if(!DataCleaner.webAddress(word)){
                if(DataCleaner.uniqueLetters(word) == 2 ){
                    word = DataCleaner.replaceLaughter(word);
                    word = DataCleaner.replaceXO(word);
                }
                if(!corpus.contains(word)){
                    corpus.add(word);
                 }
            }
        return  corpus;
    }

    //write all words in corpus to a file
    public static void writeToFile(ArrayList<String> corpus){
        //sort corpus
        Collections.sort(corpus);
        System.out.println(corpus.size());
        Writer writer = null;
        //write corpus to a file
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("corpus.txt"), "utf-8"));
            for(int i = 0; i < corpus.size(); i++){
                writer.write(corpus.get(i));
                writer.write("\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {writer.close();} catch (Exception e) {/*ignore*/}
        }
    }

    //take a string and convert words to alternatives
    private static String cleanMessage(String message){
        String[] words = message.split(" ");

        for(int i = 0; i < words.length; i++){
            words[i] = DataCleaner.sanitiseString(words[i]);/*basic sanitisation*/
            if(DataCleaner.webAddress(words[i])) {
                words[i] = ""; /*remove web addresses*/
            }
            if(DataCleaner.uniqueLetters(words[i]) == 2 ){/*replace special cases*/
                words[i] = DataCleaner.replaceLaughter(words[i]);
                words[i] = DataCleaner.replaceXO(words[i]);
            }

        }
        if(words.length > 0){/*concatenate sanitary words*/
            message = words[0];
            for(int j = 1; j < words.length; j++){
                message = message + " " + words[j];
            }
        }

        return message;
    }

}
