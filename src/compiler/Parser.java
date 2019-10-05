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

    public boolean isAccepted() {
        program();
        return isAccept;
    }

    public void debug(String rulename) {
        if ( !tokens.getFirst().equals(null) ) {
            System.out.println(rulename + " " + tokens.getFirst().getLexeme());
        }
    }

    public void reject() {
        isAccept = false;
        System.out.println("REJECT");
        System.exit(0);
    }

    public void program() {
        debug("program");
        declaration_list();
    }

    //declaration_list -> declaration declaration_list`
    // FIRSTS: int void empty FOLLOWS: $
    public void declaration_list() {
        debug("declaration_list");
        if (tokens.isEmpty()) return;
        declaration();
        declaration_list_prime();
    }

    //declaration -> fun-declaration | var-declaration
    //fun-declaration -> ( params ) compound-stmt
    // var-declaration -> var-declaration'
    // FIRSTS: int void FOLLOWS: $ int void
    public void declaration() {
        debug("declaration");
        if (tokens.isEmpty()) return;
        type_specifier();
        if ( tokens.getFirst().getCategory().equals("ID") ) {// for some reason if you change this it gets stack overflow error :/
            tokens.removeFirst();
            if ( tokens.getFirst().getLexeme().matches("\\(") ) {
                function_declaration();
            }
            var_declaration();
        }
    }

    //declaration-list' -> declaration declaration-list' | empty
    //FIRSTS: int void ϵ FOLLOWS: $
    public void declaration_list_prime() {
        debug("declaration-list-prime");
        if (tokens.isEmpty()) return;
        if (tokens.getFirst().equals("int") || tokens.getFirst().equals("void") ) {
            declaration();
            declaration_list_prime();
        }
        reject();
    }

    // var-declaration -> var-declaration'
    // FIRSTS: void, int FOLLOWS: [ NUM ] ;
    public void var_declaration() {
        debug("var-declaration");
        var_declaration_prime();
    }

    public void type_specifier() {
        debug("type-specifier");
        if ( tokens.getFirst().getLexeme().equals("int") || tokens.getFirst().getLexeme().equals("void") ) {
            tokens.removeFirst();
            return;
        }
    }

    //fun-declaration -> ( params ) compound-stmt
    // FIRSTS: int void FOLLOWS: $ int void
    public void function_declaration() {
        debug("function_declaration");
        if (tokens.isEmpty()) return;
        if ( tokens.getFirst().getLexeme().matches("\\(") ) {
            tokens.removeFirst();
            params();
            if (tokens.getFirst().equals(")")) {
                tokens.removeFirst();
                compound_statement();
            }
        }
    }

    //params -> params-list | void
    // FIRSTS: int void FOLLOWS: )
    public void params() {
        debug("params");
        if ( tokens.getFirst().getLexeme().equals("void") ) {
            tokens.removeFirst();
        }
        if ( tokens.getFirst().getLexeme().equals("int") || tokens.getFirst().getLexeme().equals("void") ) {
            param_list();
        }
    }

    //param-list -> param param-list'
    // FIRSTS: int void FOLLOWS: )
    public void param_list() {
        debug("param_list");
        if ( tokens.getFirst().getLexeme().equals("int") || tokens.getFirst().getLexeme().equals("void") ) {
            param();
            param_list_prime();
        }
    }

    //param -> type-specifier ID param'
    // FIRSTS: int void FOLLOWS: ) ,
    public void param() {
        debug("param");
        if ( tokens.getFirst().getLexeme().equals("int") || tokens.getFirst().getLexeme().equals("void") ) {
            type_specifier();
            if (tokens.getFirst().getCategory().equals("ID")) {
                tokens.removeFirst();
                param_prime();
            }
        }
    }

    //var-declaration' ->  [ NUM ] ; | ;
    // FIRSTS: ; [ FOLLOWS: $ ( ; ID NUM if int return void while { }
    public void var_declaration_prime() {
        debug("var_declaration-prime");
        if (tokens.isEmpty()) return;
        if ( tokens.getFirst().getLexeme().equals("[") ) {
            tokens.removeFirst();
            if ( tokens.getFirst().getCategory().equals("NUM") ) {
                tokens.removeFirst();
                if ( tokens.getFirst().getLexeme().equals("]") ) {
                    tokens.removeFirst();
                    if ( tokens.getFirst().equals(";") ) {
                        tokens.removeFirst();
                    }
                }
            }
        } if ( tokens.getFirst().equals(";") ) {
            tokens.removeFirst();
        }
    }

    //param-list' -> , param param-list' | empty
    // FIRSTS: , empty FOLLOWS: )
    public void param_list_prime() {
        if ( tokens.getFirst().getLexeme().matches("\\)") ) {
            tokens.removeFirst();
        }
        if ( tokens.getFirst().equals(",") ) {
            tokens.removeFirst();
            param();
            param_list_prime();
        }
    }

    // param_prime -> "[" "]" | empty
    // FIRSTS: [ empty FOLLOWS: ) ,
    public void param_prime() {
        debug("param_prime");
        if (tokens.getFirst().getLexeme().matches("\\)|,")) {
            tokens.removeFirst();
        }
        if (tokens.getFirst().equals("[")) {
            tokens.removeFirst();
            if (tokens.getFirst().equals("]")) {
                tokens.removeFirst();
            }
        }
    }

    // compound_statement -> "{" local-declarations statement-list "}"
    // FIRSTS: "{" FOLLOWS: $ int void
    public void compound_statement() {
        debug("compound_statement");
        if ( tokens.isEmpty() ) return;
         if ( tokens.getFirst().equals("{") ) {
             tokens.removeFirst();
             local_declarations();
             statement_list();
             if ( tokens.getFirst().equals("}") ) {
                 tokens.removeFirst();
             }
         }
    }

    // local-declarations -> var-declaration local-declarations | empty
    // FIRSTS: int void empty FOLLOWS: ( ; ID NUM if return while { }
    public void local_declarations() {
        debug("local_declarations");
        if (tokens.getFirst().getLexeme().matches("\\(|;|if|return|while|\\{|\\}") ||
            tokens.getFirst().getCategory().equals("ID") ||
            tokens.getFirst().getCategory().equals("NUM") ) {
            tokens.removeFirst();
            return;
        }
        if ( tokens.getFirst().equals("int") || tokens.getFirst().equals("void") ) {
            var_declaration();
            local_declarations();
        }
    }

    // statement_list -> statement statement_list | empty
    // FIRSTS: ( ; ID NUM if return while { ϵ   FOLLOWS: "}"
    public void statement_list() {
        debug("statement_list");
        if ( tokens.getFirst().equals("}") || tokens.getFirst().equals("(") || tokens.getFirst().equals(";") ||
                tokens.getFirst().getCategory().equals("ID") || tokens.getFirst().getCategory().equals("NUM") ||
                tokens.getFirst().equals("if") || tokens.getFirst().equals("return") || tokens.getFirst().equals("while")
                || tokens.getFirst().equals("{") ) {
        tokens.removeFirst();
        }
        if ( tokens.getFirst().equals("(") || tokens.getFirst().equals(";") || tokens.getFirst().getCategory().equals("ID")
                || tokens.getFirst().getCategory().equals("NUM") || tokens.getFirst().equals("if")
                || tokens.getFirst().equals("return") || tokens.getFirst().equals("while")
                || tokens.getFirst().equals("{") ) {
            statement();
            statement_list();
        }
    }

    // statement -> expression-stmt | { local-declarations statement-list }
    // | selection-stmt | iteration-stmt | return-stmt
    // FIRSTS: ( ; ID NUM additive if return while { FOLLOWS: ( ; ID NUM additive else if return while { }
    public void statement() {
        debug("statement");
        if ( expression_statement() ) return;
        if ( tokens.removeFirst().equals("{") ) {
            local_declarations();
            statement_list();
            if ( tokens.removeFirst().equals("}") ) return;
         }
         if ( selection_statement() || iteration_statement() || return_statement() ) return;
    }

    // expression_statement -> expression ";" | ";"
    // FIRSTS: ( ; ID NUM additive FOLLOWS: ( ; ID NUM additive else if return while { }
    public boolean expression_statement() {
        debug("expression_statement");
        if ( tokens.removeFirst().equals(";")) return true;
        expression();
        if (tokens.removeFirst().equals(";")) return true;
        reject();
        return false;
    }

    // selection_statement -> "if" "(" expression ")" statement selection-stmt`
    // FIRSTS: if FOLLOWS: ( ; ID NUM additive else if return while { }
    public boolean selection_statement() {
        debug("selection_statement");
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
    public boolean selection_statement_prime() {
        debug("selection_statement_prime");
        if (tokens.removeFirst().getLexeme().matches("\\(|;|else|if|return|while|\\{|\\}") ||
            tokens.removeFirst().getCategory().equals("ID") ||
            tokens.removeFirst().getCategory().equals("NUM") ) return true;
        else if ( tokens.removeFirst().equals("else") ) statement();
        reject();
        return false;
    }

    //iteration-statement -> "while" "(" expression ")" statement
    // FIRSTS: while FOLLOWS: ( ; ID NUM additive else if return while { }
    public boolean iteration_statement() {
        debug("iteration_statement");
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
        debug("return_statement");
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
        debug("return_statement_prime");
        if ( tokens.removeFirst().equals(";") ) return;
        else if ( tokens.removeFirst().getCategory().equals("ID") ) {
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
                } else if ( term_prime() ){
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
        } else if ( tokens.removeFirst().getCategory().equals("NUM") ) {
            term_prime();
            additive_expression_prime();
            if ( tokens.removeFirst().equals(";") ) return;
        } else if ( additive_expression() ){
            relop();
            additive_expression();
            if ( tokens.removeFirst().equals(";") ) return;
        }
    }

    // expression -> var "=" expression | simple-expression
    // FIRSTS: ( ID NUM FOLLOWS: != ) , ; < <= == > >= ]
    public boolean expression() {
        debug("expression");
        if ( var() ) {
            if ( tokens.removeFirst().equals("=") ) expression();
        } else if ( simple_expression() ) return true;
        reject();
        return false;
    }

    // var -> "ID" var_prime
    // FIRSTS: ID FOLLOWS: =
    public boolean var() {
        debug("var");
        if ( tokens.removeFirst().getCategory().equals("ID") ) {
            var_prime();
            return true;
        }
        reject();
        return false;
    }

    // var_prime -> empty | "[" expression "]"
    // FIRSTS: [ empty FOLLOWS: != ) * + , - / ; < <= = == > >= ]
    public boolean var_prime() {
        debug("var_prime");
        if (tokens.removeFirst().getLexeme().matches("!=|\\)|\\*|\\+|,|-|/|;|<|<=|=|==|>|>=|]")) return true;
        else if (tokens.removeFirst().equals("[")) {
            expression();
            if (tokens.removeFirst().equals("]")) return true;
        }
        reject();
        return false;
    }

    // simple-expression -> additive-expression relop additive-expression | additive-expression
    // FIRSTS: ( ID NUM FOLLOWS: != ) , ; < <= == > >= ]
    public boolean simple_expression() {
        debug("simple_expression");
        additive_expression();
        if ( relop() ) {
            additive_expression();
            return true;
        }
        reject();
        return false;
    }

    // relop -> <= | >= | == | != | > | <
    // FIRSTS: != < <= == > >= FOLLOWS: ( ID NUM
    public boolean relop() {
        debug("relop");
        if ( tokens.removeFirst().getLexeme().matches("<=|>=|==|!=|>|<")) return true;
        reject();
        return false;
    }

    // additive_expression -> term additive_expression_prime
    // FIRSTS: ( ID NUM FOLLOWS: != ) , ; < <= == > >= ]
    public boolean additive_expression() {
        debug("additive_expression");
        if ( term() ) {
            additive_expression_prime();
            return true;
        }
        reject();
        return false;
    }

    // additive_expression_prime -> (+|-) term additive_expression_prime | empty
    // FIRSTS: + - empty FOLLOWS: != ) , ; < <= == > >= ]
    public boolean additive_expression_prime() {
        debug("additive_expresion_prime");
        if ( tokens.removeFirst().getLexeme().matches("!=|\\)|,|;|<|<=|==|>|>=|]") ) return true;
        if ( tokens.removeFirst().getLexeme().matches("\\+|=") ) {
            term();
            additive_expression_prime();
        }
        reject();
        return false;
    }

    // term -> factor term_prime
    // FIRSTS: ( ID NUM FOLLOWS: != ) + , - ; < <= == > >= ]
    public boolean term() {
        debug("term");

        if ( factor() ) {
            term_prime();
            return true;
        }
        reject();
        return false;
    }

    // term_prime -> (\\*|/) factor term_prime | empty
    // FIRSTS: * / empty FOLLOWS: != ) + , - ; < <= == > >= ]
    public boolean term_prime() {
        debug("term_prime");
        if ( tokens.removeFirst().getLexeme().matches("!=|\\)|\\+|,|-|;|<|<=|==|>|>=|]")) return true;
        if ( tokens.removeFirst().getLexeme().matches("\\*|/") ) { // mulop
            factor();
            term_prime();
            return true;
        }
        reject();
        return false;
    }

    //factor -> ( expression ) | ID var' | call | NUM
    // FIRSTS: ( ID NUM FOLLOWS: != ) * + , - / ; < <= == > >= ]
    public boolean factor() {
        debug("factor");
        if ( tokens.removeFirst().equals("(") ) {
            expression();
            if ( tokens.removeFirst().equals(")") ) return true;
        } else if ( tokens.removeFirst().getCategory().equals("ID") ) var_prime();
        else if ( call() ) return true;
        else if ( tokens.removeFirst().getCategory().equals("NUM") ) return true;
        reject();
        return false;
    }

    // call -> ID ( args )
    // FIRSTS: ID FOLLOWS: != ) * + , - / ; < <= == > >= ]
    public boolean call() {
        debug("call");
        if ( tokens.removeFirst().getCategory().equals("ID") ) {
            if ( tokens.removeFirst().equals("(") ) {
                args();
                if ( tokens.removeFirst().equals(")") ) return true;
            }
        }
        reject();
        return false;
    }

    // args -> args-list | empty
    // FIRSTS: ( ID NUM additive empty FOLLOWS: )
    public boolean args() {
        debug("args");
        if ( tokens.removeFirst().equals(")") ) return true;
        arg_list();
        reject();
        return false;
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
        debug("args_list");
        if ( tokens.removeFirst().getCategory().equals("ID") ) {
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
                } else if ( term_prime() ) {
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
        } else if ( tokens.removeFirst().getCategory().equals("NUM") ) {
            term_prime();
            additive_expression_prime();
            arg_list_prime();
        } else if ( additive_expression() ) {
            relop();
            additive_expression();
            arg_list_prime();
        }
    }

    // arg_list_prime -> , expression arg-list_prime | empty
    // FIRSTS: , empty FOLLOWS: )
    public boolean arg_list_prime() {
        debug("arg_list_prime");
        if ( tokens.removeFirst().equals(")") ) return true;
        else if ( tokens.removeFirst().equals(",") ) {
            expression();
            arg_list_prime();
        }
        reject();
        return false;
    }

} // Parser
