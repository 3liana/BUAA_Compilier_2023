package frontend.paser_package;

import java.util.ArrayList;

public class CompUnit {
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> defs;
    private MainFuncDef mainDef;
    public CompUnit(MainFuncDef mainDef,ArrayList<Decl> decls,ArrayList<FuncDef> defs) {
        this.mainDef = mainDef;
        this.decls = decls;
        this.defs = defs;
    }
}
