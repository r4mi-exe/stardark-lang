package stardark;

// Sealed interface representing different types of expressions in the AST
sealed interface Expr {
    record Variable(Token name) implements Expr {}
    record Logical(Expr left, Token operator, Expr right) implements Expr {}
    record Binary(Expr left, Token operator, Expr right) implements Expr {}
    record Grouping(Expr expression) implements Expr {}
    record Literal(Object value) implements Expr {}
    record Unary(Token operator, Expr right) implements Expr {}
}