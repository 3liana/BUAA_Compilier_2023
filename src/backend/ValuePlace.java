package backend;

public class ValuePlace {
    private int spPlace = 1;
    private String regPlace = null;
    private int type;
    public ValuePlace(int num){
        this.spPlace = num;
        this.type = 0;
    }
    public ValuePlace(String reg){
        this.regPlace = reg;
        this.type = 1;
    }
    public String getPlace(int curSp){
        if(this.type == 0){
            int gap = this.spPlace - curSp;
            return gap + "($sp)";
        } else {
            return this.regPlace;
        }
    }
}
