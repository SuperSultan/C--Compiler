package compiler;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;

public class FunctionScope {
    private String identifier;
    private String dataType;
    private LinkedList<Map<String,String>> symbolTable;
    private Map<String,String> symbol;

    FunctionScope() {
        this.identifier = null;
        this.dataType = null;
        this.symbolTable = new LinkedList<Map<String,String>>();
        this.symbol = new HashMap<String, String>();
    }

    public void setIdentifier(String id) {
        this.identifier = id;
    }

    public String getIdentifier() { return this.identifier; }

    public void setDataType(String dT) {
        this.dataType = dT;
    }

    public String getDataType() { return this.dataType; }

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

    public void verifyIntFunctions() {
        for(Map.Entry<String,String> entry : symbol.entrySet() ) {
            if ( entry.getKey().equals("empty") && entry.getValue().equals("return") && symbol.containsValue("int") ) {
                System.out.println("Error: int function returning no value");
                reject();
            }
            if ( !entry.getKey().equals("empty") && entry.getValue().equals("return") && symbol.containsValue("void") ) {
                System.out.println("Error: void function returns a value!");
                reject();
            }
            if ( !symbol.containsValue("return") && symbol.containsValue("int") ) { //TODO this is causing issues when void return type and no return
                System.out.println("Error: int function with no return!");
                reject();
            }
        }
    }


    public void put(String identifier, String keyword) {
        if (symbol.containsKey(identifier)) {
            System.out.println("Error: identifier " + identifier + " already defined!");
            reject();
        } else {
            symbol.put(identifier, keyword);
        }
    }

}
