package backend;

import middleend.IRModule;
import middleend.Operator;
import middleend.Value;
import middleend.value.ConstValue;
import middleend.value.GlobalVar;
import middleend.value.ParamVarValue;
import middleend.value.VarValue;
import middleend.value.user.BasicBlock;
import middleend.value.user.Function;
import middleend.value.user.Instruction;
import middleend.value.user.instruction.*;
import middleend.value.user.instruction.terminateInst.*;

import java.util.ArrayList;
import java.util.HashMap;

public class MipsGenerator {
    private IRModule irModule = IRModule.getModuleInstance();
    public ArrayList<String> datas = new ArrayList<>();
    public ArrayList<String> texts = new ArrayList<>();
    public HashMap<String, Integer> spTable = new HashMap<>();
    // public HashMap<String, String> tempTable = new HashMap<>();
    private MipsFactory mipsFactory = new MipsFactory(datas, texts);
    private int curSp = 0;
    private boolean inMain = false;
    private boolean debug = true;

    public MipsGenerator() {
        this.visitModule();
    }

    public void visitModule() {
        this.initData();
        for (GlobalVar globalVar : irModule.globalVars) {
            this.visitGlobalVar(globalVar);
        }
        this.initText();
        this.inMain = true;
        this.visitFunction(irModule.mainFunction);
        this.inMain = false;
        for (Function function : irModule.functions) {
            this.visitFunction(function);
        }
    }
    private void minusSp() {
        mipsFactory.MinusSpBy4();
        curSp -= 4;
    }
    private void restoreSp(int num){
        mipsFactory.restoreSpByGap(num);
        curSp += num;
    }
    public void visitGlobalVar(GlobalVar globalVar) {
        GlobalMips globalMips;
        if (globalVar.type == 0) {
            globalMips = new GlobalMips(globalVar.name, "word", globalVar.getInitNum());

        } else {
            //数组
            globalMips = new GlobalMips(globalVar.name, "word", globalVar.initValNums);
        }
        this.datas.add(globalMips.toString());
    }

    public void visitFunction(Function function) {
        this.initFunctionLabel(function);
        this.spTable = new HashMap<>();//每个function都要把table清零
        this.curSp = 0;
        // this.tempTable = new HashMap<>();//每个function都要把table清零
        //把形参加入符号表
        int len = function.params.size();
        //0到4存的是$ra
        int place = 4;
        for (int i = len - 1; i >= 0; i--) {
            ParamVarValue v = function.params.get(i);
            spTable.put(v.getMipsName(), place);
            // System.out.println("put " + v.getMipsName() + " in " + place);
            place += 4;
        }
        for (BasicBlock basicBlock : function.basicBlocks) {
            this.visitBasicBlock(basicBlock);
        }

    }

    private void initFunctionLabel(Function function) {
        this.texts.add(function.name + ":\n");
    }

    public void visitBasicBlock(BasicBlock basicBlock) {
        for (Instruction instruction : basicBlock.instructions) {
            this.visitInstruction(instruction);
        }
    }

    public void visitInstruction(Instruction instruction) {
        if (debug) {
            this.texts.add("#");
            this.texts.add(instruction.getPrint());
            System.out.println(instruction.getPrint());
        }
        if (instruction instanceof AllocaInst) {
            this.visitAllocaInst((AllocaInst) instruction);
        }
        if (instruction instanceof LoadInst) {
            this.visitLoadInst((LoadInst) instruction);
        }
        if (instruction instanceof StoreInst) {
            this.visitStoreInst((StoreInst) instruction);
        }
        if (instruction instanceof BinaryInst) {
            this.visitBinaryInst((BinaryInst) instruction);
        }
        if (instruction instanceof RetInst) {
            this.visitRetInst((RetInst) instruction);
        }
        if (instruction instanceof CallInst) {
            this.visitCallInst((CallInst) instruction);
        }
        if (instruction instanceof LibraryCallInst) {
            this.visitLibraryCallInst((LibraryCallInst) instruction);
        }
    }



    //part1
    public void visitAllocaInst(AllocaInst allocaInst) {
        this.minusSp();
        this.spTable.put(allocaInst.result.getMipsName(), curSp);
    }

    public void visitLoadInst(LoadInst loadInst) {
        String fromStr;
        Value fromValue = loadInst.fromValue;
        if (fromValue instanceof GlobalVar) {
            fromStr = fromValue.getMipsName();
        } else {
            String name = fromValue.getMipsName();
            int fromSp = spTable.get(name);
            int gap = fromSp - curSp;
            fromStr = gap + "($sp)";
        }
        String reg = "$t0";
        mipsFactory.genLw(fromStr, reg);
        this.minusSp();
        mipsFactory.genSw(reg);
        this.spTable.put(loadInst.result.getMipsName(), curSp);
    }

