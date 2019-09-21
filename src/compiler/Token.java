package compiler;
import java.util.ArrayList;
import java.util.List;

class Token {

    private String lexeme;
    private String category;
    private int scope;
    private boolean validity;
    private static int tokensize;
    private static List<Token> tokens = new ArrayList<Token>();

    Token (String lexeme, String category) {
        this.lexeme = lexeme; // KEYWORD, ID, NUM, ERROR, SPECIAL_SYMBOL, ERROR
        this.category = category; // the actual token itself
    }

    Token () {}

    public static void addTokens(Token token) {
        tokens.add(token);
    }

    public static void printTokens() {
        for(Token token : tokens ) {
            System.out.println(token.lexeme + " = " + token.category + " ");
        }
    }

    public List<Token> getTokenList() {
        return tokens;
    }

} // class Token