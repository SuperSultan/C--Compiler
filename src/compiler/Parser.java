package compiler;
import java.util.*;

// TODO : Change the types of functions from void to "Node"

class Parser {

    private boolean isAccept;
    private Queue<Token> tokens;

    public Parser (Queue<Token> tokens) {
        isAccept = true;
        this.tokens = tokens;
    }

    public String nextLexeme() {
        return (String)tokens.peek().getLexeme();
    }

    public String nextCategory() {
        return (String)tokens.peek().getCategory();
    }

    public boolean isAccepted() {
        program();
        return nextLexeme().equals("$") && isAccept;
    }

    public void print_rule(String rulename) {
        if ( tokens.peek() != null ) {
            System.out.println(rulename + " " + tokens.peek().toString());
        }
    }

    public void reject() {
        System.out.println("REJECT");
        System.exit(0);
    }

    // program -> declaration-list FIRST: int, void FOLLOWS: $
    public Node program() {
        print_rule("program");

        Node program = new Node("program");
        if (tokens.isEmpty())
            return null;

        Token declaration_list = tokens.peek();
        program.setChild(declaration_list);
        declaration_list();
        return program;
    }

    //declaration_list -> declaration declaration_list_prime FIRSTS: int void empty FOLLOWS: $
    public Node declaration_list() {
        print_rule("declaration_list");
        Node declaration_list = new Node("declaration_list");
        if (tokens.isEmpty())
            return null;

        Token declaration = tokens.peek();
        declaration_list.setChild(declaration);
        declaration();

        Token declaration_list_prime = tokens.peek();
        declaration_list.setChild(declaration_list_prime);
        declaration_list_prime();

        return declaration_list;
    }

    //declaration -> type-specifier ID declaration_prime FIRSTS: int void FOLLOWS: $ int void
    public Node declaration() {
        print_rule("declaration");
        Node declaration = new Node("declaration");
        if ( tokens.isEmpty() )
            return null;

        Token type_specifier = tokens.peek();
        declaration.setChild(type_specifier);
        type_specifier();

        if ( nextCategory().equals("ID") ) {
            Token id = tokens.peek();
            declaration.setChild(id);
            tokens.remove();
        }

        Token declaration_prime = tokens.peek();
        declaration.setChild(declaration_prime);
        declaration_prime();

        return declaration;
    }

    //declaration-list_prime -> declaration declaration-list_prime | empty FIRSTS: int void ϵ FOLLOWS: $
    public Node declaration_list_prime() {
        print_rule("declaration_list_prime");
        Node declaration_list_prime = new Node("declaration_list_prime");
        if (tokens.isEmpty())
            return null;

        if ( nextLexeme().equals("int") || nextLexeme().equals("void") ) {
            Token declaration = tokens.peek();
            declaration_list_prime.setChild(declaration);
            declaration();

            Token declaration_list_prime_ = tokens.peek();
            declaration_list_prime.setChild(declaration_list_prime_);
            declaration_list_prime();
        }

        return declaration_list_prime;
    }

    // type specifier -> int | void FIRSTS: int void FOLLOWS: ID
    public Node type_specifier() {
        print_rule("type_specifier");
        Node type_specifier = new Node("type_specifier");
        if ( nextLexeme().equals("int") || nextLexeme().equals("void") ) {
            type_specifier.setChild(tokens.peek());
            tokens.remove();
        } else reject();

        return type_specifier;
    }

    //declaration_prime -> var-declaration_prime | fun-declaration FIRSTS: ( ; [ FOLLOWS: int void $
    public Node declaration_prime() {
        print_rule("declaration_prime");
        Node declaration_prime = new Node("declaration_prime");
        if (tokens.isEmpty())
            return null;

        if (nextLexeme().equals("(")) {
            Token fun_declaration = tokens.peek();
            declaration_prime.setChild(fun_declaration);
            fun_declaration();
        } else {
            Token var_declaration_prime = tokens.peek();
            declaration_prime.setChild(var_declaration_prime);
            var_declaration_prime();
        }

        return declaration_prime;
    }

