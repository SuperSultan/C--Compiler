package compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;

public class Variable {
    private String varIdentifier;
    private String dataType;
    private Map<String,String> symbol;
    private LinkedList<Map<String,String>> symbolTable;

    Variable() {
        this.varIdentifier = null;
        this.dataType = null;
        symbol = new HashMap<>();
        symbolTable = new LinkedList<>();
        symbolTable.add(symbol);
    }

    public void setVariableIdentifier(String id) {
        this.varIdentifier = id;
    }

    public void setVariableType(String dT) {
        this.dataType = dT;
    }

    public String getVariableIdentifier() { return this.varIdentifier; }
    public String getVariableType() { return this.dataType; }

    public void reject() {
        System.out.println("REJECT");
        System.exit(0);
    }

    private void variableSymbolTableTest() {
        if ( symbolTable.size() == 0 ) System.out.println("Empty variableSymbolTable");
        for(int i=0; i<symbolTable.size(); i++) {
            for(Map.Entry<String,String> entry : symbol.entrySet() ) {
                String variable = entry.getKey();
                String type = entry.getValue();
                System.out.println("IDENTIFIER " + variable + " TYPE: " + type + " SCOPE_LEVEL: " + symbolTable.indexOf(symbol));
            }
        }
    }

    public void createNewScope() {
        Map<String,String> new_scope = new HashMap<>();
        symbolTable.add(new_scope);
    }

    public void removeScope() {
        if ( !symbolTable.isEmpty() ) {
            symbolTable.remove();
        }
        System.out.println("Deleted variable scope");
        this.variableSymbolTableTest();
    }

    public void verifyVariableScope() {
        for(Map.Entry<String,String> entry : symbol.entrySet() ) {
            if ( symbol.containsValue(null) ) {
                System.out.println("Error: Referencing variable with null value (null pointer exception");
                reject();
            }
        }
    }

    public void checkDuplicates(String identifier, String keyword) {
        for(Map.Entry<String,String> entry: symbol.entrySet() ) {
            if ( entry.getKey().equals(identifier) ) {
                System.out.println("Error: " + identifier + " was already defined");
            }
        }
    }

    public void put(String identifier, String keyword) {

        checkDuplicates(identifier, keyword);

        if (keyword.equals("void") ) {
            System.out.println("Error, identifiers cannot be void!");
            reject();
        } else {
            symbol.put(identifier,keyword);
        }
    }

}