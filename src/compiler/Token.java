package compiler;

class Token<String> {

    private String lexeme;
    private String category;

    Token (String lexeme, String category) {
        this.lexeme = lexeme;
        this.category = category; // KEYWORD, ID, NUM, ERROR, SPECIAL_SYMBOL
    }

    @Override
    public java.lang.String toString() {
        return "Token {" + "lexeme=" + lexeme + ", category=" + category + '}';
    }

    public String getLexeme() {
        return this.lexeme;
    }

    public String getCategory() {
        return this.category;
    }

} // class Token