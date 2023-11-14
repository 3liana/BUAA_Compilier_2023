package middleend;

import frontend.lexer_package.Category;
import frontend.paser_package.*;
import frontend.paser_package.stmt_package.*;
import middleend.symbol.*;
import middleend.type.*;
import middleend.value.*;
import middleend.value.user.*;
import middleend.value.user.instruction.*;
import middleend.value.user.instruction.terminateInst.BrInst;
import middleend.value.user.instruction.terminateInst.RetInst;


import java.util.ArrayList;

public class Generator {
    private CompUnit compUnit;//语法分析生成的AST树
    public TableList tableList;
    private Function curFunction = null;//通过curFunction来获取虚拟寄存器的值
    private Factory factory;
    private IRModule irModule;

    // 保留的数
    private boolean isGlobal = true;

    public Generator(CompUnit compUnit) {
        this.compUnit = compUnit;
        this.irModule = IRModule.getModuleInstance();
        this.tableList = new TableList();
        this.factory = new Factory(tableList);
    }

    public SymbolTable getCurTable() {
        return this.tableList.getCurSymbolTable();
    }

    //开始visit AST
    public void visitCompUnit() {
        this.tableList.addTable();//0层
        this.initLibraryFunc();//初始化库函数
        for (Decl decl : this.compUnit.decls) {
            visitDecl(decl);
        }
        for (FuncDef funcDef : this.compUnit.defs) {
            visitFuncDef(funcDef);
        }
        visitMainFuncDef(this.compUnit.mainDef);
    }

    public void initLibraryFunc() {
        //在库里注册函数 getint和printf
        this.tableList.getCurSymbolTable().addValue(new LibraryFunction("getint"));
        this.tableList.getCurSymbolTable().addValue(new LibraryFunction("printf"));
    }

    public void visitFuncDef(FuncDef funcDef) {
        //建立func类型的value
        this.isGlobal = false;
        Type returnType = funcDef.funcType.token.getCategory() == Category.VOIDTK ?
                new VoidType() : new IntegerType(32);
        Function function;
        SymbolTable newSymbolTable = new SymbolTable();
        if (funcDef.type == 0) {
            //无形参
            function = new Function(funcDef.ident.getName(), returnType);
        } else {
            //有形参
            FuncFParams fParams = funcDef.params;
            ArrayList<FuncFParam> params = fParams.params;
            function = new Function(funcDef.ident.getName(), returnType,
                    params, newSymbolTable);
            //在function初始化里面完成对形参的表示，且加入符号表
        }
        //将函数名放入0层符号表，再新建一层符号表
        getCurTable().addValue(function);
        //新建符号表 如果有形参 那么这个符号表已经存过形参的信息了
        this.tableList.addATable(newSymbolTable);
        //切换curFunction
        this.curFunction = function;
        this.visitBlock(funcDef.block);
        //函数编译完成，pop出这一层的符号表
        this.tableList.popTable();
    }

    public void visitMainFuncDef(MainFuncDef mainFuncDef) {
        this.tableList.addTable();
        this.isGlobal = false;
        Function main = new Function("main", new IntegerType(32));
        this.curFunction = main;
        this.visitBlock(mainFuncDef.block);
        this.tableList.popTable();
    }

    public void visitDecl(Decl decl) {
        if (decl.type == 0) {
            visitConstDecl(decl.constDecl);
        } else {
            visitVarDecl(decl.varDecl);
        }
    }

    public void visitConstDecl(ConstDecl decl) {
        // const int {constDef}
        for (ConstDef constDef : decl.defs) {
            visitConstDef(constDef);
        }
    }

