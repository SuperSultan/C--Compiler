package compiler;
import java.util.*;

public class Parser {

    private boolean isAccept;
    private ArrayDeque<Token> tokens;
    private Variable variable;
    private Function function;

    public Parser (ArrayDeque<Token> theTokens) {
        isAccept = true;
        this.tokens = theTokens;
        function = new Function();
        variable = new Variable();
    }

    public String nextLexeme() { return tokens.getFirst().getLexeme(); }
    public String nextCategory() { return tokens.getFirst().getCategory(); }
    public void removeToken() { tokens.removeFirst(); }

    public boolean isAccepted() {
        program();
        //function.verifyFunctions();
        variable.testForNullValues();
        function.functionSymbolTableTest();
        variable.variableSymbolTableTest();
        Parameter parameter = new Parameter(variable);
        //TODO test parameters here!
        function.checkReturnTypes();
        function.checkForMain(); // check void main(void) exists
        function.checkMainIsLast(); // check if main is last
        return nextLexeme().equals("$") && isAccept;
    }

    public void print_rule(String rulename) {
        if ( tokens.getFirst() != null ) {
            System.out.println(rulename + " " + nextLexeme());
        }
    }

    public void reject() {
        System.out.println("REJECT");
        System.exit(0);
    }

    // program -> declaration-list FIRST: int, void FOLLOWS: $
    public void program() {
        print_rule("program");
        if (tokens.isEmpty()) return;
        declaration_list();
    }

    //declaration_list -> declaration declaration_list_prime FIRSTS: int void empty FOLLOWS: $
    public void declaration_list() {
        print_rule("declaration_list");
        if (tokens.isEmpty()) return;
        declaration();
        declaration_list_prime();
    }

    //declaration-list_prime -> declaration declaration-list_prime | empty FIRSTS: int void ϵ FOLLOWS: $
    public void declaration_list_prime() {
        print_rule("declaration_list_prime");
        if (tokens.isEmpty()) return;
        if ( nextLexeme().equals("int") || nextLexeme().equals("void") ) {
            declaration();
            declaration_list_prime();
        }
    }

    // type specifier -> int | void FIRSTS: int void FOLLOWS: ID
    public void type_specifier() {
        print_rule("type_specifier");
        if ( nextLexeme().equals("int") || nextLexeme().equals("void") ) {
            function.setType(nextLexeme());
            variable.setType(nextLexeme());
            removeToken();
        } else reject();
    }

    //declaration -> type-specifier ID declaration_prime FIRSTS: int void FOLLOWS: $ int void
    public void declaration() {
        print_rule("declaration");
        if ( tokens.isEmpty() ) return;
        type_specifier();
        if ( nextCategory().equals("ID") ) {
            function.setId(nextLexeme());
            variable.setId(nextLexeme());
            removeToken();
        }
        declaration_prime();
    }

    //declaration_prime -> var-declaration_prime | fun-declaration FIRSTS: ( ; [ FOLLOWS: int void $
    public void declaration_prime() {
        print_rule("declaration_prime");
        if (tokens.isEmpty()) return;
        if (nextLexeme().equals("(")) {
            fun_declaration();
        } else {
            var_declaration_prime();
        }
    }

    //var-declaration_prime ->  ; | [ NUM ] ; FIRSTS: ; [ FOLLOWS: int void $
    public void var_declaration_prime() {
        print_rule("var_declaration_prime");
        if (tokens.isEmpty()) return;
        if ( nextLexeme().equals(";")) {
            variable.setIsArray("var");
        } else {
            variable.setIsArray("arr");
        }
        removeToken();
        if ( nextLexeme().equals("[") ) {
            removeToken();
            if ( nextCategory().equals("NUM") ) {
                removeToken();
                if ( nextLexeme().equals("]") ) {
                    removeToken();
                    if ( nextLexeme().equals(";") ) {
                        removeToken();
                    }
                }
            }
        }
    }

    //fun-declaration -> ( params ) compound-stmt FIRSTS: ( FOLLOWS: int void $
    public void fun_declaration() {
        print_rule("fun_declaration");
        if ( tokens.isEmpty() ) return;
        if ( nextLexeme().equals("(") ) {
            variable.createNewScope(); // param scope same scope as function scope
            removeToken();
            params();
            if ( nextLexeme().equals(")") ) {
                removeToken();
                compound_statement();
            }
        }
    }

