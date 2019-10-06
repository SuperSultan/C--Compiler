//TODO IF THERE IS AN EPSILON IN YOUR FIRST SET OF YOUR RULE, YOU CODE THE FOLLOW SET IN IF ELSE STATEMENT
//TODO IF THERE IS A $ IN THE FOLLOW SET, YOU CHECK IF THE CURRENT TOKEN IS EMPTY (REGARDLESS OF WHETHER THERE IS AN EPSILON)

package compiler;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;

public class Parser {

    private boolean isAccept;
    private ArrayDeque<Token> tokens;

    public Parser (ArrayDeque<Token> theTokens) {
        isAccept = true;
        this.tokens = theTokens;
    }

    public boolean isAccepted() {

        try {
            program();
            if ( tokens.size() == 0 ) {
                return isAccept;
            } else return !isAccept;
        } catch (NoSuchElementException exception) {
            System.out.println("REJECT");
            System.exit(0);
        }


        if ( tokens.size() == 0) {
            return isAccept;
        } else return !isAccept;
    }

    public void debug(String rulename) {
        if ( tokens.getFirst() != null ) {
            System.out.println(rulename + " " + tokens.getFirst().getLexeme());
        }
    }

    public void program() {
        debug("program");
        declaration_list();
    }

    //declaration_list -> declaration declaration_list`
    // FIRSTS: int void empty FOLLOWS: $
    public void declaration_list() {
        debug("declaration_list");
        if (tokens.isEmpty()) isAccepted();
        if ( tokens.getFirst().getLexeme().equals("int") || tokens.getFirst().getLexeme().equals("void") ) {
            declaration();
            declaration_list_prime();
        }
        //reject();
    }

    //declaration-list' -> declaration declaration-list' | empty
    //FIRSTS: int void ϵ FOLLOWS: $
    public void declaration_list_prime() {
        debug("declaration-list-prime");
        if (tokens.isEmpty()) return;
        if (tokens.getFirst().getLexeme().equals("int") || tokens.getFirst().getLexeme().equals("void") ) {
            declaration();
            declaration_list_prime();
        }
    }

    //declaration -> fun-declaration | var-declaration
    //fun-declaration -> ( params ) compound-stmt
    // var-declaration -> var-declaration'
    // FIRSTS: int void FOLLOWS: $ int void
    public void declaration() {
        debug("declaration");
        if (tokens.isEmpty()) return;
        type_specifier();
        if ( tokens.getFirst().getCategory().equals("ID") ) {
            tokens.removeFirst();
        }
        if (tokens.getFirst().getLexeme().equals("(")) {
            function_declaration();
        } else {
            var_declaration();
        }
    }

    //fun-declaration -> ( params ) compound-stmt
    // FIRSTS: int void FOLLOWS: $ int void
    public void function_declaration() {
        debug("function_declaration");
        if (tokens.isEmpty()) return;
        if ( tokens.getFirst().getLexeme().equals("(") ) {
            tokens.removeFirst();
            params();
            if ( tokens.getFirst().getLexeme().equals(")")) {
                tokens.removeFirst();
                compound_statement();
            }
        }
    }

    // var-declaration -> var-declaration'
    // FIRSTS: void, int FOLLOWS: [ NUM ] ;
    public void var_declaration() {
        debug("var-declaration");
        if ( tokens.getFirst().getLexeme().equals("void") || tokens.getFirst().getLexeme().equals("int") ) {
            tokens.removeFirst();
            var_declaration_prime();
        }
    }

