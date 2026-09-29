package stardark;

public class Main {
    public static void main(String[] args) {
        Token token = new Token(TokenType.LEFT_PARENTHESIS, "(", null, 1);
        System.out.println(token);
    }
}