package Backend;

import Assembly.AsmOperand.Reg;

public class edge {

        public Reg x, y;

        public edge(Reg u, Reg v) {
            this.x = u;
            this.y = v;
        }

        @Override
        public boolean equals(Object it) {
            return (it instanceof edge && ((edge) it).x == x && ((edge) it).y == y);
        }

        @Override
        public int hashCode() {
            return x.toString().hashCode() ^ y.toString().hashCode();
        }

}
