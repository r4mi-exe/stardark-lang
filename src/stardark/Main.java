package stardark;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner("var count_1 = 10 check orchid do print nil end");
        for (Token token : scanner.scanTokens()) {
            System.out.println(token);
        }
    }
}