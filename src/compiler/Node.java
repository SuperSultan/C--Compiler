package compiler;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private String name;
    private List<Token> children = new ArrayList<>(); // Children are stored internally

    public void setChild(Token childName) {
        this.children.add(childName);
    }

    Node(String name) { // Add the children AFTER calling the constructor
        this.name = name;
    }
}
