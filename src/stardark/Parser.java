package stardark;

import java.util.List;

public class Parser {
    // List of tokens to be parsed and current position in the list
    private final List<Token> tokens;
    private int current = 0;

    // Constructor to set the list of tokens to be parsed
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Function to return the current token without advancing the position
    private Token peek() {
        return tokens.get(current);
    }

    // Function to return the previous token without advancing the position
    private Token previous() {
        return tokens.get(current - 1);
    }

    // Function to check if the end of the token list has been reached
    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    // Function to return the current token and advance the position
    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }
}