    public void visitConstDef(ConstDef constDef) {
        // ConstDef -> Ident { '[' ConstExp ']' } '=' ConstInitVal
        String name = constDef.getName();
        if (constDef.exps.size() == 0) {
            //0维
            //visitConstInitVal(constDef.constInitVal); //访问初始化InitVal 获得Const的值
            //将常量的初值存在saveInt里
            if (this.isGlobal) {
                //全局变量
                //对于全局变量 初始值是确定的 可以在factory里面算出具体值
                int tempNum = factory.calConstExp(constDef.constInitVal.exp);
                Value tempValue = new GlobalVar(name, true, tempNum);
                getCurTable().addValue(tempValue);//加入符号表
            } else {
                BasicBlock basicBlock = curFunction.getCurBasicBlock();
                int registerNum = curFunction.assignRegister();
                VarValue var = new VarValue(registerNum, true, name);
                new AllocaInst(basicBlock, var);
                getCurTable().addValue(var);
                //下面是计算初始值的地方
                Value value = this.visitConstInitVal(constDef.constInitVal);
                new StoreInst(basicBlock, value, var);
            }
        } else {
            // todo 数组
        }
    }

    public Value visitConstInitVal(ConstInitVal constInitVal) {
        if (constInitVal.type == 0) {
            //0维度
            return this.visitAddExp(constInitVal.exp.addExp);
        } else {
            //todo 数组初值
        }
        return new Value();
    }

    public void visitVarDecl(VarDecl decl) {
        // VarDecl → BType VarDef { ',' VarDef } ';'
        for (VarDef item : decl.defs) {
            this.visitVarDef(item);
        }
    }

    public void visitVarDef(VarDef varDef) {
        String name = varDef.getName();
        if (this.isGlobal) {
            //全局变量 在factory中计算
            int tempNum = 0;
            if (varDef.type == 1) {
                tempNum = factory.calAddExp(varDef.initVal.exp.addExp);
            }
            //以上 确定变量名和初始值
            Value tempValue = new GlobalVar(name, false, tempNum);
            getCurTable().addValue(tempValue);
        } else {
            BasicBlock basicBlock = curFunction.getCurBasicBlock();
            int registerNum = curFunction.assignRegister();
            VarValue var = new VarValue(registerNum, false, name);
            getCurTable().addValue(var);
            new AllocaInst(basicBlock, var);
            if (varDef.type == 1) {
                Value value = this.visitInitVal(varDef.initVal);
                new StoreInst(basicBlock, value, var);
            }
        }
    }

    public Value visitInitVal(InitVal initVal) {
        if (initVal.type == 0) {
            //0维
            return this.visitAddExp(initVal.exp.addExp);
        } else {
            //todo 数组
        }
        return new Value();
    }


    public void visitBlock(Block block) {
        //
        for (BlockItem item : block.blockItems) {
            this.visitBlockItem(item);
        }
    }

    public void visitBlockItem(BlockItem blockItem) {
        if (blockItem.type == 0) {
            this.visitDecl(blockItem.decl);
        } else {
            this.visitStmt(blockItem.stmt);
        }
    }

    public void visitStmt(Stmt stmt) {
        if (stmt instanceof StmtReturn) {
            this.visitStmtReturn((StmtReturn) stmt);
        }
        if (stmt instanceof StmtBC) {
            this.visitStmtBC((StmtBC) stmt);
        }
        if (stmt instanceof StmtBlock) {
            this.visitStmtBlock((StmtBlock) stmt);
        }
        if (stmt instanceof StmtExp) {
            this.visitStmtExp((StmtExp) stmt);
        }
        if (stmt instanceof StmtFor) {
            this.visitStmtFor((StmtFor) stmt);
        }
        if (stmt instanceof StmtIf) {
            this.visitStmtIf((StmtIf) stmt);
        }
        if (stmt instanceof StmtLValExp) {
            this.visitStmtLValExp((StmtLValExp) stmt);
        }
        if (stmt instanceof StmtPrint) {
            this.visitStmtPrint((StmtPrint) stmt);
        }
        if (stmt instanceof StmtRead) {
            this.visitStmtRead((StmtRead) stmt);
        }
    }


