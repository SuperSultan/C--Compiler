package compiler;
import java.util.*;

public class Parser {

    private boolean isAccept;
    private ArrayDeque<Token> tokens;
    private ArrayDeque<Node> nodes;
    private VariableScope varScope;
    private FunctionScope funScope;

    //private static String varIdentifier;
    //private static String funIdentifier;
    //private static String varType;
    //private static String funType;

    public Parser (ArrayDeque<Token> theTokens) {
        isAccept = true;
        this.tokens = theTokens;
        nodes = new ArrayDeque<>();
        varScope = new VariableScope();
        funScope = new FunctionScope();
    }

    public ArrayDeque<Node> getNodes() {
        return nodes;
    }

    public String nextLexeme() { return tokens.getFirst().getLexeme(); }
    public String nextCategory() { return tokens.getFirst().getCategory(); }
    public String nextToken() { return tokens.getFirst().getLexeme(); }
    public void removeToken() { tokens.removeFirst(); }

    public FunctionScope getFunScope() {
        return this.funScope;
    }
    public VariableScope getVarScope() {
        return this.varScope;
    }

    public boolean isAccepted() {
        program();
        varScope.verifyVariableScope();
        funScope.verifyIntFunctions();
        //varScope.verifyVariableScope();
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
        Node program = new Node("program");
        nodes.add(program);
        if (tokens.isEmpty()) return;
        program.addChildNode("declaration_list");
        declaration_list();
    }

    //declaration_list -> declaration declaration_list_prime FIRSTS: int void empty FOLLOWS: $
    public void declaration_list() {
        print_rule("declaration_list");
        Node declaration_list = new Node("declaration_list");
        nodes.add(declaration_list);
        if (tokens.isEmpty()) return;
            declaration_list.addChildNode("declaration");
            declaration();
            declaration_list.addChildNode("declaration_list_prime");
            declaration_list_prime();
    }

    //declaration-list_prime -> declaration declaration-list_prime | empty FIRSTS: int void ϵ FOLLOWS: $
    public void declaration_list_prime() {
        print_rule("declaration_list_prime");
        Node declaration_list_prime = new Node("declaration_list_prime");
        nodes.add(declaration_list_prime);
        if (tokens.isEmpty()) return;
        if ( nextLexeme().equals("int") || nextLexeme().equals("void") ) {
            declaration_list_prime.addChildNode("declaration");
            declaration();
            declaration_list_prime.addChildNode("declaration_list_prime");
            declaration_list_prime();
        }
    }

    // type specifier -> int | void FIRSTS: int void FOLLOWS: ID
    public void type_specifier() {
        print_rule("type_specifier");
        Node type_specifier = new Node("type_specifier");
        nodes.add(type_specifier);
        if ( nextLexeme().equals("int") || nextLexeme().equals("void") ) {

            funType = nextLexeme();
            varType = nextLexeme();
            type_specifier.addChildToken(nextToken());
            removeToken();
        } else reject();
    }

    //declaration -> type-specifier ID declaration_prime FIRSTS: int void FOLLOWS: $ int void
    public void declaration() {
        print_rule("declaration");
        Node declaration = new Node("declaration");
        nodes.add(declaration);
        if ( tokens.isEmpty() ) return;
        declaration.addChildNode("type_specifier");
        type_specifier();
        if ( nextCategory().equals("ID") ) {
            varScope.setIdentifier(nextLexeme());
            funScope.setIdentifier(nextLexeme());
            //funIdentifier = nextLexeme();
            //varIdentifier = nextLexeme();
            declaration.addChildToken(nextToken());
            removeToken();
        }
        declaration.addChildNode("declaration_prime");
        declaration_prime();
    }

    //declaration_prime -> var-declaration_prime | fun-declaration FIRSTS: ( ; [ FOLLOWS: int void $
    public void declaration_prime() {
        print_rule("declaration_prime");
        Node declaration_prime = new Node("declaration_prime");
        nodes.add(declaration_prime);
        if (tokens.isEmpty()) return;
        if (nextLexeme().equals("(")) {
            String funIdentifier = funScope.
            funScope.put(funIdentifier, funType);
            funIdentifier = null; funType = null;
            declaration_prime.addChildNode("fun_declaration");
            fun_declaration();
        } else {
            varScope.put(varIdentifier, varType);
            varIdentifier = null; varType = null;
            declaration_prime.addChildNode("var_declaration_prime");
            var_declaration_prime();
        }
    }

