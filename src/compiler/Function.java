package compiler;

import java.util.*;
import java.util.Map.Entry;

public class Function {
    private String type;
    private String id;
    private String returnType;
    private Map<String, LinkedList<String>> symbols;
    private LinkedList<Map<String, LinkedList<String>>> list;
    private int scopeSize;
    private boolean seenMain;
    private boolean isMainLast;

    Function() {
        this.type = null;
        this.id = null;
        this.returnType = null;
        this.list = new LinkedList<Map<String,LinkedList<String>>>();
        this.symbols = new HashMap<String,LinkedList<String>>();
        this.scopeSize = 0;
        list.add(symbols);
        seenMain = false;
        isMainLast = false;
    }

    public void setType(String dT) {
        this.type = dT;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setReturn(String rT) {
        this.returnType = rT;
    }
    public String getType() {
        return this.type;
    }
    public String getId() {
        return this.id;
    }
    public String getReturn() {
        return this.returnType;
    }
    public int getScopeSize() {
        return this.scopeSize;
    }

    public void reject() {
        System.out.println("REJECT");
        System.exit(0);
    }

    public void functionSymbolTableTest() {
        //if ( list.size() == 0) System.out.println("Empty functionSymbolTable");
        for(int i=0; i<list.size(); i++) {
            for(Map.Entry<String,LinkedList<String>> entry : symbols.entrySet() ) {
                String type = entry.getKey();
                List<String> values = entry.getValue();
         //       System.out.println("FUNCTION TYPE " + type + " VALUES: " + values.toString() + " SCOPE_LEVEL: " + list.indexOf(symbols));
            }
        }
    }

    public void createNewScope() {
        Map<String,LinkedList<String>> new_scope = new HashMap<>();
        list.add(new_scope);
        ++scopeSize;
       // System.out.println("Created new function scope. Current function scope size: " + this.scopeSize);
    }

    public void removeScope() {
        if ( !list.isEmpty() ) {
            //checkReturnTypes();
            //list.getFirst().clear();
            //list.remove();
            --scopeSize;
         //   System.out.println("Deleted function scope Current function scope size: " + this.scopeSize);
        }
        this.functionSymbolTableTest();
    }

    public void checkForMain() {
        for(Entry<String, LinkedList<String>> entry : symbols.entrySet() ) {
            String theKey = entry.getKey();
            LinkedList<String> values = entry.getValue();
            String id = values.getFirst();
            String rT = values.getLast();
            if ( theKey.equals("void") && id.equals("main") && rT.equals("void") ) { seenMain = true; }
        }
        if ( !seenMain ) {
          //  System.out.println("Error: Program missing void main(void)");
            reject();
        }
    }

    public void checkMainIsLast() {
        if ( seenMain ) {
            Map<String,LinkedList<String>> lastListItem = list.getLast();
            for(Map.Entry<String,LinkedList<String>> lastEntry : lastListItem.entrySet() ) {
                String key = lastEntry.getKey();
                LinkedList<String> values = lastEntry.getValue();
                String id = values.getFirst();
                String rT = values.getLast();
                if ( key.equals("void") && id.equals("main") && rT.equals("void") ) {
                    isMainLast = true;
                } else {
                 //   System.out.println("Error: void main(void) is not the last function!");
                    reject();
                }
            }
        }
    }

    public void put(String key, String id, String rT) {
        for(Entry<String,LinkedList<String>> entry : symbols.entrySet() ) {
            if (entry.getValue().contains(id)) {
           //     System.out.println("Error: " + id + " is already defined!");
                reject();
            }
        }
        if ( key.equals("int") && rT == null) {
         //   System.out.println("Error: int function" + id + " needs a return value!");
        }
        LinkedList<String> functionData = new LinkedList<>();
        functionData.add(id);
        functionData.add(rT);
        symbols.put(key,functionData);
        checkReturnTypes();
       // System.out.println("Added " + key + " " + id + " " + rT + " to function symbol table!");
       // System.out.println("Functions in current scope: " + this.symbols.size());

        this.setType(null);
        this.setId(null);
        this.setReturn(null);
    }


    public void checkReturnTypes() {
        for(int i=0; i<scopeSize; i++) {
            for (Map.Entry<String, LinkedList<String>> function : symbols.entrySet()) {
                String key = function.getKey();
                String id = function.getValue().getFirst();
                String rT = function.getValue().getLast();
                if (key.equals("int") && rT.equals("void")) {
                   // System.out.println("Error: int function " + id + "  cannot return void!");
                    reject();
                }
                if (key.equals("int") && ! rT.matches("^[a-zA-Z0-9]*$") ) { // checks returnType is alphanumeric
                   // System.out.println("Error: int function " + id + " should return a value!");
                    reject();//TODO check this is correct!
                }
                if (key.equals("int") &&  ( function.getValue().getLast() == null) ) {
                    //System.out.println("Error: int function " + id + " cannot return void!");
                }
                if (key.equals("void") && !rT.equals("void")) {
                  //  System.out.println("Error: void function " + id + " cannot return a value!");
                    reject();
                }
            }
        }
    }

}
