package compiler;

import java.lang.String;
import java.util.ArrayDeque;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

public class Lexer {
    Scanner s;

    Lexer(Scanner scanner) {
        this.s = scanner;
    }

    public List<String> stripComments() {

        boolean comment_mode = false;
        String complete_block_comment = "(\\/\\*).*(\\*\\/)|(\\/\\*).*";
        String incomplete_block_comment = "(\\/\\*).*";
        String closing_block_comment = "^(.*?)(\\*\\/)";

        List<String> lines = new ArrayList<>();

        while ( s.hasNext() ) {
            String line = s.nextLine().trim();
            if ( line.length() == 0 ) continue; // skip empty line in stream

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
            lines.add(line);
        }
        return lines;
    } //stripComments

    public ArrayDeque<Token> addTokens(List<String> lines) {

        String keyword = "\\b(?:else|if|int|return|void|while)\\b";
        String identifier = "\\b[a-zA-Z]+\\b";
        String number = "\\b[\\d]+\\b";
        String special_symbol = "==|!=|<=|>=|[+\\-*/<>=;,()\\[\\]{}]";
        String error = "\\S+";
        String regex = "(" + keyword + ")|(" + identifier + ")|(" + number + ")|(" + special_symbol + ")|(" + error + ")";
        Pattern pattern = Pattern.compile(regex);

        ArrayDeque<Token> tokens = new ArrayDeque<>();

        for(String str: lines) {

            for (Matcher matcher = pattern.matcher(str); matcher.find(); ) { // Attempt to match each capture group against the regex
                if (matcher.start(1) != -1) {
                    tokens.add(new Token(matcher.group(), "KEYWORD"));
                } else if (matcher.start(2) != -1) {
                    tokens.add(new Token(matcher.group(), "ID"));
                } else if (matcher.start(3) != -1) {
                    tokens.add(new Token(matcher.group(), "NUM"));
                } else if (matcher.start(4) != -1) {
                    tokens.add(new Token(matcher.group(), "SPECIAL"));
                } else if (matcher.start(5) != -1) {
                    tokens.add(new Token(matcher.group(), "ERROR"));
                    System.out.println("REJECT");
                    System.exit(0); // Exit program upon finding BAD token!
                }
            }
        }

        tokens.add(new Token("$", "SPECIAL"));

        return tokens;

    } // getTokens
} // class Lexer