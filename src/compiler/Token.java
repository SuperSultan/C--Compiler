package compiler;

import java.util.ArrayDeque;

class Token {

    String lexeme;
    String category;
    private int scope;
    private boolean validity;
    private static int tokensize;
    private static ArrayDeque<Token> tokens = new ArrayDeque<Token>();

    Token (String lexeme, String category) {
        this.lexeme = lexeme;
        this.category = category; // KEYWORD, ID, NUM, ERROR, SPECIAL_SYMBOL, ERROR
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

    public ArrayDeque<Token> getTokenList() {
        return tokens;
    }

} // class Token