    // params -> int ID param_prime param-list_prime | void params_prime FIRSTS: int void FOLLOWS: )
    public void params() {
        print_rule("params");
        if ( nextLexeme().equals("int") ) {
            variable.setType(nextLexeme());
            removeToken();
            if ( nextCategory().equals("ID") ) {
                variable.setId(nextLexeme());
                variable.put(variable.getType(), variable.getId(), variable.getIsArray());
                removeToken();
                param_prime();
                param_list_prime();
            } else reject();
        }
        if ( nextLexeme().equals("void") ) {
            variable.setType(nextLexeme()); //TODO THIS SHOULD NOT BE HERE! WE ARE MIXING UP VARIABLE AND PARAMETERS
            //parameter.setType(nextLexeme());
            removeToken();
            params_prime();
        }
    }

    // params_prime -> ID param_prime param-list_prime | empty FIRSTS: ID empty FOLLOWS: )
    public void params_prime() {
        print_rule("params_prime");
        if ( nextLexeme().equals(")") ) return;
        if ( nextCategory().equals("ID") ) {
            variable.setId(nextLexeme());
            removeToken();
            param_prime();
            variable.put(variable.getType(),variable.getId(),variable.getIsArray());
            param_list_prime();
        }
    }

    // param_prime -> [ ] | empty FIRSTS: [ empty FOLLOWS: , )
    public void param_prime() {
        print_rule("param_prime");
        if ( nextLexeme().equals(",") || nextLexeme().equals(")") ) return;
        if ( nextLexeme().equals("[") ) {
            variable.setIsArray("arr");
            removeToken();
            if ( nextLexeme().equals("]") ) {
                removeToken();
            } else reject();
        }
        variable.setIsArray("var"); //TODO should this be here?
    }

    //param-list_prime -> , type-specifier ID param_prime param_list_prime | empty FIRSTS: , empty FOLLOWS: )
    public void param_list_prime() {
        print_rule("param_list_prime");
        if ( nextLexeme().equals(")") ) return;
        if ( nextLexeme().equals(",") ) {
            removeToken();
            type_specifier();
            if ( nextCategory().equals("ID") ) {
                variable.setId(nextLexeme());
                removeToken();
                param_prime();
                variable.put(variable.getType(),variable.getId(),variable.getIsArray());
                param_list_prime();
            } else reject();
        }
    }

    // local-declarations -> var-declaration local-declarations | empty FIRSTS: int void empty FOLLOWS: ( ; ID NUM if return while { }
    public void local_declarations() {
        print_rule("local_declarations");
        if ( nextCategory().equals("ID") || nextCategory().equals("NUM") || nextLexeme().matches("\\(|;|if|return|while|\\{|}")) return;
        if ( nextLexeme().equals("int") || nextLexeme().equals("void") ) {
            var_declaration();
            local_declarations();
        }
    }

    // var-declaration -> type-specifier ID var-declaration_prime FIRSTS: int void FOLLOWS: int void
    public void var_declaration() {
        print_rule("var_declaration");
        type_specifier();
        if ( nextCategory().equals("ID") ) {
            variable.setId(nextLexeme());
            removeToken();
        } else reject();
        var_declaration_prime();
        variable.put(variable.getType(), variable.getId(),variable.getIsArray()); //TODO problem here: isArr is NULL
    }

    // statement-list -> statement statement-list | empty FIRSTS NUM ID ( ; { if while return empty FOLLOWS: }
    public void statement_list() {
        print_rule("statement_list");
        if ( nextLexeme().equals("}") ) return;
        if ( nextCategory().equals("NUM") || nextCategory().equals("ID") || nextLexeme().matches("\\(|;|if|return|while|\\{") ) {
            statement();
            statement_list();
        }
    }

    // statement -> expression-stmt | compound-stmt | selection-stmt | iteration-stmt | return-stmt FIRSTS: NUM ID ( ; { if while return FOLLOWS: NUM ID ( ; { if while return }
    public void statement() {
        print_rule("statement");
        if ( nextLexeme().equals("(") || nextLexeme().equals(";") || nextCategory().equals("ID") || nextCategory().equals("NUM") )
            expression_statement();
        if ( nextLexeme().equals("{") )
            compound_statement();
        if ( nextLexeme().equals("if"))
            selection_statement();
        if ( nextLexeme().equals("while") )
            iteration_statement();
        if ( nextLexeme().equals("return") )
            return_statement();
    }

