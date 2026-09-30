package stardark;

/*
 * TokenType enum class; represents different types of tokens that can be found in source code.
 */
public enum TokenType {
    LEFT_PARENTHESIS, RIGHT_PARENTHESIS, COMMA,
    PLUS, MINUS, STAR, SLASH,
    EQUAL, EQUAL_EQUAL, NOT_EQUAL,
    LESS, LESS_EQUAL, GREATER, GREATER_EQUAL,
    EOF
}