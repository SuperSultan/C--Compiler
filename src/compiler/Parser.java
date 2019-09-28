package compiler;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Iterator;

public class Parser {

    private boolean isAccept;
    private Token token;
    private ArrayDeque<Token> tokens;

    public Parser (Token token) {
        isAccept = true;
        this.token = token;
    }

    public boolean isAccepted(Token theTokens) {
        return isAccept;
    }

    public void isLexicallyCorrect(Token token) {

        ArrayDeque<Token> tokens = token.getTokenList();
        for(Token tok: tokens) {
            if (tok.getCategory(tok).equals("ERROR")) {
                isAccept = false;
            }
        }
    }

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

        if (tokens.getFirst().equals("")) return;
        else {
            declaration();
            declaration_list_prime();
        }
    }

    //declaration -> var_declaration | function_declaration
    public void declaration() {
        var_declaration();
        function_declaration();
    }

    //var_declaration -> type-specifier ID var-declaration'
    public void var_declaration() {
        type_specifier();
        if (tokens.removeFirst().getCategory(token).equals("ID") ) var_declaration_prime();
    }

    //var-declaration' -> ; | [ NUM ] ;
    public void var_declaration_prime() {
        if ( tokens.removeFirst().equals(";") ) { return; }
        else if ( tokens.removeFirst().equals("[") ) {
            if ( tokens.removeFirst().getCategory(token).equals("NUM") ) {
                if ( tokens.removeFirst().getCategory(token).equals("]") ) {
                    if ( tokens.removeFirst().equals(";") ) return;
                }
            }
        }
    }

    //type-specifier -> "int" | "void"
    public boolean type_specifier() {
        return tokens.removeFirst().equals("int") || tokens.removeFirst().equals("void");
    }

    //fun-declaration -> int ID ( params ) compound-stmt || void ID ( params ) compound-stmt
    public void function_declaration() {
        if ( tokens.removeFirst().getCategory(token).equals("NUM") ) {
            if ( tokens.removeFirst().equals("(") ) {
                params();
                if ( tokens.removeFirst().equals(")") ) compound_statement();
            }
        }
    }

    //params -> param-list | void
    public void params() {
        if ( tokens.removeFirst().equals("void") ) return;
        else param_list();
    }

    //params_list -> param params-list`
    public void param_list() {
        param();
        param_list_prime();
    }

    //params_list_prime -> "," type-specifier "ID" param_prime
    public void param_list_prime() {
        if ( tokens.removeFirst().equals(",") ) {
            param();
            param_list_prime();
        } else return;
    }

    //param -> int ID param` | void ID param'
    public void param() {
        if ( tokens.removeFirst().getCategory(token).equals("NUM") ) {
            if ( tokens.removeFirst().getCategory(token).equals("ID") ) param_prime();
        }
    }

    // param_prime -> "[" "]" | empty
    public void param_prime() {
        if ( tokens.removeFirst().equals("[") ) {
            if ( tokens.removeFirst().equals("]") ) return;
        } else return;
    }

    // compound_statement -> "{" local-declarations statement-list "}"
    public void compound_statement() {
         if ( tokens.removeFirst().equals("{") ) {
             local_declarations();
             statement_list();
             if ( tokens.removeFirst().equals("}") ) return;
         }
    }

    // local-declarations -> var-declaration local-declarations | empty
    public void local_declarations() {
        if ( tokens.removeFirst().equals("") ) return;
        else {
            var_declaration();
            local_declarations();
        }
    }

    // statement_list -> statement statement_list | empty
    public void statement_list() {
        if ( tokens.removeFirst().equals("") ) return;
        else {
            statement();
            statement_list();
        }
    }

    // statement -> expression-stmt | { local-declarations statement-list }
    // | selection-stmt | iteration-stmt | return-stmt
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
    public void selection_statement_prime() {
        if ( tokens.removeFirst().equals("") ) return;
        else if ( tokens.removeFirst().equals("else") ) return;
    }

    //iteration-statement -> "while" "(" expression ")" statement
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
    public void expression() {
        if ( var() ) {
            if ( tokens.removeFirst().equals("=") ) { expression(); }
        } else simple_expression();
    }

    // var -> "ID" var_prime
    public boolean var() {
        if ( tokens.removeFirst().getCategory(token).equals("ID") ) {
            var_prime();
            return true;
        }
        isAccept = false;
        return false;
    }

    // var_prime -> empty | "[" expression "]"
    public boolean var_prime() {
        if ( tokens.removeFirst().equals("[") ) {
            expression();
            if ( tokens.removeFirst().equals("]") ) { return true; }
        } else if ( tokens.removeFirst().equals("") ) { return true; }
        isAccept = false;
        return false;
    }

    // simple-expression -> additive expression relop additive-expression | additive-expression
    public void simple_expression() {
        additive_expression();
        if ( relop() ) { additive_expression(); }
        else { additive_expression(); }
    }

    // relop -> <= | >= | == | != | > | <
    public boolean relop() {
        if ( tokens.removeFirst().equals("<=") || tokens.removeFirst().equals(">=") ||
                tokens.removeFirst().equals("==") || tokens.removeFirst().equals("!=") ||
                tokens.removeFirst().equals(">") || tokens.removeFirst().equals("<")
        ) { return true; }
        isAccept = false;
        return false;
    }

    // additive_expression -> term additive_expression_prime
    public void additive_expression() {
        term();
        additive_expression_prime();
    }

    // additive_expression_prime -> addop term additive_expression_prime | empty
    public void additive_expression_prime() {
        if (tokens.removeFirst().equals("")) { return; }
        else {
            addop();
            term();
            additive_expression_prime();
        }
    }

    // addop -> + | -
    public void addop() {
        if ( tokens.removeFirst().equals("+") || tokens.removeFirst().equals("=") ) { return; }
    }

    // term -> factor term_prime
    public void term() {
        factor();
        term_prime();
    }

    // term_prime -> mulop factor term_prime | empty
    public void term_prime() {
        if ( tokens.removeFirst().equals("") ) { return; }
        else {
            mulop();
            factor();
            term_prime();
        }
    }

    // mulop -> * | /
    public boolean mulop() {
        return tokens.removeFirst().equals("*") || tokens.removeFirst().equals("/");
    }

    //factor -> ( expression ) || ID var' || call || NUM
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
    public void call() {
        if ( tokens.removeFirst().getCategory(token).equals("ID") ) {
            if ( tokens.removeFirst().equals("(") ) {
                args();
                if ( tokens.removeFirst().equals(")") ) { return; }
            }
        }
    }

    // args -> args-list | empty
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
    public void arg_list_prime() {
        if ( tokens.removeFirst().equals("") ) return;
        else if ( tokens.removeFirst().equals(",") ) {
            expression();
            arg_list_prime();
        }
    }

} // Parser