    // expression_statement -> expression ; | ; FIRSTS: ( ; ID NUM FOLLOWS: ( ; ID NUM additive else if return while { }
    public void expression_statement() {
        print_rule("expression_statement");
        if ( nextLexeme().equals(";") )
            removeToken();
        if ( nextLexeme().equals("(") || nextCategory().equals("ID") || nextCategory().equals("NUM") ) {
            expression();
            if (nextLexeme().equals(";")) {
                removeToken();
            } else reject(); //TODO is this wrong
        }
    }

    // compound-stmt -> { local-declarations statement-list } FIRSTS: { FOLLOWS: }
    public void compound_statement() {
        print_rule("compound_statement");
        if ( nextLexeme().equals("{") ) {
            int functionScopeSize = function.getScopeSize();
            int variableScopeSize = variable.getScopeSize();
            System.out.println("Function scope size before: " + functionScopeSize);
            System.out.println("Variable scope size before: " + variableScopeSize);
            if ( functionScopeSize == 0 ) {
                function.createNewScope();
            } else if ( functionScopeSize == 1 && variableScopeSize == 1 ) {
                variable.createNewScope();
            }
            removeToken();
            local_declarations();
            statement_list();
            functionScopeSize = function.getScopeSize();
            variableScopeSize = variable.getScopeSize();
            if ( nextLexeme().equals("}") ) {
                if ( variableScopeSize > 1 ) {
                    variable.deleteScope();
                    variableScopeSize = variable.getScopeSize();
                    System.out.println("Variable scope size after deleting: " + variableScopeSize);
                }
                if ( functionScopeSize == 1 ) {
                    function.removeScope();
                    functionScopeSize = function.getScopeSize();
                    System.out.println("Function scope size after deleting: " + functionScopeSize);
                }
                removeToken();
            } else reject();
        }
    }

    // selection_statement -> if ( expression ) statement selection-stmt_prime FIRSTS: if FOLLOWS: ( ; ID NUM if return while { }
    public void selection_statement() {
        print_rule("selection_statement");
        if ( nextLexeme().equals("if") ) {
            removeToken();
            if ( nextLexeme().equals("(") ) {
                removeToken();
                expression();
                if ( nextLexeme().equals(")") ) {
                    removeToken();
                    statement();
                    selection_statement_prime();
                } else reject();
            } else reject();
        } else reject();
    }

    // selection_statement_prime -> empty | else statement FIRSTS: else empty FOLLOWS: ( ; ID NUM if return while { }
    public void selection_statement_prime() {
        print_rule("selection_statement_prime");
        if (nextLexeme().matches("\\(|;|if|return|while|\\{|}") || nextCategory().equals("ID") || nextCategory().equals("NUM") ) return;
        if ( nextLexeme().equals("else") ) {
            removeToken();
            statement();
        } else reject();
    }

    //iteration-statement -> while ( expression ) statement FIRSTS: while FOLLOWS: ( ; ID NUM additive else if return while { }
    public void iteration_statement() {
        print_rule("iteration_statement");
        if ( nextLexeme().equals("while") ) {
            removeToken();
            if ( nextLexeme().equals("(") ) {
                removeToken();
                expression();
                if ( nextLexeme().equals(")") ) {
                    removeToken();
                    statement();
                } else reject();
            } else reject();
        } else reject();
    }

    // return-stmt -> return return-stmt_prime FIRSTS: return FOLLOWS: ( ; ID NUM additive else if return while { }
    public void return_statement() {
        print_rule("return_statement");
        if ( nextLexeme().equals("return") ) {
            removeToken();
            return_statement_prime();
        } else reject();
    }

    // return-stmt_prime -> ; | expression ; FIRSTS: ; NUM ID ( FOLLOWS: NUM ID ( ; { if while return }
    public void return_statement_prime() {
        print_rule("return_statement_prime");
        if ( nextLexeme().equals(";") ) {
            function.setReturn("void");
            function.put(function.getType(), function.getId(), function.getReturn());
            removeToken();
        }
        if ( nextCategory().equals("NUM") || nextCategory().equals("ID") || nextLexeme().equals("(") ) {
            expression();
            if ( nextLexeme().equals(";") ) {
                removeToken();
            } else reject();
        }
    }

