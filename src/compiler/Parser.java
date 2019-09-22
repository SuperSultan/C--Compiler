package compiler;

public class Parser {

    // program -> declaration-list
    public void program() {
        declaration_list();
    }

    //declaration-list -> declaration declaration-list`
    public void declaration_list() {
        declaration();
        declaration_list_prime();
    }

    //declaration_list` -> declaration declaration_list` | empty
    public void declaration_list_prime() {

        // if (empty) return
       // else {
       //     declaration();
      //      declaration_list_prime();
       // }
    }

    //declaration -> var_declaration | function_declaration
    public void declaration() {
        var_declaration();
        function_declaration();
    }

    //var_declaration -> ";" | "[" "NUM" "]" ";"
    public void var_declaration() {
        //if match(";") return true
        //else if match("[" NUM "]" ";") return true
    }

    //type-specifier -> "int" | "void"
    public void type_specifier() {
        //if match("int") return true
        //else if match("void") return true
    }

    //function_declaration -> type-specifier "ID" "(" params ")" compound-stmt
    public void function_declaration() {
        type_specifier();
        //if match("ID") && match("(") && match(")"
            compound_statement();
    }

    //params -> "int" "ID" param` param-list` | "void" params`
    public void params() {
        //if match("int") && match("ID")
            params_prime();
            param_list_prime();
        //else if (match("void")) {
        //  params_prime();
        // }
    }

    //params_prime -> "ID" param` param-list` | empty
    public void params_prime() {
        //if match("ID")
            param_prime();
            param_list_prime();
        //else empty
    }

    //params_list -> param params-list`
    public void params_list() {
        param();
        params_list_prime();
    }

    //params_list_prime -> "," type-specifier "ID" param_prime
    public void params_list_prime() {
        // if match( "," )
            type_specifier();
            // if match("ID") {
            //      param_prime();
            // }
            // else empty
    }

    //param -> type-specifier "ID" param`
    public void param() {
        type_specifier();
        // if ( match("ID") ) {
        //      param_prime()
        // }
    }

    // param_prime -> "[" "]" | empty
    public void param_prime() {
        // if ( match("[") && match("]") return true
        // else empty
    }

    // compound_statement -> "{" local-declarations statement-list "}"
    public void compound_statement() {
        // if match("{")
             local_declarations();
             statement_list();
        // if ( match("}") ) return true
    }

    //local-declarations -> local-declarations_prime
    public void local_declarations() {
        local_declarations_prime();
    }

    // local-declarations_prime -> var-declaration local-declarations_prime | empty
    public void local_declarations_prime() {
        // if match( empty ) return
        var_declaration();
        local_declarations_prime();
    }

    // statement_list -> statement_list_prime
    public void statement_list() {
        statement_list_prime();
    }

    // statement_list_prime -> statement statement_list | empty
    public void statement_list_prime() {
        //if match( empty ) return
        statement();
        statement_list();
    }

    // statement -> expression-stmt | compound-stmt | selection-stmt | iteration-stmt | return-stmt
    public void statement() {
        // if match expression_statement;
        // else if ( compound_statement() )
        // else if match ( selection_statement() )
        // else if match ( iteration_statement() )
        // else if match ( return_statement() )
    }

    // expression_statement -> expression ";" | ";"
    public void expression_statement() {
        // if match(";") return
        // else
            expression();
            //if match( ";" ) return true
    }

    // selection_statement -> "if" "(" expression ")" statement selection-stmt`
    public void selection_statement() {
        // if ( match(";") && match("(") ) {
        //     expression();
                // if ( match(")") )
                    // statement();
        // }
    }

    // selection_statement_prime -> empty | "else" statement
    public void selection_statement_prime() {
        // if ( match( empty ) ) return
        else {
            // if (match("else") {
            //      statement();
            // }
        }
    }

    //iteration-statement -> "while" "(" expression ")" statement
    public void iteration_statement() {
        // if ( match("while") ) && match("(") ) {
        //      expression();
        //      if ( match(")") ) {
        //          statement();
        //      }
        // }
    }

    // return-stmt -> "return" return-stmt`
    public void return_statement() {
        // if ( match("return") ) {
        //      return_statement_prime();
        // }
    }

    // return-stmt_prime -> ";" | expression ";"
    public void return_statement_prime() {
        // if ( match(";" ) return
        // else {
        //      expression();
        //      if ( match(";") ) return
        // }
    }

    // expression -> var "=" expression | simple-expression
    public void expression() {
        var();
        if ( match("=") ) {
            expression();
        } else {
            simple_expression();
        }
    }

    // var -> "ID" var_prime
    public void var() {
        if ( match("ID") ) {
            var_prime();
        }
    }

    // var_prime -> empty | "[" expression "]"
    public void var_prime() {
        if ( match(empty) ) return;
        else {
            if match("[") {
                expression();
            }
            if match("]") return;
        }
    }

    // simple_expression -> additive_expression simple_expression`
    public void simple_expression() {
        additive_expression();
        simple_expression_prime();
    }

    // simple_expression_prime -> relop additive-expression | empty
    public void simple_expression_prime() {
        if ( match( empty ) ) return;
        relop();
        additive_expression();
    }

    // relop -> <= | >= | == | != | > | <
    public void relop() {
        if ( match("<=") ) {
            return;
        } else if ( match(">=") ) {
            return;
        } else if ( match("==") ) {
            return;
        } else if ( match("!=") ) {
            return;
        } else if ( match(">") ) {
            return;
        } else if ( match("<") ) {
            return;
        }
    }

    // additive_expression -> term additive_expression_prime
    public void additive_expression() {
        term();
        additive_expression_prime();
    }

    // additive_expression_prime -> addop term additive_expression_prime | empty
    public void additive_expression_prime() {
        if ( match(empty) ) return;
        addop();
        term();
        additive_expression_prime();
    }

    // addop -> + | -
    public void addop() {
        if ( match("+") ) return;
        else if ( match("-") ) return;
    }

    // term -> factor term_prime
    public void term() {
        factor();
        term_prime();
    }

    // term_prime -> mulop factor term_prime | empty
    public void term_prime() {
        if ( match("empty") ) return;
        mulop();
        factor();
        term_prime();
    }

    // mulop -> * | /
    public void mulop() {
        if match ("*") return;
        else if ( match("/") ) return;
    }

    // factor -> ( expression ) | var | call | NUM
    public void factor() {
        if ( match ("(") ) {
            expression();
            if ( match(")") ) {
                var();
                call();
                if ( match("NUM") ) {
                    return;
                }
            }
        }
    }

    // call -> ID ( args )
    public void call() {
        if ( match("ID") ) {
            if ( match("(") ) {
                args();
                if ( match(")") ) {
                    return;
                }
            }
        }
    }

    // args -> args-list | empty
    public void args() {
        if ( match(empty) ) return;
        arg_list();
    }

    // arg_list -> expression arg-list_prime
    public void arg_list() {
        expression();
        arg_list_prime();
    }

    // arg_list_prime -> , expression arg-list_prime | empty
    public void arg_list_prime() {
        if ( match(",") ) {
            expression();
            arg_list_prime();
        } else if ( match("empty") ) {
            return;
        }
    }


}
