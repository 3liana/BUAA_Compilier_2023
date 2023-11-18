package middleend;
import frontend.lexer_package.*;
import frontend.paser_package.*;
import frontend.paser_package.ConstExp;
import middleend.symbol.TableList;
import middleend.value.GlobalVar;

import java.util.ArrayList;

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
    public ArrayList<ArrayList<Integer>> calArrayConstInitVal(ConstInitVal constInitVal){
        ArrayList<ArrayList<Integer>> returnArray = new ArrayList<>();
        //调用此函数
        //不可能为0 即只有一个数组的情况
        if(constInitVal.type == 1){
            return null;
        } else {
            ConstInitVal temp = constInitVal.initVals.get(0);
            if(temp.type == 0){
                //一维数组
                ArrayList<Integer> array = new ArrayList<>();
                for(ConstInitVal initVal:constInitVal.initVals){
                    array.add(this.calConstExp(initVal.exp));
                }
                returnArray.add(array);
            } else {
                //1,2:二维数组
                for(ConstInitVal initVal:constInitVal.initVals){
                    ArrayList<Integer> array = new ArrayList<>();
                    for(ConstInitVal inside: initVal.initVals){
                        array.add(this.calConstExp(inside.exp));
                    }
                    returnArray.add(array);
                }
            }
        }
        return returnArray;
    }
    public ArrayList<ArrayList<Integer>> calArrayInitVal(InitVal initVal){
        ArrayList<ArrayList<Integer>> returnArray = new ArrayList<>();
        //调用此函数
        //不可能为0 即只有一个数组的情况
        if(initVal.type == 1){
            return null;
        } else {
            InitVal temp = initVal.initVals.get(0);
            if(temp.type == 0){
                //一维数组
                ArrayList<Integer> array = new ArrayList<>();
                for(InitVal tempInitVal:initVal.initVals){
                    array.add(this.calAddExp(tempInitVal.exp.addExp));
                }
                returnArray.add(array);
            } else {
                //1,2:二维数组
                for(InitVal tempInitVal:initVal.initVals){
                    ArrayList<Integer> array = new ArrayList<>();
                    for(InitVal inside: tempInitVal.initVals){
                        array.add(this.calAddExp(inside.exp.addExp));
                    }
                    returnArray.add(array);
                }
            }
        }
        return returnArray;
    }
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
        //查符号表
        Value value = this.tableList.foundDef(name);//逐层查找
        return value.getNum();
    }
    //build 针对llvm结构

}
