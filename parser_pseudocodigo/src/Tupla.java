public abstract class Tupla {
    protected int saltoVerdadero, saltoFalso;

    public Tupla(int sv, int sf) {
        saltoVerdadero = sv;
        saltoFalso = sf;
    }

    public void setSaltoVerdadero(int sv) {
        saltoVerdadero = sv;
    }

    public void setSaltoFalso(int sf) {
        saltoFalso = sf;
    }

    public int getSaltoVerdadero() {
        return saltoVerdadero;
    }

    public int getSaltoFalso() {
        return saltoFalso;
    }

    public String toString() {
        String className = this.getClass().getSimpleName();
        return className + ", " + saltoVerdadero + ", " + saltoFalso;
    }

    public abstract int ejecutar(SymbolTable ts);
}