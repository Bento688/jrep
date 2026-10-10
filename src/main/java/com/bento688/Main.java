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

    @Override
    public Integer call() {
        // get the list of paths
        List<Path> paths = Arrays.stream(files).map(Path::of).toList();
        boolean multipleFiles = paths.size() > 1;
        String cleanPattern = ignoreCase ? pattern.toLowerCase(Locale.ROOT) : pattern;

        // flags
        boolean found = false;
        boolean issues = false;

        if (multipleFiles) {
            for (Path path : paths) {
                try {
                    List<String> lines = Files.readAllLines(path);

                    for (String line : lines) {
                        if (ignoreCase ? line.toLowerCase(Locale.ROOT).contains(cleanPattern) : line.contains(cleanPattern)) {
                            System.out.println(path + ":" + line);
                            found = true;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error: " + e);
                    issues = true;
                }
            }
        } else {
            try {
                List<String> lines = Files.readAllLines(paths.getFirst());
                for (String line : lines) {
                    if (ignoreCase ? line.toLowerCase(Locale.ROOT).contains(cleanPattern) : line.contains(cleanPattern)) {
                        System.out.println(line);
                        found = true;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error: " + e);
                issues = true;
            }
        }

        if (issues) {
            return 2;
        } else if (found) {
            return 0;
        } else {
            return 1;
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
