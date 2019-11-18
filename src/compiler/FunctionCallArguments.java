package compiler;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FunctionCallArguments {

    private Parameters parameters;
    private Functions functions;
    private String type;
    private String id;
    private String arr;
    private Map<String, LinkedList<String>> arguments;
    private LinkedList<Map<String,LinkedList<String>>> list;
    private int numArguments;

    FunctionCallArguments(Functions funcs, Parameters params) {
        this.functions = funcs;
        this.parameters = params;
        this.type = null;
        this.id = null;
        this.arr = null;
        this.arguments = null;
        this.list = null;
    }

    public void setType(String t) { this.type = t; }
    public void setId(String identifier) { this.id = identifier; }
    public void setArr(String isArray) { this.arr = isArray; }
    public void setNumberOfArguments(int num) { this.numArguments = num; }
    public String getType() { return this.type; }
    public String getId() { return this.id; }
    public String getArr() { return this.arr; }

    public void reject() {
        System.out.println("REJECT");
        System.exit(0);
    }

    public void checkNumArgumentsEqualsNumParameters() {
        int numParameters = parameters.getNumberOfParameters();
        if ( this.numArguments != numParameters ) {
            System.out.println("Error: The number of parameters, " + numParameters + " and number of arguments, " + this.numArguments + " do not match!");
            reject();
        }
    }

    public void checkType(String lexeme) {

    }

    public void createNewFunctionCallArguments() {
        Map<String, LinkedList<String>> new_arguments = new HashMap<>();
        list.add(new_arguments);
        ++numArguments; //TODO should we increment numArgs here or in our put() method?
        System.out.println("Created function call scope. Current total number of arguments: " + this.numArguments);
    }

    public void removeFunctionCallArguments() {
        if ( !list.isEmpty() ) {
            --numArguments;
            System.out.println("Removed function call from collection. Current number of function calls in collection: " + this.numArguments);
        }
        this.argumentsSymbolTableTest();
    }

    public void argumentsSymbolTableTest() {
        if ( list.size() == 0) System.out.println("Empty functionSymbolTable");
        for(Map.Entry<String,LinkedList<String>> entry : arguments.entrySet() ) {
            String type = entry.getKey();
            List<String> values = entry.getValue();
            System.out.println("ARGUMENT TYPE " + type + " VALUES: " + values.toString());
        }
    }

    public void put(String type, String id, String isArray) {

    }

    // TODO check if a function is declared during a function call

    // TODO check that parameters and arguments agree in number

    //TODO check that parameters and arguments agree in type


}
