import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WordCount {
    private String word;
    private HashMap<String, Double> occurenceMap;
    private double totalCorrelations;

    WordCount(String word){
        this.word = word;
        this.occurenceMap = new HashMap<String, Double>();
    }

    public String getWord() {
        return word;
    }

    public HashMap<String, Double> getOccurenceMap() {
        return occurenceMap;
    }

    public void addCount(String key) {
        if(!key.equals(word)){
            if(occurenceMap.get(key) == null){
                occurenceMap.put(key, 1.0);
                totalCorrelations += 1;
            }else{
                double val = occurenceMap.get(key);
                val +=1;
                totalCorrelations += 1;
                occurenceMap.replace(key, val);
            }
        }
    }

    public void convert2Probability(){
        Iterator it = occurenceMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Double val = occurenceMap.get(pair.getKey());
            val = val/totalCorrelations;
            occurenceMap.replace((String)pair.getKey(), val);

        }
    }

}
