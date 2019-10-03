//TODO IF THERE IS AN EPSILON IN YOUR FIRST SET OF YOUR RULE, YOU CODE THE FOLLOW SET IN IF ELSE STATEMENT
//TODO IF THERE IS A $ IN THE FOLLOW SET, YOU CHECK IF THE CURRENT TOKEN IS EMPTY (REGARDLESS OF WHETHER THERE IS AN EPSILON)

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
        declaration_list();
        return isAccept;
    }

    public void reject() {
        isAccept = false;
        System.out.println("REJECT");
        System.exit(0);
    }

    //declaration_list` -> declaration declaration_list` | empty
    // FIRSTS: int void empty FOLLOWS: $
    public void declaration_list() {
        if (tokens.isEmpty()) return;
        declaration();
        declaration_list();
    }

    //declaration -> var_declaration | function_declaration
    // FIRSTS: int void FOLLOWS: $ int void
    public void declaration() {
        if (tokens.isEmpty()) return;
        var_declaration();
        function_declaration();
    }

    //var_declaration -> int ID var-declaration' | void ID var-declaration'
    // FIRSTS: int void FOLLOWS: $ ( ; ID NUM if int return void while { }
    public void var_declaration() {
        if (tokens.isEmpty()) return;
        if ( tokens.removeFirst().getLexeme(token).matches("int|void") ) {
            if ( tokens.removeFirst().getCategory(token).equals("ID") )
                var_declaration_prime();
        }
    }

    //fun-declaration -> int ID ( params ) compound-stmt | void ID ( params ) compound-stmt
    // FIRSTS: int void FOLLOWS: $ int void
    public void function_declaration() {
        if (tokens.isEmpty()) return;
        if ( tokens.removeFirst().getLexeme(token).matches("int|void") ) {
            if ( tokens.removeFirst().getCategory(token).equals("ID")) {
                if (tokens.removeFirst().equals("(")) {
                    params();
                    if (tokens.removeFirst().equals(")")) compound_statement();
                }
            }
        }
    }

    //var-declaration' -> ; | [ NUM ] ;
    // FIRSTS: ; [ FOLLOWS: $ ( ; ID NUM if int return void while { }
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

    //params -> param params-list` | void
    // FIRSTS: int void FOLLOWS: )
    public void params() {
        if ( tokens.removeFirst().equals("void") ) return;
        param();
        param_list_prime();
    }

    //param -> int ID param` | void ID param'
    // FIRSTS: int void FOLLOWS: ) ,
    public void param() {
        if ( tokens.removeFirst().getCategory(token).equals("NUM") ) {
            if ( tokens.removeFirst().getCategory(token).equals("ID") ) param_prime();
        }
    }

    //params_list_prime -> "," type-specifier "ID" param_prime <- WTF where did this rule come from?
    //param-list_prime -> , param param-list_prime | empty
    // FIRSTS: , empty FOLLOWS: )
    public void param_list_prime() {
        if ( tokens.removeFirst().equals(")") ) return;
        if ( tokens.removeFirst().equals(",") ) {
            param();
            param_list_prime();
        }
    }

    // param_prime -> "[" "]" | empty
    // FIRSTS: [ empty FOLLOWS: ) ,
    public void param_prime() {
        if ( tokens.removeFirst().getLexeme(token).matches("\\)|,") ) return;
        if ( tokens.removeFirst().equals("[") ) {
            if ( tokens.removeFirst().equals("]") ) return;
        }
    }

    // compound_statement -> "{" local-declarations statement-list "}"
    // FIRSTS: "{" FOLLOWS: $ int void
    public void compound_statement() {
        if ( tokens.isEmpty() ) return;
         if ( tokens.removeFirst().equals("{") ) {
             local_declarations();
             statement_list();
             if ( tokens.removeFirst().equals("}") ) return;
         }
    }

    // local-declarations -> var-declaration local-declarations | empty
    // FIRSTS: int void empty FOLLOWS: ( ; ID NUM if return while { }
    public void local_declarations() {
        if (tokens.removeFirst().getLexeme(token).matches("\\(|;|if|return|while|\\{|\\}") ||
            tokens.removeFirst().getCategory(token).equals("ID") ||
            tokens.removeFirst().getCategory(token).equals("NUM") ) return;
        var_declaration();
        local_declarations();
    }

    // statement_list -> statement statement_list | empty
    // FIRSTS: ( ; ID NUM additive if return while { empty FOLLOWS: "}"
    public void statement_list() {
        if ( tokens.removeFirst().equals("}") ) return;
        statement();
        statement_list();
    }

    // statement -> expression-stmt | { local-declarations statement-list }
    // | selection-stmt | iteration-stmt | return-stmt
    // FIRSTS: ( ; ID NUM additive if return while { FOLLOWS: ( ; ID NUM additive else if return while { }
    public void statement() {
         if ( expression_statement() ) return;
         if ( tokens.removeFirst().equals("{") ) {
             local_declarations();
             statement_list();
             if ( tokens.removeFirst().equals("}") ) return;
         }
         if ( selection_statement() | iteration_statement() | return_statement() ) return;
    }

    // expression_statement -> expression ";" | ";"
    // FIRSTS: ( ; ID NUM additive FOLLOWS: ( ; ID NUM additive else if return while { }
    public boolean expression_statement() {
        if ( tokens.removeFirst().equals(";")) return true;
        expression();
        if (tokens.removeFirst().equals(";")) return true;
        reject();
        return false;
    }

    // selection_statement -> "if" "(" expression ")" statement selection-stmt`
    // FIRSTS: if FOLLOWS: ( ; ID NUM additive else if return while { }
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
         reject();
         return false;
    }

    // selection_statement_prime -> empty | "else" statement
    // FIRSTS: else empty FOLLOWS: ( ; ID NUM else if return while { }
    public void selection_statement_prime() {
        if (tokens.removeFirst().getLexeme(token).matches("\\(|;|else|if|return|while|\\{|\\}") ||
            tokens.removeFirst().getCategory(token).equals("ID") ||
            tokens.removeFirst().getCategory(token).equals("NUM") ) return;
        else if ( tokens.removeFirst().equals("else") ) {
            statement();
        }
    }

    //iteration-statement -> "while" "(" expression ")" statement
    // FIRSTS: while FOLLOWS: ( ; ID NUM additive else if return while { }
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
        reject();
        return false;
    }

    // return-stmt -> "return" return-stmt`
    // FIRSTS: return FOLLOWS: ( ; ID NUM additive else if return while { }
    public boolean return_statement() {
        if ( tokens.removeFirst().equals("return") ) {
            return_statement_prime();
            return true;
        }
        reject();
        return false;
    }

    // return-stmt_prime -> ; | ID ( args ) term' additive-expression' ; | ID var' = expression ;
    // | ID var' term' additive-expression' ; | ( expression ) term' additive-expression' ;
    // | NUM term' additive-expression' ; | additive-expression relop additive-expression ;
    // FIRSTS: ( ; ID NUM FOLLOWS: ( ; ID NUM else if return while { }
    public void return_statement_prime() {
        if ( tokens.removeFirst().equals(";") ) return;
        else if ( tokens.removeFirst().getCategory(token).equals("ID") ) {
            if ( tokens.removeFirst().equals("(") ) {
                args();
                if ( tokens.removeFirst().equals(")") ) {
                    term_prime();
                    additive_expression_prime();
                    if ( tokens.removeFirst().equals(";") ) return;
                }
            } else if ( var_prime() ) {
                if ( tokens.removeFirst().equals("=") ) {
                    expression();
                    if ( tokens.removeFirst().equals(";") ) return;
                } else {
                    term_prime();
                    additive_expression_prime();
                    if ( tokens.removeFirst().equals(";")) return;
                }
            }
        } else if ( tokens.removeFirst().equals("(") ) {
            expression();
            if ( tokens.removeFirst().equals(")") ) {
                term_prime();
                additive_expression_prime();
                if ( tokens.removeFirst().equals(";") ) return;
            }
        } else if ( tokens.removeFirst().getCategory(token).equals("NUM") ) {
            term_prime();
            additive_expression_prime();
            if ( tokens.removeFirst().equals(";") ) return;
        } else if ( additive_expression() ){
            //additive_expression();
            relop();
            additive_expression();
            if ( tokens.removeFirst().equals(";") ) return;
        }
    }

    // expression -> var "=" expression | simple-expression
    // FIRSTS: ( ID NUM FOLLOWS: != ) , ; < <= == > >= ]
    public void expression() {
        if ( var() ) {
            if ( tokens.removeFirst().equals("=") ) expression();
        } else simple_expression();
    }

    // var -> "ID" var_prime
    // FIRSTS: ID FOLLOWS: =
    public boolean var() {
        if ( tokens.removeFirst().getCategory(token).equals("ID") ) {
            var_prime();
            return true;
        }
        reject();
        return false;
    }

    // var_prime -> empty | "[" expression "]"
    // FIRSTS: [ empty FOLLOWS: != ) * + , - / ; < <= = == > >= ]
    public boolean var_prime() {
        if (tokens.removeFirst().getLexeme(token).matches("!=|\\)|\\*|\\+|,|-|/|;|<|<=|=|==|>|>=|]")) return true;
        else if (tokens.removeFirst().equals("[")) {
            expression();
            if (tokens.removeFirst().equals("]")) {
                return true;
            }
        }
        reject();
        return false;
    }

    // simple-expression -> additive-expression relop additive-expression | additive-expression
    // FIRSTS: ( ID NUM FOLLOWS: != ) , ; < <= == > >= ]
    public void simple_expression() {
        additive_expression();
        if ( relop() ) { additive_expression(); }
    }

    // relop -> <= | >= | == | != | > | <
    // FIRSTS: != < <= == > >= FOLLOWS: ( ID NUM
    public boolean relop() {
        if ( tokens.removeFirst().getLexeme(token).matches("<=|>=|==|!=|>|<")) return true;
        reject();
        return false;
    }

    // additive_expression -> term additive_expression_prime
    // FIRSTS: ( ID NUM FOLLOWS: != ) , ; < <= == > >= ]
    public boolean additive_expression() {
        if ( term() ) {
            additive_expression_prime();
            return true;
        }
        reject();
        return false;
    }

    // additive_expression_prime -> addop term additive_expression_prime | empty
    // FIRSTS: + - empty FOLLOWS: != ) , ; < <= == > >= ]
    public void additive_expression_prime() { //TODO fix this
        if ( tokens.removeFirst().getLexeme(token).matches("!=|\\)|,|;|<|<=|==|>|>=|]") )
        addop();
        term();
        additive_expression_prime();
    }

    // addop -> + | -
    // FIRSTS: + - FOLLOWS: ( ID NUM
    public boolean addop() {
        if ( tokens.removeFirst().getLexeme(token).matches("\\+|=")) return true;
        return false;
    }

    // term -> factor term_prime
    // FIRSTS: ( ID NUM FOLLOWS: != ) + , - ; < <= == > >= ]
    public boolean term() {
        if ( factor() ) {
            term_prime();
            return true;
        }
        reject();
        return false;
    }

    // term_prime -> mulop factor term_prime | empty
    // FIRSTS: * / empty FOLLOWS: != ) + , - ; < <= == > >= ]
    public void term_prime() {
        if ( tokens.removeFirst().getLexeme(token).matches("!=|\\)|\\+|,|-|;|<|<=|==|>|>=|]")) return;
        mulop();
        factor();
        term_prime();
    }

    // mulop -> * | /
    // FIRSTS: * / FOLLOWS: ( ID NUM
    public boolean mulop() {
        if ( tokens.removeFirst().getLexeme(token).matches("\\*|/")) return true;
        reject();
        return false;
    }

    //factor -> ( expression ) || ID var' || call || NUM
    // FIRSTS: ( ID NUM FOLLOWS: != ) * + , - / ; < <= == > >= ]
    public boolean factor() {
        if ( tokens.removeFirst().equals("(") ) {
            expression();
            if ( tokens.removeFirst().equals(")") ) return true;
        } else if ( tokens.removeFirst().getCategory(token).equals("ID") ) {
            var_prime();
        } else if ( tokens.removeFirst().getCategory(token).equals("NUM") ) {
            return true;
        } else { call(); }
        reject();
        return false;
    }

    // call -> ID ( args )
    // FIRSTS: ID FOLLOWS: != ) * + , - / ; < <= == > >= ]
    public void call() {
        if ( tokens.removeFirst().getCategory(token).equals("ID") ) {
            if ( tokens.removeFirst().equals("(") ) {
                args();
                if ( tokens.removeFirst().equals(")") ) return;
            }
        }
    }

    // args -> args-list | empty
    // FIRSTS: ( ID NUM additive empty FOLLOWS: )
    public void args() {
        if ( tokens.removeFirst().equals(")") ) return;
        arg_list();
    }

    //arg-list ->
    //          ID ( args ) term' additive-expression' arg-list'
    //        | ID var' = expression arg-list'
    //        | ID var' term' additive-expression' arg-list'
    //        | ( expression ) term' additive-expression' arg-list'
    //        | NUM term' additive-expression' arg-list'
    //        | additive-expression relop additive-expression arg-list'
    // FIRSTS: ( ID NUM additive FOLLOWS: )
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
            if ( var_prime() ) {
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
    // FIRSTS: , empty FOLLOWS: )
    public void arg_list_prime() {
        if ( tokens.removeFirst().equals(")") ) return;
        else if ( tokens.removeFirst().equals(",") ) {
            expression();
            arg_list_prime();
        }
    }

} // Parser