    //fun-dclaration -> ( params ) compound-stmt FIRSTS: ( FOLLOWS: int void $
    public void fun_declaration() {
        print_rule("fun_declaration");
        Node fun_declaration = new Node("fun_declaration");
        nodes.add(fun_declaration);
        if ( tokens.isEmpty() ) return;
        if ( nextLexeme().equals("(") ) {
            fun_declaration.addChildToken(nextToken());
            removeToken();
            fun_declaration.addChildNode("params");
            params();
            if ( nextLexeme().equals(")") ) {
                fun_declaration.addChildToken(nextToken());
                removeToken();
                fun_declaration.addChildNode("compound_statement");
                compound_statement();
            }
        }
    }

    //var-declaration_prime ->  ; | [ NUM ] ; FIRSTS: ; [ FOLLOWS: int void $
    public void var_declaration_prime() {
        print_rule("var_declaration_prime");
        Node var_declaration_prime = new Node("var_declaration_prime");
        nodes.add(var_declaration_prime);
        if (tokens.isEmpty()) return;
        if ( nextLexeme().equals(";")) {
            var_declaration_prime.addChildToken(nextToken());
            removeToken();
        }
        if ( nextLexeme().equals("[") ) {
            var_declaration_prime.addChildToken(nextToken());
            removeToken();
            if ( nextCategory().equals("NUM") ) {
                var_declaration_prime.addChildToken(nextToken());
                removeToken();
                if ( nextLexeme().equals("]") ) {
                    var_declaration_prime.addChildToken(nextToken());
                    removeToken();
                    if ( nextLexeme().equals(";") ) {
                        var_declaration_prime.addChildToken(nextToken());
                        removeToken();
                    }
                }
            }
        }
    }

    // params -> int ID param_prime param-list_prime | void params_prime FIRSTS: int void FOLLOWS: )
    public void params() {
        print_rule("params");
        Node params = new Node("params");
        nodes.add(params);
        if ( nextLexeme().equals("int") ) {
            varType = nextLexeme();
            params.addChildToken(nextToken());
            removeToken();
            if ( nextCategory().equals("ID") ) {
                varIdentifier = nextLexeme();
                varScope.put(varIdentifier, varType);
                varIdentifier = null; varType = null;
                params.addChildToken(nextToken());
                removeToken();
                params.addChildNode("param_prime");
                param_prime();
                params.addChildNode("param_list_prime");
                param_list_prime();
            } else reject();
        }
        if ( nextLexeme().equals("void") ) {
            varIdentifier = nextLexeme();
            varScope.put(varIdentifier, varType);
            varIdentifier = null; varType = null;
            params.addChildToken(nextToken());
            removeToken();
            params.addChildNode("params_prime");
            params_prime();
        }
    }

    // param_prime -> [ ] | empty FIRSTS: [ empty FOLLOWS: , )
    public void param_prime() {
        print_rule("param_prime");
        Node param_prime = new Node("param_prime");
        nodes.add(param_prime);
        if ( nextLexeme().equals(",") || nextLexeme().equals(")") ) return;
        if ( nextLexeme().equals("[") ) {
            param_prime.addChildToken(nextToken());
            removeToken();
            if ( nextLexeme().equals("]") ) {
                param_prime.addChildToken(nextToken());
                removeToken();
            } else reject();
        }
    }

    // params_prime -> ID param_prime param-list_prime | empty FIRSTS: ID empty FOLLOWS: )
    public void params_prime() {
        print_rule("params_prime");
        Node params_prime = new Node("params_prime");
        nodes.add(params_prime);
        if ( nextLexeme().equals(")") ) return;
        if ( nextCategory().equals("ID") ) {
            varIdentifier = nextLexeme();
            varScope.put(varIdentifier,varType);
            varIdentifier = null; varType = null;
            params_prime.addChildToken(nextToken());
            removeToken();
            params_prime.addChildNode("param_prime");
            param_prime();
            params_prime.addChildNode("param_list_prime");
            param_list_prime();
        }
    }

