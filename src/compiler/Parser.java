//TODO IF THERE IS AN EPSILON IN YOUR FIRST SET OF YOUR RULE, YOU CODE THE FOLLOW SET IN IF ELSE STATEMENT

package compiler;

import java.util.ArrayDeque;

public class Parser {

    private boolean isAccept;
    private Token token;
    private ArrayDeque<Token> tokens;

    public Parser (ArrayDeque<Token> theTokens) {
        isAccept = true;
        this.tokens = theTokens;
    }

    public Token getCurrentToken() {
        return tokens.removeFirst();
    }

    public boolean isAccepted(ArrayDeque<Token> theTokens) {
        program();
        return isAccept;
    }

    // program -> declaration-list
    // firsts: int void
    // follows: $
    public void program() {
        if (tokens.isEmpty()) return;
        declaration_list();
    }

    //declaration-list -> declaration declaration-list`
    // firsts: int void
    // follows: $
    public void declaration_list() {
        if (tokens.isEmpty()) return; //TODO should this even be here?
        declaration();
        declaration_list_prime();
    }

    //declaration_list` -> declaration declaration_list` | empty
    // firsts: int void empty
    // follows: $
    public void declaration_list_prime() {
        if (tokens.isEmpty()) return;
        else {
            declaration();
            declaration_list_prime();
        }
    }

    //declaration -> var_declaration | function_declaration
    // firsts: int void
    // follows: $ int void
    public void declaration() {
        if (tokens.isEmpty()) return;
        var_declaration();
        function_declaration();
    }

    //var_declaration -> type-specifier ID var-declaration'
    // firsts: int void
    // follows: $ ( ; ID NUM if int return void while { }
    public void var_declaration() {
        if (tokens.isEmpty()) return;
        type_specifier();
        if (tokens.removeFirst().getLexeme(token).matches("\\(|;|if|int|return|void|while|\\{|\\}") ||
            tokens.removeFirst().getCategory(token).equals("ID") ||
            tokens.remove().getCategory(token).equals("NUM")
            ) {
            var_declaration_prime();
        } else return;
    }

    //var-declaration' -> ; | [ NUM ] ;
    // firsts: ; [
    // follows: $ ( ; ID NUM if int return void while { }
    public void var_declaration_prime() {
        if (tokens.isEmpty()) return;
        if ( tokens.removeFirst().equals(";") ) return;
        else if ( tokens.removeFirst().equals("[") ) {
            if ( tokens.removeFirst().getCategory(token).equals("NUM") ) {
                if ( tokens.removeFirst().getCategory(token).equals("]") ) {
                    if ( tokens.removeFirst().equals(";") ) return;
                }
            }
        }
    }

    //type-specifier -> "int" | "void"
    // firsts: int void
    // follows: ID
    public boolean type_specifier() {
        return tokens.removeFirst().equals("int") || tokens.removeFirst().equals("void");
    }

    //fun-declaration -> int ID ( params ) compound-stmt || void ID ( params ) compound-stmt
    // firsts: int void
    // follows: $ int void
    public void function_declaration() {
        if (tokens.isEmpty()) return;
        if ( tokens.removeFirst().getCategory(token).equals("NUM") ) { // int OR void
            if ( tokens.removeFirst().getCategory(token).equals("ID")) {
                if (tokens.removeFirst().equals("(")) {
                    params();
                    if (tokens.removeFirst().equals(")")) compound_statement();
                }
            }
        }
    }

    //params -> param-list | void
    // firsts: int void
    // follows: )
    public void params() {
        if ( tokens.removeFirst().equals("void") ) return;
        else param_list();
    }

    //params_list -> param params-list`
    // firsts: int void
    // follows: )
    public void param_list() {
        param();
        param_list_prime();
    }

    //param -> int ID param` | void ID param'
    // firsts: int void
    // follows: ) ,
    public void param() {
        if ( tokens.removeFirst().getCategory(token).equals("NUM") ) {
            if ( tokens.removeFirst().getCategory(token).equals("ID") ) param_prime();
        }
    }

