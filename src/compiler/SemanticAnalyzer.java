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
        List<String> childTokens = current.getChildTokens();
        List<String> childNodes = current.getChildNodes();
        switch (nodeName) {
            case "program": // -> declaration-list FIRST: int, void FOLLOWS: $
                program(childNodes);
                break;
            case "declaration_list": // -> declaration declaration_list_prime FIRSTS: int void empty FOLLOWS: $
                //declaration_list(childNodes);
                break;
            case "declaration_list_prime": // -> declaration declaration-list_prime | empty FIRSTS: int void ϵ FOLLOWS: $
                //declaration_list_prime();
                break;
            case "declaration": // -> type-specifier ID declaration_prime FIRSTS: int void FOLLOWS: $ int void
              //  declaration();
                break;
            case "declaration_prime": // -> var-declaration_prime | fun-declaration FIRSTS: ( ; [ FOLLOWS: int void $
               // declaration_prime();
                break;
            case "fun_declaration": // -> ( params ) compound-stmt FIRSTS: ( FOLLOWS: int void $
               // fun_declaration();
                break;
            case "var_declaration": // -> type-specifier ID var-declaration_prime FIRSTS: int void FOLLOWS: int void
              //  var_declaration();
                break;
            case "var_declaration_prime": // ->  ; | [ NUM ] ; FIRSTS: ; [ FOLLOWS: int void $
               // var_declaration_prime();
                break;
            case "type_specifier": // -> int | void FIRSTS: int void FOLLOWS: ID
               // type_specifier();
                break;
            case "params": // -> int ID param_prime param-list_prime | void params_prime FIRSTS: int void FOLLOWS: )
               // params();
                break;
            case "params_prime": // -> ID param_prime param-list_prime | empty FIRSTS: ID empty FOLLOWS: )
              //  params_prime();
                break;
            case "param_list_prime": // -> , type-specifier ID param_prime param-list_prime | empty FIRSTS: , empty FOLLOWS: )
              //  param_list_prime();
                break;
            case "param_prime": // -> [ ] | empty FIRSTS: [ empty FOLLOWS: , )
              //  param_prime();
                break;
            case "compound_statement": // -> { local-declarations statement-list } FIRSTS: { FOLLOWS: }
              //  compound_statement();
                break;
            case "local_declarations": // -> var-declaration local-declarations | empty FIRSTS: int void empty FOLLOWS: ( ; ID NUM if return while { }
              //  local_declarations();
                break;
            case "statement_list": // -> statement statement-list | empty FIRSTS NUM ID ( ; { if while return empty FOLLOWS: }
              //  statement_list();
                break;
            case "statement": // -> expression-stmt | compound-stmt | selection-stmt | iteration-stmt | return-stmt FIRSTS: NUM ID ( ; { if while return FOLLOWS: NUM ID ( ; { if while return }
               // statement();
                break;
            case "expression_statement": // -> expression ; | ; FIRSTS: ( ; ID NUM FOLLOWS: ( ; ID NUM additive else if return while { }
             //   expression_statement();
                break;
            case "selection_statement": // -> if ( expression ) statement selection-stmt_prime FIRSTS: if FOLLOWS: ( ; ID NUM if return while { }
             //   selection_statement();
                break;
            case "selection_statement_prime": // -> empty | else statement FIRSTS: else empty FOLLOWS: ( ; ID NUM if return while { }
             //   selection_statement_prime();
                break;
            case "iteration_statement": // -> while ( expression ) statement FIRSTS: while FOLLOWS: ( ; ID NUM additive else if return while { }
              //  iteration_statement();
                break;
            case "return_statement": // -> return return-stmt_prime FIRSTS: return FOLLOWS: ( ; ID NUM additive else if return while { }
              //  return_statement();
                break;
            case "return_statement_prime": // -> ; | expression ; FIRSTS: ; NUM ID ( FOLLOWS: NUM ID ( ; { if while return }
              //  return_statement_prime();
                break;
            case "expression": // ->  NUM term_prime additive-expression_prime simple-expression_prime | ( expression ) term_prime additive-expression_prime simple-expression_prime | ID expression_prime FIRSTS: NUM ( ID FOLLOWS: , ) ; ]
               // expression();
                break;
            case "expression_prime": // -> = expression | [ expression ] expression_prime_prime | term_prime additive-expression_prime simple-expression_prime  | ( args ) term_prime additive-expression_prime simple-expression_prime FIRSTS = [ * / + - ( <= < > >= == != empty FOLLOWS , ) ; ]
               // expression_prime();
                break;
            case "expression_prime_prime": // -> = expression | term_prime additive-expression_prime simple-expression_prime FIRST: = * / + - <= < > >= == != empty FOLLOWS: , ) ; ]
              //  expression_prime_prime();
                break;
            case "simple_expression_prime": // -> relop additive-expression | empty FIRSTS: <= < > >= == != empty FOLLOWS: , ) ; ]
              //  simple_expression_prime();
                break;
            case "relop": // -> <= | >= | == | != | > | < FIRSTS: != < <= == > >= FOLLOWS: ID NUM (
               // relop();
                break;
            case "additive_expression": // -> term additive-expression_prime FIRSTS: ID NUM ( FOLLOWS: , ) ; ]
              //  additive_expression();
                break;
            case "additive_expression_prime": // -> addop term additive-expression_prime | empty FIRSTS: + - empty FOLLOWS: <= < > >= == != , ) ; ] NUM ( ID
              //  additive_expression_prime();
                break;
            case "addop": // -> + | - FIRSTS: + - FOLLOWS: NUM ID (
              //  addop();
                break;
            case "term": // -> factor term_prime FIRSTS: NUM ID ( FOLLOWS: + - <= < > >= == != , ) ; ] NUM ( ID
              //  term();
                break;
            case "term_prime": // -> mulop factor term_prime | empty FIRSTS: * / empty FOLLOWS: + - <= < > >= == != , ) ; ] NUM ( ID
              //  term_prime();
                break;
            case "mulop": // -> * | / FIRSTS: * / FOLLOWS: ( ID NUM
              //  mulop();
                break;
            case "factor": // -> ( expression ) | ID factor_prime | NUM FIRSTS: NUM ID ( FOLLOWS: * / + - <= < > >= == != , ) ; ] NUM ( ID
             //   factor();
                break;
            case "factor_prime": // -> [ expression ] | ( args ) | empty FIRSTS: [ ( empty FOLLOWS: NUM ID * / + - <= < > >= == != , ) ; ] (
             ///   factor_prime();
                break;
            case "args": // -> arg-list | empty FIRSTS: NUM ID ( FOLLOWS: )
             //   args();
                break;
            case "arg_list": // -> expression arg-list_prime FIRSTS: NUM ID ( FOLLOWS: )
            //    arg_list();
                break;
            case "arg_list_prime": // -> , expression arg-list_prime | empty FIRSTS: , empty FOLLOWS: )
            //    arg_list_prime();
                break;
            default:
                System.out.println("Error, unrecognized node type!");
        }
        nodes.removeFirst();
        return symbol;
    }

    public void program(List<String> childNodes) {
        // check if declaration_list is not null
     //   if ( nodeName.equals(""))
    }

    public void declaration() {}



    public List<HashMap<String, String>> createSymbolTable(Symbol s) {
        List<HashMap<String, String>> theSymbols = new LinkedList<>();
        while ( !nodes.isEmpty() ) {
            createSymbol(nodes.getFirst());
            nodes.removeFirst();
        }
        return theSymbols;
    }



}
