package compiler;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class CodeGeneration {

    private Integer index;
    private Integer tempCounter;
    private String operation;
    private String nextOperation;
    private String operand1;
    private String operand2;
    private String paramResult;
    private String result;
    private String backpatch;
    private String tempResult;
    private boolean isBackpatch;
    private boolean nextIsBackpatch;
    private boolean isVariable;
    private boolean hasTempCounter;
    private List<StringBuilder> quadruples;
    private StringBuilder code;
    private int bpIndex;

    CodeGeneration() {
        this.index = 0;
        this.tempCounter = 0;
        this.isBackpatch = false;
        this.isVariable = true;
        this.quadruples = new ArrayList<>();
        this.code = new StringBuilder();
    }

    public void setOperation(String op) { this.operation = op; }

    public void setNextOperation(String op) {
        this.nextOperation = op;
    }

    public void setOperand1(String operand1) { this.operand1 = operand1; }

    public void setOperand2(String operand2) { this.operand2 = operand2; }

    public void setResult(String res) {
        this.result = res;
    }

    public void setParam(String parameter) {
        this.operation = "param";
        this.paramResult = parameter;
        System.out.println(this.index++ + "\t" + this.operation + "\t" + " " + "\t" + " " + "\t" + this.paramResult);
        reset();
    }

    public void setNextIsbackpatch(Boolean isbp) {
        this.nextIsBackpatch = isbp;
    }

    public void reset() {
        this.operation = null;
        this.nextOperation = null;
        this.operand1 = null;
        this.operand2 = null;
        this.backpatch = null;
        this.isBackpatch = false;
        this.isVariable = true;
        this.result = null;
        this.paramResult = null;
    }

    public void printQuadruples() {
        Iterator it = quadruples.iterator();
        while ( it.hasNext() ) {
           // System.out.println(this.code);
        }
    }

    public void joinCodes() {
        index++;
        code.append(this.index);
        code.append("\t");
        code.append(this.operation);
        code.append("\t");
        code.append(this.operand1);
        code.append("\t");
        code.append(this.operand2);
        code.append("\t");
    }

    public void createQuadruple(boolean usingTempCounter) {

        if ( !usingTempCounter) { // there is no t_i for this quadruple
            this.joinCodes();
            code.append(this.result);
            //System.out.println(this.code.toString());
            this.quadruples.add(code);
        } else  { // there is a t_i for this quadruple
            this.joinCodes();
            tempCounter++;
            code.append("t" + this.tempCounter);
            //System.out.println(this.code.toString());
            this.quadruples.add(code);
        }

        if ( this.nextIsBackpatch ) {
            this.isBackpatch = true; // set current operation to backpatch mode
            this.nextIsBackpatch = false; // turn off nextIsBackpatch
            this.operation = this.nextOperation; // copy nextOperation to thisoperation
            this.nextOperation = null; // turn off nextOperation
        }

        if ( isBackpatch ) {

            Integer index = this.index;
            String indx = index.toString();
            code.append(indx);
            code.append(this.operation);
            code.append("t" + this.tempCounter);
            code.append("\t");
            code.append("\t");
            code.append("\t");
            code.append("?"); // the index we jump to if the backpatch operation is satisfied
            code.append("bp = " + this.bpIndex++);
            System.out.println(this.code.toString());
            quadruples.add(code);
        }

        //reset();
    }
}