package stardark;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Scanner {
    // Source code to be scanned, list of tokens found, positions in source code, and current line number
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    // Map of keywords to their corresponding token types
    private static final Map<String, TokenType> keywords = new HashMap<>();
    static {
        keywords.put("var", TokenType.VAR);
        keywords.put("func", TokenType.FUNC);
        keywords.put("ret", TokenType.RET);
        keywords.put("check", TokenType.CHECK);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("for", TokenType.FOR);
        keywords.put("do", TokenType.DO);
        keywords.put("end", TokenType.END);
        keywords.put("print", TokenType.PRINT);
        keywords.put("and", TokenType.AND);
        keywords.put("or", TokenType.OR);
        keywords.put("not", TokenType.NOT);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("nil", TokenType.NIL);
    }

    // Constructor to set source code to be scanned
    Scanner(String source) {
        this.source = source;
    }

    // Function to check if end of source code has been reached
    private boolean isAtEnd() {
        return current >= source.length();
    }

    // Function to return character at current position and increment current position
    private char advance() {
        return source.charAt(current++);
    }

    // Function to conditionally advance, checks whether next character is the expected one, if so:
    // move forward and return true, otherwise, leave current variable alone and return false
    private boolean match(char expected) {
        // do not read past the end of the text, would crash
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }
        current++;
        return true;
    }
    
    // Looks and never moves current position, returns character at current position or '\0' if at end of source
    // used to check for comments, which start with '!'
    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    // Function to check if a character is a digit (0-9)
    private Boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    // Function to check if a character is an alphabetic character (a-z, A-Z) or an underscore (_)
    private Boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    // Function to check if a character is alphanumeric (or an underscore)
    private Boolean isAlphaNumeric(char c) {
        return isDigit(c) || isAlpha(c);
    }

    // Looks at next character after current position; returns it or '\0' if at end of source
    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    // Function to scan a number from the source code, adds it to list of tokens
    // Numbers can be integers or floating-point numbers
    // Every stardark number will be a double, similiar to Lua and Javascript
    private void number() {
        while (isDigit(peek())) {
            advance();
        }
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) {
                advance();
            }
        }
        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    // Function to scan a string literal from the source code, adds it to list of tokens
    // Strings are surrounded by double quotes; can span multiple lines
    // Prints an error if string is not terminated before end of source code
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }
        if (isAtEnd()) {
            Main.error(line, "Unterminated string.");
            return;
        }
        advance();
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    // Function to scan a keyword or identifier from the source code, adds it to list of tokens
    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) {
            type = TokenType.IDENTIFIER;
        }
        addToken(type);
    }

    // Overloading to not write null everytime, this is for tokens with no literal value, like "("
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    // Slices out token's text from source code, then builds the token from its type, text, literal value, and line number, and adds it to list of tokens
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    // Scans a single token from the source code and adds it to list of tokens
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
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<': 
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '~':
                if (match('=')) {
                    addToken(TokenType.NOT_EQUAL);
                } else {
                    Main.error(line, "Unexpected character '~'.");
                }
                break;
            case '"':
                string();
                break;
            case '!':
                while (peek() != '\n' && !isAtEnd()) {
                    advance();
                }
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
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Main.error(line, "Unexpected character '" + c + "'.");
                }
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
