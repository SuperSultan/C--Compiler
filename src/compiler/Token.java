package compiler;

import java.util.ArrayDeque;

class Token {

    private String lexeme;
    private String category;
    private boolean isError;

    Token (String lexeme, String category) {
        this.lexeme = lexeme;
        this.category = category; // KEYWORD, ID, NUM, ERROR, SPECIAL_SYMBOL, ERROR
    }

    Token () {}

    public String getCategory(Token token) {
        return token.category;
    }

} // class Token