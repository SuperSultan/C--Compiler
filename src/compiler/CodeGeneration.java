package compiler;

import java.util.ArrayList;
import java.util.List;

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
    private List<String> codes;
    private ArrayList<List<String>> quadruples;
    private int bpIndex;

    CodeGeneration() {
        this.index = 0;
        this.tempCounter = 0;
        this.isBackpatch = false;
        this.isVariable = true;
        this.quadruples = new ArrayList<>();
        this.codes = new ArrayList<>();
    }

    public void setOperation(String op) {
        this.operation = op;
    }

    public void setNextOperation(String op) {
        this.nextOperation = op;
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
        for(List<String> instructions: quadruples) {
            for (String quads : codes) {
                if ( !this.hasTempCounter) {
                    System.out.print(this.index + " " + this.operation + " " + this.operand1 + " " + this.operand2 + " " + this.result);
                } else {
                    System.out.println(this.index + " " + this.operation + " " + this.operand1 + " " + this.operand2 + " " + "t" + this.tempCounter);
                }
            }
        System.out.println();
        }
    }

    public void addCodes() {
        index++;
        codes.add(this.index.toString());
        codes.add("\t");
        codes.add(this.operation);
        codes.add("\t");
        codes.add(this.operand1);
        codes.add("\t");
        codes.add(this.operand2);
        codes.add("\t");
    }

    public void createQuadruple(boolean usingTempCounter) {

        if ( !usingTempCounter) {
            codes.add(this.result);
            this.addCodes();
            this.quadruples.add(codes);
            System.out.println(this.index++ + "\t" + this.operation + "\t" + this.operand1 + "\t" + this.operand2 + "\t" + this.result);
        } else if ( usingTempCounter) {
            this.addCodes();
            tempCounter++;
            codes.add("t" + tempCounter);
            this.quadruples.add(codes);
            System.out.println(this.index++ + "\t" + this.operation + "\t" + this.operand1 + "\t" + this.operand2 + "\t" + "t" + tempCounter++);
        }

        if ( this.nextIsBackpatch ) {
            this.isBackpatch = true;
            this.nextIsBackpatch = false;
            this.operation = this.nextOperation;
        }

        if ( isBackpatch ) {

            Integer index = this.index;
            String indx = index.toString();
            codes.add(indx);
            codes.add(this.operation);
            codes.add("t" + this.tempCounter);
            codes.add("\t");
            codes.add("\t");
            codes.add("\t");
            codes.add(null); // the index we jump to if the backpatch operation is satisfied
            codes.add("bp = " + this.bpIndex++);
            quadruples.add(codes);
            System.out.println(this.index++ + "\t" + this.operand1 + "\t" + " " + this.bpIndex);
        }

        //reset();
    }
}