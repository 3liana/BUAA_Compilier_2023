package middleend.value.user;

import middleend.Value;

public class LibraryFunction extends Value {
    private String name;
    public LibraryFunction(String name){
        this.name = name;
    }
    public String getTableName(){
        return this.getName();
    }
    public String getName(){
        return "@" + this.name;
    }
}
