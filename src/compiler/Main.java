package compiler;
import java.io.IOException;
import java.io.File;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(new File(args[0])); // used to parse tokens
        if ( args.length != 1 ) {
            System.out.println("Usage: java p3 filename");
            System.exit(0);
        }

        Lexer lexer = new Lexer(scanner);
        List<String> lines = new ArrayList<>();
        lines = lexer.stripComments(); // stripped comments

        ArrayDeque<Token> tokens = new ArrayDeque<>();
        tokens = lexer.addTokens(lines);

        Parser parser = new Parser(tokens);
        System.out.println(parser.isAccepted() ? "ACCEPT" : "REJECT");

        //List<HashMap<String,String>> symbol_table = new LinkedList<>();
        //symbol_table = parser.getSymbolTable();

        ArrayDeque<Node> nodes = new ArrayDeque<>();
        nodes = parser.getNodes();
/*
        System.out.println();
        for(HashMap<String,String> maps : symbol_table) {
            for(Map.Entry<String,String> entry : maps.entrySet()) {
                String identifier = entry.getKey();
                String value = entry.getValue();
                System.out.println("IDENTIFIER: " + identifier + " TYPE: " + value);
            }
        }
*/
        System.out.println();
        for(Node n : nodes) {
            System.out.println(n.getNodeName() + "\nChild Nodes: " + n.getChildNodes() + "\nTokens: " + n.getChildTokens() + "\nScope: " + n.getScope() + "\n");
        }

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(nodes);
        //System.out.println(semanticAnalyzer.isAccepted() ? "ACCEPT" : "REJECT");
    }

}
