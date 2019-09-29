package compiler;
import java.io.IOException;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Scanner;

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

        Lexer lexer = new Lexer();
        Scanner removedComments = lexer.stripComments(scanner); // return a scanner without comments

        ArrayDeque<Token> tokens = new ArrayDeque<>();

        while ( removedComments.hasNext() ) {
            lexer.addTokens(tokens, removedComments.nextLine()); // add each token from each line
        }

        System.out.println();
        System.out.println("The tokens: ");
        for(Token token: tokens) {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        System.out.println();
        parser.isLexicallyCorrect(tokens);
        System.out.println(parser.isAccepted(tokens) ? "ACCEPT" : "REJECT");
    }

}
