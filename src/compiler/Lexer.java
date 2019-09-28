package compiler;

import java.lang.String;
import java.util.ArrayDeque;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    public Scanner stripComments(Scanner scanner) {

        boolean comment_mode = false;
        String complete_block_comment = "(\\/\\*).*(\\*\\/)|(\\/\\*).*";
        String incomplete_block_comment = "(\\/\\*).*";
        String closing_block_comment = "^(.*?)(\\*\\/)";

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
        }
        return scanner;
    } //stripComments

    public Token addTokens(Token tokens, String str) {

        String keyword = "\\b(?:else|if|int|return|void|while)\\b";
        String identifier = "\\b[a-zA-Z]+\\b";
        String number = "\\b[\\d]+\\b";
        String special_symbol = "==|!=|<=|>=|[+\\-*/<>=;,()\\[\\]{}]";
        String error = "\\S+";
        String regex = "(" + keyword + ")|(" + identifier + ")|(" + number + ")|(" + special_symbol + ")|(" + error + ")";

        Pattern pattern = Pattern.compile(regex);

        for( Matcher matcher = pattern.matcher(str); matcher.find(); ) { // Attempt to match each capture group against the regex
            if ( matcher.start(1) != -1 ) {
        //        System.out.println("keyword: " + matcher.group() );
                Token t = new Token(matcher.group(), "KEYWORD" );
                tokens.addToken(t);
            } else if ( matcher.start(2) != -1 ) {
         //       System.out.println("identifier: " + matcher.group() );
                Token t = new Token (matcher.group(), "ID" );
                tokens.addToken(t);
            } else if ( matcher.start(3) != -1 ) {
         //       System.out.println("number: " + matcher.group());
                Token t = new Token(matcher.group(), "NUM");
                tokens.addToken(t);
            } else if ( matcher.start(4) != -1 ) {
        //        System.out.println( matcher.group() );
                Token t= new Token(matcher.group(), "SPECIAL");
                tokens.addToken(t);
            } else if ( matcher.start(5) != -1 ) {
        //        System.out.println("error: " + matcher.group() );
                Token t = new Token(matcher.group(), "ERROR");
                tokens.addBadToken(t);
            }
        }

        return tokens;

    } // getTokens
} // class Lexer