    public Value visitAddExp(AddExp exp) {
        BasicBlock curBasicBlock = curFunction.getCurBasicBlock();
        Value pre = this.visitMulExp(exp.mulExps.get(0));
        ArrayList<Value> values = new ArrayList<>();
        if (exp.mulExps.size() != 1) {
            for (int i = 1; i < exp.mulExps.size(); i++) {
                values.add(this.visitMulExp(exp.mulExps.get(i)));
            }
        }
        //先统一visit了mulExp,然后再加起来
        for (int i = 0; i < values.size(); i++) {
            Value temp = values.get(i);
            int symbol = exp.symbols.get(i + 1);
            //
            int registerNum = curFunction.assignRegister();
            Value result = new VarValue(registerNum, false);
            if (symbol == 1) {
                //1表示加 -1表示-
                new BinaryInst(curBasicBlock, result, pre, temp, Operator.add);
            } else {
                new BinaryInst(curBasicBlock, result, pre, temp, Operator.sub);
            }
            pre = result;
        }
        return pre;
    }

    public Value visitMulExp(MulExp mulExp) {
        BasicBlock curBasicBlock = curFunction.getCurBasicBlock();
        Value pre = this.visitUnaryExp(mulExp.unaryExps.get(0));
        ArrayList<Value> values = new ArrayList<>();
        if (mulExp.unaryExps.size() != 1) {
            for (int i = 1; i < mulExp.unaryExps.size(); i++) {
                values.add(this.visitUnaryExp(mulExp.unaryExps.get(i)));
            }
        }
        for (int i = 0; i < values.size(); i++) {
            Value temp = values.get(i);
            int symbol = mulExp.symbols.get(i + 1);
            //
            int registerNum = curFunction.assignRegister();
            Value result = new VarValue(registerNum, false);
            if (symbol == 1) {
                new BinaryInst(curBasicBlock, result, pre, temp, Operator.mul);
            } else if (symbol == 2) {
                new BinaryInst(curBasicBlock, result, pre, temp, Operator.sdiv);
            } else {
                new BinaryInst(curBasicBlock, result, pre, temp, Operator.srem);
            }
            pre = result;
        }
        return pre;
    }

    public Value visitUnaryExp(UnaryExp exp) {
        BasicBlock curBlock = curFunction.getCurBasicBlock();
        if (exp.type == 0) {
            //PrimaryExp
            return this.visitPrimaryExp(exp.primaryExp);
        } else if (exp.type == 1) {
            //Ident '(' [FuncRParams] ')'
            String functionName = exp.ident.getName();
            FuncRParams funcRParams = exp.funcRParams;
            Value v = this.tableList.foundFuncDef(functionName);
            if (v instanceof Function) {
                //非库函数
                Function f = (Function) v;
                ArrayList<Value> rParamValue = new ArrayList<>();
                //visit每个实参 获得实参的寄存器
                if (funcRParams != null) {
                    for (Exp tempExp : funcRParams.exps) {
                        rParamValue.add(this.visitAddExp(tempExp.addExp));
                    }
                }
                if (f.returnType instanceof IntegerType) {
                    //有返回值函数
                    //xxx = call()的形式
                    int registerNum = curFunction.assignRegister();
                    VarValue var = new VarValue(registerNum, false);
                    new CallInst(curBlock, f, rParamValue, var);
                    return var;
                } else {
                    //无返回值函数
                    //call()
                    return new CallInst(curBlock, f, rParamValue);
                }
            } else {
                //库函数
                //1 getint() callInst
                if (functionName.equals("getint")) {
                    int registerNum = curFunction.assignRegister();
                    VarValue var = new VarValue(registerNum, false);
                    new LibraryCallInst(curBlock, 1, var);
                    return var;
                }
                //2 printf() callInst
                //不太符合定义 因为funcFParams里是一堆exp 而 printf需要formatString
                return new Value();
            }
        } else {
            //UnaryOp UnaryExp
            // + - 是与常数0做运算
            ConstValue zero = new ConstValue("0");
            Value v = this.visitUnaryExp(exp.unaryExp);
            if (exp.unaryOp.op.getCategory() == Category.PLUS) {
                int registerNum = curFunction.assignRegister();
                VarValue var = new VarValue(registerNum, false);
                new BinaryInst(curBlock, var, zero, v, Operator.add);
                return var;
            } else if (exp.unaryOp.op.getCategory() == Category.MINU) {
                int registerNum = curFunction.assignRegister();
                VarValue var = new VarValue(registerNum, false);
                new BinaryInst(curBlock, var, zero, v, Operator.sub);
                return var;
            } else {
                //! NOT
                return new Value();
            }
        }
    }

