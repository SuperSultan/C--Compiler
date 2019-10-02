package compiler;

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
        //return this.getFirst().category;
        return this.category;
    }

    public String getLexeme(Token token) {
        return this.lexeme;
    }

} // class Token