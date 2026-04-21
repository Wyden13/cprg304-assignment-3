package appDomain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WordEntry implements Comparable<WordEntry>, Serializable {

    private static final long serialVersionUID = 1L;

    private String word;
    private Map<String, ArrayList<Integer>> occurrences;

    public WordEntry(String word) {
        this.word = word;
        this.occurrences = new HashMap<>();
    }

    // Record one occurrence of this word in the given file at the given line. 
    public void addOccurrence(String fileName, int lineNumber) {
        occurrences.computeIfAbsent(fileName, k -> new ArrayList<>()).add(lineNumber);
    }

    public String getWord() {
        return word;
    }

    // Returns the map of filename -> line number list
    public Map<String, ArrayList<Integer>> getOccurrences() {
        return occurrences;
    }
    // return total count of occurrences across all files
    public int getTotalCount() {
        int total = 0;
        for (ArrayList<Integer> lines : occurrences.values()) {
            total += lines.size();
        }
        return total;
    }

    @Override
    public int compareTo(WordEntry other) {
        return this.word.compareToIgnoreCase(other.word);
    }

    @Override
    public String toString() {
        return word;
    }
}