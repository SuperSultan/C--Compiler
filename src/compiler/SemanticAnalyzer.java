package compiler;

public class SemanticAnalyzer {
    Parser parser;

    SemanticAnalyzer(Parser p) {
        this.parser = p;
    }

    public boolean isAccepted() {
        return true;
    }
}
