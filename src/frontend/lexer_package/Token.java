package frontend.lexer_package;

public class Token {
    private Category category;
    private String name;
    public Token(Category category, String name) {
        this.category = category;
        this.name = name;
    }
    public Category getCategory(){
        return this.category;
    }
    public String toString(){
        return this.category.toString() + ' ' + this.name;
    }
    public String getName(){
        return this.name;
    }
}
