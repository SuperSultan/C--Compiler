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

        program();
        if ( tokens.getFirst().getLexeme().equals("$") && isAccept) {
            return true;
        } else {
            return false;
        }
    }

    public void print_rule(String rulename) {
        if ( tokens.getFirst() != null ) {
            System.out.println(rulename + " " + tokens.getFirst().getLexeme());
        }
    }

    public void reject() {
        System.out.println("REJECT");
        System.exit(0);
    }

    // program -> declaration-list
    // FIRST: int, void FOLLOWS: $
    public void program() {
        print_rule("program");
        declaration_list();
    }

    //declaration_list -> declaration declaration_list`
    // FIRSTS: int void empty FOLLOWS: $
    public void declaration_list() {
        print_rule("declaration_list");
        if (tokens.isEmpty()) return;
        if ( tokens.getFirst().getLexeme().equals("int") || tokens.getFirst().getLexeme().equals("void") ) {
            declaration();
            declaration_list_prime();
        }
    }

    //declaration-list' -> declaration declaration-list' | empty
    //FIRSTS: int void ϵ FOLLOWS: $
    public void declaration_list_prime() {
        print_rule("declaration-list-prime");
        if (tokens.isEmpty()) return;
        if (tokens.getFirst().getLexeme().equals("int") || tokens.getFirst().getLexeme().equals("void") ) {
            declaration();
            declaration_list_prime();
        }
    }

    //declaration -> type-specifier ID declaration'
    // FIRSTS: int void FOLLOWS: $ int void
    public void declaration() {
        print_rule("declaration");
        if ( tokens.getFirst().getLexeme().equals("int") || tokens.getFirst().getLexeme().equals("void") ) {
            type_specifier();
        }
        if ( tokens.getFirst().getCategory().equals("ID") ) {
            tokens.removeFirst();
        }
        declaration_prime();
    }

    //declaration' -> var-declaration` | ( params ) compound-stmt
    //FIRSTS: ( ; [ FOLLOWS: int void $
    public void declaration_prime() {
        print_rule("declaration-prime");
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
    // FIRSTS: ; [ FOLLOWS: int void $
    public void var_declaration_prime() {
        print_rule("var_declaration-prime");
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
        print_rule("type-specifier");
        if ( tokens.getFirst().getLexeme().equals("int") || tokens.getFirst().getLexeme().equals("void") ) {
            tokens.removeFirst();
        } else reject();
    }

    // params -> int ID param` param-list` | void params`
    // FIRSTS: int void FOLLOWS: )
    public void params() {
        print_rule("params");
        if ( tokens.getFirst().getLexeme().equals("int") ) {
            tokens.removeFirst();
            if ( tokens.getFirst().getCategory().equals("ID") ) {
                tokens.removeFirst();
                param_prime();
                param_list_prime();
            } else reject();
        }
        if ( tokens.getFirst().getLexeme().equals("void") ) {
            tokens.removeFirst();
            params_prime();
        }
    }

    // params' -> ID param` param-list` | empty
    // FIRSTS: ID empty FOLLOWS: )
    public void params_prime() {
        print_rule("params_prime");
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
        print_rule("param_list_prime");
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
            } else reject();

        }
    }

    // param` -> [ ] | empty
    // FIRSTS: [ empty FOLLOWS: , )
    public void param_prime() {
        print_rule("param-prime");
        if ( tokens.getFirst().getLexeme().equals(",") || tokens.getFirst().getLexeme().equals(")") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().equals("[") ) {
            tokens.removeFirst();
            if ( tokens.getFirst().getLexeme().equals("]") ) {
                tokens.removeFirst();
            } else reject();
        }
    }

    // local-declarations -> var-declaration local-declarations | empty
    // FIRSTS: int void empty FOLLOWS: ( ; ID NUM if return while { }
    public void local_declarations() {
        print_rule("local_declarations");
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
    // FIRSTS: int void FOLLOWS: int void
    public void var_declaration() {
        print_rule("var-declaration");
        type_specifier();
        if ( tokens.getFirst().getCategory().equals("ID") ) {
            tokens.removeFirst();
        } else reject();
        var_declaration_prime();
    }

    // statement-list -> statement statement-list | empty
    // FIRSTS NUM ID ( ; { if while return empty FOLLOWS: }
    public void statement_list() {
        print_rule("statement_list");
        if ( tokens.getFirst().getLexeme().equals("}") ) return;
        if ( tokens.getFirst().getCategory().equals("NUM") || tokens.getFirst().getCategory().equals("ID") ||
                tokens.getFirst().getLexeme().matches("\\(|;|if|return|while|\\{") ) {
            statement();
            statement_list();
        }
    }

    // statement -> expression-stmt | compound-stmt | selection-stmt | iteration-stmt | return-stmt
    // FIRSTS: NUM ID ( ; { if while return FOLLOWS: NUM ID ( ; { if while return }
    public void statement() {
        print_rule("statement");
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

    // expression_statement -> expression ; | ;
    // FIRSTS: ( ; ID NUM FOLLOWS: ( ; ID NUM additive else if return while { }
    public void expression_statement() {
        print_rule("expression_statement");
        if ( tokens.getFirst().getLexeme().equals(";") ) {
            tokens.removeFirst();
        }
        if ( tokens.getFirst().getLexeme().equals("(") || tokens.getFirst().getCategory().equals("ID") ||
                tokens.getFirst().getCategory().equals("NUM") ) {
            expression();
            if (tokens.getFirst().getLexeme().equals(";")) {
                tokens.removeFirst();
            } else {
                reject();
            }
        }
    }

    // compound-stmt -> { local-declarations statement-list }
    // FIRSTS: { FOLLOWS: }
    public void compound_statement() {
        print_rule("compound_statement");
        if (tokens.getFirst().getLexeme().equals("{")) {
            tokens.removeFirst();
            local_declarations();
            statement_list();
            if (tokens.getFirst().getLexeme().equals("}")) {
                tokens.removeFirst();
            } else {
                reject();
            }
        }
    }

    // selection_statement -> if ( expression ) statement selection-stmt`
    // FIRSTS: if FOLLOWS: ( ; ID NUM if return while { }
    public void selection_statement() {
        print_rule("selection_statement");
        if ( tokens.getFirst().getLexeme().equals("if") ) {
            tokens.removeFirst();
            if ( tokens.getFirst().getLexeme().equals("(") ) {
                tokens.removeFirst();
                expression();
                if ( tokens.getFirst().getLexeme().equals(")") ) {
                    tokens.removeFirst();
                    statement();
                    selection_statement_prime();
                } else reject();
            } else reject();
        } else reject();
    }

    // selection_statement_prime -> empty | else statement
    // FIRSTS: else empty FOLLOWS: ( ; ID NUM if return while { }
    public void selection_statement_prime() {
        print_rule("selection_statement_prime");
        if (tokens.getFirst().getLexeme().matches("\\(|;|if|return|while|\\{|}") ||
                tokens.getFirst().getCategory().equals("ID") || tokens.getFirst().getCategory().equals("NUM") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().equals("else") ) {
            tokens.removeFirst();
            statement();
        } else reject();
    }

    //iteration-statement -> while ( expression ) statement
    // FIRSTS: while FOLLOWS: ( ; ID NUM additive else if return while { }
    public void iteration_statement() {
        print_rule("iteration_statement");
        if ( tokens.getFirst().getLexeme().equals("while") ) {
            tokens.removeFirst();
            if ( tokens.getFirst().getLexeme().equals("(") ) {
                tokens.removeFirst();
                expression();
                if ( tokens.getFirst().getLexeme().equals(")") ) {
                    tokens.removeFirst();
                    statement();
                } else {
                    reject();
                }
            } else reject();
        } else reject();
    }

    // return-stmt -> return return-stmt`
    // FIRSTS: return FOLLOWS: ( ; ID NUM additive else if return while { }
    public void return_statement() {
        print_rule("return_statement");
        if ( tokens.getFirst().getLexeme().equals("return") ) {
            tokens.removeFirst();
            return_statement_prime();
        } else reject();
    }

    // return-stmt` -> ; | expression ;
    // FIRSTS: ; NUM ID ( FOLLOWS: NUM ID ( ; { if while return }
    public void return_statement_prime() {
        print_rule("return_statement_prime");
        if ( tokens.getFirst().getLexeme().equals(";") ) {
            tokens.removeFirst();
        }
        if ( tokens.getFirst().getCategory().equals("NUM") || tokens.getFirst().getCategory().equals("ID") ||
            tokens.getFirst().getLexeme().equals("(") ) {
            expression();
            if ( tokens.getFirst().getLexeme().equals(";") ) {
                tokens.removeFirst();
            } else {
                reject();
            }
        }
    }

    // expression ->  NUM term` additive-expression` simple-expression` | ( expression ) term` additive-expression` simple-expression` | ID expression`
    // FIRSTS: NUM ( ID FOLLOWS: , ) ; ]
    public void expression() {
        print_rule("expression");
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
            } else reject();
        }
        if ( tokens.getFirst().getCategory().equals("ID") ) {
            tokens.removeFirst();
            expression_prime();
        }
    }

    // expression` -> = expression | [ expression ] expression`` | term` additive-expression` simple-expression`  | ( args ) term` additive-expression` simple-expression`
    // FIRSTS = [ * / + - ( <= < > >= == != empty FOLLOWS , ) ; ]
    public void expression_prime() {
        print_rule("expression_prime");
        if ( tokens.getFirst().getLexeme().equals("=") ) {
            tokens.removeFirst();
            expression();
        }
        if ( tokens.getFirst().getLexeme().equals("[") ) {
            tokens.removeFirst();
            expression();
            expression_prime_prime();
            if ( tokens.getFirst().equals("]") ) {
                expression_prime_prime();
            } else {
                reject();
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
            } else reject();
        }
    }

    // expression`` -> = expression | term` additive-expression` simple-expression`
    // FIRST: = * / + - <= < > >= == != empty FOLLOWS: , ) ; ]
    public void expression_prime_prime() {
        print_rule("expression_prime_prime");
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
        print_rule("simple_expression_prime");
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
    // FIRSTS: != < <= == > >= FOLLOWS: ID NUM (
    public void relop() {
        print_rule("relop");
        if ( tokens.getFirst().getLexeme().matches("<=|>=|==|!=|>|<")) {
            tokens.removeFirst();
        } else {
            reject();
        }
    }

    //additive-expression -> term additive-expression`
    //FIRSTS: ID NUM ( FOLLOWS: , ) ; ]
    public void additive_expression() {
        print_rule("additive_expression");
        if ( tokens.getFirst().getCategory().equals("ID") || tokens.getFirst().getCategory().equals("NUM") ||
                tokens.getFirst().getLexeme().equals("(") ) {
            term();
            additive_expression_prime();
        } else {
            reject();
        }
    }

    // additive-expression` -> addop term additive-expression` | empty
    // FIRSTS: + - empty FOLLOWS: <= < > >= == != , ) ; ] NUM ( ID
    public void additive_expression_prime() {
        print_rule("additive_expresion_prime");
        if ( tokens.getFirst().getLexeme().matches("!=|\\)|,|;|<=|==|>=|]|<|>|\\(") ||
                tokens.getFirst().getCategory().equals("NUM") || tokens.getFirst().getCategory().equals("ID") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().matches("\\+|-") ) {
            addop();
            term();
            additive_expression_prime();
        } else reject();
    }

    // addop -> + | -
    // FIRSTS: + - FOLLOWS: NUM ID (
    public void addop() {
        print_rule("addop");
        if ( tokens.getFirst().getLexeme().matches("\\+|-") ) {
            tokens.removeFirst();
        } else reject();
    }

    // term -> factor term`
    // FIRSTS: NUM ID ( FOLLOWS: + - <= < > >= == != , ) ; ] NUM ( ID
    public void term() {
        print_rule("term");
        if ( tokens.getFirst().getCategory().equals("ID") || tokens.getFirst().getCategory().equals("NUM") ||
                tokens.getFirst().getLexeme().equals("(") ) {
            factor();
            term_prime();
        } else reject();
    }

    // term` -> mulop factor term` | empty
    // FIRSTS: * / empty FOLLOWS: + - <= < > >= == != , ) ; ] NUM ( ID
    // real follows? : + - empty
    public void term_prime() {
        print_rule("term_prime");
        if ( tokens.getFirst().getLexeme().matches("!=|\\)|\\+|,|-|;|<=|==|>=|<|>|]|\\(") ||
                tokens.getFirst().getCategory().equals("NUM") || tokens.getFirst().getCategory().equals("ID") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().equals("*") || tokens.getFirst().getLexeme().equals("/") ) {
            mulop();
            factor();
            term_prime();
        } else reject();
    }

    // mulop -> * | /
    // FIRSTS: * / FOLLOWS: ( ID NUM
    public void mulop() {
        print_rule("mulop");
        if ( tokens.getFirst().getLexeme().equals("*") || tokens.getFirst().getLexeme().equals("/") ) {
            tokens.removeFirst();
        } else reject();
    }

    // factor -> ( expression ) | ID factor` | NUM
    // FIRSTS: NUM ID ( FOLLOWS: * / + - <= < > >= == != , ) ; ] NUM ( ID
    public void factor() {
        print_rule("factor");
        if ( tokens.getFirst().getLexeme().equals("(") ) {
            tokens.removeFirst();
            expression();
            if ( tokens.getFirst().getLexeme().equals(")") ) {
                tokens.removeFirst();
            } else reject();
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
    // FIRSTS: [ ( empty FOLLOWS: NUM ID * / + - <= < > >= == != , ) ; ] (
    public void  factor_prime() {
        print_rule("factor_prime");
        if ( tokens.getFirst().getCategory().equals("NUM") || tokens.getFirst().getCategory().equals("ID") ||
                tokens.getFirst().getLexeme().matches("<=|>=|==|!=|\\*|/|\\+|-|<|>|,|\\)|;|]|\\(") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().equals("[") ) {
            tokens.removeFirst();
            expression();
            if ( tokens.getFirst().getLexeme().equals("]") ) {
                tokens.removeFirst();
            } else reject();
        }
        if ( tokens.getFirst().getLexeme().equals("(") ) {
            tokens.removeFirst();
            args();
            if ( tokens.getFirst().getLexeme().equals(")") ) {
                tokens.removeFirst();
            } else reject();
        }
    }

    // args -> arg-list | empty
    // FIRSTS: NUM ID ( FOLLOWS: )
    public void args() {
        print_rule("args");
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
        print_rule("arg_list");
        if ( tokens.getFirst().getCategory().equals("NUM") || tokens.getFirst().getCategory().equals("ID") ||
        tokens.getFirst().getLexeme().equals("(") ) {
            expression();
            arg_list_prime();
        } else reject();
    }


    // arg_list_prime -> , expression arg-list_prime | empty
    // FIRSTS: , empty FOLLOWS: )
    public void arg_list_prime() {
        print_rule("arg_list_prime");
        if ( tokens.getFirst().getLexeme().equals(")") ) {
            return;
        }
        if ( tokens.getFirst().getLexeme().equals(",") ) {
            tokens.removeFirst();
            expression();
            arg_list_prime();
        } else reject();
    }

} // Parser
