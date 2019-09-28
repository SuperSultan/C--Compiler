package compiler;
import java.io.IOException;
import java.io.File;
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

        Token token = new Token();

        while ( removedComments.hasNext() ) {
            lexer.addTokens(token, removedComments.nextLine()); // add each token from each line
        }

        System.out.println();
        System.out.println("The tokens: ");
        token.printTokens();

        Parser parser = new Parser(token);
        System.out.println();
        parser.isLexicallyCorrect(token);
        System.out.println(parser.isAccepted(token) ? "ACCEPT" : "REJECT");
    }

}
