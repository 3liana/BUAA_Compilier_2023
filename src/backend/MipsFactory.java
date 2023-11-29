package backend;

import middleend.Value;
import middleend.value.ConstValue;
import middleend.value.GlobalVar;
import middleend.value.VarValue;

import java.util.ArrayList;

public class MipsFactory {
    public ArrayList<String> datas;
    public ArrayList<String> texts;
//    private int t = 0;

    public MipsFactory(ArrayList<String> datas, ArrayList<String> texts) {
        this.datas = datas;
        this.texts = texts;
    }

//    public int assignT() {
//        int ans = t;
//        t = (t + 1) % 8;
//        return ans;
//    }

    public void MinusSpBy4() {
        this.texts.add("addi $sp,$sp,-4\n");
    }
    public void restoreSpByGap(int gap){
        this.texts.add("addi $sp,$sp,"+gap+"\n");
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
    public void saveRaBeforeCall(String reg){
        //String s0 = "move $ra," + reg + "\n";
        String s0 = "move " + reg + ",$ra\n";
        this.texts.add(s0);
    }

    public void genBinary(String op) {
        String s0;
        if(op.equals("div") || op.equals("mult")){
            s0 = op + " $t1,$t2\n";
        } else {
            s0 = op + " $t0,$t1,$t2\n";
        }
        this.texts.add(s0);
    }

    public void genLi(String fromNum, String reg) {
        String s0 = "li " + reg + "," + fromNum + "\n";
        this.texts.add(s0);
    }
    public void genMove(String reg1,String reg2){
        String s0 = "move " + reg1 + "," + reg2 + "\n";
        this.texts.add(s0);
    }
    public void genMfhi(String reg){
        String s0 = "mfhi " + reg + "\n";
        this.texts.add(s0);
    }
    public void genMflo(String reg){
        String s0 = "mflo " + reg + "\n";
        this.texts.add(s0);
    }
    public void genError(String string){
        this.texts.add("error " + string + "\n");
    }
    public void genRead(){
        this.genLiV0(5);
        this.syscall();
    }
    public void genPutch(){
        this.genLiV0(11);
        this.syscall();
    }
    public void genPutint(){
        this.genLiV0(1);
        this.syscall();
    }
    public void genLiV0(int num){
        this.texts.add("li $v0," + num + "\n");
    }
    public void syscall(){
        this.texts.add("syscall\n");
    }
    public void jrra(){
        this.texts.add("jr $ra\n");
    }
    public void genJal(String label){
        this.texts.add("jal " + label + "\n");
    }

}
