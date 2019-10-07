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
        } catch (NoSuchElementException exception) {
            System.out.println("ACCEPT");
            System.exit(0);
        }

        program();
        if ( tokens.size() == 0 && isAccept) {
            return true;
        } else {
            return false;
        }
    }

    public void debug(String rulename) {
        if ( tokens.getFirst() != null ) {
            System.out.println(rulename + " " + tokens.getFirst().getLexeme());
        }
    }

    // program -> declaration-list
    // FIRST: int, void FOLLOWS: $
    public void program() {
        debug("program");
        declaration_list();
    }

    //declaration_list -> declaration declaration_list`
    // FIRSTS: int void empty FOLLOWS: $
    public void declaration_list() {
        debug("declaration_list");
        if (tokens.isEmpty()) return;
        if ( tokens.getFirst().getLexeme().equals("int") || tokens.getFirst().getLexeme().equals("void") ) {
            declaration();
            declaration_list_prime();
        }
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

    //declaration -> type-specifier ID declaration'
    // FIRSTS: int void FOLLOWS: $ int void
    public void declaration() {
        debug("declaration");
        type_specifier();
        if ( tokens.getFirst().getCategory().equals("ID") ) {
            tokens.removeFirst();
        }
        declaration_prime();
    }

    //declaration' -> var-declaration` | ( params ) compound-stmt
    //FIRSTS: ( ; [ FOLLOWS: int void $
    public void declaration_prime() {
        debug("declaration-prime");
        if ( tokens.isEmpty() ) return;
        if ( tokens.getFirst().getLexeme().equals("(") ) {
            tokens.removeFirst();
            params();
            if ( tokens.getFirst().getLexeme().equals(")") ) {
                tokens.removeFirst();
                compound_statement();
            }
        } else {
            var_declaration_prime();
        }
    }

    //var-declaration' ->  ; | [ NUM ] ;
    // FIRSTS: ; [ FOLLOWS: $ ( ; ID NUM if int return void while { }
    public void var_declaration_prime() {
        debug("var_declaration-prime");
        if (tokens.isEmpty()) return;
        if ( tokens.getFirst().getLexeme().equals(";")) {
            tokens.removeFirst();
        }
        if ( tokens.getFirst().getLexeme().equals("[") ) {
            tokens.removeFirst();
            if ( tokens.getFirst().getCategory().equals("NUM") ) {
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

    // type specifier -> int | void
    // FIRSTS: int void FOLLOWS: ID
    public void type_specifier() {
        debug("type-specifier");
        if ( tokens.getFirst().getLexeme().equals("int") || tokens.getFirst().getLexeme().equals("void") ) {
            tokens.removeFirst();
        }
    }

    // params -> int ID param` param-list` | void params`
    // FIRSTS: int void FOLLOWS: )
    public void params() {
        debug("params");
        if ( tokens.getFirst().getLexeme().equals("int") ) {
            tokens.removeFirst();
            if ( tokens.getFirst().getCategory().equals("ID") ) {
                tokens.removeFirst();
                param_prime();
                param_list_prime();
            }
        }
        if ( tokens.getFirst().getLexeme().equals("void") ) {
            tokens.removeFirst();
            params_prime();
        }
    }

    // params' -> ID param` param-list` | empty
    // FIRSTS: ID empty FOLLOWS: )
    public void params_prime() {
        debug("params_prime");
        if ( tokens.getFirst().getLexeme().equals(")") ) {
            return;
        }
        if ( tokens.getFirst().getCategory().equals("ID") ) {
            tokens.removeFirst();
            param_prime();
            param_list_prime();
        }
    }

    //param-list` -> , type-specifier ID param` param-list` | empty
    // FIRSTS: , empty FOLLOWS: )
    public void param_list_prime() {
        debug("param_list_prime");
        if ( tokens.getFirst().getLexeme().equals(")") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().equals(",") ) {
            tokens.removeFirst();
            type_specifier();
            if ( tokens.getFirst().getCategory().equals("ID") ) {
                tokens.removeFirst();
                param_prime();
                param_list_prime();
            }

        }
    }

    // param` -> [ ] | empty
    // FIRSTS: [ empty FOLLOWS: , )
    public void param_prime() {
        debug("param-prime");
        if ( tokens.getFirst().getLexeme().equals(",") || tokens.getFirst().getLexeme().equals(")") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().equals("[") ) {
            tokens.removeFirst();
            if ( tokens.getFirst().getLexeme().equals("]") ) {
                tokens.removeFirst();
            }
        }
    }

    // local-declarations -> var-declaration local-declarations | empty
    // FIRSTS: int void empty FOLLOWS: ( ; ID NUM if return while { }
    public void local_declarations() {
        debug("local_declarations");
        if ( tokens.getFirst().getCategory().equals("ID") || tokens.getFirst().getCategory().equals("NUM") ||
                tokens.getFirst().getLexeme().matches("\\(|;|if|return|while|\\{|}")) {
            return;
        }
        if ( tokens.getFirst().getLexeme().equals("int") || tokens.getFirst().getLexeme().equals("void") ) {
            var_declaration();
            local_declarations();
        }
    }

    // var-declaration -> type-specifier ID var-declaration`
    // FIRSTS: int void FOLLOWS: ( ; ID NUM if int return void while { }
    public void var_declaration() {
        debug("var-declaration");
        type_specifier();
        if ( tokens.getFirst().getCategory().equals("ID") ) {
            tokens.removeFirst();
        }
        var_declaration_prime();
    }

    // statement-list -> statement statement-list | empty
    // FIRSTS NUM ID ( ; { if while return empty FOLLOWS: }
    public void statement_list() {
        debug("statement_list");
        if ( tokens.getFirst().getLexeme().equals("}") ) return;
        if ( tokens.getFirst().getCategory().equals("NUM") || tokens.getFirst().getCategory().equals("ID") ||
                tokens.getFirst().getLexeme().matches("\\(|;|if|return|while|\\{") ) {
            statement();
            statement_list();
        }
    }

    // statement -> expression-stmt | compound-stmt | selection-stmt | iteration-stmt | return-stmt
    // FIRSTS: NUM ID ( ; { if while return FOLLOWS: ( ; ID NUM else if return while { }
    public void statement() {
        debug("statement");
        if ( tokens.getFirst().getLexeme().equals("(") || tokens.getFirst().getLexeme().equals(";") ||
                tokens.getFirst().getCategory().equals("ID") || tokens.getFirst().getCategory().equals("NUM") ) {
            expression_statement();
        }
        if ( tokens.getFirst().getLexeme().equals("{") ) {
            compound_statement();
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
    // FIRSTS: ( ; ID NUM FOLLOWS: ( ; ID NUM else if return while { }
    public void expression_statement() {
        debug("expression_statement");
        if ( tokens.getFirst().getLexeme().equals(";") ) {
            tokens.removeFirst();
        }
        if ( tokens.getFirst().getLexeme().equals("(") || tokens.getFirst().getCategory().equals("ID") ||
                tokens.getFirst().getCategory().equals("NUM") ) {
            expression();
            if (tokens.getFirst().getLexeme().equals(";")) {
                tokens.removeFirst();
            }
        }
    }

    // compound-stmt -> { local-declarations statement-list }
    // FIRSTS: { FOLLOWS: $ ( ; ID NUM else if int return void while { }
    public void compound_statement() {
        debug("compound_statement");
        if ( tokens.isEmpty() ) return;
        if (tokens.getFirst().getLexeme().equals("{")) {
            tokens.removeFirst();
            local_declarations();
            statement_list();
            if (tokens.getFirst().getLexeme().equals("}")) {
                tokens.removeFirst();
            }
        }
    }

    // selection_statement -> if ( expression ) statement selection-stmt`
    // FIRSTS: if FOLLOWS: ( ; ID NUM else if return while { }
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

    // selection_statement_prime -> empty | else statement
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

    //iteration-statement -> while ( expression ) statement
    // FIRSTS: while FOLLOWS: ( ; ID NUM else if return while { }
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

    // return-stmt -> return return-stmt`
    // FIRSTS: return FOLLOWS: ( ; ID NUM else if return while { }
    public void return_statement() {
        debug("return_statement");
        if ( tokens.getFirst().getLexeme().equals("return") ) {
            tokens.removeFirst();
            return_statement_prime();
        }
    }

    // return-stmt` -> ; | expression ;
    // FIRSTS: ( ; NUM ID FOLLOWS: ( ; ID NUM else if return while { }

    public void return_statement_prime() {
        debug("return_statement_prime");
        if ( tokens.getFirst().getLexeme().equals(";") ) {
            tokens.removeFirst();
        }
        if ( tokens.getFirst().getCategory().equals("NUM") || tokens.getFirst().getCategory().equals("ID") ||
            tokens.getFirst().getLexeme().equals("(") ) {
            expression();
            if ( tokens.getFirst().getLexeme().equals(";") ) {
                tokens.removeFirst();
            }
        }
    }

    // expression ->  NUM term` additive-expression` simple-expression` | ( expression ) term` additive-expression` simple-expression` | ID expression`
    // FIRSTS: ( NUM ID FOLLOWS: , ) ; ]
    public void expression() {
        debug("expression");
        if ( tokens.getFirst().getCategory().equals("NUM") ) {
            tokens.removeFirst();
            term_prime();
            additive_expression_prime();
            simple_expression_prime();
        }
        if ( tokens.getFirst().getLexeme().equals("(") ) {
            tokens.removeFirst();
            expression();
            if ( tokens.getFirst().getLexeme().equals(")") ) {
                tokens.removeFirst();
                term_prime();
                additive_expression_prime();
                simple_expression_prime();
            }
        }
        if ( tokens.getFirst().getCategory().equals("ID") ) {
            tokens.removeFirst();
            expression_prime();
        }
    }

    // expression` -> = expression | [ expression ] expression`` | term` additive-expression` simple-expression`  | ( args ) term` additive-expression` simple-expression`
    // FIRSTS = [ * / + - ( <= < > >= == != empty FOLLOWS , ) ; ]
    public void expression_prime() {
        debug("expression_prime");
        if ( tokens.getFirst().getLexeme().equals("=") ) {
            tokens.removeFirst();
            expression();
        }
        if ( tokens.getFirst().getLexeme().equals("[") ) {
            tokens.removeFirst();
            expression();
            if ( tokens.getFirst().equals("]") ) {
                tokens.removeFirst();
                expression_prime_prime();
            }
        }
        if ( tokens.getFirst().getLexeme().equals("*") || tokens.getFirst().getLexeme().equals("/") ||
            tokens.getFirst().getLexeme().matches("<=|>=|==|!=|,|\\)|;|]|\\(|\\+|-|<|>") ||
                tokens.getFirst().getCategory().equals("NUM") || tokens.getFirst().getCategory().equals("ID") ) {
            term_prime();
            additive_expression_prime();
            simple_expression_prime();
        }
        if ( tokens.getFirst().getLexeme().equals("(") ) {
            tokens.removeFirst();
            args();
            if ( tokens.getFirst().getLexeme().equals(")") ) {
                tokens.removeFirst();
                term_prime();
                additive_expression_prime();
                simple_expression_prime();
            }
        }
    }

    // expression`` -> = expression | term` additive-expression` simple-expression`
    // FIRST: = * / + - <= < > >= == != empty FOLLOWS: , ) ; ]
    public void expression_prime_prime() {
        debug("expression_prime_prime");
        if ( tokens.getFirst().getLexeme().equals("=") ) {
            tokens.removeFirst();
            expression();
        }
        if ( tokens.getFirst().getLexeme().equals("*") || tokens.getFirst().getLexeme().equals("/") ||
                tokens.getFirst().getLexeme().matches("<=|>=|==|!=|,|\\)|;|]|\\(|\\+|-|<|>") ||
                tokens.getFirst().getCategory().equals("NUM") || tokens.getFirst().getCategory().equals("ID") ) {
            term_prime();
            additive_expression_prime();
            simple_expression_prime();
        }
    }

    // simple-expression` -> relop additive-expression | empty
    // FIRSTS: <= < > >= == != empty FOLLOWS: , ) ; ]
    public void simple_expression_prime() {
        debug("simple_expression_prime");
        if ( tokens.getFirst().getLexeme().equals(",") || tokens.getFirst().getLexeme().equals(")") ||
        tokens.getFirst().getLexeme().equals(";") || tokens.getFirst().getLexeme().equals("]") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().matches("<=|>=|==|!=|<|>") ) {
            relop();
            additive_expression();
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

    //additive-expression -> term additive-expression`
    //FIRSTS: ID NUM ( FOLLOWS: , ) ; ]
    public void additive_expression() {
        debug("additive_expression");
        if ( tokens.getFirst().getCategory().equals("ID") || tokens.getFirst().getCategory().equals("NUM") ||
                tokens.getFirst().getLexeme().equals("(") ) {
            term();
            additive_expression_prime();
        }
    }

    // additive-expression` -> addop term additive-expression` | empty
    // FIRSTS: + - empty FOLLOWS: <= < > >= == != , ) ; ] NUM ( ID
    public void additive_expression_prime() {
        debug("additive_expresion_prime");
        if ( tokens.getFirst().getLexeme().matches("!=|\\)|,|;|<=|==|>=|]|<|>") ||
                tokens.getFirst().getCategory().equals("NUM") || tokens.getFirst().getCategory().equals("ID") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().matches("\\+|-") ) {
            addop();
            term();
            additive_expression_prime();
        }
    }

    // addop -> + | -
    // FIRSTS: + - FOLLOWS: NUM ID (
    public void addop() {
        debug("addop");
        if ( tokens.getFirst().getLexeme().matches("\\+|-") ) {
            tokens.removeFirst();
        }
    }

    // term -> factor term`
    // FIRSTS: NUM ID ( FOLLOWS: != ) + , - ; < <= == > >= ]
    public void term() {
        debug("term");
        if ( tokens.getFirst().getCategory().equals("ID") || tokens.getFirst().getCategory().equals("NUM") ||
                tokens.getFirst().getLexeme().equals("(") ) {
            factor();
            term_prime();
        }
    }

    // term` -> mulop factor term` | empty
    // FIRSTS: * / empty FOLLOWS: != ) + , - ; < <= == > >= ]
    public void term_prime() {
        debug("term_prime");
        if ( tokens.getFirst().getLexeme().matches("!=|\\)|\\+|,|-|;|<=|==|>=|<|>|]") ||
                tokens.getFirst().getCategory().equals("NUM") || tokens.getFirst().getCategory().equals("ID") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().equals("*") || tokens.getFirst().getLexeme().equals("/") ) {
            mulop();
            factor();
            term_prime();
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

    // factor -> ( expression ) | ID factor` | NUM
    // FIRSTS: ( NUM ID FOLLOWS: != ) * + , - / ; < <= == > >= ]
    public void factor() {
        debug("factor");
        if ( tokens.getFirst().getLexeme().equals("(") ) {
            tokens.removeFirst();
            expression();
            if ( tokens.getFirst().getLexeme().equals(")") ) {
                tokens.removeFirst();
            }
        }
        if ( tokens.getFirst().getCategory().equals("ID") ) {
            tokens.removeFirst();
            factor_prime();
        }
        if ( tokens.getFirst().getCategory().equals("NUM") ) {
            tokens.removeFirst();
        }
    }

    // factor` -> [ expression ] | ( args ) | empty
    // FIRSTS: [ ( empty FOLLOWS: FOLLOWS: != ) * + , - / ; < <= == > >= ]
    public void  factor_prime() {
        debug("factor_prime");
        if ( tokens.getFirst().getLexeme().matches("<=|>=|==|!=|\\*|/|\\+|-|<|>|,|\\)|;|]") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().equals("[") ) {
            tokens.removeFirst();
            expression();
            if ( tokens.getFirst().getLexeme().equals("]") ) {
                tokens.removeFirst();
            }
        }
        if ( tokens.getFirst().getLexeme().equals("(") ) {
            tokens.removeFirst();
            args();
            if ( tokens.getFirst().getLexeme().equals(")") ) {
                tokens.removeFirst();
            }
        }
    }

    // args -> arg-list | empty
    // FIRSTS: NUM ID ( FOLLOWS: )
    public void args() {
        debug("args");
        if ( tokens.getFirst().getLexeme().equals(")") ) {
            return;
        }
        if ( tokens.getFirst().getCategory().equals("ID") || tokens.getFirst().getCategory().equals("NUM")
            || tokens.getFirst().getLexeme().equals("(") ) {
            arg_list();
        }
    }

    // arg-list -> expression arg-list`
    // FIRSTS: NUM ID ( FOLLOWS: )
    public void arg_list() {
        debug("arg_list");
        if ( tokens.getFirst().getCategory().equals("NUM") || tokens.getFirst().getCategory().equals("ID") ||
        tokens.getFirst().getLexeme().equals("(") ) {
            expression();
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
