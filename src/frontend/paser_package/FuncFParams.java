package frontend.paser_package;

import java.util.ArrayList;

public class FuncFParams {
    public ArrayList<FuncFParam> params;
    public FuncFParams(FuncFParam param) {
        this.params = new ArrayList<>();
        this.params.add(param);
    }
    public void appendParam(FuncFParam param){
        this.params.add(param);
    }
}
