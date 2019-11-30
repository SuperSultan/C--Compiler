package compiler;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class CodeGeneration {

    private Integer index;
    private String operation;
    private String secondaryOperation;
    private String previousOperation;
    private String nextOperation;
    private String operand1;
    private String operand2;
    private Integer tCount;
    private String result;

    private List<String> quadruples;

    private static String bpIndex;
    private static String bpIndex2;

    CodeGeneration() {
        this.index = 0;
        this.tCount = 0;
        this.quadruples = new ArrayList<>();
    }

    public List<String> getQuadruples() { return this.quadruples; }

    public Integer getIndex() { return ++this.index; }

    public String getOperation() { return this.operation; }

    public Integer getCurrentCount() { return this.tCount--; }

    public String getSecondaryOperation() { return this.secondaryOperation; }

    public String getPreviousOperation() { return this.previousOperation; }

    public String getOperand1() { return this.operand1; }

    public String getOperand2() { return this.operand2; }

    public Integer gettCount() { return this.tCount++; }

    public String getResult() { return this.result; }

    public void setOperation(String op) { this.operation = op; }

    public void setSecondaryOperation(String secondaryOp) { this.secondaryOperation = secondaryOp; }

    public void setPreviousOperation(String prev) { this.previousOperation = prev; }

    public void setNextOperation(String nextOp) { this.nextOperation = nextOp; }

    public void setOperand1(String operand1) { this.operand1 = operand1; }

    public void setOperand2(String operand2) { this.operand2 = operand2; }

    public void settCount(Integer i) { this.tCount = i; }

    public void setResult(String res) { this.result = res; }

    public void resetSecondaryOperation() { this.secondaryOperation = null;}

    public List<String> removeNulls() {

        List<String> newQuads = new ArrayList<>();

        for (String quad : this.quadruples) {
            if (!quad.contains("null"))
                newQuads.add(quad); // remove nulls
        }

        return newQuads;
    }

    public List<String> fixBackPatches(List<String> newQuads) {

        List<String> bpIndexes = new ArrayList<>();

        for (int i = 0; i < newQuads.size(); i++) {

            String temp = newQuads.get(i); // get current string
            if ((temp.contains("brge") || temp.contains("brle") || temp.contains("brneq") || temp.contains("breq") || temp.contains("brgt") || temp.contains("brlt"))
                    && temp.contains("?")) {
                bpIndex = temp.split("\\s+")[0];
                bpIndexes.add(bpIndex);
            }
        }

        for (int j=0; j< newQuads.size(); j++) {
            if ( bpIndexes.size() != 0 ) {
                if (newQuads.get(j).contains("brk")) {
                    String res = newQuads.get(j).replace("?", bpIndexes.remove(0));
                    newQuads.set(j, res);
                }
            }
        }
        return newQuads;
    }

    public List<String> fixBackPatchesTwo(List<String> newQuads) {

        List<String> bpIndexes = new ArrayList<>();

        for(int i=0; i<newQuads.size(); i++) {
            String temp = newQuads.get(i);
            if ( temp.contains("brk") && ! temp.contains("?") ) {
                bpIndex2 = temp.split("\\s+")[0];
                bpIndexes.add(bpIndex2);
            }
        }
        System.out.println(bpIndexes);
        for(int j=0; j<newQuads.size(); j++) {
            String curr = newQuads.get(j);
            if ( bpIndexes.size() !=0 ) {
                if ( curr.contains("brge") || curr.contains("brle") || curr.contains("brneq") || curr.contains("breq") || curr.contains("brgt") || curr.contains("brlt")
                    && curr.contains("?") ) {
                    String res = curr.replace("?", bpIndexes.remove(0));
                    newQuads.set(j, res);
                }
            }
        }
        return newQuads;
    }

    public void printQuadruples(List<String> quadruples) {
        for(String quad : quadruples) {
            System.out.println(quad);
        }

    }


    public void createStatementQuadruple() {
        if ( this.operation == null || this.operation.isEmpty() || this.result == null || this.result.isEmpty() && !this.operation.equals("end") ) return;

        this.index++;
        String statement = String.join("\t", this.index.toString(), this.operation, this.operand1, this.operand2, this.result);
        quadruples.add(statement);
        System.out.println(statement);
    }

    public void createConditionalQuadruple() {
        if ( this.nextOperation == null || this.nextOperation.isEmpty() || this.operand1 == null || this.operand1.isEmpty()
                || this.operand2 == null || this.operand2.isEmpty() || this.result == null || this.result.isEmpty() ) return;

        this.index++;
        String statement = String.join("\t", this.index.toString(), this.nextOperation, this.operand1, this.operand2, this.result, "bp = " + this.index);
        // TODO add to list
        quadruples.add(statement);
        System.out.println(statement);
    }

    public void resetStatementQuadruple() {
        this.operation = null;
        this.operand1 = null;
        this.operand2 = null;
        this.result = null;
    }

    public void resetConditionalQuadruple() {
        this.nextOperation = null;
        this.operand1 = null;
        this.operand2 = null;
        this.result = null;
    }

}