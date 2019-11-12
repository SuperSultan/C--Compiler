package compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;

public class Variable {
    private String varIdentifier;
    private String dataType;
    private Map<String,String> table;
    private LinkedList<Map<String,String>> list;

    Variable() {
        this.varIdentifier = null;
        this.dataType = null;
        table = new HashMap<>(); // global scope
        list = new LinkedList<>(); // global scope
        list.add(table);
    }

    public void setVariableIdentifier(String id) { this.varIdentifier = id; }
    public void setVariableType(String dT) { this.dataType = dT; }
    public String getVariableIdentifier() { return this.varIdentifier; }
    public String getVariableType() { return this.dataType; }
    public int getVariableScopeSize() { return this.list.size(); }

    public void reject() {
        System.out.println("REJECT");
        System.exit(0);
    }

    public void put(String identifier, String keyword) {
        checkDuplicates(identifier, keyword);
        if (keyword.equals("void") ) {
            System.out.println("Error, identifiers cannot be void!");
            reject();
        } else {
            table.put(identifier,keyword);
            System.out.println("Added " + identifier + " " + keyword + " to variable symbol table");
            System.out.println("Number of variables in current scope: " + this.table.size());
        }
        this.varIdentifier = null;
        this.dataType = null;
    }

    public void createNewScope() {
        Map<String,String> new_scope = new HashMap<>();
        list.add(new_scope);
        System.out.println("Created new variable scope. Current variable scope size: " + list.size());
    }

    public void removeScope() {
        if ( !list.isEmpty() ) {
            list.getFirst().clear();
            list.remove();
            System.out.println("Deleted variable scope. Current variable scope size: " + list.size());
        } else if ( list.isEmpty() ) {
            System.out.println("Error: removed scope despite being size 0");
        }
        this.variableSymbolTableTest();
    }

    public void verifyVariableScope() {
        for(Map.Entry<String,String> entry : table.entrySet() ) {
            if ( table.containsValue(null) ) {
                System.out.println("Error: Referencing variable with null value (null pointer exception");
                reject();
            }
        }
    }

    private void variableSymbolTableTest() {
        if ( list.size() == 0 ) System.out.println("Empty variableSymbolTable");
        for(int i=0; i<list.size(); i++) {
            for(Map.Entry<String,String> entry : table.entrySet() ) {
                String variable = entry.getKey();
                String type = entry.getValue();
                System.out.println("IDENTIFIER " + variable + " TYPE: " + type + " SCOPE_LEVEL: " + list.indexOf(table));
            }
        }
    }

    public void checkDuplicates(String identifier, String keyword) {
        for(Map.Entry<String,String> entry: table.entrySet() ) {
            if ( entry.getKey().equals(identifier) ) {
                System.out.println("Error: " + identifier + " was already defined");
            }
        }
    }

}