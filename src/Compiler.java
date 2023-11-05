import java.io.*;
import java.util.ArrayList;

import frontend.Lexer;
import frontend.Parser;
import frontend.lexer_package.*;
import frontend.paser_package.CompUnit;

public class Compiler {
    public static void main(String[] args) throws IOException {
        File outputFile = new File("output.txt");
        OutputStream fOut = new FileOutputStream(outputFile);
        String fileName = "testfile.txt";
        ArrayList<Token> tokens = new ArrayList<>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            Compiler.getTokens(br, tokens);
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
        Parser parser = new Parser(tokens);
        CompUnit compUnit = parser.parseCompUnit();
        for (String res : parser.getAns()) {
            byte[] dataBytes = (res + '\n').getBytes();
            fOut.write(dataBytes);
        }
    }

    private static void getTokens(BufferedReader br, ArrayList<Token> tokens) throws IOException {
        String line;
        boolean inComment = false;
        while ((line = br.readLine()) != null) {
            //操作line
            if (line.equals("")) {
                continue;
            }
            if (inComment) {
                String subStr = "*/";
                int index = line.indexOf(subStr);
                if (index == -1) {
                    //跳过这一行
                    continue;
                } else {
                    line = line.substring(index + 2);
                    inComment = false;
                }
            }
            Lexer lexer = new Lexer(line);
            while (lexer.getValue() == 0) {
                Token curToken = lexer.peekToken();
                tokens.add(curToken);
                lexer.next();
            }
            //lexer.getValue : -1:这句已经读到了末尾；-2：遇见了注释
            if (lexer.getValue() == -2) {
                inComment = true;
            } else {
                inComment = false;
            }
        }
    }
}