    public Value visitPrimaryExp(PrimaryExp exp) {
        BasicBlock curBasicBlock = curFunction.getCurBasicBlock();
        if (exp.type == 0) {
            //exp
            return this.visitAddExp(exp.exp.addExp);
        } else if (exp.type == 2) {
            //number
            return new ConstValue(exp.number.token.getName());
        } else {
            //type = 1 LVal
            //如果是形参不可以load 要 alloc + store + load
            Value fromValue = this.tableList.foundDef(exp.lVal.ident.getName());
            int registerNum = curFunction.assignRegister();
            Value result = new VarValue(registerNum, false);
            if (fromValue instanceof ParamVarValue) {
                //形参
                new AllocaInst(curBasicBlock, result);
                new StoreInst(curBasicBlock, fromValue, result);
                int registerNum2 = curFunction.assignRegister();
                Value result2 = new VarValue(registerNum2, false);
                new LoadInst(curBasicBlock, result2, result);
                return result2;
            } else {
                //之前定义的变量
                new LoadInst(curBasicBlock, result, fromValue);
            }
            return result;
        }
        // return new Value();
    }

    public void visitStmtReturn(StmtReturn stmt) {
        BasicBlock curBasicBlock = curFunction.getCurBasicBlock();
        if (stmt.type == 0) {
            RetInst retInst = new RetInst(curBasicBlock);
        } else {
            Exp exp = stmt.exp;
            //根据exp构造value
            Value value = this.visitAddExp(exp.addExp);//形成一系列指令 并将最终的寄存器即Var型Value保存到saveValue中
            RetInst retInst = new RetInst(curBasicBlock, value.getType(), value);
        }
    }

    public void visitStmtBC(StmtBC stmt) {
        //'break' ';' | 'continue' ';'
        //todo 代码生成二
    }

    public void visitStmtBlock(StmtBlock stmt) {
        this.tableList.addTable();
        this.visitBlock(stmt.block);
        this.tableList.popTable();
    }

    public void visitStmtExp(StmtExp stmt) {
        if (stmt.type == 1) {
            this.visitAddExp(stmt.exp.addExp);
        }
    }

    public void visitStmtFor(StmtFor stmt) {
        //todo 代码生成二
    }

    private ArrayList<ArrayList<BasicBlock>> refillBasicBlocks = new ArrayList<>();
    private ArrayList<BasicBlock> tempLevel = new ArrayList<>();
    private BasicBlock realTrueBlock = null;//if成立
    private BasicBlock realFalseBlock = null;//if不成立
    private BasicBlock endBlock = null;

    public void refill() {
        BasicBlock beforeIf = refillBasicBlocks.get(0).get(0);
        //根据文法，肯定是至少有一层lAndExp存在的
        BasicBlock ifBlock = refillBasicBlocks.get(1).get(0);
        new BrInst(beforeIf, ifBlock);
        for (int i = 1; i < refillBasicBlocks.size() - 1; i++) {
            ArrayList<BasicBlock> level = refillBasicBlocks.get(i);//表示一个lAndExp
            for (int j = 0; j < level.size() - 1; j++) {
                //遍历第一个直到倒数第二个EqBlock块
                BasicBlock b = level.get(j);
                new BrInst(b, b.reVar, level.get(j + 1), refillBasicBlocks.get(i + 1).get(0));
                //为True遍历本层的下一个块，为false遍历下一层
            }
            BasicBlock b = level.get(level.size() - 1);
            new BrInst(b, b.reVar, realTrueBlock, refillBasicBlocks.get(i + 1).get(0));
        }
        //最后一个LOrExp
        ArrayList<BasicBlock> level = refillBasicBlocks.get(refillBasicBlocks.size() - 1);
        for (int j = 0; j < level.size() - 1; j++) {
            //遍历第一个直到倒数第二个EqBlock块
            BasicBlock b = level.get(j);
            BasicBlock temp = this.realFalseBlock != null ? realFalseBlock : endBlock;
            new BrInst(b, b.reVar, level.get(j + 1), temp);
            //为True遍历本层的下一个块，为false遍历下一层
        }
        BasicBlock b = level.get(level.size() - 1);
        BasicBlock temp = this.realFalseBlock != null ? realFalseBlock : endBlock;
        new BrInst(b, b.reVar, realTrueBlock, temp);
        //以下，还需要填充realTrueBlock 和 realFalseBlock
        new BrInst(realTrueBlock, endBlock);
        if (realFalseBlock != null) {
            new BrInst(realFalseBlock, endBlock);
        }
        this.clearRefill();
    }