    //params_list_prime -> "," type-specifier "ID" param_prime <- WTF where did this rule come from?
    //param-list_prime -> , param param-list_prime | empty
    // firsts: , empty
    // follows: )
    public void param_list_prime() { //TODO here
        if ( tokens.removeFirst().equals(",") ) {
            param();
            param_list_prime();
        } else return;
    }



    // param_prime -> "[" "]" | empty
    // firsts: [ empty
    // follows: ) ,
    public void param_prime() {
        if ( tokens.removeFirst().equals("[") ) {
            if ( tokens.removeFirst().equals("]") ) return;
        } else return;
    }

    // compound_statement -> "{" local-declarations statement-list "}"
    // firsts: "{"
    // follows: $ int void
    public void compound_statement() {
         if ( tokens.removeFirst().equals("{") ) {
             local_declarations();
             statement_list();
             if ( tokens.removeFirst().equals("}") ) return;
         }
    }

    // local-declarations -> var-declaration local-declarations | empty
    // firsts: int void empty
    // follows: ( ; ID NUM additive_expression if return while { }
    public void local_declarations() {
        if (tokens.removeFirst().getLexeme(token).matches("\\(|;|if|return|while|\\{|\\}") ||
            tokens.removeFirst().getCategory(token).equals("ID") ||
            tokens.removeFirst().getCategory(token).equals("NUM")
        ) return;
        else {
            var_declaration();
            local_declarations();
        }
    }

    // statement_list -> statement statement_list | empty
    // firsts: ( ; ID NUM additive if return while { empty
    // follows: "}"
    public void statement_list() {
        if ( tokens.removeFirst().equals("") ) return;
        else {
            statement();
            statement_list();
        }
    }

    // statement -> expression-stmt | { local-declarations statement-list }
    // | selection-stmt | iteration-stmt | return-stmt
    // firsts: ( ; ID NUM additive if return while {
    // follows: ( ; ID NUM additive else if return while { }
    public boolean statement() {
         if ( tokens.removeFirst().equals("{") ) {
             local_declarations();
             statement_list();
             if ( tokens.removeFirst().equals("}") ) return true;
         }
         if ( expression_statement() ) return true;
         if ( selection_statement() ) return true;
         if ( iteration_statement() ) return true;
         if ( return_statement() ) return true;
         isAccept = false;
         return false;
    }

    // expression_statement -> expression ";" | ";"
    // firsts: ( ; ID NUM additive
    // follows: ( ; ID NUM additive else if return while { }
    public boolean expression_statement() {
         if ( tokens.removeFirst().equals(";")) { return true; }
         else {
            expression();
            if ( tokens.removeFirst().equals(";") ) return true;
         }
         isAccept = false;
         return false;
    }

    // selection_statement -> "if" "(" expression ")" statement selection-stmt`
    // firsts: if
    // follows: ( ; ID NUM additive else if return while { }
    public boolean selection_statement() {
         if ( tokens.removeFirst().equals("if") ) {
             if ( tokens.removeFirst().equals("(") ) {
                 expression();
                 if ( tokens.removeFirst().equals(")") ) {
                     statement();
                     selection_statement_prime();
                     return true;
                 }
             }
         }
         isAccept = false;
         return false;
    }

    // selection_statement_prime -> empty | "else" statement
    // firsts: else empty
    // follows: ( ; ID NUM additive else if return while { }
    public void selection_statement_prime() {
        if ( tokens.removeFirst().equals("") ) return;
        else if ( tokens.removeFirst().equals("else") ) return;
    }

    //iteration-statement -> "while" "(" expression ")" statement
    // firsts: while
    // follows: ( ; ID NUM additive else if return while { }
    public boolean iteration_statement() {
        if ( tokens.removeFirst().equals("while") ) {
            if ( tokens.removeFirst().equals("(") ) {
                expression();
                if ( tokens.removeFirst().equals(")") ) {
                    statement();
                    return true;
                }
            }
        }
        isAccept = false;
        return false;
    }