    // expression ->  NUM term_prime additive-expression_prime simple-expression_prime | ID expression_prime | ( expression ) term_prime additive-expression_prime simple-expression_prime FIRSTS: NUM ( ID FOLLOWS: , ) ; ]
    public void expression() {
        print_rule("expression");
        if ( nextCategory().equals("NUM") ) {
            function.setReturn(nextLexeme());
            variable.setId(nextLexeme()); // TODO is this correct ?
            removeToken();
            term_prime();
            additive_expression_prime();
            simple_expression_prime();
        }
        if ( nextCategory().equals("ID") ) {
            function.setId(nextLexeme());
            variable.setId(nextLexeme());
            removeToken();
            expression_prime();
        }
        if ( nextLexeme().equals("(") ) {
            removeToken();
            expression();
            if ( nextLexeme().equals(")") ) {
                removeToken();
                term_prime();
                additive_expression_prime();
                simple_expression_prime();
            } else reject(); //TODO is this wrong?
        }
    }

    // expression_prime -> = expression | [ expression ] expression_prime_prime | term_prime additive-expression_prime simple-expression_prime  | ( args ) term_prime additive-expression_prime simple-expression_prime FIRSTS = [ * / + - ( <= < > >= == != empty FOLLOWS , ) ; ]
    public void expression_prime() {
        print_rule("expression_prime");
        if ( nextLexeme().equals("=") ) {
            removeToken();
            expression();
        }
        if ( nextLexeme().equals("[") ) {
            removeToken();
            expression();
            if ( nextLexeme().equals("]") ) {
                removeToken();
                expression_prime_prime();
            } else reject();
        }
        if ( nextLexeme().equals("*") || nextLexeme().equals("/") || nextLexeme().matches("<=|>=|==|!=|,|\\)|;|]|\\(|\\+|-|<|>") || nextCategory().equals("NUM") || nextCategory().equals("ID") ) {
            term_prime();
            additive_expression_prime();
            simple_expression_prime();
        }
        if ( nextLexeme().equals("(") ) {
            removeToken();
            args();                                 //TODO make sure we implement stuff in this function
            if ( nextLexeme().equals(")") ) {
                removeToken();
                term_prime();
                additive_expression_prime();
                simple_expression_prime();
            } else reject();
        }
    }

    // expression_prime_prime -> = expression | term_prime additive-expression_prime simple-expression_prime FIRST: = * / + - <= < > >= == != empty FOLLOWS: , ) ; ]
    public void expression_prime_prime() {
        print_rule("expression_prime_prime");
        if ( nextLexeme().equals("=") ) {
            removeToken();
            expression();
        }
        if ( nextLexeme().equals("*") || nextLexeme().equals("/") || nextLexeme().matches("<=|>=|==|!=|,|\\)|;|]|\\(|\\+|-|<|>") || nextCategory().equals("NUM") || nextCategory().equals("ID") ) {
            term_prime();
            additive_expression_prime();
            simple_expression_prime();
        }
    }

    // simple-expression_prime -> relop additive-expression | empty FIRSTS: <= < > >= == != empty FOLLOWS: , ) ; ]
    public void simple_expression_prime() {
        print_rule("simple_expression_prime");
        if ( nextLexeme().equals(",") || nextLexeme().equals(")") || nextLexeme().equals(";") || nextLexeme().equals("]") ) return;
        if ( nextLexeme().matches("<=|>=|==|!=|<|>") ) {
            relop();
            additive_expression();
        }
    }

    //additive-expression -> term additive-expression_prime FIRSTS: ID NUM ( FOLLOWS: , ) ; ]
    public void additive_expression() {
        print_rule("additive_expression");
        if ( nextCategory().equals("ID") || nextCategory().equals("NUM") || nextLexeme().equals("(") ) {
            term();
            additive_expression_prime();
        } else reject();
    }

    // additive-expression_prime -> addop term additive-expression_prime | empty FIRSTS: + - empty FOLLOWS: <= < > >= == != , ) ; ] NUM ( ID
    public void additive_expression_prime() {
        print_rule("additive_expresion_prime");
        if ( nextLexeme().matches("!=|\\)|,|;|<=|==|>=|]|<|>|\\(") || nextCategory().equals("NUM") || nextCategory().equals("ID") ) return;
        if ( nextLexeme().matches("\\+|-") ) {
            addop();
            term();
            additive_expression_prime();
        } else reject();
    }

