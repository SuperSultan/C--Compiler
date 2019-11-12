package compiler;

import java.util.ArrayDeque;

class Token {

    private String lexeme;
    private String category;
    private ArrayDeque<Token> tokens;

    Token (String lexeme, String category) {
        this.lexeme = lexeme;
        this.category = category; // KEYWORD, ID, NUM, ERROR, SPECIAL_SYMBOL
    }

    public String getCategory() { return this.category; }

    public String getLexeme() { return this.lexeme; }

    public void removeToken() { tokens.removeFirst(); }

} // class Token