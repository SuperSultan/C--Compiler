package compiler;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SemanticAnalyzer {
    ArrayDeque<Node> nodes;
    Symbol symbol;
    List<HashMap<String, String>> theSymbols; // identifier, type

    SemanticAnalyzer(ArrayDeque<Node> parseTreeNodes) {
        this.nodes = parseTreeNodes;
    }

    public boolean isAccepted() {
        return true;
    }

    public Symbol createSymbol(Node n) {
        Symbol symbol = new Symbol();
        Node current = nodes.getFirst();
        String nodeName = current.getNodeName();
        switch (nodeName) {
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
            default:
                System.out.println("Error, unrecognized node type!");
        }
        return symbol;
    }

    public List<HashMap<String, String>> createSymbolTable(Symbol s) {
        List<HashMap<String, String>> theSymbols = new LinkedList<>();
        while ( !nodes.isEmpty() ) {
            createSymbol(nodes.getFirst());
            nodes.removeFirst();
        }
        return theSymbols;
    }



}
