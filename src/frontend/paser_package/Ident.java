package frontend.paser_package;

import frontend.lexer_package.Token;
//是终结符
public class Ident {
    private Token token;
    public Ident(Token token){
        this.token = token;
    }
}
