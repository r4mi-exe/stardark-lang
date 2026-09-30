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

    // Returns character at current position and increments current position
    private char advance() {
        return source.charAt(current++);
    }

    // Overloading to not write null everytime, this is for tokens with no literal value, like "("
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    // Slices out token's text from source code, then builds the token from its type, text, literal value, and line number, and adds it to the list
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    // Scans a single token from the source code and adds it to the list of tokens
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(TokenType.LEFT_PARENTHESIS);
                break;
            case ')':
                addToken(TokenType.RIGHT_PARENTHESIS);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;
            case '/':
                addToken(TokenType.SLASH);
                break;
            // Ignore whitespace characters
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            default:
                System.out.println("Unexpected character '" + c + "' at line " + line + ".");
        }
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
