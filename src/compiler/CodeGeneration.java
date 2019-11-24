package compiler;

public class CodeGeneration {
    private int index;
    private String operation;
    private String operand1;
    private String operand2;
    private String result;
    private boolean isbackpatch;
    private String backpatch;

    CodeGeneration() {
        this.index = 0;
        this.operation = null;
        this.operand1 = null;
        this.operand2 = null;
        this.backpatch = null;
        this.isbackpatch = false;
    }

    public void incrementIndex() {
        this.index++;
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
    public void setResult(String res) {
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

    public void printQuadruple() {
        if ( this.operation == null ) {
            System.out.println("Ooops! Forgot so set operation!");
            System.exit(0);
        }
            System.out.println(this.index++ + "\t" + this.operation + "\t" + this.operand1 + "\t" + this.operand2 + "\t" + this.result);
    }

    //TODO write a method that says if index, op, op1, op2, and result are complete, we increment index.

}
