package compiler;

import java.util.*;
import java.util.Map.Entry;

public class Functions {
    private String type;
    private String id;
    private String returnType;
    private Map<String, LinkedList<String>> symbols;
    private LinkedList<Map<String, LinkedList<String>>> list;
    private int scopeSize;
    private boolean seenMain;
    private boolean isMainLast;
    private Variables variables;

    private LinkedList<Map<String,LinkedList<String>>> variableList;
    //private Map<String,LinkedList<String>> variableSymbols;

    Functions(Variables variables) {
        this.variables = new Variables();
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
        if ( list.size() == 0) System.out.println("Empty functionSymbolTable");
        for(Map.Entry<String,LinkedList<String>> entry : symbols.entrySet() ) {
            String type = entry.getKey();
            List<String> values = entry.getValue();
            System.out.println("FUNCTION TYPE " + type + " VALUES: " + values.toString());
        }
    }

    public void createNewScope() {
        Map<String,LinkedList<String>> new_scope = new HashMap<>();
        list.add(new_scope);
        ++scopeSize;
        System.out.println("Created new function scope. Current function scope size: " + this.scopeSize);
    }

    public void removeScope() {
        if ( !list.isEmpty() ) {
            --scopeSize;
            System.out.println("Deleted function scope Current function scope size: " + this.scopeSize);
        }
        this.functionSymbolTableTest();
    }

    public void checkForMain() {
        for(Entry<String, LinkedList<String>> entry : symbols.entrySet() ) {
            String theKey = entry.getKey();
            LinkedList<String> values = entry.getValue();
            String id = values.getFirst();
            String rT = values.getLast();
            if ( this.type.equals("void") && this.id.equals("main") ) { seenMain = true; }
        }
        if ( !seenMain ) {
            System.out.println("Error: Program missing void main(void)");
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
                    System.out.println("Error: void main(void) is not the last function!");
                    reject();
                }
            }
        } // else reject
    }

    public void put(String key, String id, String rT) {
        for(Entry<String,LinkedList<String>> entry : symbols.entrySet() ) {
            if (entry.getValue().contains(id)) {
                System.out.println("Error: " + id + " is already defined!");
                reject();
            }
        }
        LinkedList<String> functionData = new LinkedList<>();
        functionData.add(id);
        functionData.add(rT);
        symbols.put(key,functionData);
        checkReturnTypes();
        System.out.println("Added " + key + " " + id + " " + rT + " to function symbol table!");
        System.out.println("Functions in current scope: " + this.symbols.size());

        this.setType(null);
        this.setId(null);
        this.setReturn(null);
    }

    public void checkReturnTypes() {

        //TODO void hi a is being put into the symbol table erroneously

        for (Map.Entry<String, LinkedList<String>> function : symbols.entrySet()) {
            /*
            Variables vars = variables.get();
            LinkedList<Map<String,LinkedList<String>>> variableList = vars.getList();
            Map<String,LinkedList<String>> variableSymbols = vars.getSymbols();

            for(int i=0; i<variableList.size(); i++) {
                for(Map.Entry<String,LinkedList<String>> variableTypesAndIds : variableSymbols.entrySet() ) {
                    String key = variableTypesAndIds.getKey();
                    LinkedList<String> values = variableTypesAndIds.getValue();
                    String id = values.getFirst();
                    if ( ! variableTypesAndIds.getValue().contains(id) ) {
                        System.out.println("Error: " + id + " was never declared in the variable symbol table!");
                    }
                }
            }
            */

            if ( this.type.equals("int") &&  ( !this.returnType.matches("^[a-zA-Z0-9]*$") || this.returnType.equals("void") )) {
                System.out.println("Error: int function " + id + " should return a value!"); // checks if rT is a string or if it is void
                reject();
            }
            if (this.type.equals("int") &&  ( function.getValue().getLast() == null) ) {
                System.out.println("Error: int function " + id + " cannot return void!");
            }
            if (this.type.equals("void") && !this.returnType.equals("void")) {
                System.out.println("Error: void function " + id + " cannot return a value!");
                reject();
            }
        }
    }

}