    //param-list_prime -> , type-specifier ID param_prime param_list_prime | empty FIRSTS: , empty FOLLOWS: )
    public void param_list_prime() {
        print_rule("param_list_prime");
        Node param_list_prime = new Node("param_list_prime");
        nodes.add(param_list_prime);
        if ( nextLexeme().equals(")") ) return;
        if ( nextLexeme().equals(",") ) {
            param_list_prime.addChildToken(nextToken());
            removeToken();
            param_list_prime.addChildNode("type_specifier");
            type_specifier();
            if ( nextCategory().equals("ID") ) {
                varIdentifier = nextLexeme();
                varScope.put(varIdentifier,varType);
                varIdentifier = null; varType = null;
                param_list_prime.addChildToken(nextToken());
                removeToken();
                param_list_prime.addChildNode("param_prime");
                param_prime();
                param_list_prime.addChildNode("param_list_prime");
                param_list_prime();
            } else reject();

        }
    }

    // local-declarations -> var-declaration local-declarations | empty FIRSTS: int void empty FOLLOWS: ( ; ID NUM if return while { }
    public void local_declarations() {
        print_rule("local_declarations");
        Node local_declarations = new Node("local_declarations");
        nodes.add(local_declarations);
        if ( nextCategory().equals("ID") || nextCategory().equals("NUM") || nextLexeme().matches("\\(|;|if|return|while|\\{|}")) return;
        if ( nextLexeme().equals("int") || nextLexeme().equals("void") ) {
            local_declarations.addChildNode("var_declaration");
            var_declaration();
            local_declarations.addChildNode("local_declarations");
            local_declarations();
        }
    }

    // var-declaration -> type-specifier ID var-declaration_prime FIRSTS: int void FOLLOWS: int void
    public void var_declaration() {
        print_rule("var_declaration");
        Node var_declaration = new Node("var_declaration");
        nodes.add(var_declaration);
        var_declaration.addChildNode("type_specifier");
        type_specifier();
        if ( nextCategory().equals("ID") ) {
            varIdentifier = nextLexeme();
            varScope.put(varIdentifier, varType);
            varIdentifier = null; varType = null;
            var_declaration.addChildToken(nextToken());
            removeToken();
        } else reject();
        var_declaration.addChildNode("var_declaration_prime");
        var_declaration_prime();
    }

    // statement-list -> statement statement-list | empty FIRSTS NUM ID ( ; { if while return empty FOLLOWS: }
    public void statement_list() {
        print_rule("statement_list");
        Node statement_list = new Node("statement_list");
        nodes.add(statement_list);
        if ( nextLexeme().equals("}") ) return;
        if ( nextCategory().equals("NUM") || nextCategory().equals("ID") || nextLexeme().matches("\\(|;|if|return|while|\\{") ) {
            statement_list.addChildNode("statement");
            statement();
            statement_list.addChildNode("statement_list");
            statement_list();
        }
    }

    // statement -> expression-stmt | compound-stmt | selection-stmt | iteration-stmt | return-stmt FIRSTS: NUM ID ( ; { if while return FOLLOWS: NUM ID ( ; { if while return }
    public void statement() {
        print_rule("statement");
        Node statement = new Node("statement");
        nodes.add(statement);
        if ( nextLexeme().equals("(") || nextLexeme().equals(";") || nextCategory().equals("ID") || nextCategory().equals("NUM") ) {
            statement.addChildNode("expression_statement");
            expression_statement();
        }
        if ( nextLexeme().equals("{") ) {
            statement.addChildNode("compound_statement");
            compound_statement();
        }
        if ( nextLexeme().equals("if")) {
            statement.addChildNode("selection_statement");
            selection_statement();
        }
        if ( nextLexeme().equals("while") ) {
            statement.addChildNode("iteration_statement");
            iteration_statement();
        }
        if ( nextLexeme().equals("return") ) {
            statement.addChildNode("return_statement");
            return_statement();
        }
    }

    // expression_statement -> expression ; | ; FIRSTS: ( ; ID NUM FOLLOWS: ( ; ID NUM additive else if return while { }
    public void expression_statement() {
        print_rule("expression_statement");
        Node expression_statement = new Node("expression_statement");
        nodes.add(expression_statement);
        if ( nextLexeme().equals(";") ) {
            expression_statement.addChildToken(nextToken());
            removeToken();
        }
        if ( nextLexeme().equals("(") || nextCategory().equals("ID") || nextCategory().equals("NUM") ) {
            expression_statement.addChildNode("expression");
            expression();
            if (nextLexeme().equals(";")) {
                expression_statement.addChildToken(nextToken());
                removeToken();
            } else reject();
        }
    }

