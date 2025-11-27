import java.util.ArrayList;

public class PseudoGenerador {
    private ArrayList<Tupla> tuplas = new ArrayList<>();
    ArrayList<Token> tokens;

    public PseudoGenerador(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public void crearTuplaAsignacion(int indiceInicial, int indiceFinal) {
        if (indiceFinal - indiceInicial == 3) {
            // Asignacion simple: variable = valor
            tuplas.add(new Asignacion(tokens.get(indiceInicial),
                    tokens.get(indiceInicial+2),
                    tuplas.size()+1, tuplas.size()+1));
        }
        else if (indiceFinal - indiceInicial == 5) {
            // Asignacion con operacion: variable = valor1 operador valor2
            tuplas.add(new Asignacion(tokens.get(indiceInicial),
                    tokens.get(indiceInicial+2),
                    tokens.get(indiceInicial+3),
                    tokens.get(indiceInicial+4),
                    tuplas.size()+1, tuplas.size()+1));
        }
    }

    public void crearTuplaLeer(int indiceInicial) {
        tuplas.add(new Leer(tokens.get(indiceInicial),
                tuplas.size()+1, tuplas.size()+1));
    }

    public void crearTuplaEscribir(int indiceInicial, int indiceFinal) {
        if (indiceFinal - indiceInicial == 1) {
            // escribir CADENA o escribir VARIABLE
            tuplas.add(new Escribir(tokens.get(indiceInicial),
                    tuplas.size()+1, tuplas.size()+1));
        }
        else if (indiceFinal - indiceInicial == 3) {
            // escribir CADENA, VARIABLE
            tuplas.add(new Escribir(tokens.get(indiceInicial),
                    tokens.get(indiceFinal-1),
                    tuplas.size()+1, tuplas.size()+1));
        }
    }

    public void crearTuplaComparacion(int indiceInicial) {
        tuplas.add(new Comparacion(tokens.get(indiceInicial),
                tokens.get(indiceInicial+1),
                tokens.get(indiceInicial+2),
                tuplas.size()+1, tuplas.size()+1));
    }

    public void crearTuplaFinPrograma() {
        tuplas.add(new FinPrograma());
    }

    public void conectarSi(int tuplaInicial) {
        int tuplaFinal = tuplas.size()-1;

        if (tuplaInicial >= tuplas.size() || tuplaInicial >= tuplaFinal)
            return;

        tuplas.get(tuplaInicial).setSaltoFalso(tuplaFinal+1);
    }

    public void conectarMientras(int tuplaInicial) {
        int tuplaFinal = tuplas.size()-1;

        if (tuplaInicial >= tuplas.size() || tuplaInicial >= tuplaFinal)
            return;

        tuplas.get(tuplaInicial).setSaltoFalso(tuplaFinal + 1);
        tuplas.get(tuplaFinal).setSaltoVerdadero(tuplaInicial);
        tuplas.get(tuplaFinal).setSaltoFalso(tuplaInicial);

        for (int i = tuplaFinal; i > tuplaInicial; i--) {
            Tupla t = tuplas.get(i);

            if (t instanceof Comparacion && t.getSaltoFalso() == tuplaFinal + 1)
                t.setSaltoFalso(tuplaInicial);
        }
    }

    // Método para crear tuplas del ciclo repite
    public void crearTuplasRepite(int indiceVariable, int indiceInicio, int indiceFin) {
        Token varControl = tokens.get(indiceVariable);
        Token valorInicio = tokens.get(indiceInicio);
        Token varFin = tokens.get(indiceFin);

        // Tupla de inicialización: i = valorInicio
        tuplas.add(new Asignacion(varControl, valorInicio,
                tuplas.size()+1, tuplas.size()+1));
    }

    // Método para crear la comparación del repite
    public void crearComparacionRepite(int indiceVariable, int indiceFin) {
        Token varControl = tokens.get(indiceVariable);
        Token varFin = tokens.get(indiceFin);
        Token operadorMenorIgual = new Token(new TipoToken("OPRELACIONAL", "<="), "<=");

        // Comparación: varControl <= varFin
        tuplas.add(new Comparacion(varControl, operadorMenorIgual, varFin,
                tuplas.size()+1, tuplas.size()+1));
    }

    // Método para crear el incremento del repite
    public void crearIncrementoRepite(int indiceVariable) {
        Token varControl = tokens.get(indiceVariable);
        Token uno = new Token(new TipoToken("NUMERO", "1"), "1");
        Token operadorMas = new Token(new TipoToken("OPARITMETICO", "+"), "+");

        // Incremento: varControl = varControl + 1
        tuplas.add(new Asignacion(varControl, varControl, operadorMas, uno,
                tuplas.size()-1, tuplas.size()-1));
    }

    // Método para conectar las tuplas del repite
    public void conectarRepite(int tuplaInicio, int tuplaComparacion) {
        int tuplaFinal = tuplas.size()-1;

        // La comparación salta al cuerpo si es verdadera, o sale del ciclo si es falsa
        tuplas.get(tuplaComparacion).setSaltoVerdadero(tuplaComparacion + 1);
        tuplas.get(tuplaComparacion).setSaltoFalso(tuplaFinal + 1);

        // La última tupla (incremento) regresa a la comparación
        tuplas.get(tuplaFinal).setSaltoVerdadero(tuplaComparacion);
        tuplas.get(tuplaFinal).setSaltoFalso(tuplaComparacion);
    }

    public ArrayList<Tupla> getTuplas() {
        return tuplas;
    }
}