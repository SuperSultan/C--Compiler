package compiler;
import java.io.IOException;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(new File(args[0])); // used to parse tokens
        if ( args.length != 1 ) {
            System.out.println("Usage: java Lexer filename");
            System.exit(0);
        }

        Lexer lexer = new Lexer(scanner);
        List<String> lines = new ArrayList<>();
        lines = lexer.stripComments();

        ArrayDeque<Token> tokens = new ArrayDeque<>();
        tokens = lexer.addTokens(lines);

        Parser parser = new Parser(tokens);
        System.out.println(parser.isAccepted() ? "ACCEPT" : "REJECT");

    }

}
