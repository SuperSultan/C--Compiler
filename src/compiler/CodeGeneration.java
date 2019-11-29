package compiler;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class CodeGeneration {

    private Integer index;
    private String operation;
    private String secondaryOperation;
    private String nextOperation;
    private String operand1;
    private String operand2;
    private Integer tCount;
    private String result;

    private List<String> statements;
    private List<String> conditionals;

    CodeGeneration() {
        this.index = 0;
        this.tCount = 0;
        this.conditionals = new ArrayList<>();
    }

    public Integer getCurrentCount() { return this.tCount--; }

    public String getSecondaryOperation() { return this.secondaryOperation; }

    public String getOperand1() { return this.operand1; }

    public Integer gettCount() { return this.tCount++; }

    public String getResult() { return this.result; }

    public void setOperation(String op) { this.operation = op; }

    public void setSecondaryOperation(String secondaryOp) { this.secondaryOperation = secondaryOp; }

    public void setNextOperation(String nextOp) { this.nextOperation = nextOp; }

    public void setOperand1(String operand1) { this.operand1 = operand1; }

    public void setOperand2(String operand2) { this.operand2 = operand2; }

    public void settCount(Integer i) { this.tCount = i; }

    public void setResult(String res) { this.result = res; }

    public void printQuadruples() {}

    public void resetSecondaryOperation() { this.secondaryOperation = null;}

    public void createStatementQuadruple() {
        //if ( this.operation == null || this.operation.isEmpty() || this.operand1 == null || this.operand1.isEmpty() ||
      //          this.operand2 == null || this.operand2.isEmpty() || this.result == null || this.result.isEmpty() ) return;

        if ( this.operation == null || this.operation.isEmpty() || this.result == null || this.result.isEmpty() && !this.operation.equals("end") ) return;

        this.index++;
        String statement = String.join("\t", this.index.toString(), this.operation, this.operand1, this.operand2, this.result);
        System.out.println(statement);
    }

    public void createConditionalQuadruple() {
        if ( this.nextOperation == null || this.nextOperation.isEmpty() || this.operand1 == null || this.operand1.isEmpty()
                || this.operand2 == null || this.operand2.isEmpty() || this.result == null || this.result.isEmpty() ) return;

        this.index++;
        String statement = String.join("\t", this.index.toString(), this.nextOperation, this.operand1, this.operand2, this.result, "bp = " + this.index);
        System.out.println(statement);
    }

    public void resetConditionalQuadruple() {
        this.nextOperation = null;
        this.operand1 = null;
        this.operand2 = null;
        this.result = null;
    }

}