    // return-stmt -> "return" return-stmt`
    // firsts: return
    // follows: ( ; ID NUM additive else if return while { }
    public boolean return_statement() {
        if ( tokens.removeFirst().equals("return") ) {
            return_statement_prime();
            return true;
        }
        isAccept = false;
        return false;
    }

    // return-stmt_prime -> ; | ID ( args ) term' additive-expression' ; | ID var' = expression ;
    // | ID var' term' additive-expression' ; | ( expression ) term' additive-expression' ;
    // | NUM term' additive-expression' ; | additive expression relop additive-expression ;
    // firsts: ( ; ID NUM additive
    // follows: ( ; ID NUM additive else if return while { }
    public void return_statement_prime() {
        if ( tokens.removeFirst().equals(";") ) { return; }
        else if ( tokens.removeFirst().getCategory(token).equals("ID") ) {
            if ( tokens.removeFirst().equals("(") ) {
                args();
                if ( tokens.removeFirst().equals(")") ) {
                    term_prime();
                    additive_expression_prime();
                    if ( tokens.removeFirst().equals(";") ) { return; }
                }
            } else {
                var_prime();
                if ( tokens.removeFirst().equals("=") ) {
                    expression();
                    if ( tokens.removeFirst().equals(";") ) { return; }
                } else {
                    term_prime();
                    additive_expression_prime();
                    if ( tokens.removeFirst().equals(";")) { return; }
                }
            }
        } else if ( tokens.removeFirst().equals("(") ) {
            expression();
            if ( tokens.removeFirst().equals(")") ) {
                term_prime();
                additive_expression_prime();
                if ( tokens.removeFirst().equals(";") ) { return; }
            }
        } else if ( tokens.removeFirst().getCategory(token).equals("NUM") ) {
            term_prime();
            additive_expression_prime();
            if ( tokens.removeFirst().equals(";") ) { return; }
        } else {
            additive_expression();
            relop();
            additive_expression();
            if ( tokens.removeFirst().equals(";") ) { return; }
        }
    }

    // expression -> var "=" expression | simple-expression
    // firsts: ( ID NUM
    // follows: != ) , ; < <= == > >= ]
    public void expression() {
        if ( var() ) {
            if ( tokens.removeFirst().equals("=") ) { expression(); }
        } else simple_expression();
    }

    // var -> "ID" var_prime
    // firsts: ID
    // follows: =
    public boolean var() {
        if ( tokens.removeFirst().getCategory(token).equals("ID") ) {
            var_prime();
            return true;
        }
        isAccept = false;
        return false;
    }

    // var_prime -> empty | "[" expression "]"
    // firsts: [ empty
    // follows: != ) * + , - / ; < <= = == > >= ]
    public boolean var_prime() {
        if ( tokens.removeFirst().equals("[") ) {
            expression();
            if ( tokens.removeFirst().equals("]") ) { return true; }
        } else if ( tokens.removeFirst().equals("") ) { return true; }
        isAccept = false;
        return false;
    }

    // simple-expression -> additive-expression relop additive-expression | additive-expression
    // firsts: ( ID NUM
    // follows: != ) , ; < <= == > >= ]
    public void simple_expression() {
        additive_expression();
        if ( relop() ) { additive_expression(); }
        else { additive_expression(); }
    }

    // relop -> <= | >= | == | != | > | <
    // firsts: != < <= == > >=
    // follows: ( ID NUM
    public boolean relop() {
        if ( tokens.removeFirst().equals("<=") || tokens.removeFirst().equals(">=") ||
                tokens.removeFirst().equals("==") || tokens.removeFirst().equals("!=") ||
                tokens.removeFirst().equals(">") || tokens.removeFirst().equals("<")
        ) { return true; }
        isAccept = false;
        return false;
    }

    // additive_expression -> term additive_expression_prime
    // firsts: ( ID NUM
    // follows: != ) , ; < <= == > >= ]
    public void additive_expression() {
        term();
        additive_expression_prime();
    }

