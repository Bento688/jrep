package com.bento688;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * Hello world!
 *
 */
@Command(
        name = "jrep",
        description = "A simple grep implementation in Java",
        mixinStandardHelpOptions = true
)
public class Main implements Runnable {

    @Option(
            names = {"-i", "--ignore-case"},
            description = "Ignore case distinctions"
    )
    private boolean ignoreCase;

    @Parameters(
            index = "0",
            arity = "1",
            description = "Pattern to search for"
    )
    private String pattern;

    @Override
    public void run() {
        System.out.println("Searching for: " + pattern);
        System.out.println("Ignore case: " + ignoreCase);
    }

    public static void main(String[] args) {
        new CommandLine(new Main()).execute(args);
    }
}