    //var-declaration_prime ->  ; | [ NUM ] ; FIRSTS: ; [ FOLLOWS: int void $
    public Node var_declaration_prime() {
        print_rule("var_declaration_prime");
        Node var_declaration_prime = new Node("var_declaration_prime");

        if (tokens.isEmpty())
            return null;

        if ( nextLexeme().equals(";")) {
            Token semicolon = tokens.peek();
            var_declaration_prime.setChild(semicolon);
            tokens.remove();
        }

        if ( nextLexeme().equals("[") ) {
            Token left_bracket = tokens.peek();
            var_declaration_prime.setChild(left_bracket);
            tokens.remove();

            if ( nextCategory().equals("NUM") ) {
                Token NUM = tokens.peek();
                var_declaration_prime.setChild(NUM);
                tokens.remove();

                if ( nextLexeme().equals("]") ) {
                    Token right_bracket = tokens.peek();
                    var_declaration_prime.setChild(right_bracket);
                    tokens.remove();

                    if ( nextLexeme().equals(";") ) {
                        Token semicolon = tokens.peek();
                        var_declaration_prime.setChild(semicolon);
                        tokens.remove();
                    }
                }
            }
        }

        return var_declaration_prime;
    }

    //fun-declaration -> ( params ) compound-stmt FIRSTS: ( FOLLOWS: int void $
    public Node fun_declaration() {
        print_rule("fun_declaration");
        Node fun_declaration = new Node("fun_declaration");

        if ( tokens.isEmpty() )
            return null;

        if ( nextLexeme().equals("(") ) {
            Token left_parenthesis = tokens.peek();
            fun_declaration.setChild(left_parenthesis);
            tokens.remove();

            Token params = tokens.peek();
            fun_declaration.setChild(params);
            params();

            if ( nextLexeme().equals(")") ) {
                Token right_parenthesis = tokens.peek();
                fun_declaration.setChild(right_parenthesis);
                tokens.remove();

                Token compound_statement = tokens.peek();
                fun_declaration.setChild(compound_statement);
                compound_statement();
            }
        }

        return fun_declaration;
    }

    // params -> int ID param_prime param-list_prime | void params_prime FIRSTS: int void FOLLOWS: )
    public Node params() {
        print_rule("params");

        Node params = new Node("params");

        if ( nextLexeme().equals("int") ) {
            Token int_ = tokens.peek();
            params.setChild(int_);
            tokens.remove();

            if ( nextCategory().equals("ID") ) {
                Token ID = tokens.peek();
                params.setChild(ID);
                tokens.remove();

                Token param_prime = tokens.peek();
                params.setChild(param_prime);
                param_prime();

                Token param_list_prime = tokens.peek();
                params.setChild(param_list_prime);
                param_list_prime();
            } else reject();
        }

        if ( nextLexeme().equals("void") ) {
            Token void_ = tokens.peek();
            params.setChild(void_);
            tokens.remove();

            Token params_prime = tokens.peek();
            params.setChild(params_prime);
            params_prime();
        }

        return params;
    }

    // param_prime -> [ ] | empty FIRSTS: [ empty FOLLOWS: , )
    public Node param_prime() {
        print_rule("param_prime");
        Node param_prime =  new Node("param_prime");
        if (nextLexeme().equals(",") || nextLexeme().equals(")"))
            return null;

        if (nextLexeme().equals("[")) {
            Token left_bracket = tokens.peek();
            param_prime.setChild(left_bracket);
            tokens.remove();

            if (nextLexeme().equals("]")) {
                Token right_bracket = tokens.peek();
                param_prime.setChild(right_bracket);
                tokens.remove();

            } else reject();
        }

        return param_prime;
    }

    // params_prime -> ID param_prime param-list_prime | empty FIRSTS: ID empty FOLLOWS: )
    public Node params_prime() {
        print_rule("params_prime");
        Node params_prime = new Node("params_prime");

        if ( nextLexeme().equals(")") )
            return null;

        if ( nextCategory().equals("ID") ) {
            Token ID = tokens.peek();
            params_prime.setChild(ID);
            tokens.remove();

            Token param_prime = tokens.peek();
            params_prime.setChild(param_prime);
            param_prime();

            Token param_list_prime = tokens.peek();
            params_prime.setChild(param_list_prime);
            param_list_prime();
        }

        return params_prime;
    }

