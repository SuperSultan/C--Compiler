package compiler;

import java.util.HashMap;

public class Symbol {
    String identifier;
    String dataType;
    HashMap<String,String> symbol;

    Symbol() {
        this.identifier = null;
        this.dataType = null;
    }

    public void setIdentifier(String id) {
        this.identifier = id;
    }

    public void setDataType(String dT) {
        this.dataType = dT;
    }

}
