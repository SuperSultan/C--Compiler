package compiler;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;

public class Function {
    private String funIdentifier;
    private String dataType;
    private LinkedList<Map<String,String>> list;
    private Map<String,String> table;
    private boolean seenMain;

    Function() {
        this.funIdentifier = null;
        this.dataType = null;
        this.list = new LinkedList<Map<String,String>>();
        this.table = new HashMap<String, String>();
        list.add(table);
        seenMain = false;
    }

    public void setFunctionIdentifier(String id) {
        this.funIdentifier = id;
    }
    public String getFunctionType() { return this.dataType; }
    public String getFunctionIdentifier() { return this.funIdentifier; }
    public int getFunctionScopeSize() { return this.table.size(); }

    public void setFunctionType(String lexeme) {
        this.dataType = lexeme;
    }

    public void reject() {
        System.out.println("REJECT");
        System.exit(0);
    }

    public void functionSymbolTableTest() {
      //  if ( list.size() == 0) System.out.println("Empty functionSymbolTable");
        for(int i=0; i<list.size(); i++) {
            for(Map.Entry<String,String> entry : table.entrySet() ) {
                String variable = entry.getKey();
                String type = entry.getValue();
              //  System.out.println("IDENTIFIER " + variable + " TYPE: " + type + " SCOPE_LEVEL: " + list.indexOf(table));
            }
        }
    }

    public void createNewScope() {
        Map<String,String> new_scope = new HashMap<>();
        list.add(new_scope);
   //     System.out.println("Created new function scope. Current function scope size: " + this.list.size());
    }

    public void removeScope() {
        if ( !list.isEmpty() ) {
            list.getFirst().clear();
            list.remove();
    //        System.out.println("Deleted function scope Current function scope size: " + this.list.size());
        }
        this.functionSymbolTableTest();
    }

    public void verifyFunctions() {

        if ( ! funIdentifier.equals("main") ) {
    //        System.out.println("Error: void main(void) { .. } is not the last function!");
            reject();
        }

        if ( !seenMain ) {
    //        System.out.println("Error: THERE IS NO MAIN FUNCTION");
            reject();
        }

        for(Map.Entry<String,String> entry : table.entrySet() ) {
            if ( !this.table.containsKey("main") && !this.table.containsValue("void") ) {
     //           System.out.println("Error: program missing main function!");
                reject();
            }
            if ( entry.getKey().equals("empty") && entry.getValue().equals("return") && table.containsValue("int") ) {
    //            System.out.println("Error: int function returning no value");
                reject();
            }
            if ( !entry.getKey().equals("empty") && entry.getValue().equals("return") && table.containsValue("void") ) {
    //            System.out.println("Error: void function returns a value!");
                reject();
            }
            if ( !table.containsValue("return") && table.containsValue("int") ) { //TODO this is causing issues when void return type and no return
      //          System.out.println("Error: int function with no return!");
                reject();
            }
        }
    }

    public void put(String identifier, String keyword) {
        if (table.containsKey(identifier)) {
     //       System.out.println("Error: identifier " + identifier + " already defined!");
            reject();
        } else {
            table.put(identifier, keyword);
            if ( identifier.equals("main") && keyword.equals("void") ) seenMain = true;
   //         System.out.println("Added " + identifier + " " + keyword + " to function symbol table");
   //         System.out.println("Number of functions in current scope: " + this.table.size());
        }
        this.setFunctionIdentifier(null);
        this.setFunctionType(null);
    }

}