    //param-list_prime -> , int ID param_prime param_list_prime | , void ID param_prime param_list_prime |empty FIRSTS: , empty FOLLOWS: )
    public Node param_list_prime() {
        print_rule("param_list_prime");
        Node param_list_prime = new Node("param_list_prime");

        if ( nextLexeme().equals(")") )
            return null;
        if ( nextLexeme().equals(",") ) {
            Token comma = tokens.peek();
            param_list_prime.setChild(comma);
            tokens.remove();

            if ( nextLexeme().equals("int") || nextLexeme().equals("void") ) {
                param_list_prime.setChild(tokens.peek());
                tokens.remove();
            }
            if ( nextCategory().equals("ID") ) {
                Token ID = tokens.peek();
                param_list_prime.setChild(ID);
                tokens.remove();

                Token param_prime = tokens.peek();
                param_list_prime.setChild(param_prime);
                param_prime();

                Token param_list_prime_ = tokens.peek();
                param_list_prime.setChild(param_list_prime_);
                param_list_prime();
            } else reject();
        }

        return param_list_prime;
    }

    // local-declarations -> var-declaration local-declarations | empty FIRSTS: int void empty FOLLOWS: ( ; ID NUM if return while { }
    public Node local_declarations() {
        print_rule("local_declarations");
        Node local_declarations = new Node("local_declarations");

        if ( nextCategory().equals("ID") || nextCategory().equals("NUM")
             || nextLexeme().matches("\\(|;|if|return|while|\\{|}") )
            return null;
        if ( nextLexeme().equals("int") || nextLexeme().equals("void") ) {
            Token var_declaration = tokens.peek();
            local_declarations.setChild(var_declaration);
            var_declaration();

            Token local_declarations_ = tokens.peek();
            local_declarations.setChild(local_declarations_);
            local_declarations();
        }

        return local_declarations;
    }

    // var-declaration -> type-specifier ID var-declaration_prime FIRSTS: int void FOLLOWS: int void
    public Node var_declaration() {
        print_rule("var_declaration");
        Node var_declaration = new Node("var_declaration");

        Token type_specifier = tokens.peek();
        var_declaration.setChild(type_specifier);
        type_specifier();

        if ( nextCategory().equals("ID") ) {
            Token ID = tokens.peek();
            var_declaration.setChild(ID);
            tokens.remove();

            Token var_declaration_prime = tokens.peek();
            var_declaration.setChild(var_declaration_prime);
            var_declaration_prime();

        } else reject();

        return var_declaration;
    }

    // statement-list -> statement statement-list | empty FIRSTS NUM ID ( ; { if while return empty FOLLOWS: }
    public Node statement_list() {
        print_rule("statement_list");

        Node statement_list = new Node("statement_list");
        if ( nextLexeme().equals("}") )
            return null;

        if ( nextCategory().equals("NUM") || nextCategory().equals("ID") || nextLexeme().matches("\\(|;|if|return|while|\\{") ) {
            Token statement = tokens.peek();
            statement_list.setChild(statement);
            statement();

            Token statement_list_ = tokens.peek();
            statement_list.setChild(statement_list_);
            statement_list();
        }
        return statement_list;
    }

    // statement -> expression-stmt | compound-stmt | selection-stmt | iteration-stmt | return-stmt FIRSTS: NUM ID ( ; { if while return FOLLOWS: NUM ID ( ; { if while return }
    public Node statement() {
        print_rule("statement");
        Node statement = new Node("statement");
        if ( nextLexeme().equals("(") || nextLexeme().equals(";")
                || nextCategory().equals("ID") || nextCategory().equals("NUM") ) {
            statement.setChild(tokens.peek());
            expression_statement();
        }
        if ( nextLexeme().equals("{") ) {
            Token left_brace = tokens.peek();
            statement.setChild(left_brace);
            compound_statement();
        }
        if ( nextLexeme().equals("if")) {
            Token if_ = tokens.peek();
            statement.setChild(if_);
            selection_statement();
        }
        if ( nextLexeme().equals("while") ) {
            Token while_ = tokens.peek();
            statement.setChild(while_);
            iteration_statement();
        }
        if ( nextLexeme().equals("return") ) {
            Token return_ = tokens.peek();
            statement.setChild(return_);
            return_statement();
        }

        return statement;
    }

