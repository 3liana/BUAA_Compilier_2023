package backend;

public class ValuePlace {
//    public static ValuePlace a0 = new ValuePlace("$a0");
//    public static ValuePlace a1 = new ValuePlace("$a1");
//    public static ValuePlace a2 = new ValuePlace("$a2");
//    public static ValuePlace a3 = new ValuePlace("$a3");
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