    public void type_specifier() {
        debug("type-specifier");
        if ( tokens.getFirst().getLexeme().equals("int") || tokens.getFirst().getLexeme().equals("void") ) {
            tokens.removeFirst();
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
            if ( tokens.getFirst().getCategory().equals("ID")) {
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
        if ( tokens.getFirst().getLexeme().equals(";")) {
            tokens.removeFirst();
        }
        if ( tokens.getFirst().getLexeme().equals("[") ) {
            tokens.removeFirst();
            if ( tokens.getFirst().getLexeme().equals("NUM") ) {
                tokens.removeFirst();
                if ( tokens.getFirst().getLexeme().equals("]") ) {
                    tokens.removeFirst();
                    if ( tokens.getFirst().getLexeme().equals(";") ) {
                        tokens.removeFirst();
                    }
                }
            }
        }
    }

    //param-list' -> , param param-list' | empty
    // FIRSTS: , empty FOLLOWS: )
    public void param_list_prime() {
        if ( tokens.getFirst().getLexeme().equals(")") ) {
            //tokens.removeFirst();
            return;
        }
        if ( tokens.getFirst().getLexeme().equals(",") ) {
            tokens.removeFirst();
            param();
            param_list_prime();
        }
    }

    // param_prime -> "[" "]" | empty
    // FIRSTS: [ empty FOLLOWS: ) ,
    public void param_prime() {
        debug("param_prime");
        if (tokens.getFirst().getCategory().matches("\\)|,")) {
            //tokens.removeFirst();
            return;
        }
        if (tokens.getFirst().getLexeme().equals("[")) {
            tokens.removeFirst();
            if (tokens.getFirst().getLexeme().equals("]")) {
                tokens.removeFirst();
            }
        }
    }

    // compound_statement -> "{" local-declarations statement-list "}"
    // FIRSTS: "{" FOLLOWS: $ int void
    public void compound_statement() {
        debug("compound_statement");
        if (tokens.isEmpty()) return;
        if (tokens.getFirst().getLexeme().equals("{")) {
            tokens.removeFirst();
            local_declarations();
            statement_list();
            if (tokens.getFirst().getLexeme().equals("}")) {
                tokens.removeFirst();
            }
        }
    }

    // local-declarations -> local-declarations'
    // FIRSTS: int void empty FOLLOWS: ( ; ID NUM if return while { }
    public void local_declarations() {
        debug("local_declarations");
        local_declarations_prime();
    }

    // local-declarations' -> var-declaration local-declarations; | empty
    // FIRSTS: int void empty FOLLOWS: ( ; ID NUM if return while { }
    public void local_declarations_prime() {
        debug("local_declarations_prime");
        if (tokens.getFirst().getLexeme().matches("\\(|;|if|return|while|\\{|}") ||
                tokens.getFirst().getCategory().equals("ID") || tokens.getFirst().getCategory().equals("NUM") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().equals("int") || tokens.getFirst().getLexeme().equals("void") ) {
            var_declaration();
            local_declarations();
        }
    }

    // statement_list -> statement statement_list | empty
    // FIRSTS: ( ; ID NUM if return while { ϵ   FOLLOWS: "}"
    public void statement_list() {
        debug("statement_list");
        if ( tokens.getFirst().getLexeme().equals("}") ) return;
        if ( tokens.getFirst().getLexeme().matches("\\(|;|if|return|while|\\{") ||
                tokens.getFirst().getCategory().equals("ID") || tokens.getFirst().getCategory().equals("NUM") ) {
           // tokens.removeFirst(); //TODO is this correct?
            statement();
            statement_list();
        }
    }

    // statement -> expression-stmt | { local-declarations statement-list }
    // | selection-stmt | iteration-stmt | return-stmt
    // FIRSTS: ( ; ID NUM if return while { FOLLOWS: ( ; ID NUM additive else if return while { }
    public void statement() {
        debug("statement");
        if ( tokens.getFirst().getLexeme().matches("\\(|;") || tokens.getFirst().getCategory().equals("ID")
            || tokens.getFirst().getCategory().equals("NUM") ) {
            expression_statement();
        }
        if ( tokens.getFirst().getLexeme().equals("{") ) {
            tokens.removeFirst();
            local_declarations();
            statement_list();
            if ( tokens.getFirst().getLexeme().equals("}") ) {
                tokens.removeFirst();
            }
        }
        if ( tokens.getFirst().getLexeme().equals("if") ) {
            selection_statement();
        }
        if ( tokens.getFirst().getLexeme().equals("while") ) {
            iteration_statement();
        }
        if ( tokens.getFirst().getLexeme().equals("return") ) {
            return_statement();
        }
    }

    // expression_statement -> expression ";" | ";"
    // FIRSTS: ( ; ID NUM FOLLOWS: ( ; ID NUM additive else if return while { }
    public void expression_statement() {
        debug("expression_statement");
        if ( tokens.getFirst().getLexeme().equals(";") ) {
            tokens.removeFirst();
            return;
        }
        if ( tokens.getFirst().getLexeme().equals("(") || tokens.getFirst().getCategory().equals("ID") ||
                tokens.getFirst().getCategory().equals("NUM") ) {
            expression();
            if (tokens.getFirst().getLexeme().equals(";")) {
                tokens.removeFirst();
            }
        }
    }

    // expression ->  simple-expression | var "=" expression
    // FIRSTS: ( ID NUM FOLLOWS: != ) , ; < <= == > >= ]
    public void expression() {
        debug("expression");
        if ( tokens.getFirst().getLexeme().equals("(") || tokens.getFirst().getCategory().equals("ID") ||
                tokens.getFirst().getCategory().equals("NUM") ) {
            simple_expression();
        }
        if ( tokens.getFirst().getCategory().equals("ID") ) {
            var();
            if ( tokens.getFirst().getLexeme().equals("=") ) {
                tokens.removeFirst();
                expression();
            }
        }
    }

    // simple-expression -> additive-expression relop additive-expression | additive-expression
    // FIRSTS: ( ID NUM FOLLOWS: != ) , ; < <= == > >= ]
    public void simple_expression() {
        debug("simple_expression");
        if ( tokens.getFirst().getCategory().equals("(") || tokens.getFirst().getCategory().equals("ID") ||
            tokens.getFirst().getCategory().equals("NUM") ) {
            additive_expression();
            if ( tokens.getFirst().getLexeme().matches("!=|<=|==|>=|<|>") ) {
                relop();
                if (tokens.getFirst().getCategory().equals("(") || tokens.getFirst().getCategory().equals("ID") ||
                        tokens.getFirst().getCategory().equals("NUM")) {
                    additive_expression();
                }
            }
        }
    }

    // relop -> <= | >= | == | != | > | <
    // FIRSTS: != < <= == > >= FOLLOWS: ( ID NUM
    public void relop() {
        debug("relop");
        if ( tokens.getFirst().getLexeme().matches("<=|>=|==|!=|>|<")) {
            tokens.removeFirst();
        }
    }

    // mulop -> * | /
    // FIRSTS: * / FOLLOWS: ( ID NUM
    public void mulop() {
        debug("mulop");
        if ( tokens.getFirst().getLexeme().equals("*") || tokens.getFirst().getLexeme().equals("/") ) {
            tokens.removeFirst();
        }
    }

    //factor -> ( expression ) | ID var' | call | NUM
    // FIRSTS: ( ID NUM FOLLOWS: != ) * + , - / ; < <= == > >= ]
    public void factor() {
        debug("factor");
        if ( tokens.getFirst().getCategory().equals("(") ) {
            tokens.removeFirst();
            expression();
            if ( tokens.getFirst().getCategory().equals(")") ) {
                tokens.removeFirst();
            }
        }
        if ( tokens.getFirst().getCategory().equals("ID") ) {
            tokens.removeFirst();
            if ( tokens.getFirst().getLexeme().equals("(") ) {
                call();
            } else {
                var_prime();
            }
        }
        if ( tokens.getFirst().getCategory().equals("NUM") ) {
            tokens.removeFirst();
        }
    }

    // var_prime -> empty | "[" expression "]"
    // FIRSTS: [ empty FOLLOWS: != ) * + , - / ; < <= = == > >= ]
    public void var_prime() {
        debug("var_prime");
        if (tokens.getFirst().getLexeme().matches("!=|\\)|\\*|\\+|,|-|/|;|<=|==|>=|>=|<|=|>|]")) {
            return;
        }
        if (tokens.getFirst().getLexeme().equals("[")) {
            tokens.removeFirst();
            expression();
            if (tokens.getFirst().getLexeme().equals("]")) {
                tokens.removeFirst();
            }
        }
    }

    // call -> ( args )
    // FIRSTS: ( FOLLOWS: != ) * + , - / ; < <= == > >= ]
    public void call() {
        debug("call");
        if (tokens.getFirst().getLexeme().equals("(")) {
            tokens.removeFirst();
            args();
            if (tokens.getFirst().getLexeme().equals(")")) {
                tokens.removeFirst();
            }
        }
    }

    // additive_expression_prime -> addop term additive_expression_prime | empty
    // FIRSTS: + - empty FOLLOWS: != ) , ; < <= == > >= ]
    public void additive_expression_prime() {
        debug("additive_expresion_prime");
        if ( tokens.getFirst().getLexeme().matches("!=|\\)|,|;|<=|==|>=|]|<|>") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().matches("\\+|=") ) {
            addop();
            term();
            additive_expression_prime();
        }
    }

    // addop -> + | -
    public void addop() {
        debug("addop");
        if ( tokens.getFirst().getLexeme().matches("\\+|-") ) {
            tokens.removeFirst();
        }
    }

    // var -> "ID" var_prime
    // FIRSTS: ID FOLLOWS: =
    public void var() {
        debug("var");
        if ( tokens.getFirst().getCategory().equals("ID") ) {
            tokens.removeFirst();
            var_prime();
        }
    }

    // selection_statement -> "if" "(" expression ")" statement selection-stmt`
    // FIRSTS: if FOLLOWS: ( ; ID NUM additive else if return while { }
    public void selection_statement() {
        debug("selection_statement");
         if ( tokens.getFirst().getLexeme().equals("if") ) {
             tokens.removeFirst();
             if ( tokens.getFirst().getLexeme().equals("(") ) {
                 tokens.removeFirst();
                 expression();
                 if ( tokens.getFirst().getLexeme().equals(")") ) {
                     tokens.removeFirst();
                     statement();
                     selection_statement_prime();
                 }
             }
         }
    }

    // selection_statement_prime -> empty | "else" statement
    // FIRSTS: else empty FOLLOWS: ( ; ID NUM else if return while { }
    public void selection_statement_prime() {
        debug("selection_statement_prime");
        if (tokens.getFirst().getLexeme().matches("\\(|;|else|if|return|while|\\{|}") ||
            tokens.getFirst().getCategory().equals("ID") || tokens.getFirst().getCategory().equals("NUM") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().equals("else") ) {
            tokens.removeFirst();
            statement();
        }
    }

    //iteration-statement -> "while" "(" expression ")" statement
    // FIRSTS: while FOLLOWS: ( ; ID NUM additive else if return while { }
    public void iteration_statement() {
        debug("iteration_statement");
        if ( tokens.getFirst().getLexeme().equals("while") ) {
            tokens.removeFirst();
            if ( tokens.getFirst().getLexeme().equals("(") ) {
                tokens.removeFirst();
                expression();
                if ( tokens.getFirst().getLexeme().equals(")") ) {
                    tokens.removeFirst();
                    statement();
                }
            }
        }
    }

    // return-stmt -> "return" return-stmt`
    // FIRSTS: return FOLLOWS: ( ; ID NUM additive else if return while { }
    public void return_statement() {
        debug("return_statement");
        if ( tokens.getFirst().getLexeme().equals("return") ) {
            tokens.removeFirst();
            return_statement_prime();
        }
    }

    // term_prime -> mulop factor term_prime | empty
    // FIRSTS: * / empty FOLLOWS: != ) + , - ; < <= == > >= ]
    public void term_prime() {
        debug("term_prime");
        if ( tokens.getFirst().getLexeme().matches("!=|\\)|\\+|,|-|;|<=|==|>=|<|>|]") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().equals("*") || tokens.getFirst().getLexeme().equals("/") ) {
            mulop();
            factor();
            term_prime();
        }
    }

    // term -> factor term_prime
    // FIRSTS: ( ID NUM FOLLOWS: != ) + , - ; < <= == > >= ]
    public void term() {
        debug("term");
        if ( tokens.getFirst().getCategory().equals("(") || tokens.getFirst().getCategory().equals("ID") ||
                tokens.getFirst().getCategory().equals("NUM") ) {
            factor();
            term_prime();
        }
    }

    // additive_expression -> term additive_expression_prime
    // FIRSTS: ( ID NUM FOLLOWS: != ) , ; < <= == > >= ]
    public void additive_expression() {
        debug("additive_expression");
        term();
        additive_expression_prime();
    }

    // return-stmt_prime -> ; | additive-expression relop additive-expression ; | NUM term' additive-expression' ;
    // | ( expression ) term' additive-expression' ; | ID ( args ) term' additive-expression' ;
    // | ID var' = expression ; | ID var' term' additive-expression' ;
    // FIRSTS: ( ; ID NUM FOLLOWS: ( ; ID NUM else if return while { }

    public void return_statement_prime() {
        debug("return_statement_prime");
        if ( tokens.getFirst().getLexeme().equals(";") ) {
            tokens.removeFirst();
        }

        if ( tokens.getFirst().getLexeme().equals("(") ||
                tokens.getFirst().getCategory().equals("NUM") || tokens.getFirst().getCategory().equals("ID") ) {
            tokens.removeFirst();
            additive_expression();
            relop();
            additive_expression();
            if ( tokens.getFirst().getLexeme().equals(";") ) {
                tokens.removeFirst();
            }
        } // additive-expression relop additive-expression ;

        if ( tokens.getFirst().getCategory().equals("NUM") ) { // NUM term' additive-expression' ;
            tokens.removeFirst();
            term_prime();
            additive_expression_prime();
            if ( tokens.getFirst().getLexeme().equals(";") ) {
                tokens.removeFirst();
            }
        }

        if ( tokens.getFirst().getLexeme().equals("(") ) { // ( expression ) term' additive-expression' ;
            tokens.removeFirst();
            expression();
            if ( tokens.getFirst().getLexeme().equals(")") ) {
                tokens.removeFirst();
                term_prime();
                additive_expression_prime();
                if ( tokens.getFirst().getLexeme().equals(";") ) {
                    tokens.removeFirst();
                }
            }
        }

        if ( tokens.getFirst().getCategory().equals("ID") ) {
            tokens.removeFirst();
            if (tokens.getFirst().getLexeme().equals("(")) {
                tokens.removeFirst();
                args();
                if (tokens.getFirst().getLexeme().equals(")")) {
                    tokens.removeFirst();
                    term_prime();
                    additive_expression_prime();
                    if (tokens.getFirst().getLexeme().equals(";")) {
                        tokens.removeFirst();
                    }
                }
            } else {
                var_prime();
                if ( tokens.getFirst().getLexeme().equals("=") ) {
                    tokens.removeFirst();
                    expression();
                } else {
                    term_prime();
                    additive_expression_prime();
                }
            }
            if ( tokens.getFirst().getLexeme().equals(";") ) {
                tokens.removeFirst();
            }
        }
    }

    // args -> args-list | empty
    // FIRSTS: ( ID NUM empty FOLLOWS: )
    public void args() {
        debug("args");
        if ( tokens.getFirst().getLexeme().equals(")") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().equals("(") || tokens.getFirst().getCategory().equals("ID") ||
        tokens.getFirst().getCategory().equals("NUM") ) {
            arg_list();
        }
    }

    //arg-list ->
    //          ID ( args ) term' additive-expression' arg-list'
    //        | ID var' = expression arg-list'
    //        | ID var' term' additive-expression' arg-list'
    //        | ( expression ) term' additive-expression' arg-list'
    //        | additive-expression relop additive-expression arg-list'
    //        | NUM term' additive-expression' arg-list'
    // FIRSTS: ( ID NUM additive FOLLOWS: )
    public void arg_list() {
        debug("args_list");
        if ( tokens.getFirst().getCategory().equals("ID") ) {
            tokens.removeFirst();
            if ( tokens.getFirst().getLexeme().equals("(") ) {
                tokens.removeFirst();
                args();
                if ( tokens.getFirst().getLexeme().equals(")") ) {
                    tokens.removeFirst();
                    term_prime();
                    additive_expression_prime();
                    arg_list_prime();
                }
            } else {
                var_prime();
                if ( tokens.getFirst().getLexeme().equals("=") ) {
                    tokens.removeFirst();
                    expression();
                    arg_list_prime();
                } else {
                    term_prime();
                    additive_expression_prime();
                    arg_list_prime();
                }
            }
        }
        if ( tokens.getFirst().getLexeme().equals("(") ) {
            tokens.removeFirst();
            expression();
            if ( tokens.getFirst().getLexeme().equals(")") ) {
                tokens.removeFirst();
                term_prime();
                additive_expression_prime();
                arg_list_prime();
            }
        }

        if ( tokens.getFirst().getLexeme().equals("(") || tokens.getFirst().getCategory().equals("ID") ||
                tokens.getFirst().getCategory().equals("NUM") ) {
            tokens.removeFirst();
            additive_expression();
            relop();
            additive_expression();
            arg_list_prime();
        } else if ( tokens.getFirst().getCategory().equals("NUM") ) {
            tokens.removeFirst();
            term_prime();
            additive_expression_prime();
            arg_list_prime();
        }
    }

    // arg_list_prime -> , expression arg-list_prime | empty
    // FIRSTS: , empty FOLLOWS: )
    public void arg_list_prime() {
        debug("arg_list_prime");

        if ( tokens.getFirst().getLexeme().equals(")") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().equals(",") ) {
            tokens.removeFirst();
            expression();
            arg_list_prime();
        }
    }

} // Parser