    // expression_statement -> expression ; | ; FIRSTS: ( ; ID NUM FOLLOWS: ( ; ID NUM additive else if return while { }
    public Node expression_statement() {
        print_rule("expression_statement");

        Node expression_statement = new Node("expression_statement");
        if ( nextLexeme().equals(";") ) {
            Token semicolon = tokens.peek();
            expression_statement.setChild(semicolon);
            tokens.remove();
        }
        if ( nextLexeme().equals("(") || nextCategory().equals("ID") || nextCategory().equals("NUM") ) {
            expression_statement.setChild(tokens.peek());
            expression();
            if (nextLexeme().equals(";")) {
                Token semicolon_ = tokens.peek();
                expression_statement.setChild(semicolon_);
                tokens.remove();
            } else reject();
        }

        return expression_statement;
    }

    // compound-stmt -> { local-declarations statement-list } FIRSTS: { FOLLOWS: }
    public Node compound_statement() {
        print_rule("compound_statement");
        Node compound_statement = new Node("compound_statement");

        if ( nextLexeme().equals("{") ) {
            Token left_brace = tokens.peek();
            compound_statement.setChild(left_brace);
            tokens.remove();

            Token local_declarations = tokens.peek();
            compound_statement.setChild(local_declarations);
            local_declarations();

            Token statement_list = tokens.peek();
            compound_statement.setChild(statement_list);
            statement_list();

            if ( nextLexeme().equals("}") ) {
                Token right_brace = tokens.peek();
                compound_statement.setChild(right_brace);
                tokens.remove();
            } else reject();
        }

        return compound_statement;
    }

    // selection_statement -> if ( expression ) statement selection-stmt_prime FIRSTS: if FOLLOWS: ( ; ID NUM if return while { }
    public Node selection_statement() {
        print_rule("selection_statement");
        Node selection_statement = new Node("selection_statement");

        if ( nextLexeme().equals("if") ) {
            Token if_ = tokens.peek();
            selection_statement.setChild(if_);
            tokens.remove();

            if ( nextLexeme().equals("(") ) {
                Token left_parenthesis = tokens.peek();
                selection_statement.setChild(left_parenthesis);
                tokens.remove();

                Token expression = tokens.peek();
                selection_statement.setChild(expression);
                expression();

                if ( nextLexeme().equals(")") ) {
                    Token right_parenthesis = tokens.peek();
                    selection_statement.setChild(right_parenthesis);
                    tokens.remove();

                    Token statement = tokens.peek();
                    selection_statement.setChild(statement);
                    statement();

                    Token selection_statement_prime = tokens.peek();
                    selection_statement.setChild(selection_statement_prime);
                    selection_statement_prime();
                } else reject();
            } else reject();
        } else reject();

        return selection_statement;
    }

    // selection_statement_prime -> empty | else statement FIRSTS: else empty FOLLOWS: ( ; ID NUM if return while { }
    public Node selection_statement_prime() {
        print_rule("selection_statement_prime");

        Node selection_statement_prime = new Node("selection_statement_prime");

        if ( nextLexeme().matches("\\(|;|if|return|while|\\{|}")
             || nextCategory().equals("ID") || nextCategory().equals("NUM") )
            return null;
        if ( nextLexeme().equals("else") ) {
            Token else_ = tokens.peek();
            selection_statement_prime.setChild(else_);
            tokens.remove();

            Token statement = tokens.peek();
            selection_statement_prime.setChild(statement);
            statement();

        } else reject();

        return selection_statement_prime;
    }

    //iteration-statement -> while ( expression ) statement FIRSTS: while FOLLOWS: ( ; ID NUM additive else if return while { }
    public Node iteration_statement() {
        print_rule("iteration_statement");
        Node iteration_statment = new Node("iteration_statement");

        if ( nextLexeme().equals("while") ) {
            Token while_ = tokens.peek();
            iteration_statment.setChild(while_);
            tokens.remove();

            if ( nextLexeme().equals("(") ) {
                Token left_parenthesis = tokens.peek();
                iteration_statment.setChild(left_parenthesis);
                tokens.remove();

                Token expression = tokens.peek();
                iteration_statment.setChild(expression);
                expression();

                if ( nextLexeme().equals(")") ) {
                    Token right_parenthesis = tokens.peek();
                    iteration_statment.setChild(right_parenthesis);
                    tokens.remove();

                    Token statement = tokens.peek();
                    iteration_statment.setChild(statement);
                    statement();
                } else reject();
            } else reject();
        } else reject();

        return iteration_statment;
    }

