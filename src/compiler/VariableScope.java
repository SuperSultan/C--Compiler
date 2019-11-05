package compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;

public class VariableScope {
    String varIdentifier;
    String dataType;
    Map<String,String> symbol;
    LinkedList<Map<String,String>> symbolTable;

    VariableScope() {
        this.varIdentifier = null;
        this.dataType = null;
        symbol = new HashMap<>();
        symbolTable = new LinkedList<>();
        symbolTable.add(symbol);
    }

    public void setIdentifier(String id) {
        this.varIdentifier = id;
    }

    public void setDataType(String dT) {
        this.dataType = dT;
    }

    public void reject() {
        System.out.println("REJECT");
        System.exit(0);
    }

    public void symbolTableTest() {
        System.out.println();
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
    }

    public void verifyVariableScope() {
        for(Map.Entry<String,String> entry : symbol.entrySet() ) {
            if ( symbol.containsValue(null) ) {
                System.out.println("Error: Referencing variable with null value (null pointer exception");
                reject();
            }
        }
    }

    public void put(String identifier, String keyword) {
        if (symbol.containsKey(identifier)) {
            System.out.println("Error: " + identifier + " already defined!");
            reject();
        }
        if (keyword.equals("void") ) {
            System.out.println("Error, identifiers cannot be void!");
            reject();
        } else {
            symbol.put(identifier,keyword);
        }
    }

}
