package compiler;

import java.util.List;

public class Node {

    private String name;
    private Node parent;
    private List<Node> children;

    Node(String name) {
        this.name = name;
        switch(name) {
            case "program":
                break;
            case "declaration_list":
                break;
            case "declaration_list_prime":
                break;
            case "declaration":
                break;
            case "declaration_prime":
                break;
            case "fun_declaration":
                break;
            case "var_declaration":
                break;
            case "var_declaration_prime":
                break;
            case "type_specifier":
                break;
            case "params":
                break;
            case "params_prime":
                break;
            case "param_list_prime":
                break;
            case "param_prime":
                break;
            case "compound_statement":
                break;
            case "local_declarations":
                break;
            case "statement_list":
                break;
            case "statement":
                break;
            case "expression_statement":
                break;
            case "selection_statement":
                break;
            case "selection_statement_prime":
                break;
            case "iteration_statement":
                break;
            case "return_statement":
                break;
            case "return_statement_prime":
                break;
            case "expression":
                break;
            case "expression_prime":
                break;
            case "expression_prime_prime":
                break;
            case "simple_expression_prime":
                break;
            case "relop":
                break;
            case "additive_expression":
                break;
            case "additive_expression_prime":
                break;
            case "addop":
                break;
            case "term":
                break;
            case "term_prime":
                break;
            case "mulop":
                break;
            case "factor":
                break;
            case "factor_prime":
                break;
            case "args":
                break;
            case "arg_list":
                break;
            case "arg_list_prime":
                break;
            default: System.out.println("Error, unrecognized node type!");
        }
    }

    public void isValid(Node node) {
        // a node is valid if it's children are valid
    }

    public void validateType(Node node) {

    }

}