    // return-stmt -> return return-stmt_prime FIRSTS: return FOLLOWS: ( ; ID NUM additive else if return while { }
    public Node return_statement() {
        print_rule("return_statement");
        Node return_statement = new Node("return_statement");

        if ( nextLexeme().equals("return") ) {
            Token return_ = tokens.peek();
            return_statement.setChild(return_);
            tokens.remove();

            Token return_statement_prime = tokens.peek();
            return_statement.setChild(return_statement_prime);
            return_statement_prime();
        } else reject();

        return return_statement;
    }

    // return-stmt_prime -> ; | expression ; FIRSTS: ; NUM ID ( FOLLOWS: NUM ID ( ; { if while return }
    public Node return_statement_prime() {
        print_rule("return_statement_prime");

        Node return_statement_prime = new Node("return_statement_prime");
        if ( nextLexeme().equals(";") ) {
            Token semicolon = tokens.peek();
            return_statement_prime.setChild(semicolon);
            tokens.remove();
        }
        else if ( nextCategory().equals("NUM") || nextCategory().equals("ID") || nextLexeme().equals("(") ) {
            Token expression = tokens.peek();
            return_statement_prime.setChild(expression);
            expression();

            if ( nextLexeme().equals(";") ) {
                Token semicolon_ = tokens.peek();
                return_statement_prime.setChild(semicolon_);
                tokens.remove();
            } else reject();
        }

        return return_statement_prime;
    }

    // expression ->  NUM term_prime additive-expression_prime simple-expression_prime | ID expression_prime | ( expression ) term_prime additive-expression_prime simple-expression_prime FIRSTS: NUM ( ID FOLLOWS: , ) ; ]
    public Node expression() {
        print_rule("expression");
        Node expression = new Node("expression");

        if ( nextCategory().equals("NUM") ) {
            Token NUM = tokens.peek();
            expression.setChild(NUM);
            tokens.remove();

            Token term_prime = tokens.peek();
            expression.setChild(term_prime);
            term_prime();

            Token additive_expression_prime = tokens.peek();
            expression.setChild(additive_expression_prime);
            additive_expression_prime();

            Token simple_expression_prime = tokens.peek();
            expression.setChild(simple_expression_prime);
            simple_expression_prime();
        }

        else if ( nextCategory().equals("ID") ) {
            Token ID = tokens.peek();
            expression.setChild(ID);
            tokens.remove();

            Token expression_prime = tokens.peek();
            expression.setChild(expression_prime);
            expression_prime();
        }

        else if ( nextLexeme().equals("(") ) {
            Token left_parenthesis = tokens.peek();
            expression.setChild(left_parenthesis);
            tokens.remove();

            Token expression_ = tokens.peek();
            expression.setChild(expression_);
            expression();

            if ( nextLexeme().equals(")") ) {
                Token right_parenthesis = tokens.peek();
                expression.setChild(right_parenthesis);
                tokens.remove();

                Token term_prime = tokens.peek();
                expression.setChild(term_prime);
                term_prime();

                Token additive_expression_prime = tokens.peek();
                expression.setChild(additive_expression_prime);
                additive_expression_prime();

                Token simple_expression_prime = tokens.peek();
                expression.setChild(simple_expression_prime);
                simple_expression_prime();
            } else reject();
        }

        return expression;
    }

