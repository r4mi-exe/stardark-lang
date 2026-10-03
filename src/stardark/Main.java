package stardark;

public class Main {
    public static void main(String[] args) {
        // Token token = new Token(TokenType.LEFT_PARENTHESIS, "(", null, 1);
        // System.out.println(token);
        Scanner scanner = new Scanner("= == ~= < <= > >= ! this is a comment\n+ ~");
        for (Token token : scanner.scanTokens()) {
            System.out.println(token);
        }

        Scanner scanner1 = new Scanner("\"hello\" 42 3.14 7.");
        for (Token token : scanner1.scanTokens()) {
            System.out.println(token);
        }
    }
}