    // compound-stmt -> { local-declarations statement-list } FIRSTS: { FOLLOWS: }
    public void compound_statement() {
        print_rule("compound_statement");
        Node compound_statement = new Node("compound_statement");
        nodes.add(compound_statement);
        if ( nextLexeme().equals("{") ) {
            varScope.createNewScope();
            funScope.createNewScope();
            compound_statement.addChildToken(nextToken());
            removeToken();
            compound_statement.addChildNode("local_declarations");
            local_declarations();
            compound_statement.addChildNode("statement_list");
            statement_list();
            if ( nextLexeme().equals("}") ) {
                varScope.removeScope();
                funScope.removeScope();
                compound_statement.addChildToken(nextToken());
                removeToken();
            } else reject();
        }
    }

    // selection_statement -> if ( expression ) statement selection-stmt_prime FIRSTS: if FOLLOWS: ( ; ID NUM if return while { }
    public void selection_statement() {
        print_rule("selection_statement");
        Node selection_statement = new Node("selection_statement");
        nodes.add(selection_statement);
        if ( nextLexeme().equals("if") ) {
            selection_statement.addChildToken(nextToken());
            removeToken();
            if ( nextLexeme().equals("(") ) {
                selection_statement.addChildToken(nextToken());
                removeToken();
                selection_statement.addChildNode("expression");
                expression();
                if ( nextLexeme().equals(")") ) {
                    selection_statement.addChildToken(nextToken());
                    removeToken();
                    selection_statement.addChildNode("statement");
                    statement();
                    selection_statement.addChildNode("selection_statement_prime");
                    selection_statement_prime();
                } else reject();
            } else reject();
        } else reject();
    }

    // selection_statement_prime -> empty | else statement FIRSTS: else empty FOLLOWS: ( ; ID NUM if return while { }
    public void selection_statement_prime() {
        print_rule("selection_statement_prime");
        Node selection_statement_prime = new Node("selection_statement_prime");
        nodes.add(selection_statement_prime);
        if (nextLexeme().matches("\\(|;|if|return|while|\\{|}") || nextCategory().equals("ID") || nextCategory().equals("NUM") ) return;
        if ( nextLexeme().equals("else") ) {
            selection_statement_prime.addChildToken(nextToken());
            removeToken();
            selection_statement_prime.addChildNode("statement");
            statement();
        } else reject();
    }

    //iteration-statement -> while ( expression ) statement FIRSTS: while FOLLOWS: ( ; ID NUM additive else if return while { }
    public void iteration_statement() {
        print_rule("iteration_statement");
        Node iteration_statement = new Node("iteration_statement");
        nodes.add(iteration_statement);
        if ( nextLexeme().equals("while") ) {
            iteration_statement.addChildToken(nextToken());
            removeToken();
            if ( nextLexeme().equals("(") ) {
                iteration_statement.addChildToken(nextToken());
                removeToken();
                iteration_statement.addChildNode("expression");
                expression();
                if ( nextLexeme().equals(")") ) {
                    iteration_statement.addChildToken(nextToken());
                    removeToken();
                    iteration_statement.addChildNode("statement");
                    statement();
                } else reject();
            } else reject();
        } else reject();
    }

    // return-stmt -> return return-stmt_prime FIRSTS: return FOLLOWS: ( ; ID NUM additive else if return while { }
    public void return_statement() {
        print_rule("return_statement");
        Node return_statement = new Node("return_statement");
        nodes.add(return_statement);
        if ( nextLexeme().equals("return") ) {
            funType = nextLexeme();
            return_statement.addChildToken(nextToken());
            removeToken();
            return_statement.addChildNode("return_statement_prime");
            return_statement_prime();
        } else reject();
    }

    // return-stmt_prime -> ; | expression ; FIRSTS: ; NUM ID ( FOLLOWS: NUM ID ( ; { if while return }
    public void return_statement_prime() {
        print_rule("return_statement_prime");
        Node return_statement_prime = new Node("return_statement_prime");
        nodes.add(return_statement_prime);
        if ( nextLexeme().equals(";") ) {
            funIdentifier = "empty";
            funScope.put(funIdentifier,funType);
            funIdentifier= null; funType = null;
            return_statement_prime.addChildToken(nextToken());
            removeToken();
        }
        if ( nextCategory().equals("NUM") || nextCategory().equals("ID") || nextLexeme().equals("(") ) {
            return_statement_prime.addChildNode("expression");
            expression();
            if ( nextLexeme().equals(";") ) {
                return_statement_prime.addChildToken(nextToken());
                removeToken();
            } else reject();
        }
    }