    // expression_prime -> = expression | [ expression ] expression_prime_prime | term_prime additive-expression_prime simple-expression_prime  | ( args ) term_prime additive-expression_prime simple-expression_prime FIRSTS = [ * / + - ( <= < > >= == != empty FOLLOWS , ) ; ]
    public Node expression_prime() {
        print_rule("expression_prime");

        Node expression_prime = new Node("expression_prime");

        if ( nextLexeme().equals("=") ) {
            Token equals = tokens.peek();
            expression_prime.setChild(equals);
            tokens.remove();

            Token expression = tokens.peek();
            expression_prime.setChild(expression);
            expression();
        }

        if ( nextLexeme().equals("[") ) {
            Token left_bracket = tokens.peek();
            expression_prime.setChild(left_bracket);
            tokens.remove();

            Token expression_ = tokens.peek();
            expression_prime.setChild(expression_);
            expression();

            if ( nextLexeme().equals("]") ) {
                Token right_bracket = tokens.peek();
                expression_prime.setChild(right_bracket);
                tokens.remove();

                Token expression_prime_prime = tokens.peek();
                expression_prime.setChild(expression_prime_prime);
                expression_prime_prime();
            } else reject();
        }

        if ( nextLexeme().equals("*") || nextLexeme().equals("/") || nextLexeme().matches("<=|>=|==|!=|,|\\)|;|]|\\(|\\+|-|<|>") || nextCategory().equals("NUM") || nextCategory().equals("ID") ) {
            Token term_prime = tokens.peek();
            expression_prime.setChild(term_prime);
            term_prime();

            Token additive_expression_prime = tokens.peek();
            expression_prime.setChild(additive_expression_prime);
            additive_expression_prime();

            Token simple_expression_prime = tokens.peek();
            expression_prime.setChild(simple_expression_prime);
            simple_expression_prime();
        }

        if ( nextLexeme().equals("(") ) {
            Token left_parenthesis = tokens.peek();
            expression_prime.setChild(left_parenthesis);
            tokens.remove();

            Token args = tokens.peek();
            expression_prime.setChild(args);
            args();

            if ( nextLexeme().equals(")") ) {
                Token right_parenthesis = tokens.peek();
                expression_prime.setChild(right_parenthesis);
                tokens.remove();

                Token term_prime = tokens.peek();
                expression_prime.setChild(term_prime);
                term_prime();

                Token additive_expression_prime = tokens.peek();
                expression_prime.setChild(additive_expression_prime);
                additive_expression_prime();

                Token simple_expression_prime = tokens.peek();
                expression_prime.setChild(simple_expression_prime);
                simple_expression_prime();
            } else reject();
        }

        return expression_prime;
    }

    // expression_prime_prime -> = expression | term_prime additive-expression_prime simple-expression_prime FIRST: = * / + - <= < > >= == != empty FOLLOWS: , ) ; ]
    public Node expression_prime_prime() {
        print_rule("expression_prime_prime");

        Node expression_prime_prime = new Node("expression_prime_prime");
        if ( nextLexeme().equals("=") ) {
            Token equals = tokens.peek();
            expression_prime_prime.setChild(equals);
            tokens.remove();

            Token expression = tokens.peek();
            expression_prime_prime.setChild(expression);
            expression();
        }
        if ( nextLexeme().equals("*") || nextLexeme().equals("/") ||
                nextLexeme().matches("<=|>=|==|!=|,|\\)|;|]|\\(|\\+|-|<|>") ||
                nextCategory().equals("NUM") || nextCategory().equals("ID") ) {
            Token term_prime = tokens.peek();
            expression_prime_prime.setChild(term_prime);
            term_prime();

            Token additive_expression_prime = tokens.peek();
            expression_prime_prime.setChild(additive_expression_prime);
            additive_expression_prime();

            Token simple_expression_prime = tokens.peek();
            expression_prime_prime.setChild(simple_expression_prime);
            simple_expression_prime();
        }

        return expression_prime_prime;
    }

    // simple-expression_prime -> relop additive-expression | empty FIRSTS: <= < > >= == != empty FOLLOWS: , ) ; ]
    public Node simple_expression_prime() {
        print_rule("simple_expression_prime");
        Node simple_expression_prime = new Node("simple_expression_prime");

        if ( nextLexeme().equals(",") || nextLexeme().equals(")") || nextLexeme().equals(";") || nextLexeme().equals("]") )
            return null;

        if ( nextLexeme().matches("<=|>=|==|!=|<|>") ) {
            Token relop = tokens.peek();
            simple_expression_prime.setChild(relop);
            relop();

            Token additive_expression = tokens.peek();
            simple_expression_prime.setChild(additive_expression);
            additive_expression();
        }

        return simple_expression_prime;
    }

