package compiler;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private String nodeName;
    private int scope;
    private Node parent;
    private Node child;
    private Token t;
    private List<String> childNodes = new ArrayList<>();
    private List<String> childTokens = new ArrayList<>();

    Node(String name) {
        this.nodeName = name;
        this.scope = 0;
        this.parent = null;
        this.child = null;
        this.t = null;
    }

    public String getNodeName() {
        return this.nodeName;
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
