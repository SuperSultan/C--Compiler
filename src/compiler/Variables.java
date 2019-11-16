package compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;

public class Variables {

    private String type;
    private String id;
    private String arr;
    private Map<String,LinkedList<String>> symbols;
    private LinkedList<Map<String,LinkedList<String>>> list;

    Variables() {
        this.type = null;
        this.id = null;
        this.arr = null;
        symbols = new HashMap<>(); // global scope
        list = new LinkedList<>(); // global scope
        list.add(symbols);
    }

    public void setType(String rT) {
        this.type = rT;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIsArray(String arr) {
        this.arr = arr;
    }

    public String getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    public String getIsArray() {
        return this.arr;
    }

    public int getScopeSize() {
        return this.list.size();
    }

    public LinkedList<Map<String,LinkedList<String>>> getList() {
        return this.list;
    }

    public Map<String,LinkedList<String>> getSymbols() {
        return this.symbols;
    }

    public Variables get() {
        return this;
    }

    public void reject() {
        System.out.println("REJECT");
        System.exit(0);
    }

    public void put(String type, String id, String isArray) {
        if ( symbols.containsValue(id) ) {
            System.out.println("Error: " + id + " was already defined");
            reject();
        }
        if ( type.equals("void") ) {
            System.out.println("Error: variables cannot be of type void!");
            reject();
        } else {
            LinkedList<String> values = new LinkedList<>();
            values.addFirst(id);
            values.addLast(isArray);
            symbols.put(type, values);
            System.out.println("Added " + type + " " + id + " to variable symbol table. Is array?: " + isArray);
            System.out.println("Number of variables in current scope: " + symbols.size());
        }
        this.type = null;
        this.id = null;
        this.arr = null;
    }

    public void createNewScope() {
        Map<String,LinkedList<String>> new_scope = new HashMap<>();
        list.add(new_scope);
        System.out.println("Created new variable scope. Current variable scope size: " + this.list.size());
    }

    public void deleteScope() {
        if ( !list.isEmpty() ) {
            list.getFirst().clear();
            list.remove();
            System.out.println("Deleted variable scope. Current variable scope size: " + list.size());
        } else {
            System.out.println("AFNAN WHY YOU ARE TRYING TO DELETE VARIABLE SCOPE EVEN THOUGH IT'S SIZE 0?");
        }
        this.variableSymbolTableTest();
    }

    public boolean checkArrayIndexIsNumber(String str) {
        if ( !str.matches("\\d+") ) {
            System.out.println("Error: Array index " + str + " is not an integer!");
            reject();
        }
        return true;
    }

    public void variableSymbolTableTest() {
        if ( list.size() == 0 ) System.out.println("Empty variableSymbolTable");
            for(Map.Entry<String,LinkedList<String>> entry : symbols.entrySet() ) {
                String key = entry.getKey();
                String id = entry.getValue().getFirst();
                String isArray = entry.getValue().getLast();
                //System.out.println("Type: " + key + " Id: " + id + " isArray? : " + isArray + "SCOPE LEVEL: " + this.list.size());
            }
    }

    public void testForNullValues() {
        for(Map.Entry<String,LinkedList<String>> entry : symbols.entrySet() ) {
            if ( symbols.containsValue(null) ) {
                System.out.println("ERROR: " + entry.getKey() + " " + entry.getValue() + " has null value(s)");
                reject();
            }
        }
    }

}