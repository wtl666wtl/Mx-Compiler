package Backend;

import Assembly.AsmOperand.Reg;

public class edge {

    public Reg x, y;

    public edge(Reg x, Reg y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object it){
        return (it instanceof edge && ((edge) it).x == x && ((edge) it).y == y);
    }

    @Override
    public int hashCode(){
        return x.toString().hashCode() ^ y.toString().hashCode();
    }
}