    public void visitStoreInst(StoreInst storeInst) {
        Value toValue = storeInst.toValue;
        String name = toValue.getMipsName();
        //如果要改变全局变量
        String toStr;
        if(toValue instanceof GlobalVar){
            toStr = name;
        }
        //
        else {
            int toSp = spTable.get(name);
            int gap = toSp - curSp;
            toStr = gap + "($sp)";
        }
        Value fromValue = storeInst.fromValue;
        String reg = "$t0";
        this.visitValueToReg(reg, fromValue);
        mipsFactory.genSwTo(reg, toStr);
    }

    public void visitBinaryInst(BinaryInst binaryInst) {
        Value result = binaryInst.result;//t0
        Value v1 = binaryInst.op1;//t1
        Value v2 = binaryInst.op2;//t2
        Operator op = binaryInst.operator;

        this.visitValueToReg("$t1", v1);
        this.visitValueToReg("$t2", v2);
        String opStr = op.toMips();
        if (opStr.equals("mod")) {
            mipsFactory.genBinary("div");
            mipsFactory.genMfhi("$t0");
        } else if (opStr.equals("div") || opStr.equals("mult")) {
            //todo div 和 mult都是取低位？
            mipsFactory.genBinary(opStr);
            mipsFactory.genMflo("$t0");
        } else {
            mipsFactory.genBinary(opStr);
        }
        this.minusSp();
        mipsFactory.genSw("$t0");
        this.spTable.put(result.getMipsName(), curSp);
    }

    private void visitValueToReg(String reg, Value value) {
        if (value instanceof ConstValue) {
            mipsFactory.genLi(((ConstValue) value).num, reg);
        } else if (value instanceof GlobalVar) {
            String fromStr = value.getMipsName();
            mipsFactory.genLw(fromStr, reg);
        } else if (value instanceof VarValue || value instanceof ParamVarValue) {
            String name = value.getMipsName();
            //System.out.println(name);
            int fromSp = spTable.get(name);
            int gap = fromSp - curSp;
            String fromStr = gap + "($sp)";
            mipsFactory.genLw(fromStr, reg);
        } else {
            mipsFactory.genError("visitValueToReg");
        }
    }

    public void visitRetInst(RetInst retInst) {
        //return
        //获取返回值
        if(retInst.value != null){
            Value value = retInst.value;
            String reg = "$t0";
            this.visitValueToReg(reg, value);
            mipsFactory.genMove("$v0", reg);
        }
        if (this.inMain) {
            this.texts.add("li $v0,10\n");
            mipsFactory.syscall();
        } else {
            int gap = 0 - curSp;
            this.restoreSp(gap);
            mipsFactory.jrra();
        }
    }
    //part2


    public void visitCallInst(CallInst callInst) {
        String fname = callInst.function.getMipsName();
        //确定实参
//        int count = 0;
//        for(Value v:callInst.rParams){
//            if(count <= 3){
//                String reg = "$a" + count;
//                this.visitValueToReg(reg,v);
//            } else {
//                String reg = "$t0";
//                this.visitValueToReg(reg, v);
//                this.MinusSp\();
//                mipsFactory.genSw(reg);
//            }
//            count++;
//        }
        //全放内存得了
        //传递实参
        for (Value v : callInst.rParams) {
            String reg = "$t0";
            this.visitValueToReg(reg, v);
            this.minusSp();
            mipsFactory.genSw(reg);
        }
        //保存ra
        mipsFactory.saveRaBeforeCall("$t0");
        this.minusSp();
        mipsFactory.genSw("$t0");
        //
        mipsFactory.genJal(fname);
        //恢复ra
        mipsFactory.genLw("0($sp)","$ra");
        this.restoreSp(4);
        //如果参数超过四个 还要恢复内存里给参数的
        int paramGap = 4 * (callInst.rParams.size());
        this.restoreSp(paramGap);
        //
        Value result = callInst.result;
        if (result != null) {
            mipsFactory.genMove("$t0", "$v0");
            this.minusSp();
            mipsFactory.genSw("$t0");
            this.spTable.put(result.getMipsName(), curSp);
        }
    }

    public void visitLibraryCallInst(LibraryCallInst libraryCallInst) {
        Value v = libraryCallInst.value;
        if (libraryCallInst.type == 1) {
            //read
            mipsFactory.genRead();
            this.minusSp();
            mipsFactory.genSw("$v0");
            this.spTable.put(v.getMipsName(), curSp);
        } else if (libraryCallInst.type == 2) {
            //putch
            String reg = "$a0";//放到a0上然后syscall输出
            this.visitValueToReg(reg, v);
            mipsFactory.genPutch();
        } else {
            //putint
            String reg = "$a0";//放到a0上然后syscall输出
            this.visitValueToReg(reg, v);
            mipsFactory.genPutint();
        }
    }

    //part3
    public void visitBrInst(BrInst brInst) {
    }

    public void visitIcmpInst(IcmpInst icmpInst) {
    }

    public void visitZextInst(ZextInst zextInst) {
    }

    private void initData() {
        this.datas.add(".data\n");
    }

    private void initText() {
        this.texts.add(".text\n");
    }

}
