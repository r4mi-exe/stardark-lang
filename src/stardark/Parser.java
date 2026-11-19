package stardark;

import java.util.List;
import java.util.ArrayList;

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

    // Insists on the required token type, if it is, advance and return, otherwise token is missing and error
    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw error(peek(), message);
    }

    // Function to throw a ParseError with a message and the line number of the token that caused the error
    private static class ParseError extends RuntimeException {}
    private ParseError error(Token token, String message) {
        Main.error(token.line, message);
        return new ParseError();
    }

    // Function to parse the tokens into a primary expression
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
        if (match(TokenType.LEFT_PARENTHESIS)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PARENTHESIS, "Expected ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expected expression.");
    }

    // Function to parse the tokens into a unary expression
    private Expr unary() {
        if (match(TokenType.MINUS, TokenType.NOT)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    // Parse first operand, as long as next token is * or /, use it and go around loop again
    // Loop handles any number of opreators in a row (ex. 2 * 3 * 4)
    // Save operator token, parse right side operand
    // Build new binary node whose left side is everything we've built so far and whose right side is new operand
    // Store it back into expr, and when done looping, hand back finished tree
    private Expr factor() {
        Expr expr = unary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // Similiar to factor above, parses first operand, uses +/- token and go around loop again
    // Loop handles any number of operators in a row (ex. 1 - 2 - 3)
    // Save operator token, parse right side operand
    // Build new binary node whose left side is everything we've built so far and whose right side is new operand
    // Store it back into expr, and when done looping, hand back finished tree
    private Expr term() {
        Expr expr = factor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // Same format as term and factor above
    private Expr comparison() {
        Expr expr = term();

        while (match(TokenType.LESS, TokenType.LESS_EQUAL, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // Same format as comparison, term, and factor above
    private Expr equality() {
        Expr expr = comparison();

        while (match(TokenType.EQUAL_EQUAL, TokenType.NOT_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // Same format as comparison, term, factor, and equality above
    private Expr logicAnd() {
        Expr expr = equality();

        while (match(TokenType.AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    // Same format as comparison, term, factor, equality and logicAnd above
    private Expr logicOr() {
        Expr expr = logicAnd();

        while (match(TokenType.OR)) {
            Token operator = previous();
            Expr right = logicAnd();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    // Parses an expression with expression(), returns a Stmt.Print holding it
    private Stmt printStatement() {
        Expr expr = expression();
        return new Stmt.Print(expr);
    }

    // Parses an expression with expression(), returns a Stmt.Expression holding it
    private Stmt expressionStatement() {
        Expr expr = expression();
        return new Stmt.Expression(expr);
    }

    // Returns the correct statement type given the token
    private Stmt statement() {
        if (match(TokenType.PRINT)) {
            return printStatement();
        } else {
            return expressionStatement();
        }
    }

    // Function to parse the tokens into an expression, starting with the logicOr expression
    private Expr expression() {
        return logicOr();
    }

    // Function to parse statements and return them in a list
    List<Stmt> parse() {
        try {
            ArrayList<Stmt> stmts = new ArrayList<>();
            while (!isAtEnd()) {
                stmts.add(statement());
            }
            return stmts;
        } catch (ParseError error) {
            return null;
        }
    }
}
