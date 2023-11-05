package frontend;

import frontend.lexer_package.*;
import frontend.paser_package.*;
import frontend.paser_package.Number;
import frontend.paser_package.stmt_package.*;

import java.util.ArrayList;

public class Parser {
    private final ArrayList<Token> tokens;
    private int pos;
    private ArrayList<String> ans;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
        this.ans = new ArrayList<>();
    }

    public Token getToken() {
        return this.tokens.get(pos);
    }

    public void next() {
        this.ans.add(this.getToken().toString());
        pos++;
    }

    public ArrayList<String> getAns() {
        return this.ans;
    }

    public CompUnit parseCompUnit() {
        ArrayList<Decl> decls = new ArrayList<>();
        ArrayList<FuncDef> defs = new ArrayList<>();
        while ((this.tokens.get(this.pos + 2).getCategory() != Category.LPARENT && this.pos + 2 < this.tokens.size()) &&
                (this.tokens.get(this.pos + 1).getCategory() != Category.MAINTK) && this.pos + 1 < this.tokens.size()) {
            Decl decl = this.parseDecl();
            decls.add(decl);
        }
        while (this.tokens.get(this.pos + 1).getCategory() != Category.MAINTK && this.pos + 1 < this.tokens.size()) {
            FuncDef def = this.parseFuncDef();
            defs.add(def);
        }
        MainFuncDef mainFuncDef = this.parseMainFuncDef();
        CompUnit compUnit = new CompUnit(mainFuncDef, decls, defs);
        this.ans.add("<CompUnit>");
        return compUnit;
    }

    public Cond parseCond() {
        LOrExp exp = this.parseLorExp();
        Cond cond = new Cond(exp);
        this.ans.add("<Cond>");
        return cond;
    }

    public ForStmt parseForStmt() {
        LVal lVal = this.parseLVal();
        this.next();//=
        Exp exp = this.parseExp();
        ForStmt forStmt = new ForStmt(lVal, exp);
        this.ans.add("<ForStmt>");
        return forStmt;
    }

    public RelExp parseRelExp() {
        //规则：如果还有下一个元素就在前一个插入<RelExp>
        AddExp addExp = this.parseAddExp();
        RelExp relExp = new RelExp(addExp);
        int prePos = this.ans.size();
        while (this.getToken().getCategory() == Category.LSS ||
                this.getToken().getCategory() == Category.LEQ ||
                this.getToken().getCategory() == Category.GRE ||
                this.getToken().getCategory() == Category.GEQ) {
            this.ans.add(prePos, "<RelExp>");
            Token t = this.getToken();
            this.next();
            AddExp exp2 = this.parseAddExp();
            relExp.appendAddExp(exp2, t);
            prePos = this.ans.size();
        }
        this.ans.add("<RelExp>");
        return relExp;
    }

    public EqExp parseEqExp() {
        RelExp relExp = this.parseRelExp();
        EqExp eqExp = new EqExp(relExp);
        int prePos = this.ans.size();
        while (this.getToken().getCategory() == Category.EQL ||
                this.getToken().getCategory() == Category.NEQ) {
            this.ans.add(prePos, "<EqExp>");
            Token t = this.getToken();
            this.next();
            RelExp exp2 = this.parseRelExp();
            eqExp.appendRelExp(exp2, t);
            prePos = this.ans.size();
        }
        this.ans.add("<EqExp>");
        return eqExp;
    }

    public LAndExp parseLAndExp() {
        EqExp eqExp = this.parseEqExp();
        LAndExp lAndExp = new LAndExp(eqExp);
        int prePos = this.ans.size();
        while (this.getToken().getCategory() == Category.AND) {
            this.ans.add(prePos, "<LAndExp>");
            this.next();//&&
            EqExp exp2 = this.parseEqExp();
            lAndExp.appendEqExp(exp2);
            prePos = this.ans.size();
        }
        this.ans.add("<LAndExp>");
        return lAndExp;
    }

    public LOrExp parseLorExp() {
        LAndExp lAndExp = this.parseLAndExp();
        LOrExp lOrExp = new LOrExp(lAndExp);
        int prePos = this.ans.size();
        while (this.getToken().getCategory() == Category.OR) {
            this.ans.add(prePos, "<LOrExp>");
            this.next();//&&
            LAndExp exp2 = this.parseLAndExp();
            lOrExp.appendLAndExp(exp2);
            prePos = this.ans.size();
        }
        this.ans.add("<LOrExp>");
        return lOrExp;
    }

    public FuncFParam parseFuncFParam() {
        this.next();//int
        Token t = this.getToken();
        Ident ident = new Ident(t);
        this.next();
        FuncFParam funcFParam = new FuncFParam(ident);
        if (this.getToken().getCategory() == Category.LBRACK) {
            this.next();//[
            this.next();//]
            while (this.getToken().getCategory() == Category.LBRACK) {
                this.next();//[
                ConstExp exp = this.parseConstExp();
                funcFParam.appendExp(exp);
                this.next();//]
            }
        }
        this.ans.add("<FuncFParam>");
        return funcFParam;
    }

    public FuncFParams parseFuncFParams() {
        FuncFParam param = this.parseFuncFParam();
        FuncFParams funcFParams = new FuncFParams(param);
        while (this.getToken().getCategory() == Category.COMMA) {
            this.next();//,
            FuncFParam param2 = this.parseFuncFParam();
            funcFParams.appendParam(param2);
        }
        this.ans.add("<FuncFParams>");
        return funcFParams;
    }

    public MainFuncDef parseMainFuncDef() {
        this.next();
        this.next();
        this.next();
        this.next();
        Block block = this.parseBlock();
        MainFuncDef mainFuncDef = new MainFuncDef(block);
        this.ans.add("<MainFuncDef>");
        return mainFuncDef;
    }

    public FuncDef parseFuncDef() {
        FuncType fType = this.parseFuncType();
        Token t = this.getToken();
        Ident ident = new Ident(t);
        this.next();
        FuncDef funcDef = new FuncDef(fType, ident);
        this.next();//(
        if (this.getToken().getCategory() != Category.RPARENT) {
            FuncFParams funcFParams = this.parseFuncFParams();
            funcDef.addParams(funcFParams);
        }
        this.next();//)
        Block b = this.parseBlock();
        funcDef.addBlock(b);
        this.ans.add("<FuncDef>");
        return funcDef;
    }

    public FuncType parseFuncType() {
        Token t = this.getToken();
        this.next();
        this.ans.add("<FuncType>");
        return new FuncType(t);
    }

    public ConstInitVal parseConstInitVal() {
        ConstInitVal ans;
        if (this.getToken().getCategory() == Category.LBRACE) {
            this.next();//{
            if (this.getToken().getCategory() == Category.RBRACE) {
                this.next();//}
                ans = new ConstInitVal();
            } else {
                ConstInitVal initVal = this.parseConstInitVal();
                ans = new ConstInitVal(initVal);
                while (this.getToken().getCategory() == Category.COMMA) {
                    this.next();//,
                    ConstInitVal initVal2 = this.parseConstInitVal();
                    ans.appendInitVal(initVal2);
                }
                this.next();//}
            }
        } else {
            ConstExp exp = this.parseConstExp();
            ans = new ConstInitVal(exp);
        }
        this.ans.add("<ConstInitVal>");
        return ans;
    }

    public InitVal parseInitVal() {
        InitVal ans;
        if (this.getToken().getCategory() == Category.LBRACE) {
            this.next();//{
            if (this.getToken().getCategory() == Category.RBRACE) {
                this.next();//}
                ans = new InitVal();
            } else {
                InitVal initVal = this.parseInitVal();
                ans = new InitVal(initVal);
                while (this.getToken().getCategory() == Category.COMMA) {
                    this.next();//,
                    InitVal initVal2 = this.parseInitVal();
                    ans.appendInitVal(initVal2);
                }
                this.next();//}
            }
        } else {
            Exp exp = this.parseExp();
            ans = new InitVal(exp);
        }
        this.ans.add("<InitVal>");
        return ans;
    }

    public ConstDef parseConstDef() {
        Ident ident = new Ident(this.getToken());
        this.next();
        ConstDef def = new ConstDef(ident);
        while (this.getToken().getCategory() == Category.LBRACK) {
            this.next();//[
            ConstExp exp = this.parseConstExp();
            def.appendExp(exp);
            this.next();//]
        }
        this.next();//=
        ConstInitVal constInitVal = this.parseConstInitVal();
        def.setInitVal(constInitVal);
        this.ans.add("<ConstDef>");
        return def;
    }

    public VarDef parseVarDef() {
        Ident ident = new Ident(this.getToken());
        this.next();
        VarDef def = new VarDef(ident);
        while (this.getToken().getCategory() == Category.LBRACK) {
            this.next();//[
            ConstExp exp = this.parseConstExp();
            def.appendExp(exp);
            this.next();//]
        }
        if (this.getToken().getCategory() == Category.ASSIGN) {
            this.next();//=
            InitVal val = this.parseInitVal();
            def.setInitVal(val);
        }
        this.ans.add("<VarDef>");
        return def;
    }

    public ConstDecl parseConstDecl() {
        this.next();//const
        this.next();//int
        ConstDef def = this.parseConstDef();
        ConstDecl decl = new ConstDecl(def);
        while (this.getToken().getCategory() == Category.COMMA) {
            this.next();
            ConstDef def2 = this.parseConstDef();
            decl.appendDef(def2);
        }
        this.next();//;
        this.ans.add("<ConstDecl>");
        return decl;
    }

    public VarDecl parseVarDecl() {
        this.next();//int
        VarDef def = this.parseVarDef();
        VarDecl decl = new VarDecl(def);
        while (this.getToken().getCategory() == Category.COMMA) {
            this.next();
            VarDef def2 = this.parseVarDef();
            decl.appendDef(def2);
        }
        this.next();//;
        this.ans.add("<VarDecl>");
        return decl;
    }

    public Decl parseDecl() {
        Decl d;
        if (this.getToken().getCategory() == Category.CONSTTK) {
            ConstDecl cd = this.parseConstDecl();
            d = new Decl(cd);
        } else {
            VarDecl vd = this.parseVarDecl();
            d = new Decl(vd);
        }
        //this.ans.add("<Decl>");
        return d;
    }

    public BlockItem parseBlockItem() {
        BlockItem bi;
        if (this.getToken().getCategory() == Category.CONSTTK ||
                this.getToken().getCategory() == Category.INTTK) {
            Decl d = this.parseDecl();
            bi = new BlockItem(d);
        } else {
            Stmt s = this.parseStmt();
            bi = new BlockItem(s);
        }
        //this.ans.add("<BlockItem>");
        return bi;
    }

    public Block parseBlock() {
        this.next();
        Block block = new Block();
        while (this.getToken().getCategory() != Category.RBRACE) {
            BlockItem b = this.parseBlockItem();
            block.appendBlockItem(b);
        }
        this.next();//}
        this.ans.add("<Block>");
        return block;
    }

    public Stmt parseStmt() {
        Token t = this.getToken();
        Stmt ans;
        switch (t.getCategory()) {
            case IFTK:
                ans = this.parseStmtIf();
                break;
            case FORTK:
                ans = this.parseStmtFor();
                break;
            case BREAKTK, CONTINUETK:
                ans = this.parseStmtBC();
                break;
            case RETURNTK:
                ans = this.parseStmtReturn();
                break;
            case PRINTFTK:
                ans = this.parseStmtPrint();
                break;
            case LBRACE:
                ans = this.parseStmtBlock();
                break;
            case IDENFR:
                int i;
                boolean isLVal = false;
                for (i = 1; this.pos + i < this.tokens.size(); i++) {
                    if (this.tokens.get(i + this.pos).getCategory() == Category.SEMICN) {
                        break;
                    }
                    if (this.tokens.get(i + this.pos).getCategory() == Category.ASSIGN) {
                        //比起；先遇见= 说明是LVal = Exp 或者 LVal = getint()
                        isLVal = true;
                        break;
                    }
                }
                if (isLVal){
                    //i + this.pos 是等于号的位置
                    if(i + this.pos + 1 < this.tokens.size() &&
                    this.tokens.get(i + this.pos + 1).getCategory() == Category.GETINTTK){
                        //=的下一个是getint
                        ans = this.parseStmtRead();
                    } else {
                        ans = this.parseStmtLValExp();
                    }
                    //分发给stmtRead或stmtLValEql
                } else {
                    ans = this.parseStmtExp();
                }
                break;
            default:
                ans = this.parseStmtExp();
        }
        this.ans.add("<Stmt>");
        return ans;
    }

    public StmtFor parseStmtFor() {
        StmtFor stmt = new StmtFor();
        this.next();//for
        this.next();//(
        if (this.getToken().getCategory() != Category.SEMICN) {
            ForStmt forStmt1 = this.parseForStmt();
            stmt.addFor1(forStmt1);
        }
        this.next();//;
        if (this.getToken().getCategory() != Category.SEMICN) {
            Cond cond = this.parseCond();
            stmt.addCond(cond);
        }
        this.next();//;
        if (this.getToken().getCategory() != Category.RPARENT) {
            ForStmt forStmt2 = this.parseForStmt();
            stmt.addFor2(forStmt2);
        }
        this.next();//)
        Stmt childStmt = this.parseStmt();
        stmt.addStmt(childStmt);
        return stmt;
    }

    public StmtIf parseStmtIf() {
        this.next();//if
        this.next();//(
        Cond cond = this.parseCond();
        this.next();//)
        Stmt stmt1 = this.parseStmt();
        StmtIf stmtIf = new StmtIf(cond, stmt1);
        if (this.getToken().getCategory() == Category.ELSETK) {
            this.next();//else
            Stmt stmt2 = this.parseStmt();
            stmtIf.addElse(stmt2);
        }
        return stmtIf;
    }

    public StmtBC parseStmtBC() {
        Token t = this.getToken();
        this.next();
        StmtBC stmt = new StmtBC(t);
        this.next();//;
        return stmt;
    }

    public StmtLValExp parseStmtLValExp() {
        LVal lVal = this.parseLVal();
        this.next();//跳过并输出=
        Exp exp = this.parseExp();
        this.next();//;
        return new StmtLValExp(lVal, exp);
    }

    public StmtExp parseStmtExp() {
        if (this.getToken().getCategory() == Category.SEMICN) {
            this.next();
            return new StmtExp();
        } else {
            Exp exp = this.parseExp();
            this.next();//跳过并输出；
            return new StmtExp(exp);
        }
    }

    public StmtBlock parseStmtBlock() {
        Block block = this.parseBlock();
        return new StmtBlock(block);
    }

    public StmtReturn parseStmtReturn() {
        this.next();//跳过并输出returntk
        if (this.getToken().getCategory() == Category.SEMICN) {
            this.next();
            return new StmtReturn();
        } else {
            Exp exp = this.parseExp();
            this.next();//跳过并输出；
            return new StmtReturn(exp);
        }
    }

    public StmtRead parseStmtRead() {
        LVal lVal = this.parseLVal();
        this.next();//跳过并输出=
        this.next();
        this.next();
        this.next();
        this.next();
        return new StmtRead(lVal);
    }

    public StmtPrint parseStmtPrint() {
        this.next();//printf
        this.next();//(
        Token format = this.getToken();
        StmtPrint stmt = new StmtPrint(format);
        this.next();
        while (this.getToken().getCategory() == Category.COMMA) {
            this.next();
            Exp exp = this.parseExp();
            stmt.appendExp(exp);
        }
        this.next();//)
        this.next();//;
        return stmt;
    }

    public ConstExp parseConstExp() {
        AddExp addExp = this.parseAddExp();
        this.ans.add("<ConstExp>");
        return new ConstExp(addExp);
    }

    public FuncRParams parseFuncRParams() {
        Exp exp = this.parseExp();
        FuncRParams rParams = new FuncRParams(exp);
        while (this.getToken().getCategory() == Category.COMMA) {
            this.next();//跳过并输出，
            Exp exp2 = this.parseExp();
            rParams.appendExp(exp2);
        }
        this.ans.add("<FuncRParams>");
        return rParams;
    }

    public UnaryExp parseUnaryExp() {
        Token t = this.getToken();
        UnaryExp ans = new UnaryExp();
        switch (t.getCategory()) {
            case PLUS, MINU, NOT:
                UnaryOp unaryOp = this.parseUnaryOp();
                UnaryExp unaryExp = this.parseUnaryExp();
                ans = new UnaryExp(unaryOp, unaryExp);
                break;
            case IDENFR:
                if (this.pos + 1 < this.tokens.size() && this.tokens.get(this.pos + 1).getCategory() == Category.LPARENT) {
                    Ident ident = new Ident(t);
                    this.next();//跳过ident
                    this.next();//跳过（
                    if (this.getToken().getCategory() != Category.RPARENT) {
                        FuncRParams rParams = this.parseFuncRParams();
                        ans = new UnaryExp(ident, rParams);
                    } else {
                        ans = new UnaryExp(ident);
                    }
                    this.next();//跳过并输出右括号
                } else {
                    PrimaryExp exp = this.parsePrimaryExp();
                    ans = new UnaryExp(exp);
                }
                break;
            default:
                PrimaryExp exp = this.parsePrimaryExp();
                ans = new UnaryExp(exp);
        }
        this.ans.add("<UnaryExp>");
        return ans;
    }

    public MulExp parseMulExp() {
        UnaryExp unaryExp = this.parseUnaryExp();
        MulExp mulExp = new MulExp(unaryExp, 1);
        int prePos = this.ans.size();
        while (this.getToken().getCategory() == Category.MULT ||
                this.getToken().getCategory() == Category.DIV ||
                this.getToken().getCategory() == Category.MOD) {
            this.ans.add(prePos, "<MulExp>");
            this.next();
            UnaryExp exp2 = this.parseUnaryExp();
            switch (this.getToken().getCategory()) {
                case MULT:
                    mulExp.appendUnaryExp(exp2, 1);
                    break;
                case DIV:
                    mulExp.appendUnaryExp(exp2, 2);
                    break;
                case MOD:
                    mulExp.appendUnaryExp(exp2, 3);
            }
            prePos = this.ans.size();
        }
        this.ans.add("<MulExp>");
        return mulExp;
    }

    public AddExp parseAddExp() {
        //规则：如果还有下一个元素就在前一个插入<AddExp>
        MulExp mulExp = this.parseMulExp();
        AddExp addExp = new AddExp(mulExp, 1);
        int prePos = this.ans.size();
        while (this.getToken().getCategory() == Category.PLUS ||
                this.getToken().getCategory() == Category.MINU) {
            this.ans.add(prePos, "<AddExp>");
            if (this.getToken().getCategory() == Category.PLUS) {
                this.next();
                MulExp exp2 = this.parseMulExp();
                addExp.appendMulExp(exp2, 1);
            } else {
                this.next();
                MulExp exp2 = this.parseMulExp();
                addExp.appendMulExp(exp2, -1);
            }
            prePos = this.ans.size();
        }
        this.ans.add("<AddExp>");
        return addExp;
    }

    public Exp parseExp() {
        AddExp addExp = this.parseAddExp();
        this.ans.add("<Exp>");
        return new Exp(addExp);
    }

    public LVal parseLVal() {
        Token t = this.getToken();
        Ident ident = new Ident(t);
        this.next();
        LVal lVal = new LVal(ident);
        while (this.getToken().getCategory() == Category.LBRACK) {
            this.next();//跳过并录入左括号
            Exp exp = this.parseExp();
            lVal.appendExp(exp);
            this.next();//跳过并录入右括号
        }
        this.ans.add("<LVal>");
        return lVal;
    }

    public Number parseNumber() {
        Token t = this.getToken();
        //todo 检查
        this.next();
        this.ans.add("<Number>");
        return new Number(t);
    }

    public UnaryOp parseUnaryOp() {
        Token t = this.getToken();
        //todo 检查是
        this.next();
        this.ans.add("<UnaryOp>");
        return new UnaryOp(t);
    }

    public PrimaryExp parsePrimaryExp() {
        Token t = this.getToken();
        PrimaryExp primaryExp;
        switch (t.getCategory()) {
            case LPARENT:
                this.next();//跳过左括号
                Exp exp = this.parseExp();
                primaryExp = new PrimaryExp(exp);
                this.next();//跳过右括号
                break;
            case IDENFR:
                LVal lVal = this.parseLVal();
                primaryExp = new PrimaryExp(lVal);
                break;
            case INTCON:
                Number number = this.parseNumber();
                primaryExp = new PrimaryExp(number);
                break;
            default:
                primaryExp = new PrimaryExp();//报错
        }
        this.ans.add("<PrimaryExp>");
        return primaryExp;
    }

}