    // term -> factor term_prime FIRSTS: NUM ID ( FOLLOWS: + - <= < > >= == != , ) ; ] NUM ( ID
    public void term() {
        print_rule("term");
        if ( nextCategory().equals("ID") || nextCategory().equals("NUM") || nextLexeme().equals("(") ) {
            factor();
            term_prime();
        } else reject();
    }

    // factor -> ( expression ) | ID factor_prime | NUM FIRSTS: NUM ID ( FOLLOWS: * / + - <= < > >= == != , ) ; ] NUM ( ID
    public void factor() {
        print_rule("factor");
        if ( nextLexeme().equals("(") ) {
            removeToken();
            expression();
            if ( nextLexeme().equals(")") ) {
                removeToken();
            } else reject();
        }
        if ( nextCategory().equals("ID") ) {
            removeToken();
            factor_prime();
        }
        if ( nextCategory().equals("NUM") ) {
            removeToken();
        }
    }

    // term_prime -> mulop factor term_prime | empty FIRSTS: * / empty FOLLOWS: + - <= < > >= == != , ) ; ] NUM ( ID
    public void term_prime() {
        print_rule("term_prime");
        if ( nextLexeme().matches("!=|\\)|\\+|,|-|;|<=|==|>=|<|>|]|\\(") || nextCategory().equals("NUM") || nextCategory().equals("ID") ) return;
        if ( nextLexeme().equals("*") || nextLexeme().equals("/") ) {
            mulop();
            factor();
            term_prime();
        } else reject();
    }

    // factor_prime -> [ expression ] | ( args ) | empty FIRSTS: [ ( empty FOLLOWS: NUM ID * / + - <= < > >= == != , ) ; ] (
    public void  factor_prime() {
        print_rule("factor_prime");
        if ( tokens.isEmpty() ) return;
        if ( nextCategory().equals("NUM") || nextCategory().equals("ID") || nextLexeme().matches("<=|>=|==|!=|\\*|/|\\+|-|<|>|,|\\)|;|]|\\(") ) return;
        if ( nextLexeme().equals("[") ) {
            removeToken();
            expression();
            if ( nextLexeme().equals("]") ) {
                removeToken();
            } else reject();
        }
        if ( nextLexeme().equals("(") ) {
            removeToken();
            args();                                  //TODO should we be adding stuff here?
            if ( nextLexeme().equals(")") ) {
                removeToken();
            } else reject();
        }
    }

    // args -> arg-list | empty FIRSTS: NUM ID ( FOLLOWS: )
    public void args() {
        print_rule("args");
        if ( nextLexeme().equals(")") ) return;
        if ( nextCategory().equals("ID") || nextCategory().equals("NUM") || nextLexeme().equals("(") ) {
            arg_list();
        }
    }

    // arg-list -> expression arg-list_prime FIRSTS: NUM ID ( FOLLOWS: )
    public void arg_list() {
        print_rule("arg_list");
        if ( nextCategory().equals("NUM") || nextCategory().equals("ID") || nextLexeme().equals("(") ) {
            expression();
            arg_list_prime();
        } else reject();
    }

    // arg_list_prime -> , expression arg-list_prime | empty FIRSTS: , empty FOLLOWS: )
    public void arg_list_prime() {
        print_rule("arg_list_prime");
        if ( nextLexeme().equals(")") ) return;
        if ( nextLexeme().equals(",") ) {
            removeToken();
            expression();
            arg_list_prime();
        } else reject();
    }

    // mulop -> * | / FIRSTS: * / FOLLOWS: ( ID NUM
    public void mulop() {
        print_rule("mulop");
        if ( nextLexeme().equals("*") || nextLexeme().equals("/") ) {
            removeToken();
        } else reject();
    }

    // addop -> + | - FIRSTS: + - FOLLOWS: NUM ID (
    public void addop() {
        print_rule("addop");
        if ( nextLexeme().matches("\\+|-") ) {
            removeToken();
        } else reject();
    }

    // relop -> <= | >= | == | != | > | < FIRSTS: != < <= == > >= FOLLOWS: ID NUM (
    public void relop() {
        print_rule("relop");
        if ( nextLexeme().matches("<=|>=|==|!=|>|<") ) {
            removeToken();
        } else reject();
    }

} // Parser