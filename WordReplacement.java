import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class WordReplacement {
    public static void main(String[] args) {
        String inputTextFile = "/home/vk/Adv_Java/Project1/src/main/java/t8.shakespeare.txt";
        String findWordsListFile = "/home/vk/Adv_Java/Project1/src/main/java/find_words.txt";
        String dictionaryFile = "/home/vk/Adv_Java/Project1/src/main/java/french_dictionary.csv";
        String outputFile = "/home/vk/Adv_Java/Project1/src/main/java/t8.shakespeare.translated.txt";
        String performanceFile = "/home/vk/Adv_Java/Project1/src/main/java/performance.txt";
        String frequencyFile = "/home/vk/Adv_Java/Project1/src/main/java/frequency.csv";

        try {
            // Load find words list
            Set<String> findWords = new HashSet<>();
            try (BufferedReader findWordsReader = new BufferedReader(new FileReader(findWordsListFile))) {
                String findWord;
                while ((findWord = findWordsReader.readLine()) != null) {
                    findWords.add(findWord);
                }
            }

            // Load English to French dictionary
            Map<String, String> dictionary = new HashMap<>();
            try (BufferedReader dictionaryReader = new BufferedReader(new FileReader(dictionaryFile))) {
                String dictionaryLine;
                while ((dictionaryLine = dictionaryReader.readLine()) != null) {
                    String[] parts = dictionaryLine.split(",");
                    String englishWord = parts[0].toLowerCase();
                    String frenchWord = parts[1];
                    dictionary.put(englishWord, frenchWord);
                }
            }

            // Process the input text file
            StringBuilder processedOutput = new StringBuilder();
            Set<String> replacedWords = new HashSet<>();
            Map<String, Integer> wordReplacements = new HashMap<>();
            int replacementCount = 0;
            Instant startTime = Instant.now();
            try (BufferedReader inputReader = new BufferedReader(new FileReader(inputTextFile))) {
                String line;
                while ((line = inputReader.readLine()) != null) {
                    String[] words = line.split(" ");
                    StringBuilder processedLine = new StringBuilder();
                    for (String word : words) {
                        String lowercaseWord = word.toLowerCase();
                        if (findWords.contains(lowercaseWord) && dictionary.containsKey(lowercaseWord)) {
                            String replacedWord = dictionary.get(lowercaseWord);
                            replacedWords.add(replacedWord);
                            wordReplacements.put(replacedWord, wordReplacements.getOrDefault(replacedWord, 0) + 1);
                            replacementCount++;
                            processedLine.append(replaceWithSameCase(word, replacedWord)).append(" ");
                        } else {
                            processedLine.append(word).append(" ");
                        }
                    }
                    processedOutput.append(processedLine.toString().trim()).append("\n");
                }
            }
            Instant endTime = Instant.now();

            // Write the processed output to t8.shakespeare.translated.txt file
            try (FileWriter outputWriter = new FileWriter(outputFile)) {
                outputWriter.write(processedOutput.toString());
            }

            // Write performance information to performance.txt file
            Duration duration = Duration.between(startTime, endTime);
            long minutes = duration.toMinutes();
            long seconds = duration.getSeconds() % 60;
            long memoryUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);

            DecimalFormat decimalFormat = new DecimalFormat("00");
            String formattedTime = decimalFormat.format(minutes) + " minutes " + decimalFormat.format(seconds) + " seconds";

            try (FileWriter performanceWriter = new FileWriter(performanceFile)) {
                performanceWriter.write("Time to process: " + formattedTime + "\n");
                performanceWriter.write("Memory used: " + memoryUsed + " MB\n");
            }

            // Write word frequency information to frequency.csv file
            try (FileWriter frequencyWriter = new FileWriter(frequencyFile)) {
                frequencyWriter.write("English word,French word,Frequency\n");
                for (Map.Entry<String, Integer> entry : wordReplacements.entrySet()) {
                    String frenchWord = entry.getKey();
                    String englishWord = getEnglishWordFromDictionary(frenchWord, dictionary);
                    int frequency = entry.getValue();
                    frequencyWriter.write(englishWord + "," + frenchWord + "," + frequency + "\n");
                }
            }

            System.out.println("Word replacement completed. Processed file saved as " + outputFile);
            System.out.println("Performance information saved as " + performanceFile);
            System.out.println("Word frequency information saved as " + frequencyFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String replaceWithSameCase(String originalWord, String replacement) {
        if (originalWord.isEmpty() || replacement.isEmpty()) {
            return originalWord;
        }

        boolean isUpperCase = Character.isUpperCase(originalWord.charAt(0));
        if (isUpperCase) {
            return replacement.substring(0, 1).toUpperCase() + replacement.substring(1);
        } else {
            return replacement.toLowerCase();
        }
    }

    private static String getEnglishWordFromDictionary(String frenchWord, Map<String, String> dictionary) {
        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(frenchWord)) {
                return entry.getKey();
            }
        }
        return "";
    }
}