    //additive-expression -> term additive-expression_prime FIRSTS: ID NUM ( FOLLOWS: , ) ; ]
    public Node additive_expression() {
        print_rule("additive_expression");

        Node additive_expression = new Node("additive_expression");

        if ( nextCategory().equals("ID") || nextCategory().equals("NUM") || nextLexeme().equals("(") ) {
            Token term = tokens.peek();
            additive_expression.setChild(term);
            term();

            Token additive_expression_prime = tokens.peek();
            additive_expression.setChild(additive_expression_prime);
            additive_expression_prime();
        } else reject();

        return additive_expression;
    }

    // additive-expression_prime -> addop term additive-expression_prime | empty FIRSTS: + - empty FOLLOWS: <= < > >= == != , ) ; ] NUM ( ID
    public Node additive_expression_prime() {
        print_rule("additive_expresion_prime");

        Node additive_expression_prime = new Node("additive_expression_prime");
        if ( nextLexeme().matches("!=|\\)|,|;|<=|==|>=|]|<|>|\\(") || nextCategory().equals("NUM") || nextCategory().equals("ID") )
            return null;
        if ( nextLexeme().matches("\\+|-") ) {
            Token addop = tokens.peek();
            additive_expression_prime.setChild(addop);
            addop();

            Token term = tokens.peek();
            additive_expression_prime.setChild(term);
            term();

            Token additive_expression_prime_ = tokens.peek();
            additive_expression_prime.setChild(additive_expression_prime_);
            additive_expression_prime();

        } else reject();

        return additive_expression_prime;
    }

    // term -> factor term_prime FIRSTS: NUM ID ( FOLLOWS: + - <= < > >= == != , ) ; ] NUM ( ID
    public Node term() {
        print_rule("term");
        Node term = new Node("term");

        if ( nextCategory().equals("ID") || nextCategory().equals("NUM") || nextLexeme().equals("(") ) {
            Token factor = tokens.peek();
            term.setChild(factor);
            factor();

            Token term_prime = tokens.peek();
            term.setChild(term_prime);
            term_prime();
        } else reject();

        return term;
    }

    // factor -> ( expression ) | ID factor_prime | NUM FIRSTS: NUM ID ( FOLLOWS: * / + - <= < > >= == != , ) ; ] NUM ( ID
    public Node factor() {
        print_rule("factor");

        Node factor = new Node("factor");

        if ( nextLexeme().equals("(") ) {
            Token left_parenthesis = tokens.peek();
            factor.setChild(left_parenthesis);
            tokens.remove();

            Token expresion = tokens.peek();
            factor.setChild(expresion);
            expression();

            if ( nextLexeme().equals(")") ) {
                Token right_parenthesis = tokens.peek();
                factor.setChild(right_parenthesis);
                tokens.remove();

            } else reject();
        }
        if ( nextCategory().equals("ID") ) {
            Token ID = tokens.peek();
            factor.setChild(ID);
            tokens.remove();

            Token factor_prime = tokens.peek();
            factor.setChild(factor_prime);
            factor_prime();
        }
        if ( nextCategory().equals("NUM") ) {
            Token NUM = tokens.peek();
            factor.setChild(NUM);
            tokens.remove();
        }

        return factor;
    }

    // term_prime -> mulop factor term_prime | empty FIRSTS: * / empty FOLLOWS: + - <= < > >= == != , ) ; ] NUM ( ID
    public Node term_prime() {
        print_rule("term_prime");

        Node term_prime = new Node("term_prime");

        if ( nextLexeme().matches("!=|\\)|\\+|,|-|;|<=|==|>=|<|>|]|\\(") ||
                nextCategory().equals("NUM") || nextCategory().equals("ID") )
            return null;

        if ( nextLexeme().equals("*") || nextLexeme().equals("/") ) {
            Token mulop = tokens.peek();
            term_prime.setChild(mulop);
            mulop();

            Token factor = tokens.peek();
            term_prime.setChild(factor);
            factor();

            Token term_prime_ = tokens.peek();
            term_prime.setChild(term_prime_);
            term_prime();
        } else reject();

        return term_prime;
    }