    public void clearRefill() {
        this.refillBasicBlocks = new ArrayList<>();
        this.tempLevel = new ArrayList<>();
        this.realTrueBlock = null;
        this.realFalseBlock = null;
        this.endBlock = null;
    }

    public void visitStmtIf(StmtIf stmt) {
        BasicBlock block0 = curFunction.getCurBasicBlock();
        Cond cond = stmt.cond;
        LOrExp lOrExp = cond.exp;
        //根据cond生成代码放入block0
        //refill
        ArrayList<BasicBlock> zeroLevel = new ArrayList<>();
        zeroLevel.add(curFunction.getCurBasicBlock());
        this.refillBasicBlocks.add(zeroLevel);
        this.visitLOrExp(lOrExp);
        //以下是管理Stmt
        curFunction.addBasicBlock();
        BasicBlock block1 = curFunction.getCurBasicBlock();
        this.realTrueBlock = block1;//refill
        Stmt trueStmt = stmt.stmt;
        this.visitStmt(trueStmt);
        if (stmt.type == 1) {
            curFunction.addBasicBlock();
            BasicBlock block2 = curFunction.getCurBasicBlock();
            this.realFalseBlock = block2;
            Stmt falseStmt = stmt.elseStmt;
            this.visitStmt(falseStmt);
        }
        curFunction.addBasicBlock();
        this.endBlock = curFunction.getCurBasicBlock();
        //之后的代码都放入空新block里
        this.refill(); //回填
        //todo 如果endBlock是空的呢 符合llvm要求吗
    }

    public void visitLOrExp(LOrExp exp) {
        for (int i = 0; i < exp.exps.size(); i++) {
            LAndExp lAndExp = exp.exps.get(i);
            //refill
            tempLevel = new ArrayList<>();
            this.visitLAndExp(lAndExp);
            this.refillBasicBlocks.add(tempLevel);
        }
    }

    public void visitLAndExp(LAndExp exp) {
        for (int i = 0; i < exp.exps.size(); i++) {
            EqExp eqExp = exp.exps.get(i);
            this.visitEqExp(eqExp);
        }
    }

    public void visitEqExp(EqExp exp) {
        //每一个EqExp代表一个新的BasicBlock
        curFunction.addBasicBlock();
        BasicBlock b = curFunction.getCurBasicBlock();
        this.tempLevel.add(b);//refill
        Value result;//eqExp的结果
        if (exp.relExps.size() >= 2) {
            //有两个以上的RelExp
            Value v1 = this.visitRelExp(exp.relExps.get(0));
            Value v2 = this.visitRelExp(exp.relExps.get(1));
            CondString condString = exp.tokens.get(0).getCategory() == Category.EQL ?
                    CondString.eq : CondString.ne;
            int registerNum = curFunction.assignRegister();
            VarValue var = new VarValue(registerNum, false);
            new IcmpInst(b, var, v1, v2, condString);
            int i = 2, j = 1;
            Value pre = var;
            while (i <= exp.relExps.size() && j <= exp.tokens.size()) {
                CondString tempCondString = exp.tokens.get(j).getCategory() == Category.EQL ?
                        CondString.eq : CondString.ne;
                Value v3 = this.visitRelExp(exp.relExps.get(i));
                //分配结果寄存器
                int tempRegisterNum = curFunction.assignRegister();
                VarValue tempVar = new VarValue(tempRegisterNum, false);
                //生成指令
                new IcmpInst(b, tempVar, pre, v3, tempCondString);
            }
            result = pre;
        } else {
            //只有一个RelExp
            result = this.visitRelExp(exp.relExps.get(0));
        }
        //将result 与 0 做 ne 比较
        int r = curFunction.assignRegister();
        VarValue reVar = new VarValue(r, false);
        new IcmpInst(b, reVar, result, new ConstValue("0"), CondString.ne);
        b.reVar = reVar;//refill
    }

