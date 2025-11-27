public class FinPrograma extends Tupla {

    public FinPrograma() {
        super(-1, -1);
    }

    public String toString() {
        return "( " + super.toString() + ", [], )";
    }

    public int ejecutar(SymbolTable ts) {
        return -1;
    }
}