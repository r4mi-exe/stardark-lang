package stardark;

// Sealed interface representing different types of statements in the AST
sealed interface Stmt {
    record Expression(Expr expression) implements Stmt {}
    record Print(Expr expression) implements Stmt {}
}