    public Value visitRelExp(RelExp relExp) {
        //< slt
        //<= sle
        //> sgt
        //>= sge
        BasicBlock b = curFunction.getCurBasicBlock();
        if (relExp.addExps.size() >= 2) {
            Value v1 = this.visitAddExp(relExp.addExps.get(0));
            Value v2 = this.visitAddExp(relExp.addExps.get(1));
            CondString condString = CondString.eq;//初始化为一个不可能的
            switch (relExp.tokens.get(0).getCategory()) {
                case LSS:
                    condString = CondString.slt;
                    break;
                case LEQ:
                    condString = CondString.sle;
                    break;
                case GRE:
                    condString = CondString.sgt;
                    break;
                case GEQ:
                    condString = CondString.sge;
                    break;
            }
            int registerNum = curFunction.assignRegister();
            VarValue var = new VarValue(registerNum, false);
            new IcmpInst(b, var, v1, v2, condString);
            int i = 2, j = 1;
            Value pre = var;
            while (i < relExp.addExps.size() && j < relExp.tokens.size()) {
                Value v3 = this.visitAddExp(relExp.addExps.get(i));
                CondString temp = CondString.eq;
                switch (relExp.tokens.get(j).getCategory()) {
                    case LSS:
                        temp = CondString.slt;
                        break;
                    case LEQ:
                        temp = CondString.sle;
                        break;
                    case GRE:
                        temp = CondString.sgt;
                        break;
                    case GEQ:
                        temp = CondString.sge;
                        break;
                }
                int tempRegisterNum = curFunction.assignRegister();
                VarValue tempVar = new VarValue(tempRegisterNum, false);
                new IcmpInst(b, tempVar, pre, v3, temp);
            }
            return pre;
        } else {
            //只有一个AddExp
            return this.visitAddExp(relExp.addExps.get(0));
        }
    }

    public void visitStmtLValExp(StmtLValExp stmt) {
        //store
        Value v = this.visitAddExp(stmt.exp.addExp);
        this.storeLValWithValue(stmt.lVal, v);
    }

    public void visitStmtPrint(StmtPrint stmt) {
        BasicBlock curBasicBlock = curFunction.getCurBasicBlock();
        String s = stmt.token.getName();
        ArrayList<Exp> exps = stmt.exps;
        int j = 0;
        int i;
        for (i = 1; i < s.length() - 1; ) {
            //第一个和最后一个字符是“
            if (s.charAt(i) == '%') {
                //%d
                Value v = this.visitAddExp(exps.get(j).addExp);
                j++;
                new LibraryCallInst(curBasicBlock, 3, v);
                i++;
            } else if (s.charAt(i) == '\\') {
                //\n
                new LibraryCallInst(curBasicBlock, 2, new ConstValue("10"));
                i++;
            } else {
                int cAscii = s.charAt(i);
                new LibraryCallInst(curBasicBlock, 2, new ConstValue(String.valueOf(cAscii)));
            }
            i++;
        }
    }

    public void visitStmtRead(StmtRead stmt) {
        BasicBlock curBasicBlock = curFunction.getCurBasicBlock();
        LVal lVal = stmt.lVal;
        int registerNum = curFunction.assignRegister();
        VarValue value = new VarValue(registerNum, false);
        new LibraryCallInst(curBasicBlock, 1, value);
        this.storeLValWithValue(lVal, value);
    }

    private void storeLValWithValue(LVal lVal, Value value) {
        BasicBlock curBasicBlock = curFunction.getCurBasicBlock();
        Value toValue = this.tableList.foundDef(lVal.ident.getName());
        new StoreInst(curBasicBlock, value, toValue);
    }

}
