package backend;

import middleend.type.SureArrayType;
import middleend.value.GlobalVar;

import java.util.ArrayList;

public class GlobalMips {
    //.data部分的全局变量
    private String name;
    private String str;
    private int num;
    private ArrayList<ArrayList<Integer>> initValNums;
    private int type;
    private boolean allZero;
    private GlobalVar globalVar;
    public GlobalMips(String name,String str,int num){
        this.name = name;
        this.str = str;
        this.num = num;
        this.type = 0;
    }
    public GlobalMips(String name, String str, ArrayList<ArrayList<Integer>> initValNums,
                      GlobalVar globalVar){
        this.name = name;
        this.str = str;
        this.initValNums = initValNums;
        this.type = 1;
        this.allZero = globalVar.ax2AllZero;
        if(allZero){
            this.str = "space";
        }
        this.globalVar = globalVar;
    }
    public String toString() {
        if(this.type == 0){
            return name + ": ." + str + " " + num + "\n";
        } else {
            if(allZero){
                StringBuilder sb = new StringBuilder();
                sb.append(name + ": ");
                SureArrayType arrayType = (SureArrayType) globalVar.targetType;
                int space = arrayType.type == 0 ? arrayType.n:arrayType.n*arrayType.m;
                space *= 4;
                sb.append(".space " + space + "\n");
                return sb.toString();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(name + ": ");
            int i;
            for(i = 0;i<this.initValNums.size()-1;i++){
                this.appendLevel(sb,this.initValNums.get(i));
                sb.append(",");
            }
            if(i < this.initValNums.size()){
                this.appendLevel(sb,this.initValNums.get(i));
                sb.append("\n");
            }
            return sb.toString();
        }

    }
    private void appendLevel(StringBuilder sb,ArrayList<Integer> level){
        int i;
        for(i = 0;i < level.size()-1;i++){
            sb.append(level.get(i) + ", ");
        }
        if(i < level.size()){
            sb.append(level.get(i));
            //sb.append(level.get(i) + "\n");
        }
    }
}
