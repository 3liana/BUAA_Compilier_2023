package frontend.paser_package;

import java.util.ArrayList;

public class CompUnit {
    public ArrayList<Decl> decls;
    public ArrayList<FuncDef> defs;
    public MainFuncDef mainDef;
    public CompUnit(MainFuncDef mainDef,ArrayList<Decl> decls,ArrayList<FuncDef> defs) {
        this.mainDef = mainDef;
        this.decls = decls;
        this.defs = defs;
    }
}
