package compiler;
import java.io.IOException;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        Scanner printer = new Scanner(new File(args[0])); // used to print sample input
        Scanner scanner = new Scanner(new File(args[0])); // used to parse tokens
        if ( args.length != 1 ) {
            System.out.println("Usage: java Lexer filename");
            System.exit(0);
        }

        System.out.println("SAMPLE INPUT: ");
        while ( printer.hasNext() ) { System.out.println(printer.nextLine()); } // print source code

        Lexer lexer = new Lexer(scanner);
        List<String> lines = new ArrayList<>();
        lines = lexer.stripComments();

        ArrayDeque<Token> tokens = new ArrayDeque<>();
        tokens = lexer.addTokens(lines);

        for(Token toks: tokens) {
            System.out.println("Lexeme: " + toks.getLexeme(toks) + "\nCategory: " + toks.getCategory(toks));
        }

        Parser parser = new Parser(tokens);
        System.out.println(parser.isAccepted(tokens) ? "ACCEPT" : "REJECT");

    }

}
