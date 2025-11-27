/**Represents a variable definition (name,type) in symbol table*/
public class VariableSymbol extends Symbol {
    private float valor = 0;

    public VariableSymbol(String name, Type type) {
        super(name, type);
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public float getValor() {
        return valor;
    }
}