    // expression ->  NUM term_prime additive-expression_prime simple-expression_prime | ID expression_prime | ( expression ) term_prime additive-expression_prime simple-expression_prime FIRSTS: NUM ( ID FOLLOWS: , ) ; ]
    public void expression() {
        print_rule("expression");
        Node expression = new Node("expression");
        nodes.add(expression);
        if ( nextCategory().equals("NUM") ) {
            funIdentifier = nextLexeme();
            funScope.put(funIdentifier,funType);
            funIdentifier = null; funType = null;
            expression.addChildToken(nextToken());
            removeToken();
            expression.addChildNode("term_prime");
            term_prime();
            expression.addChildNode("additive_expression_prime");
            additive_expression_prime();
            expression.addChildNode("simple_expression_prime");
            simple_expression_prime();
        }
        if ( nextCategory().equals("ID") ) {
            funIdentifier = nextLexeme();
            funScope.put(funIdentifier,funType);
            funIdentifier = null; funType = null;
            expression.addChildToken(nextToken());
            removeToken();
            expression.addChildNode("expression_prime");
            expression_prime();
        }
        if ( nextLexeme().equals("(") ) {
            expression.addChildToken(nextToken());
            removeToken();
            expression.addChildNode("expression");
            expression();
            if ( nextLexeme().equals(")") ) {
                expression.addChildToken(nextToken());
                removeToken();
                expression.addChildNode("term_prime");
                term_prime();
                expression.addChildNode("additive_expression_prime");
                additive_expression_prime();
                expression.addChildNode("simple_expression_prime");
                simple_expression_prime();
            } else reject();
        }
    }

    // expression_prime -> = expression | [ expression ] expression_prime_prime | term_prime additive-expression_prime simple-expression_prime  | ( args ) term_prime additive-expression_prime simple-expression_prime FIRSTS = [ * / + - ( <= < > >= == != empty FOLLOWS , ) ; ]
    public void expression_prime() {
        print_rule("expression_prime");
        Node expression_prime = new Node("expression_prime");
        nodes.add(expression_prime);
        if ( nextLexeme().equals("=") ) {
            expression_prime.addChildToken(nextToken());
            removeToken();
            expression_prime.addChildNode("expression");
            expression();
        }
        if ( nextLexeme().equals("[") ) {
            expression_prime.addChildToken(nextToken());
            removeToken();
            expression();
            if ( nextLexeme().equals("]") ) {
                expression_prime.addChildToken(nextToken());
                removeToken();
                expression_prime.addChildNode("expression_prime_prime");
                expression_prime_prime();
            } else reject();
        }
        if ( nextLexeme().equals("*") || nextLexeme().equals("/") || nextLexeme().matches("<=|>=|==|!=|,|\\)|;|]|\\(|\\+|-|<|>") || nextCategory().equals("NUM") || nextCategory().equals("ID") ) {
            expression_prime.addChildNode("term_prime");
            term_prime();
            expression_prime.addChildNode("additive_expression_prime");
            additive_expression_prime();
            expression_prime.addChildNode("simple_expression_prime");
            simple_expression_prime();
        }
        if ( nextLexeme().equals("(") ) {
            expression_prime.addChildToken(nextToken());
            removeToken();
            expression_prime.addChildNode("args");
            args();
            if ( nextLexeme().equals(")") ) {
                expression_prime.addChildToken(nextToken());
                removeToken();
                expression_prime.addChildNode("term_prime");
                term_prime();
                expression_prime.addChildNode("additive_expression_prime");
                additive_expression_prime();
                expression_prime.addChildNode("simple_expression_prime");
                simple_expression_prime();
            } else reject();
        }
    }

