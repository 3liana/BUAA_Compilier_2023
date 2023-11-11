package frontend.paser_package;

import frontend.lexer_package.Token;

public class FuncType {
    public Token token;
    //void|int
    public FuncType(Token token){
        this.token = token;
    }
}
