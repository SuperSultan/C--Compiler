package compiler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Parameters {

    private String type;
    private String id;
    private String arr;
    private Map<String,LinkedList<String>> symbols;
    private LinkedList<Map<String,LinkedList<String>>> list;
    private int numberOfParameters;

    Parameters() {
        this.type = null;
        this.id = null;
        this.arr = null;
        symbols = new HashMap<>();
        list = new LinkedList<>();
        list.add(symbols);
        this.numberOfParameters = 0;
    }

    public void setType(String type) { this.type = type; }
    public void setId(String id) { this.id = id; }
    public void setIsArray(String arr) { this.arr = arr; }
    public String getType() { return this.type; }
    public String getId() { return this.id; }
    public String getIsArray() { return this.arr; }
    public int getNumberOfParameters() { return this.numberOfParameters; }

    public void reject() {
        System.out.println("REJECT");
        System.exit(0);
    }

    public void put(String type) {
        if ( type.equals("void") ) {
            System.out.println("Added void parameter to symbol table");
            symbols.put(type, null);
        }
    }

    public void put(String type, String id, String arr) {
        if ( type.equals("void") ) {
            System.out.println("Error: void parameter with " + id + " identifier!");
            reject();
        }
        LinkedList<String> values = new LinkedList<>();
        values.addFirst(id);
        values.addLast(arr);
        symbols.put(type,values);
        ++numberOfParameters;
        System.out.println("Added " + type + " " + id + " " + " to parameter symbol table. isArray? " + arr);
        System.out.println("Number of parameters in parameter symbol table: " + symbols.size() );
        this.type = null;
        this.id = null;
        this.arr = null;
    }

    public void checkParamCount() {

    }

    public void checkParamTypes() { //TODO finish this
        for(int i=0; i<list.size(); i++) {
            for(Map.Entry<String,LinkedList<String>> params : symbols.entrySet() ) {

            }
        }
    }

}
