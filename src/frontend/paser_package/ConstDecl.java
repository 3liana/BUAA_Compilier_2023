package frontend.paser_package;

import java.util.ArrayList;

public class ConstDecl {
    private ArrayList<ConstDef> defs;
    public ConstDecl(ConstDef def) {
        this.defs = new ArrayList<>();
        this.defs.add(def);
    }
    public void appendDef(ConstDef def){
        this.defs.add(def);
    }
}