    // factor_prime -> [ expression ] | ( args ) | empty FIRSTS: [ ( empty FOLLOWS: NUM ID * / + - <= < > >= == != , ) ; ] (
    public Node factor_prime() {
        print_rule("factor_prime");

        Node factor_prime = new Node("factor_prime");
        if ( tokens.isEmpty() )
            return null;
        if ( nextCategory().equals("NUM") || nextCategory().equals("ID") || nextLexeme().matches("<=|>=|==|!=|\\*|/|\\+|-|<|>|,|\\)|;|]|\\(") )
            return null;

        if ( nextLexeme().equals("[") ) {
            Token left_bracket = tokens.peek();
            factor_prime.setChild(left_bracket);
            tokens.remove();

            Token expression = tokens.peek();
            factor_prime.setChild(expression);
            expression();

            if ( nextLexeme().equals("]") ) {
                Token right_bracket = tokens.peek();
                factor_prime.setChild(right_bracket);
                tokens.remove();
            } else reject();
        }
        if ( nextLexeme().equals("(") ) {
            Token left_parenthesis = tokens.peek();
            factor_prime.setChild(left_parenthesis);
            tokens.remove();

            Token args = tokens.peek();
            factor_prime.setChild(args);
            args();

            if ( nextLexeme().equals(")") ) {
                Token right_parenthesis = tokens.peek();
                factor_prime.setChild(right_parenthesis);
                tokens.remove();

            } else reject();
        }

        return factor_prime;
    }

    // args -> arg-list | empty FIRSTS: NUM ID ( FOLLOWS: )
    public Node args() {
        print_rule("args");
        Node args = new Node("args");

        if ( nextLexeme().equals(")") )
            return null;

        if ( nextCategory().equals("ID") || nextCategory().equals("NUM") ) {
            Token arg_list = tokens.peek();
            args.setChild(arg_list);
            arg_list();
        }
        if ( nextLexeme().equals("(") ) { // created to segregate checkType()
            Token left_parenthesis = tokens.peek();
            args.setChild(left_parenthesis);
            arg_list();
        }

        return args;
    }

    // arg-list -> expression arg-list_prime FIRSTS: NUM ID ( FOLLOWS: )
    public Node arg_list() {
        print_rule("arg_list");
        Node arg_list = new Node("arg_list");

        if ( nextCategory().equals("NUM") || nextCategory().equals("ID") || nextLexeme().equals("(") ) {
            Token expression = tokens.peek();
            arg_list.setChild(expression);
            expression();

            Token arg_list_prime = tokens.peek();
            arg_list.setChild(arg_list_prime);
            arg_list_prime();
        } else reject();

        return arg_list;
    }

    // arg_list_prime -> , expression arg-list_prime | empty FIRSTS: , empty FOLLOWS: )
    public Node arg_list_prime() {
        print_rule("arg_list_prime");
        Node arg_list_prime = new Node("arg_list_prime");

        if ( nextLexeme().equals(")") )
            return null;

        if ( nextLexeme().equals(",") ) {
            Token comma = tokens.peek();
            arg_list_prime.setChild(comma);
            tokens.remove();

            Token expression = tokens.peek();
            arg_list_prime.setChild(expression);
            expression();

            Token arg_list_prime_ = tokens.peek();
            arg_list_prime.setChild(arg_list_prime_);
            arg_list_prime();

        } else reject();

        return arg_list_prime;
    }

    // mulop -> * | / FIRSTS: * / FOLLOWS: ( ID NUM
    public Node mulop() {
        print_rule("mulop");
        Node mulop = new Node("mulop");
        if ( nextLexeme().equals("*") || nextLexeme().equals("/") ) {
            mulop.setChild(tokens.peek());
            tokens.remove();

        } else reject();

        return mulop;
    }

    // addop -> + | - FIRSTS: + - FOLLOWS: NUM ID (
    public Node addop() {
        print_rule("addop");

        Node addop = new Node("addop");
        if ( nextLexeme().matches("\\+|-") ) {
            addop.setChild(tokens.peek());
            tokens.remove();
        } else reject();

        return addop;
    }

    // relop -> <= | >= | == | != | > | < FIRSTS: != < <= == > >= FOLLOWS: ID NUM (
    public Node relop() {
        print_rule("relop");
        Node relop = new Node("relop");
        if ( nextLexeme().matches("<=|>=|==|!=|>|<") ) {
            relop.setChild(tokens.peek());
            tokens.remove();
        } else reject();

        return relop;
    }

} // Parser