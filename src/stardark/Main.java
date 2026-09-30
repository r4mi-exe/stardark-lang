package stardark;

public class Main {
    public static void main(String[] args) {
        // Token token = new Token(TokenType.LEFT_PARENTHESIS, "(", null, 1);
        // System.out.println(token);
        Scanner scanner = new Scanner("(+ - * /), @");
        for (Token token : scanner.scanTokens()) {
            System.out.println(token);
        }
    }
}