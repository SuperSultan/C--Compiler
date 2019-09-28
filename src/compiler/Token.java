package compiler;

import java.util.ArrayDeque;

class Token {

    private String lexeme;
    private String category;
    private boolean isError;
    private ArrayDeque<Token> tokens = new ArrayDeque<>();

    Token (String lexeme, String category) {
        this.lexeme = lexeme;
        this.category = category; // KEYWORD, ID, NUM, ERROR, SPECIAL_SYMBOL, ERROR
    }

    Token () {}

    public String getCategory(Token token) {
        return token.category;
    }

    public ArrayDeque<Token> getTokenList() {
        return tokens;
    }

    public void addToken(Token token) {
        tokens.add(token);
    }

    public void addBadToken(Token token) {
        tokens.add(token);
        isError = true;
    }

    public void printTokens() {
        for(Token token : tokens ) {
            System.out.println(token.lexeme + " = " + token.category + " ");
        }
    }

} // class Token