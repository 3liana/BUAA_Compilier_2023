package backend;

import middleend.Value;
import middleend.type.LabelType;
import middleend.value.ConstValue;
import middleend.value.GlobalVar;
import middleend.value.VarValue;
import middleend.value.user.instruction.CondString;

import java.util.ArrayList;

public class MipsFactory {
    public ArrayList<String> datas;
    public ArrayList<String> texts;
    public ArrayList<String> macros;

    //    private int t = 0;
    public MipsFactory(ArrayList<String> datas, ArrayList<String> texts, ArrayList<String> macros) {
        this.datas = datas;
        this.texts = texts;
        this.macros = macros;
        this.genIcmpMacro();
    }

    //    public int assignT() {
//        int ans = t;
//        t = (t + 1) % 8;
//        return ans;
//    }
    public void genIcmpMacro() {
        String sSub = "subu $t0,%reg1, %reg2\n    ";
        String s0 = ".macro ";
        String s1_name = "";//宏的名字
        String s2 = " %reg1, %reg2\n";
        String s3 = "    ";
        String s4_bKind = "";//b类跳转的主题
        String s5 = " %reg1, %reg2, true_label  # 如果 reg1 和 reg2 condString，跳转到 not_equal\n";
        String s5_2 = " $t0,true_label\n";
        String s6 = "    li $t0, 0                    # 如果true，将 $t0 设置为 1\n" +
                "    j end_check                  # 跳转到宏的末尾\n" +
                " true_label:\n" +
                "    li $t0, 1                    # 将 $t0 设置为 0\n" +
                "end_check:\n" +
                ".end_macro\n";
        for (CondString consString : CondString.values()) {
            s1_name = "check_" + consString.toString();
            if (consString.toString().equals("eq")) {
                s4_bKind = "BEQ";
                this.macros.add(s0 + s1_name + s2 + s3 + s4_bKind + s5 + s6);
            }
            if (consString.toString().equals("ne")) {
                s4_bKind = "BNE";
                this.macros.add(s0 + s1_name + s2 + s3 + s4_bKind + s5 + s6);
            }
            if (consString.toString().equals("sgt")) {
                s4_bKind = "BGTZ";
                this.macros.add(s0 + s1_name + s2 + s3 +
                        sSub +
                        s4_bKind +
                        s5_2 + s6);
            }
            if (consString.toString().equals("sge")) {
                s4_bKind = "BGEZ";
                this.macros.add(s0 + s1_name + s2 + s3 +
                        sSub +
                        s4_bKind +
                        s5_2 + s6);
            }
            if (consString.toString().equals("slt")) {
                s4_bKind = "BLTZ";
                this.macros.add(s0 + s1_name + s2 + s3 +
                        sSub +
                        s4_bKind +
                        s5_2 + s6);
            }
            if (consString.toString().equals("sle")) {
                s4_bKind = "BLEZ";
                this.macros.add(s0 + s1_name + s2 + s3 +
                        sSub +
                        s4_bKind +
                        s5_2 + s6);
            }
        }
    }

    public void genIcmp(String macroName, String reg1, String reg2) {
        this.texts.add(macroName + " " + reg1 + "," + reg2 + "\n");
    }

    public void genBrWithLabel(String reg, String trueLabel, String falseLabel) {
        this.texts.add("BNE " + reg + ",$zero," + trueLabel + "\n");
        this.genJ(falseLabel);
    }

    public void MinusSpBy4() {
        this.texts.add("addiu $sp,$sp,-4\n");
    }

    public void MinusSpByNum(int num) {
        int minus_num = num * -1;
        this.texts.add("addiu $sp,$sp," + minus_num + "\n");
    }

    public void restoreSpByGap(int gap) {
        this.texts.add("addiu $sp,$sp," + gap + "\n");
    }

    public void genLw(String fromStr, String reg) {
        //t0
        String s0 = "lw " + reg + "," + fromStr + "\n";
        this.texts.add(s0);
    }

    public void genSw(String reg) {
        String s0 = "sw " + reg + "," + "0($sp)\n";
        this.texts.add(s0);
    }

    public void genSwTo(String reg, String toStr) {
        String s0 = "sw " + reg + "," + toStr + "\n";
        this.texts.add(s0);
    }

    public void saveRaBeforeCall(String reg) {
        //String s0 = "move $ra," + reg + "\n";
        String s0 = "move " + reg + ",$ra\n";
        this.texts.add(s0);
    }

    public void genBinary(String op) {
        String s0;
        if (op.equals("div") || op.equals("mult")) {
            s0 = op + " $t1,$t2\n";
        } else {
            s0 = op + " $t0,$t1,$t2\n";
        }
        this.texts.add(s0);
    }

    public void genJ(String label) {
        this.texts.add("j " + label + "\n");
    }

    public void genLi(String fromNum, String reg) {
        String s0 = "li " + reg + "," + fromNum + "\n";
        this.texts.add(s0);
    }
    public void genLa(String fromStr,String reg){
        String s0 = "la " + reg + "," + fromStr + "\n";
        this.texts.add(s0);
    }
    public void genMove(String reg1, String reg2) {
        String s0 = "move " + reg1 + "," + reg2 + "\n";
        this.texts.add(s0);
    }

    public void genMfhi(String reg) {
        String s0 = "mfhi " + reg + "\n";
        this.texts.add(s0);
    }

    public void genMflo(String reg) {
        String s0 = "mflo " + reg + "\n";
        this.texts.add(s0);
    }
    public void genSll2(String reg){
        this.texts.add("sll " + reg + "," + reg + ",2\n");
    }
    public void genError(String string) {
        this.texts.add("error " + string + "\n");
    }

    public void genRead() {
        this.genLiV0(5);
        this.syscall();
    }

    public void genPutch() {
        this.genLiV0(11);
        this.syscall();
    }

    public void genPutint() {
        this.genLiV0(1);
        this.syscall();
    }

    public void genLiV0(int num) {
        this.texts.add("li $v0," + num + "\n");
    }

    public void syscall() {
        this.texts.add("syscall\n");
    }

    public void jrra() {
        this.texts.add("jr $ra\n");
    }

    public void genJal(String label) {
        this.texts.add("jal " + label + "\n");
    }

}
