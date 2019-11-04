package compiler;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;

public class FunctionScope extends HashMap {
    String funIdentifier;
    String dataType;
    LinkedList<Map<String,String>> symbolTable;
    Map<String,String> symbol;

    FunctionScope() {
        this.funIdentifier = null;
        this.dataType = null;
        this.symbolTable = new LinkedList<Map<String,String>>();
        this.symbol = new HashMap<String, String>();
    }

    public void setIdentifier(String id) {
        this.funIdentifier = id;
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

    public void verifyIntFunctions() {
        //for(int i=0;i<symbolTable.size(); i++) {
         //   for(Map.Entry<String,String> entry : this.entrySet() ) {
         //       if ( entry.getKey().equals("empty") && entry.getValue().equals("return") && symbol.containsValue("int") ) {
          //          System.out.println("Error: int function returning no value");
             //   }
            }
        //}
    //}


    @Override
    public Object put(Object identifier, Object keyword) {
        if (this.containsKey(identifier)) {
            System.out.println("Error: identifier " + identifier + " already defined!");
            reject();
            return null;
        }
       // if ( identifier.equals("empty") && keyword.equals("return") && this.containsKey("int") ) {
       //     System.out.println("Error: int function without a return!");
        //    reject();
        //    return null;
        else {
            return super.put(identifier, keyword);
        }
    }

}
