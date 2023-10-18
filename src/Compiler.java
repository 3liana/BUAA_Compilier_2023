import java.io.*;
import java.util.ArrayList;

import lexer_package.*;

public class Compiler {
    public static void main(String[] args) throws IOException {
        File outputFile = new File("output.txt");
        OutputStream fOut = new FileOutputStream(outputFile);
        String fileName = "testfile.txt";
        ArrayList<Token> tokens = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
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
//                int value = lexer.next();
                while (lexer.getValue() == 0) {
                    Token curToken = lexer.peekToken();
                    tokens.add(curToken);
//                    String res = lexer.peek();
//                    byte[] dataBytes = (res + '\n').getBytes();
//                    fOut.write(dataBytes);
                    lexer.next();
                }
                if (lexer.getValue() == -2) {
                    inComment = true;
                } else {
                    inComment = false;
                }
            }
        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }
        Parser parser = new Parser(tokens);
        parser.parseCompUnit();
        for (String res : parser.getAns()) {
            byte[] dataBytes = (res + '\n').getBytes();
            fOut.write(dataBytes);
        }
    }
}