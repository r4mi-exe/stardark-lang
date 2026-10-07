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

    // Function to check if current token matches the expected type
    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type == type;
    }

    // Function to check if current token matches any of the expected types and advance the position if it does
    // Returns false otherwise
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    // Function to throw a ParseError with a message and the line number of the token that caused the error
    private static class ParseError extends RuntimeException {}
    private ParseError error(Token token, String message) {
        Main.error(token.line, message);
        return new ParseError();
    }

    private Expr primary() {
        if (match(TokenType.FALSE)) {
            return new Expr.Literal(false);
        }
        if (match(TokenType.TRUE)) {
            return new Expr.Literal(true);
        }
        if (match(TokenType.NIL)) {
            return new Expr.Literal(null);
        }

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(previous().literal);
        }

        throw error(peek(), "Expected expression.");
    }

    private Expr expression() {
        return primary();
    }

    Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }
}
