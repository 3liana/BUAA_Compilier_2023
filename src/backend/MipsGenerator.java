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
    public ArrayList<String> macros = new ArrayList<>();
    public HashMap<String, Integer> spTable = new HashMap<>();
    // public HashMap<String, String> tempTable = new HashMap<>();
    public HashMap<String, Integer> basicBlockSpTable = new HashMap<>();
    private MipsFactory mipsFactory = new MipsFactory(datas, texts, macros);
    private int curSp = 0;
    private boolean inMain = false;
    private boolean debug = true;
    private String curFunction;
    private int curBlockNum;

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

    private void restoreSp(int num) {
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
        this.basicBlockSpTable = new HashMap<>();
        //把形参加入符号表
        int len = function.params.size();
        //0到4存的是$ra
        this.texts.add("move $s6,$s7\n");//*
        this.texts.add("move $s7,$sp\n");//*
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
        this.texts.add("#function " + function.name + ":\n");
        this.texts.add(function.name + ":\n");
        this.curFunction = function.name;
    }

    private void initBasicBlockLabel(BasicBlock basicBlock) {
        if (!basicBlock.isFirst) {
            String s0 = curFunction + "_block" + basicBlock.registerNum;
            this.basicBlockSpTable.put(s0,curSp);
            this.curBlockNum = basicBlock.registerNum;
            this.texts.add("#block " + basicBlock.registerNum + ":\n");
            this.texts.add(s0 + ":\n");
        }
    }

    public void visitBasicBlock(BasicBlock basicBlock) {
        this.initBasicBlockLabel(basicBlock);
        //恢复block的sp
//        int originalSp = 12284;
//        int nowSp = originalSp + curSp;
//        this.texts.add("li $sp," + nowSp + "\n");
        String orginalReg = "$s7";//最开始的sp被保存在t7里
        this.texts.add("addi $sp," + orginalReg + "," + curSp + "\n");
        //this.texts.add("addi $sp,$sp," + curSp + "\n");
        for (Instruction instruction : basicBlock.instructions) {
            this.visitInstruction(instruction);
        }
    }

    public void visitInstruction(Instruction instruction) {
        if (debug) {
            this.texts.add("#");
            this.texts.add(instruction.getPrint());
            //System.out.println(instruction.getPrint());
        }
        if (instruction instanceof AllocaInst) {
            this.visitAllocaInst((AllocaInst) instruction);
        } else if (instruction instanceof LoadInst) {
            this.visitLoadInst((LoadInst) instruction);
        } else if (instruction instanceof StoreInst) {
            this.visitStoreInst((StoreInst) instruction);
        } else if (instruction instanceof BinaryInst) {
            this.visitBinaryInst((BinaryInst) instruction);
        } else if (instruction instanceof RetInst) {
            this.visitRetInst((RetInst) instruction);
        } else if (instruction instanceof CallInst) {
            this.visitCallInst((CallInst) instruction);
        } else if (instruction instanceof LibraryCallInst) {
            this.visitLibraryCallInst((LibraryCallInst) instruction);
        } else if (instruction instanceof IcmpInst) {
            this.visitIcmpInst((IcmpInst) instruction);
        } else if (instruction instanceof ZextInst) {
            this.visitZextInst((ZextInst) instruction);
        } else if(instruction instanceof BrInst){
            this.visitBrInst((BrInst) instruction);
        }
        else {
            System.err.println("unknown instruction type " + instruction.getClass().getName());
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
            //param
            //VarValue
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
        if (toValue instanceof GlobalVar) {
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
            //div 和 mult都是取低位
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
        if (retInst.value != null) {
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
            this.texts.add("move $s7,$s6\n");//*
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
        mipsFactory.genLw("0($sp)", "$ra");
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
        if (brInst.type == 1) {
            //无条件跳转
            String label = getJumpToLable(brInst.dest);
//            int toNum = ((BasicBlock)brInst.dest).registerNum;
//            if(toNum < this.curBlockNum){
//                int gap = this.basicBlockSpTable.get(label) - curSp;
//                mipsFactory.restoreSpByGap(gap);
//                //往前跳，恢复当时的sp
//            }
            mipsFactory.genJ(label);
        } else {
            //0 : br i1 <condValue>, label <iftrue>, label <iffalse>
            Value condValue = brInst.condValue;
            String reg = "$t0";
            this.visitValueToReg(reg, condValue);
            //生成一条BNE + 生成一条 j
            mipsFactory.genBrWithLabel(reg,
                    this.getJumpToLable(brInst.ifTure),
                    this.getJumpToLable(brInst.ifFalse));
        }
    }

    private String getJumpToLable(Value block) {
        return curFunction + "_block" + ((BasicBlock) block).registerNum;
    }

    private String getIcmpMacro(CondString condString) {
        return "check_" + condString;
    }

    public void visitIcmpInst(IcmpInst icmpInst) {
        Value result = icmpInst.result;
        Value v0 = icmpInst.v0;//save to t1
        Value v1 = icmpInst.v1;//save t0 t2
        String reg1 = "$t1";
        String reg2 = "$t2";
        this.visitValueToReg(reg1, v0);
        this.visitValueToReg(reg2, v1);
        mipsFactory.genIcmp(this.getIcmpMacro(icmpInst.cond), reg1, reg2);
        //保存结果
        this.minusSp();
        mipsFactory.genSw("$t0");//宏把比较的结果保存在了t0上面
        this.spTable.put(result.getMipsName(), curSp);
    }

    public void visitZextInst(ZextInst zextInst) {
        Value result = zextInst.result;
        Value fromValue = zextInst.fromValue;
        String reg = "$t0";
        this.visitValueToReg(reg, fromValue);
        //保存结果
        this.minusSp();
        mipsFactory.genSw("$t0");//宏把比较的结果保存在了t0上面
        this.spTable.put(result.getMipsName(), curSp);
    }

    //part4 数组相关
    private void initData() {
        this.datas.add(".data\n");
    }

    private void initText() {
        this.texts.add(".text\n");
        //this.texts.add("move $t7,$sp\n");//
    }

}
