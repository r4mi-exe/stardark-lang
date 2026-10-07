package stardark;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    static Boolean hadError = false;

    // psvm; Prints script usage if no arguments are provided, otherwise runs the script file
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: stardark <script>");
            System.exit(64);
        }
        runFile(args[0]);
    }

    // Function to read the path of the script file, read its contents, and run the script
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, StandardCharsets.UTF_8));
        if (hadError) {
            System.exit(65);
        }
    }

    // Function to run the script, scan the source code for tokens, and print them
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();
        if (hadError) {
            return;
        }
        System.out.println(expression);
    }

    // Function to print error messages with line number and set hadError to true
    static void error(int line, String message) {
        System.err.println("[line " + line + "] Error: " + message);
        hadError = true;
    }
}