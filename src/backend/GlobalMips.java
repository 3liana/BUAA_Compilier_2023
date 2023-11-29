package backend;

import java.util.ArrayList;

public class GlobalMips {
    //.data部分的全局变量
    private String name;
    private String str;
    private int num;
    private ArrayList<ArrayList<Integer>> initValNums;
    private int type;
    public GlobalMips(String name,String str,int num){
        this.name = name;
        this.str = str;
        this.num = num;
        this.type = 0;
    }
    public GlobalMips(String name,String str,ArrayList<ArrayList<Integer>> initValNums){
        this.name = name;
        this.str = str;
        this.initValNums = initValNums;
        this.type = 1;
    }
    public String toString() {
        if(this.type == 0){
            return name + ": ." + str + " " + num + "\n";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(name + ": ");
            for(int i = 0;i<this.initValNums.size();i++){
                this.appendLevel(sb,this.initValNums.get(i));
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
            sb.append(level.get(i) + "\n");
        }
    }
}
