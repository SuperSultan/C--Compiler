package compiler;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class CodeGeneration {

    private Integer index;
    private String operation;
    private String operand1;
    private String operand2;
    private Integer tCount;
    private String result;

    private List<String> statements;
    private List<String> conditionals;

    CodeGeneration() {
        this.index = 0;
        this.tCount = 0;
    }

    public String getOperand1() { return this.operand1; }

    public void setOperation(String op) { this.operation = op; }

    public void setOperand1(String operand1) { this.operand1 = operand1; }

    public void setOperand2(String operand2) { this.operand2 = operand2; }

    public void settCount(Integer i) { this.tCount = i; }

    public void setResult(String res) { this.result = res; }

    public void printQuadruples() {}

    public void joinCodes() {
        //index++;
    }

    public void reset() {

    }

    public void createStatementQuadruple() {
        this.index++;
        String statement = String.join("\t", this.index.toString(), this.operation, this.operand1, this.operand2, this. result);
        System.out.println(statement);
    }

    public void createConditonalQuadruple() {
    }

}