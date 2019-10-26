package compiler;

import java.util.List;

public class Node {

    private String nodeName;
    private Node parent;
    private List<Node> children;

    Node() {
        this.nodeName = nodeName;
        this.parent = null;
        this.children = null;
    }

    public void isValid(Node node) {
        // a node is valid if it's children are valid
    }

    public void validateType(Node node) {

    }

}
