package com.bento688;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "jrep", description = "A simple grep implementation in Java", mixinStandardHelpOptions = true)
public class Main implements Callable<Integer> {

    @Option(names = {"-i", "--ignore-case"}, description = "Ignore case distinctions")
    private boolean ignoreCase;

    @Option(names = {"-d", "--directory"}, description = "The directory in which the search should be conducted")
    private String directory;

    @Parameters(index = "0", arity = "1", description = "Pattern to search for")
    private String pattern;

    @Parameters(index = "1", arity = "1..*", description = "The file names to search for")
    private String[] files;

    @Override
    public Integer call() {
        try {
            // get the current file as a path
            Path path = Path.of(files[0]);
            // read the lines from that path
            List<String> lines = Files.readAllLines(path);

            boolean found = false;
            // print the lines one by one
            for (String line : lines) {
                if (line.contains(pattern)) {
                    System.out.println(line);
                    found = true;
                } else {
                    continue;
                }
            }
            if (found) {
                return 0;
            } else {
                return 1;
            }
        } catch (IOException e) {
            System.err.println("Error: " + e);
            return 2;
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
