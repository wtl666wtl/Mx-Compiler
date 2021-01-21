package Util.scope;

import java.util.ArrayList;
import Util.substance;
import Util.position;

public class funScope extends Scope{
    public ArrayList<substance> parameters = new ArrayList<>();

    public funScope(Scope fScope){
        super(fScope);
    }

    public void addParameter(substance x, position pos){
        parameters.add(x);
        defineMember(x.name, x, pos);
    }
}
