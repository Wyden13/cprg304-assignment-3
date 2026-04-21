package appDomain;

import java.util.ArrayList;

import implementations.BSTree;
import implementations.BSTreeNode;

public class WordHelper {
	/**
     * Strip non-alphanumeric characters and lowercase the word.
     */
    public static String processWord(String word) {
        return word.replaceAll("[^a-zA-Z0-9-]", "");
    }

    /**
     * Insert a word+occurrence into the BST.
     * If the word already exists, merge the new occurrence into the existing node.
     * If it's new, create a fresh WordEntry node.
     */
    public final static void insertWord(BSTree<WordEntry> tree, String word, String fileName, int lineNumber) {
        // Use a temporary probe entry to search
        WordEntry probe = new WordEntry(word);
        BSTreeNode<WordEntry> existing = tree.search(probe);

        if (existing != null) {
            // Word already in tree-add this file/line to its existing entry
            existing.getElement().addOccurrence(fileName, lineNumber);
        } else {
            // New word-create entry and insert
            WordEntry newEntry = new WordEntry(word);
            newEntry.addOccurrence(fileName, lineNumber);
            tree.add(newEntry);
        }
    }

    /** Format a list of line numbers */
    public final static String formatLines(ArrayList<Integer> lines) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            sb.append(lines.get(i));
            if (i < lines.size() - 1) sb.append(",");
        }
        return sb.toString();
    }
    /**
     * Walk the tree with an inorder iterator and check if any node's
     * occurrences map already contains the given filename.
     */
    public final static boolean fileAlreadyProcessed(BSTree<WordEntry> tree, String fileName) {
        if (tree.isEmpty()) return false;
        return tree.getRoot().getElement().getOccurrences().containsKey(fileName);
    }


}
