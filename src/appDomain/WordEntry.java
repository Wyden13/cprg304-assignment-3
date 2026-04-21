package appDomain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * WordEntry stores a single word along with all its occurrences across files.
 * Occurrences are tracked as: filename -> list of line numbers.
 * Implements Comparable so it can be stored in the BST (alphabetical order).
 */
public class WordEntry implements Comparable<WordEntry>, Serializable {

    private static final long serialVersionUID = 1L;

    private String word;
    // LinkedHashMap preserves insertion order of filenames
    private Map<String, ArrayList<Integer>> occurrences;

    public WordEntry(String word) {
        this.word = word;
        this.occurrences = new LinkedHashMap<>();
    }

    /** Record one occurrence of this word in the given file at the given line. */
    public void addOccurrence(String fileName, int lineNumber) {
        occurrences.computeIfAbsent(fileName, k -> new ArrayList<>()).add(lineNumber);
    }

    public String getWord() {
        return word;
    }

    /** Returns the map of filename -> line number list */
    public Map<String, ArrayList<Integer>> getOccurrences() {
        return occurrences;
    }

    /** Total count of all occurrences across all files */
    public int getTotalCount() {
        int total = 0;
        for (ArrayList<Integer> lines : occurrences.values()) {
            total += lines.size();
        }
        return total;
    }

    /**
     * Merge another WordEntry's occurrences into this one.
     * Used when the same word appears in a new file scan.
     */
    public void merge(WordEntry other) {
        for (Map.Entry<String, ArrayList<Integer>> entry : other.occurrences.entrySet()) {
            String file = entry.getKey();
            ArrayList<Integer> lines = entry.getValue();
            ArrayList<Integer> existing = occurrences.computeIfAbsent(file, k -> new ArrayList<>());
            existing.addAll(lines);
        }
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