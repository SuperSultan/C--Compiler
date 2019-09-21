package compiler;

import java.io.File;
import java.io.IOException;
import java.lang.String;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    public void stripComments(Scanner scanner) {

        boolean comment_mode = false;
        String complete_block_comment = "(\\/\\*).*(\\*\\/)|(\\/\\*).*";
        String incomplete_block_comment = "(\\/\\*).*";
        String closing_block_comment = "^(.*?)(\\*\\/)";

        Lexer lexer = new Lexer();

        while ( scanner.hasNext() ) {
            String line = scanner.nextLine().trim();
            if ( line.length() == 0 ) continue; // skip empty line in stream

            System.out.println("INPUT: " + line);
            line = line.replaceAll("(\\/\\/).*", ""); // replace everything after line comments with ""

            if ( comment_mode && line.contains("*/") && !line.contains("/*") ) {
                line = line.replaceAll(closing_block_comment, "");
                comment_mode = false;
            } else if ( line.contains("/*") && line.contains("*/") ) {
                line = line.replaceAll(complete_block_comment, "");
                comment_mode = false;
            } else if ( line.contains("/*") ) {
                line = line.replaceAll(incomplete_block_comment, "");
                comment_mode = true;
            }
            lexer.getTokens(line); // powered by regex
        }

    } //stripComments

    public void getTokens(String str) {

        String keyword = "\\b(?:else|if|int|return|void|while)\\b";
        String identifier = "\\b[a-zA-Z]+\\b";
        String number = "\\b[\\d]+\\b";
        String special_symbol = "==|!=|<=|>=|[+\\-*/<>=;,()\\[\\]{}]";
        String error = "\\S+";
        String regex = "(" + keyword + ")|(" + identifier + ")|(" + number + ")|(" + special_symbol + ")|(" + error + ")";

        Pattern pattern = Pattern.compile(regex);
        Token tokens = new Token();

        for( Matcher matcher = pattern.matcher(str); matcher.find(); ) { // Attempt to match each capture group against the regex
            if ( matcher.start(1) != -1 ) {
                System.out.println("keyword: " + matcher.group() );
                Token t = new Token("KEYWORD", matcher.group() );
                tokens.addTokens(t);
            } else if ( matcher.start(2) != -1 ) {
                System.out.println("identifier: " + matcher.group() );
                Token t = new Token ("ID", matcher.group() );
                tokens.addTokens(t);
            } else if ( matcher.start(3) != -1 ) {
                System.out.println("number: " + matcher.group());
                Token t = new Token("NUM", matcher.group() );
                tokens.addTokens(t);
            } else if ( matcher.start(4) != -1 ) {
                System.out.println( matcher.group() );
                Token t= new Token("SPECIAL", matcher.group() );
                tokens.addTokens(t);
            } else if ( matcher.start(5) != -1 ) {
                System.out.println("error: " + matcher.group() );
                Token t = new Token("ERROR", matcher.group());
                tokens.addTokens(t);
            }
        }

    } // getTokens
} // class Lexer