    // expression_prime_prime -> = expression | term_prime additive-expression_prime simple-expression_prime FIRST: = * / + - <= < > >= == != empty FOLLOWS: , ) ; ]
    public void expression_prime_prime() {
        print_rule("expression_prime_prime");
        Node expression_prime_prime = new Node("expression_prime_prime");
        nodes.add(expression_prime_prime);
        if ( nextLexeme().equals("=") ) {
            expression_prime_prime.addChildToken(nextToken());
            removeToken();
            expression_prime_prime.addChildNode("expression");
            expression();
        }
        if ( nextLexeme().equals("*") || nextLexeme().equals("/") || nextLexeme().matches("<=|>=|==|!=|,|\\)|;|]|\\(|\\+|-|<|>") || nextCategory().equals("NUM") || nextCategory().equals("ID") ) {
            expression_prime_prime.addChildNode("term_prime");
            term_prime();
            expression_prime_prime.addChildNode("additive_expression_prime");
            additive_expression_prime();
            expression_prime_prime.addChildNode("simple_expression_prime");
            simple_expression_prime();
        }
    }

    // simple-expression_prime -> relop additive-expression | empty FIRSTS: <= < > >= == != empty FOLLOWS: , ) ; ]
    public void simple_expression_prime() {
        print_rule("simple_expression_prime");
        Node simple_expression_prime = new Node("simple_expression_prime");
        nodes.add(simple_expression_prime);
        if ( nextLexeme().equals(",") || nextLexeme().equals(")") || nextLexeme().equals(";") || nextLexeme().equals("]") ) return;
        if ( nextLexeme().matches("<=|>=|==|!=|<|>") ) {
            simple_expression_prime.addChildNode("relop");
            relop();
            simple_expression_prime.addChildNode("additive_expression");
            additive_expression();
        }
    }

    // relop -> <= | >= | == | != | > | < FIRSTS: != < <= == > >= FOLLOWS: ID NUM (
    public void relop() {
        print_rule("relop");
        Node relop = new Node("relop");
        nodes.add(relop);
        if ( nextLexeme().matches("<=|>=|==|!=|>|<") ) {
            relop.addChildToken(nextToken());
            removeToken();
        } else reject();
    }

    //additive-expression -> term additive-expression_prime FIRSTS: ID NUM ( FOLLOWS: , ) ; ]
    public void additive_expression() {
        print_rule("additive_expression");
        Node additive_expression = new Node("additive_expression");
        nodes.add(additive_expression);
        if ( nextCategory().equals("ID") || nextCategory().equals("NUM") || nextLexeme().equals("(") ) {
            additive_expression.addChildNode("term");
            term();
            additive_expression.addChildNode("additive_expression_prime");
            additive_expression_prime();
        } else reject();
    }

    // additive-expression_prime -> addop term additive-expression_prime | empty FIRSTS: + - empty FOLLOWS: <= < > >= == != , ) ; ] NUM ( ID
    public void additive_expression_prime() {
        print_rule("additive_expresion_prime");
        Node additive_expression_prime = new Node("additive_expression_prime");
        nodes.add(additive_expression_prime);
        if ( nextLexeme().matches("!=|\\)|,|;|<=|==|>=|]|<|>|\\(") || nextCategory().equals("NUM") || nextCategory().equals("ID") ) return;
        if ( nextLexeme().matches("\\+|-") ) {
            additive_expression_prime.addChildNode("addop");
            addop();
            additive_expression_prime.addChildNode("term");
            term();
            additive_expression_prime.addChildNode("additive_expression_prime");
            additive_expression_prime();
        } else reject();
    }

    // addop -> + | - FIRSTS: + - FOLLOWS: NUM ID (
    public void addop() {
        print_rule("addop");
        Node addop = new Node("addop");
        nodes.add(addop);
        if ( nextLexeme().matches("\\+|-") ) {
            addop.addChildToken(nextToken());
            removeToken();
        } else reject();
    }

    // term -> factor term_prime FIRSTS: NUM ID ( FOLLOWS: + - <= < > >= == != , ) ; ] NUM ( ID
    public void term() {
        print_rule("term");
        Node term = new Node("term");
        nodes.add(term);
        if ( nextCategory().equals("ID") || nextCategory().equals("NUM") || nextLexeme().equals("(") ) {
            term.addChildNode("factor");
            factor();
            term.addChildNode("term_prime");
            term_prime();
        } else reject();
    }

    // term_prime -> mulop factor term_prime | empty FIRSTS: * / empty FOLLOWS: + - <= < > >= == != , ) ; ] NUM ( ID
    public void term_prime() {
        print_rule("term_prime");
        Node term_prime = new Node("term_prime");
        nodes.add(term_prime);
        if ( nextLexeme().matches("!=|\\)|\\+|,|-|;|<=|==|>=|<|>|]|\\(") || nextCategory().equals("NUM") || nextCategory().equals("ID") ) return;
        if ( nextLexeme().equals("*") || nextLexeme().equals("/") ) {
            term_prime.addChildNode("mulop");
            mulop();
            term_prime.addChildNode("factor");
            factor();
            term_prime.addChildNode("term_prime");
            term_prime();
        } else reject();
    }