    // additive_expression_prime -> addop term additive_expression_prime | empty
    // firsts: + - empty
    // follows: != ) , ; < <= == > >= ]
    public void additive_expression_prime() {
        if (tokens.removeFirst().equals("")) { return; }
        else {
            addop();
            term();
            additive_expression_prime();
        }
    }

    // addop -> + | -
    // firsts: + -
    // follows: ( ID NUM
    public void addop() {
        if ( tokens.removeFirst().equals("+") || tokens.removeFirst().equals("=") ) { return; }
    }

    // term -> factor term_prime
    // firsts: ( ID NUM
    // follows: != ) + , - ; < <= == > >= ]
    public void term() {
        factor();
        term_prime();
    }

    // term_prime -> mulop factor term_prime | empty
    // firsts: * / empty
    // follows: != ) + , - ; < <= == > >= ]
    public void term_prime() {
        if ( tokens.removeFirst().equals("") ) { return; }
        else {
            mulop();
            factor();
            term_prime();
        }
    }

    // mulop -> * | /
    // firsts: * /
    // follows: ( ID NUM
    public boolean mulop() {
        return tokens.removeFirst().equals("*") || tokens.removeFirst().equals("/");
    }

    //factor -> ( expression ) || ID var' || call || NUM
    // firsts: ( ID NUM
    // follows: != ) * + , - / ; < <= == > >= ]
    public void factor() {
        if ( tokens.removeFirst().equals("(") ) {
            expression();
            if ( tokens.removeFirst().equals(")") ) { return; }
        } else if ( tokens.removeFirst().getCategory(token).equals("ID") ) {
            var_prime();
        } else if ( tokens.removeFirst().getCategory(token).equals("NUM") ) {
            return;
        } else { call(); }
    }

    // call -> ID ( args )
    // firsts: ID
    // follows: != ) * + , - / ; < <= == > >= ]
    public void call() {
        if ( tokens.removeFirst().getCategory(token).equals("ID") ) {
            if ( tokens.removeFirst().equals("(") ) {
                args();
                if ( tokens.removeFirst().equals(")") ) { return; }
            }
        }
    }

    // args -> args-list | empty
    // firsts: ( ID NUM additive empty
    // follows: )
    public void args() {
        if ( tokens.removeFirst().equals("") ) { return; }
        else { arg_list(); }
    }

    //arg-list ->
    //          ID ( args ) term' additive-expression' arg-list'
    //        | ID var' = expression arg-list'
    //        | ID var' term' additive-expression' arg-list'
    //        | ( expression ) term' additive-expression' arg-list'
    //        | NUM term' additive-expression' arg-list'
    //        | additive expression relop additive-expression arg-list'
    // firsts: ( ID NUM additive
    // follows: )
    public void arg_list() {
        if ( tokens.removeFirst().getCategory(token).equals("ID") ) {
            if ( tokens.removeFirst().equals("(") ) {
                args();
                if ( tokens.removeFirst().equals(")") ) {
                    term_prime();
                    additive_expression_prime();
                    arg_list_prime();
                }
            }
            if (tokens.removeFirst().equals(var_prime()) ) {
                if ( tokens.removeFirst().equals("=") ) {
                    expression();
                    arg_list();
                } else {
                    term_prime();
                    additive_expression_prime();
                    arg_list_prime();
                }
            }
        }
        else if ( tokens.removeFirst().equals("(") ) {
            expression();
            if ( tokens.removeFirst().equals(")") ) {
                term_prime();
                additive_expression_prime();
                arg_list_prime();
            }
        } else if ( tokens.removeFirst().getCategory(token).equals("NUM") ) {
            term_prime();
            additive_expression_prime();
            arg_list_prime();
        } else {
            additive_expression();
            relop();
            additive_expression();
            arg_list_prime();
        }
    }

    // arg_list_prime -> , expression arg-list_prime | empty
    // firsts: , empty
    // follows: )
    public void arg_list_prime() {
        if ( tokens.removeFirst().equals("") ) return;
        else if ( tokens.removeFirst().equals(",") ) {
            expression();
            arg_list_prime();
        }
    }

} // Parser
