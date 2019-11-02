package compiler;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private String name;
    private int scope;
    private Node parent;
    private Node child;
    private Token t;
    private List<String> childNodes = new ArrayList<>();
    private List<String> childTokens = new ArrayList<>();

    Node(String name) {
        this.name = name;
        this.scope = 0;
        this.parent = null;
        this.child = null;
        this.t = null;

        switch(name) {
            case "program": // -> declaration-list FIRST: int, void FOLLOWS: $
                break;
            case "declaration_list": // -> declaration declaration_list_prime FIRSTS: int void empty FOLLOWS: $
                break;
            case "declaration_list_prime": // -> declaration declaration-list_prime | empty FIRSTS: int void ϵ FOLLOWS: $
                break;
            case "declaration": // -> type-specifier ID declaration_prime FIRSTS: int void FOLLOWS: $ int void
                break;
            case "declaration_prime": // -> var-declaration_prime | fun-declaration FIRSTS: ( ; [ FOLLOWS: int void $
                break;
            case "fun_declaration": // -> ( params ) compound-stmt FIRSTS: ( FOLLOWS: int void $
                break;
            case "var_declaration": // -> type-specifier ID var-declaration_prime FIRSTS: int void FOLLOWS: int void
                break;
            case "var_declaration_prime": // ->  ; | [ NUM ] ; FIRSTS: ; [ FOLLOWS: int void $
                break;
            case "type_specifier": // -> int | void FIRSTS: int void FOLLOWS: ID
                break;
            case "params": // -> int ID param_prime param-list_prime | void params_prime FIRSTS: int void FOLLOWS: )
                break;
            case "params_prime": // -> ID param_prime param-list_prime | empty FIRSTS: ID empty FOLLOWS: )
                break;
            case "param_list_prime": // -> , type-specifier ID param_prime param-list_prime | empty FIRSTS: , empty FOLLOWS: )
                break;
            case "param_prime": // -> [ ] | empty FIRSTS: [ empty FOLLOWS: , )
                break;
            case "compound_statement": // -> { local-declarations statement-list } FIRSTS: { FOLLOWS: }
                //if ( parent.equals("param_list_prime") ) {
                //    scope = 0;
                //}
                break;
            case "local_declarations": // -> var-declaration local-declarations | empty FIRSTS: int void empty FOLLOWS: ( ; ID NUM if return while { }
                break;
            case "statement_list": // -> statement statement-list | empty FIRSTS NUM ID ( ; { if while return empty FOLLOWS: }
                break;
            case "statement": // -> expression-stmt | compound-stmt | selection-stmt | iteration-stmt | return-stmt FIRSTS: NUM ID ( ; { if while return FOLLOWS: NUM ID ( ; { if while return }
                break;
            case "expression_statement": // -> expression ; | ; FIRSTS: ( ; ID NUM FOLLOWS: ( ; ID NUM additive else if return while { }
                break;
            case "selection_statement": // -> if ( expression ) statement selection-stmt_prime FIRSTS: if FOLLOWS: ( ; ID NUM if return while { }
                break;
            case "selection_statement_prime": // -> empty | else statement FIRSTS: else empty FOLLOWS: ( ; ID NUM if return while { }
                break;
            case "iteration_statement": // -> while ( expression ) statement FIRSTS: while FOLLOWS: ( ; ID NUM additive else if return while { }
                break;
            case "return_statement": // -> return return-stmt_prime FIRSTS: return FOLLOWS: ( ; ID NUM additive else if return while { }
                break;
            case "return_statement_prime": // -> ; | expression ; FIRSTS: ; NUM ID ( FOLLOWS: NUM ID ( ; { if while return }
                break;
            case "expression": // ->  NUM term_prime additive-expression_prime simple-expression_prime | ( expression ) term_prime additive-expression_prime simple-expression_prime | ID expression_prime FIRSTS: NUM ( ID FOLLOWS: , ) ; ]
                break;
            case "expression_prime": // -> = expression | [ expression ] expression_prime_prime | term_prime additive-expression_prime simple-expression_prime  | ( args ) term_prime additive-expression_prime simple-expression_prime FIRSTS = [ * / + - ( <= < > >= == != empty FOLLOWS , ) ; ]
                break;
            case "expression_prime_prime": // -> = expression | term_prime additive-expression_prime simple-expression_prime FIRST: = * / + - <= < > >= == != empty FOLLOWS: , ) ; ]
                break;
            case "simple_expression_prime": // -> relop additive-expression | empty FIRSTS: <= < > >= == != empty FOLLOWS: , ) ; ]
                break;
            case "relop": // -> <= | >= | == | != | > | < FIRSTS: != < <= == > >= FOLLOWS: ID NUM (
                break;
            case "additive_expression": // -> term additive-expression_prime FIRSTS: ID NUM ( FOLLOWS: , ) ; ]
                break;
            case "additive_expression_prime": // -> addop term additive-expression_prime | empty FIRSTS: + - empty FOLLOWS: <= < > >= == != , ) ; ] NUM ( ID
                break;
            case "addop": // -> + | - FIRSTS: + - FOLLOWS: NUM ID (
                break;
            case "term": // -> factor term_prime FIRSTS: NUM ID ( FOLLOWS: + - <= < > >= == != , ) ; ] NUM ( ID
                break;
            case "term_prime": // -> mulop factor term_prime | empty FIRSTS: * / empty FOLLOWS: + - <= < > >= == != , ) ; ] NUM ( ID
                break;
            case "mulop": // -> * | / FIRSTS: * / FOLLOWS: ( ID NUM
                break;
            case "factor": // -> ( expression ) | ID factor_prime | NUM FIRSTS: NUM ID ( FOLLOWS: * / + - <= < > >= == != , ) ; ] NUM ( ID
                break;
            case "factor_prime": // -> [ expression ] | ( args ) | empty FIRSTS: [ ( empty FOLLOWS: NUM ID * / + - <= < > >= == != , ) ; ] (
                break;
            case "args": // -> arg-list | empty FIRSTS: NUM ID ( FOLLOWS: )
                break;
            case "arg_list": // -> expression arg-list_prime FIRSTS: NUM ID ( FOLLOWS: )
                break;
            case "arg_list_prime": // -> , expression arg-list_prime | empty FIRSTS: , empty FOLLOWS: )
                break;
            default: System.out.println("Error, unrecognized node type!");
        }
    }

    public String getName() {
        return this.name;
    }

    public List<String> getChildTokens() {
        return childTokens;
    }

    public List<String> getChildNodes() {
        return childNodes;
    }

    public int getScope() {
        return scope;
    }

    public void addChildToken(String s) {
        childTokens.add(s);
    }

    public void addChildNode(String child) {
        childNodes.add(child);
    }

    public void isValid(Node node) {
        // a node is valid if it's children are valid
    }

    public void validateType(Node node) {

    }

}
