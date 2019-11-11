package compiler;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;

public class Function {
    private String funIdentifier;
    private String dataType;
    private LinkedList<Map<String,String>> symbolTable;
    private Map<String,String> symbol;

    Function() {
        this.funIdentifier = null;
        this.dataType = null;
        this.symbolTable = new LinkedList<Map<String,String>>();
        this.symbol = new HashMap<String, String>();
    }

    public void setFunctionIdentifier(String id) {
        this.funIdentifier = id;
    }
    public void setFunctionType(String dT) {
        this.dataType = dT;
    }
    public String getFunctionType() { return this.dataType; }
    public String getFunctionIdentifier() { return this.funIdentifier; }

    public void reject() {
        System.out.println("REJECT");
        System.exit(0);
    }

    public void functionSymbolTableTest() {
        if ( symbolTable.size() == 0) System.out.println("Empty functionSymbolTable");
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
        System.out.println("Created new function scope");
    }

    public void removeScope() {
        if ( !symbolTable.isEmpty() ) {
            symbolTable.remove();
        }
        System.out.println("Deleted function scope");
        this.functionSymbolTableTest();
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
