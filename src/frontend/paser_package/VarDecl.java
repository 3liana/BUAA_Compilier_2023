package frontend.paser_package;

import java.util.ArrayList;

public class VarDecl {
    public ArrayList<VarDef> defs;
    public VarDecl(VarDef def) {
        this.defs = new ArrayList<>();
        this.defs.add(def);
    }
    public void appendDef(VarDef def){
        this.defs.add(def);
    }
}
