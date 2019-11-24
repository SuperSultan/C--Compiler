package compiler;

public class CodeGeneration {
    private int index;
    private String operation;
    private String operand1;
    private String operand2;
    private String variableResult;
    private String functionResult;
    private String result;
    private String backpatch;
    private String tempResult;
    private boolean isbackpatch;
    private boolean isVariable;

    CodeGeneration() {
        this.index = 0;
        this.operation = null;
        this.operand1 = null;
        this.operand2 = null;
        this.backpatch = null;
        this.isbackpatch = false;
        this.isVariable = true;
        this.variableResult = null;
        this.functionResult = null;
    }

    public void setOperation(String op) {
        this.operation = op;
    }
    public void setOperand1(String operand1) {
        this.operand1 = operand1;
    }
    public void setOperand2(String operand2) {
        this.operand2 = operand2;
    }

    public void setTempResult(String res) {
        this.tempResult = res;
    }

    public void setVariableResult(String res) {
        this.variableResult = res;
    }

    public void setFunctionResult(String res) {
        this.functionResult = res;
    }

    public void saveResult(String res) {
        this.result = res;
    }
    public void setBackpatch(String bp) {
        this.backpatch = bp;
    }

    public void setIsbackpatch(Boolean isbp) {
        this.isbackpatch = isbp;
    }

    public String getOperation() {
        return this.operation;
    }

    public String getOperand1() {
        return this.operand1;
    }

    public String getOperand2() {
        return this.operand2;
    }

    public String getResult() {
        return this.result;
    }

    public String getBackpatch() {
        return this.backpatch;
    }

    public String getVariableResult() {
        return this.variableResult;
    }

    public void reset() {
        this.operation = null;
        this.operand1 = null;
        this.backpatch = null;
        this.isbackpatch = false;
        this.isVariable = true;
        this.variableResult = null;
        this.functionResult = null;
    }

    public void printQuadruple() {
        if ( this.operation == null || this.result == null ) {
            System.out.println("Ooops! Forgot so set something!");
            System.exit(0);
        }
        if ( this.variableResult != null ) {
            System.out.println(this.index++ + "\t" + this.operation + "\t" + this.operand1 + "\t" + this.operand2 + "\t" + this.variableResult);

        } else {
            System.out.println(this.index++ + "\t" + this.operation + "\t" + this.operand1 + "\t" + this.operand2 + "\t" + this.functionResult);
        }
        reset();
    }

}
