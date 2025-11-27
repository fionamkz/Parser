import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Leer extends Tupla {
    Token variable;

    public Leer(Token variable, int sv, int sf) {
        super(sv, sf);
        this.variable = variable;
    }

    public String toString() {
        return "( " + super.toString() + ", [ " + variable + " ] )";
    }

    public int ejecutar(SymbolTable ts) {
        String valor = "0.0";

        System.out.print("Da un valor para " + variable.getNombre() + " ");
        BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));

        try {
            valor = entrada.readLine();
        } catch (Exception ex) {}

        VariableSymbol v = (VariableSymbol) ts.resolve(variable.getNombre());

        try {
            v.setValor(Float.parseFloat(valor));
        } catch (NumberFormatException exception) {
            System.out.println("Error: Número inválido");
        }

        return saltoVerdadero;
    }
}