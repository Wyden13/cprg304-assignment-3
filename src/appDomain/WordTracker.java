package appDomain;

import implementations.BSTree;
import utilities.Iterator;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import static appDomain.WordHelper.*;
 

/**
 * WordTracker - Assignment 3
 *
 * Reads a text file, tracks all unique words with their file and line number
 * occurrences in a BST, persists the tree via serialization, and prints
 * reports in three formats.
 *
 * Usage:
 *   java -jar WordTracker.jar <input.txt> -pf/-pl/-po [-f<output.txt>]
 *
 *   -pf  Print all words (alphabetical) + files they appear in
 *   -pl  Print all words (alphabetical) + files and line numbers
 *   -po  Print all words (alphabetical) + files, line numbers, and frequency
 *   -f<output.txt>  Optional: redirect report to a file
 */
public class WordTracker {

    private static final String REPO_PATH = "res/repository.ser";

    public static void main(String[] args) {

        // 1. Parse command-line arguments
        if (args.length < 2) {
            System.err.println("Usage: java -jar WordTracker.jar <input.txt> -pf/-pl/-po [-f<output.txt>]");
            System.exit(1);
        }

        String inputFilePath = args[0];
        String printOption = args[1];
        String outputFile = null;

        if (args.length >= 3 && args[2].startsWith("-f")) {
        	// skip the f prefix
            outputFile = args[2].substring(2);
        }

        if (!printOption.equals("-pf") && !printOption.equals("-pl") && !printOption.equals("-po")) {
            System.err.println("Invalid option: " + printOption + ". Must be -pf, -pl, or -po.");
            System.exit(1);
        }

        // 2. Restore serialized tree if exists
        BSTree<WordEntry> wordTree = deserializeTree();
        if (wordTree == null) {
            wordTree = new BSTree<>();
        }

        // 3. Process the input file
        File inputFile = new File("res/"+inputFilePath);
        if (!inputFile.exists()) {
            System.err.println("Input file not found: " + inputFilePath);
            System.exit(1);
        }

        String fileName = inputFile.getName();

        // check and skip a file if already processed
        if (fileAlreadyProcessed(wordTree, fileName)) {
            System.out.println("File '" + fileName + "' was already processed; skipping ingestion.");
        } else {
        	// process the next input file
            try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
                String line;
                int lineNumber = 1;
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split("\\s+");
                    for (String token : tokens) {
                    	// removes special characters
                        String cleaned = processWord(token);
                        if (!cleaned.isEmpty()) {
                        	// insert cleaned word into wordTree along with filename and line number
                        	insertWord(wordTree, cleaned, fileName, lineNumber);
                        
                        }
                    }
                    lineNumber++;
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
                System.exit(1);
            }
        }

        // 4. Serialize the updated tree back to repository.ser 
        serializeTree(wordTree);

        // 5. Generate report
        PrintStream out = System.out;
        if (outputFile != null) {
            try {
                // Ensure parent directories exist
                File outFile = new File(outputFile);
                if (outFile.getParentFile() != null) {
                    outFile.getParentFile().mkdirs();
                }
                out = new PrintStream(new FileOutputStream(outFile));
                System.out.println("Exporting report to: " + outputFile);
            } catch (FileNotFoundException e) {
                System.err.println("Cannot open output file: " + e.getMessage());
                System.exit(1);
            }
        } else {
            System.out.println("Not exporting file.");
        }

        printReport(wordTree, printOption, out);

        if (outputFile != null) {
            out.close();
        }
    }


    /**
     * Print the report using an inorder iterator (alphabetical order).
     * Format depends on the option: -pf, -pl, or -po.
     */
    private static void printReport(BSTree<WordEntry> tree, String option, PrintStream out) {
        switch (option) {
            case "-pf":
                out.println("Displaying -pf format");
                break;	
            case "-pl":
                out.println("Displaying -pl format");
                break;
            case "-po":
                out.println("Displaying -po format");
                break;
        }
        
        Iterator<WordEntry> it = tree.inorderIterator();
        while (it.hasNext()) {
            WordEntry entry = it.next();
            StringBuilder sb = new StringBuilder();
            sb.append("Key : ===").append(entry.getWord()).append("===  ");

            switch (option) {
                case "-pf":
                    // just filenames
                    sb.append("found in file: ");
                    sb.append(String.join(", ", entry.getOccurrences().keySet()));
                    break;

                case "-pl":
                    // Filenames + line numbers
                    for (Map.Entry<String, ArrayList<Integer>> e : entry.getOccurrences().entrySet()) {
                        sb.append("found in file: ").append(e.getKey());
                        sb.append(" on lines: ");
                        sb.append(formatLines(e.getValue()));
                        sb.append(", ");
                    }
                    // trim trailing ", "
                    if (sb.toString().endsWith(", ")) {
                        sb.setLength(sb.length() - 2);
                    }
                    break;

                case "-po":
                    // filenames + line numbers + frequency
                    sb.append("number of entries: ").append(entry.getTotalCount()).append(" ");
                    for (Map.Entry<String, ArrayList<Integer>> e : entry.getOccurrences().entrySet()) {
                        sb.append("found in file: ").append(e.getKey());
                        sb.append(" on lines: ");
                        sb.append(formatLines(e.getValue()));
                        sb.append(", ");
                    }
                    if (sb.toString().endsWith(", ")) {
                        sb.setLength(sb.length() - 2);
                    }
                    break;
            }

            out.println(sb.toString());
        }
    }

    // Serialization

 
    @SuppressWarnings("unchecked")
    public static BSTree<WordEntry> deserializeTree() {
        File repo = new File(REPO_PATH);
        if (!repo.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(repo)))) {
            return (BSTree<WordEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Warning: Could not read repository.ser (" + e.getMessage() + "). Starting fresh.");
            return null;
        }
    }

    public static void serializeTree(BSTree<WordEntry> tree) {
        File repo = new File(REPO_PATH);
        if (repo.getParentFile() != null) {
            repo.getParentFile().mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(repo)))) {
            oos.writeObject(tree);
        } catch (IOException e) {
            System.err.println("Error: Could not serialize tree: " + e.getMessage());
        }
    }
}