    // mulop -> * | / FIRSTS: * / FOLLOWS: ( ID NUM
    public void mulop() {
        print_rule("mulop");
        Node mulop = new Node("mulop");
        nodes.add(mulop);
        if ( nextLexeme().equals("*") || nextLexeme().equals("/") ) {
            mulop.addChildToken(nextToken());
            removeToken();
        } else reject();
    }

    // factor -> ( expression ) | ID factor_prime | NUM FIRSTS: NUM ID ( FOLLOWS: * / + - <= < > >= == != , ) ; ] NUM ( ID
    public void factor() {
        print_rule("factor");
        Node factor = new Node("factor");
        nodes.add(factor);
        if ( nextLexeme().equals("(") ) {
            factor.addChildToken(nextToken());
            removeToken();
            factor.addChildNode("expression");
            expression();
            if ( nextLexeme().equals(")") ) {
                factor.addChildToken(nextToken());
                removeToken();
            } else reject();
        }
        if ( nextCategory().equals("ID") ) {
            factor.addChildToken(nextToken());
            removeToken();
            factor.addChildNode("factor_prime");
            factor_prime();
        }
        if ( nextCategory().equals("NUM") ) {
            factor.addChildToken(nextToken());
            removeToken();
        }
    }

    // factor_prime -> [ expression ] | ( args ) | empty FIRSTS: [ ( empty FOLLOWS: NUM ID * / + - <= < > >= == != , ) ; ] (
    public void  factor_prime() {
        print_rule("factor_prime");
        Node factor_prime = new Node("factor_prime");
        nodes.add(factor_prime);
        if ( tokens.isEmpty() ) return;
        if ( nextCategory().equals("NUM") || nextCategory().equals("ID") || nextLexeme().matches("<=|>=|==|!=|\\*|/|\\+|-|<|>|,|\\)|;|]|\\(") ) return;
        if ( nextLexeme().equals("[") ) {
            factor_prime.addChildToken(nextToken());
            removeToken();
            factor_prime.addChildNode("expression");
            expression();
            if ( nextLexeme().equals("]") ) {
                factor_prime.addChildToken(nextToken());
                removeToken();
            } else reject();
        }
        if ( nextLexeme().equals("(") ) {
            factor_prime.addChildToken(nextToken());
            removeToken();
            factor_prime.addChildNode("args");
            args();
            if ( nextLexeme().equals(")") ) {
                factor_prime.addChildToken(nextToken());
                removeToken();
            } else reject();
        }
    }

    // args -> arg-list | empty FIRSTS: NUM ID ( FOLLOWS: )
    public void args() {
        print_rule("args");
        Node args = new Node("args");
        nodes.add(args);
        if ( nextLexeme().equals(")") ) return;
        if ( nextCategory().equals("ID") || nextCategory().equals("NUM") || nextLexeme().equals("(") ) {
            args.addChildNode("arg_list");
            arg_list();
        }
    }

    // arg-list -> expression arg-list_prime FIRSTS: NUM ID ( FOLLOWS: )
    public void arg_list() {
        print_rule("arg_list");
        Node arg_list = new Node("arg_list");
        nodes.add(arg_list);
        if ( nextCategory().equals("NUM") || nextCategory().equals("ID") || nextLexeme().equals("(") ) {
            arg_list.addChildNode("expression");
            expression();
            arg_list.addChildNode("arg_list_prime");
            arg_list_prime();
        } else reject();
    }

    // arg_list_prime -> , expression arg-list_prime | empty FIRSTS: , empty FOLLOWS: )
    public void arg_list_prime() {
        print_rule("arg_list_prime");
        Node arg_list_prime = new Node("arg_list_prime");
        nodes.add(arg_list_prime);
        if ( nextLexeme().equals(")") ) return;
        if ( nextLexeme().equals(",") ) {
            arg_list_prime.addChildToken(nextToken());
            removeToken();
            arg_list_prime.addChildNode("expression");
            expression();
            arg_list_prime.addChildNode("arg_list_prime");
            arg_list_prime();
        } else reject();
    }

} // Parser
