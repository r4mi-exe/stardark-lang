package stardark;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    // Constructor to set source code to be scanned
    Scanner(String source) {
        this.source = source;
    }

    // Function to check if end of source code has been reached
    private boolean isAtEnd() {
        return current >= source.length();
    }

    // Function to scan a single token from source code 
    // TODO: implement logic to identify different types of tokens in source code
    private void scanToken() {
        current++;
    }

    // Function to scan all tokens from source code; returns list of tokens found
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }
}
