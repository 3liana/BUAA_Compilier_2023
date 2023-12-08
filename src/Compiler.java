import java.io.*;
import java.util.ArrayList;

import backend.MipsGenerator;
import frontend.Lexer;
import frontend.Parser;
import frontend.lexer_package.*;
import frontend.paser_package.CompUnit;
import middleend.Generator;
import middleend.IRModule;
import middleend.IROptimaze;

public class Compiler {
    private static boolean debugValueCalculation = false;
    private static boolean optimaze = false;
    public static void main(String[] args) throws IOException {
        File outputFile = new File("llvm_ir.txt");
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
//        for (String res : parser.getAns()) {
//            byte[] dataBytes = (res + '\n').getBytes();
//            fOut.write(dataBytes);
//        }
        Generator generator = new Generator(compUnit);
        generator.visitCompUnit();
        IRModule module = IRModule.getModuleInstance();
        String llvm = module.getPrint();
//        System.out.println(llvm); //将llvm输出到控制台
        byte[] llvmDataBytes = (llvm).getBytes();
        fOut.write(llvmDataBytes);
//        if(Compiler.debugValueCalculation){
//            //打印符号表 和对应的值
//            generator.tableList.debugValueCalculation();
//        }
        if(optimaze){
            File outputFile_llvm_before = new File("testfilei_21373061_方沐阳_优化前中间代码.txt");
            OutputStream fOut_llvm_before = new FileOutputStream(outputFile_llvm_before);
            fOut_llvm_before.write(llvmDataBytes);
            Compiler.writeMips("testfilei_21373061_方沐阳_优化前目标代码.txt");
            //优化中间代码
            IROptimaze irOptimaze = new IROptimaze();
            irOptimaze.optimaze();
            //
            File outputFile_llvm_after = new File("testfilei_21373061_方沐阳_优化后中间代码.txt");
            OutputStream fOut_llvm_after = new FileOutputStream(outputFile_llvm_after);
            String after_llvm = module.getPrint();
            byte[] llvmDataBytes_after = (after_llvm).getBytes();
            fOut_llvm_after.write(llvmDataBytes_after);//写入优化后文件

        }
        //生成mips
        //优化后的目标代码写入两个文件
        MipsGenerator mipsGenerator = new MipsGenerator(optimaze);
        ArrayList<String> mipsOutput0 = mipsGenerator.macros;
        ArrayList<String> mipsOutput1 = mipsGenerator.datas;
        ArrayList<String> mipsOutput2 = mipsGenerator.texts;
        StringBuilder sb = new StringBuilder();
        for(String s :mipsOutput0){
            sb.append(s);
        }
        for(String s :mipsOutput1){
            sb.append(s);
        }
        for(String s :mipsOutput2){
            sb.append(s);
        }
        byte[] mipsDataBytes = (sb.toString()).getBytes();
        File mipsOutputFile = new File("testfilei_21373061_方沐阳_优化后目标代码.txt");
        OutputStream mipsFOut = new FileOutputStream(mipsOutputFile);
        mipsFOut.write(mipsDataBytes);
        mipsOutputFile = new File("mips.txt");
        mipsFOut = new FileOutputStream(mipsOutputFile);
        mipsFOut.write(mipsDataBytes);
    }
    private static void writeMips(String filename)throws IOException{
        MipsGenerator mipsGenerator = new MipsGenerator(false);
        ArrayList<String> mipsOutput0 = mipsGenerator.macros;
        ArrayList<String> mipsOutput1 = mipsGenerator.datas;
        ArrayList<String> mipsOutput2 = mipsGenerator.texts;
        StringBuilder sb = new StringBuilder();
        for(String s :mipsOutput0){
            sb.append(s);
        }
        for(String s :mipsOutput1){
            sb.append(s);
        }
        for(String s :mipsOutput2){
            sb.append(s);
        }
        //System.out.println(sb.toString());
        byte[] mipsDataBytes = (sb.toString()).getBytes();
        File mipsOutputFile = new File(filename);
        OutputStream mipsFOut = new FileOutputStream(mipsOutputFile);
        mipsFOut.write(mipsDataBytes);
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