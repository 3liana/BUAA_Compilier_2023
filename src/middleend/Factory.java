package middleend;
import frontend.lexer_package.*;
import frontend.paser_package.*;
import frontend.paser_package.ConstExp;
import middleend.symbol.TableList;
import middleend.value.GlobalVar;

public class Factory {
    //负责辅助Generator 提供一些功能
    private TableList tableList;
    private int registerNum;

    public Factory(TableList tableList) {
        this.tableList = tableList;
        this.registerNum = 0;
    }
//    //虚拟寄存器方面
//    public void refreshRegisterNum(){
//        this.registerNum = 1;//function本身占用了0号编号
//    }
//    public int assignRegisterNum(){
//        return this.registerNum++;
//    }
    // 计算方面
    public int calConstExp(ConstExp constExp) {
        return this.calAddExp(constExp.addExp);
    }

    public int calAddExp(AddExp addExp) {
        int ans = 0;
        int len = addExp.mulExps.size();
        for (int i = 0; i < len; i++) {
            ans += this.calMulExp(addExp.mulExps.get(i)) * addExp.symbols.get(i);
        }
        return ans;
    }

    public int calMulExp(MulExp mulExp) {
        int ans = this.calUnaryExp(mulExp.unaryExps.get(0));
        int len = mulExp.unaryExps.size();
        for (int i = 1; i < len; i++) {
            int num = this.calUnaryExp(mulExp.unaryExps.get(i));
            switch (mulExp.symbols.get(i)) {
                case 1:
                    ans = ans * num;
                    break;
                case 2:
                    ans = ans / num;
                    break;
                case 3:
                    ans = ans % num;
                    break;
            }
        }
        return ans;
    }
    public int calUnaryExp(UnaryExp unaryExp) {
        if(unaryExp.type == 0){
            return this.calPrimaryExp(unaryExp.primaryExp);
        } else if (unaryExp.type == 1){
            //todo 计算函数
            //Ident([FuncRParams])
            return 0;
        } else {
            int ans = this.calUnaryExp(unaryExp.unaryExp);
            switch (unaryExp.unaryOp.op.getCategory()){
                case PLUS:
                    ans = ans;
                    break;
                case MINU:
                    ans = ans * -1;
                    break;
                case NOT:
                    // not unaryExp 不一定这么写
                    ans = ans == 0?1:0;
                    break;
                default:
                    ans = 0;//不会出现
            }
            return ans;
        }
    }
    public int calPrimaryExp(PrimaryExp primaryExp){
        if(primaryExp.type==0){
            return this.calAddExp(primaryExp.exp.addExp);
        }else if(primaryExp.type==1){
            return this.calLVal(primaryExp.lVal);
        } else {
            //INTCON
            return Integer.parseInt(primaryExp.number.token.getName());
        }
    }
    public int calLVal(LVal lVal){
        String name = lVal.ident.getName();
        //查找符号表
        //注意：需要在factory中直接计算出值的只有全局变量，所以只需要在全局变量中查找
        Value value = this.tableList.foundDef(name);
        return ((GlobalVar)value).getNum();
    }
    //build 针对llvm结构

}
