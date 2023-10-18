package paser_package;

import lexer_package.Token;
//是终结符
public class Ident {
    private Token token;
    public Ident(Token token){
        this.token = token;
    }
}
