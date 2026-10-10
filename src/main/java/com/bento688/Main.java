package com.bento688;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

@Command(name = "jrep", description = "A simple grep implementation in Java", mixinStandardHelpOptions = true)
public class Main implements Callable<Integer> {

    @Option(names = {"-i", "--ignore-case"}, description = "Ignore case distinctions")
    private boolean ignoreCase;

//    @Option(names = {"-d", "--directory"}, description = "The directory in which the search should be conducted")
//    private String directory;

    @Parameters(index = "0", arity = "1", description = "Pattern to search for")
    private String pattern;

    @Parameters(index = "1", arity = "1..*", description = "The file names to search for")
    private String[] files;

    public record FileSearchResult(boolean found, boolean issues) {}

    @Override
    public Integer call() {
        // get the list of paths
        List<Path> paths = Arrays.stream(files).map(Path::of).toList();

        // call a single process files method
        FileSearchResult result = processFiles(paths, pattern, ignoreCase);

        if (result.issues) {
            return 2;
        } else if (result.found) {
            return 0;
        } else {
            return 1;
        }
    }

    private static boolean patternInLine(String line, String pattern) {
        return line.contains(pattern);
    }

    private static FileSearchResult processFiles(List<Path> paths, String pattern, boolean ignoreCase) {
        boolean found = false;
        boolean issues = false;

        boolean usePrefix = paths.size() > 1;
        String prefix = "";

        String cleanPattern = ignoreCase ? pattern.toLowerCase(Locale.ROOT) : pattern;

        for (Path path : paths) {
            if (usePrefix) {
                prefix = path.toString() + ":";
            }

            try {
                List<String> lines = Files.readAllLines(path);

                for (String line : lines) {
                    String compareLine =  ignoreCase ? line.toLowerCase(Locale.ROOT) : line;
                    boolean matches = Main.patternInLine(compareLine, cleanPattern);
                    found = matches || found;
                    if (matches) {
                        System.out.println(prefix + line);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error: " + e);
                issues = true;
            }
        }
        return new FileSearchResult(found, issues);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
