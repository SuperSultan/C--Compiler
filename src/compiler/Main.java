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
        Queue<Token> tokens = lexer.tokenize(lexer.stripComments());

        Parser parser = new Parser(tokens);
        System.out.println(parser.isAccepted() ? "ACCEPT" : "REJECT");

    }

}
