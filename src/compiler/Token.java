package compiler;

class Token {

    private String lexeme;
    private String category;
    private String dataType;

    Token (String lexeme, String category) {
        this.lexeme = lexeme;
        this.category = category; // KEYWORD, ID, NUM, ERROR, SPECIAL_SYMBOL
        //if ( this.lexeme.equals("int") ) {
        //    this.dataType = "int";
        //} else if ( this.lexeme.equals("void") ) {
        //    this.dataType = "void";
        //}
    }

    public String getCategory() { return this.category; }

    public String getLexeme() { return this.lexeme; }

} // class Token