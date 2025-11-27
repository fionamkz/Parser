import java.util.ArrayList;

public class PseudoParser {
    private ArrayList<Token> tokens;
    private int indiceToken = 0;
    private SyntaxException ex;
    private SymbolTable symbolTable;
    private PseudoGenerador generador;

    public PseudoParser() {
        symbolTable = new SymbolTable();
    }

    public void analizar(PseudoLexer lexer) throws SyntaxException {
        tokens = lexer.getTokens();
        symbolTable = new SymbolTable();
        generador = new PseudoGenerador(tokens);
        indiceToken = 0;
        ex = null;

        if(Programa()){
            if(indiceToken == tokens.size()){
                System.out.println("\nLa sintaxis del programa es correcta");
                System.out.println("\nTabla de símbolos:");
                System.out.println(symbolTable);
                return;
            }
        }

        // Si ex es null, crear una excepción genérica
        if(ex == null) {
            throw new SyntaxException("Error de sintaxis en el análisis");
        }
        throw ex;
    }

    // Método para obtener las tuplas generadas
    public ArrayList<Tupla> getTuplas() {
        if(generador != null) {
            return generador.getTuplas();
        }
        return new ArrayList<Tupla>();
    }

    // Método para obtener la tabla de símbolos
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    //<Programa> -> inicio-programa <Enunciados> fin-programa
    private boolean Programa(){
        if(match("INICIOPROGRAMA"))
            if(Enunciados())
                if(match("FINPROGRAMA")) {
                    generador.crearTuplaFinPrograma();
                    return true;
                }
        return false;
    }

    //<Enunciados> -> <Enunciado> <Enunciados>
    private boolean Enunciados(){
        int indiceAux = indiceToken;

        if(Enunciado()){
            while(Enunciado());
            return true;
        }
        indiceToken = indiceAux;
        return false;
    }

    //<Enunciado> -> <Asignacion> | <Leer> | <Escribir> | <Si> | <Mientras> | <Declaracion>
    private boolean Enunciado(){
        int indiceAux = indiceToken;

        // Verificar si es una declaración de variables
        if(Declaracion())
            return true;

        indiceToken = indiceAux;
        if(tokens.get(indiceToken).getTipo().getNombre().equals("VARIABLE"))
            if(Asignacion()) {
                String variableName = tokens.get(indiceAux).getNombre();
                Symbol symbol = symbolTable.resolve(variableName);
                if(symbol == null) {
                    System.out.println("Advertencia: Variable '" + variableName + "' usada pero no declarada");
                }
                return true;
            }

        indiceToken = indiceAux;
        if(tokens.get(indiceToken).getTipo().getNombre().equals("LEER"))
            if(Leer()) {
                if(indiceAux + 1 < tokens.size()) {
                    String variableName = tokens.get(indiceAux + 1).getNombre();
                    Symbol symbol = symbolTable.resolve(variableName);
                    if(symbol == null) {
                        System.out.println("Advertencia: Variable '" + variableName + "' usada en LEER pero no declarada");
                    }
                }
                return true;
            }

        indiceToken = indiceAux;
        if(tokens.get(indiceToken).getTipo().getNombre().equals("ESCRIBIR"))
            if(Escribir())
                return true;

        indiceToken = indiceAux;
        if(tokens.get(indiceToken).getTipo().getNombre().equals("SI"))
            if(Si())
                return true;

        indiceToken = indiceAux;
        if(tokens.get(indiceToken).getTipo().getNombre().equals("MIENTRAS"))
            if(Mientras())
                return true;

        indiceToken = indiceAux;
        if(tokens.get(indiceToken).getTipo().getNombre().equals("REPITE"))
            if(Repetir())
                return true;

        indiceToken = indiceAux;
        return false;
    }

    // <Declaracion> -> variables <Lista_Variables> | variables : <Lista_Variables>
    private boolean Declaracion() {
        if(match("DECLARAR")) {
            // Opcionalmente puede haber dos puntos
            match("DOSPUNTOS"); // No importa si falla, es opcional

            if(ListaVariables()) {
                return true;
            }
        }
        return false;
    }

    // <Lista_Variables> -> VARIABLE | VARIABLE , <Lista_Variables>
    private boolean ListaVariables() {
        if(tokens.get(indiceToken).getTipo().getNombre().equals("VARIABLE")) {
            String variableName = tokens.get(indiceToken).getNombre();
            match("VARIABLE");

            Type intType = (Type)symbolTable.resolve("int");
            VariableSymbol varSymbol = new VariableSymbol(variableName, intType);
            symbolTable.define(varSymbol);

            if(match("COMA")) {
                return ListaVariables();
            }

            return true;
        }
        return false;
    }

    //<Asignacion> -> VARIABLE = <Expresion>
    private boolean Asignacion(){
        int indiceAux = indiceToken;

        if(match("VARIABLE"))
            if(match("IGUAL"))
                if(Expresion()) {
                    generador.crearTuplaAsignacion(indiceAux, indiceToken);
                    return true;
                }

        indiceToken = indiceAux;
        return false;
    }

    //<Expresion> -> <Valor> <Operador aritmetico> <Valor> | <Valor>
    private boolean Expresion(){
        int indiceAux = indiceToken;

        if(Valor())
            if(match("OPARITMETICO"))
                if(Valor())
                    return true;

        indiceToken = indiceAux;

        if(Valor())
            return true;

        indiceToken = indiceAux;
        return false;
    }

    //<Valor> -> VARIABLE | NUMERO
    private boolean Valor(){
        int indiceAux = indiceToken;

        if(match("VARIABLE")) {
            String variableName = tokens.get(indiceAux).getNombre();
            Symbol symbol = symbolTable.resolve(variableName);
            if(symbol == null) {
                System.out.println("Advertencia: Variable '" + variableName + "' usada pero no declarada");
            }
            return true;
        }

        if(match("NUMERO"))
            return true;

        return false;
    }

    //<Leer> -> leer VARIABLE
    private boolean Leer(){
        int indiceAux = indiceToken;

        if(match("LEER"))
            if(match("VARIABLE")) {
                generador.crearTuplaLeer(indiceAux+1);
                return true;
            }

        indiceToken = indiceAux;
        return false;
    }

    //<Escribir> -> escribir CADENA , VARIABLE | escribir CADENA | escribir VARIABLE
    private boolean Escribir() {
        int indiceAux = indiceToken;

        if (match("ESCRIBIR"))
            if (match("CADENA"))
                if (match("COMA"))
                    if (match("VARIABLE")) {
                        generador.crearTuplaEscribir(indiceAux+1, indiceToken);
                        return true;
                    }

        indiceToken = indiceAux;

        if (match("ESCRIBIR"))
            if (match("CADENA")) {
                generador.crearTuplaEscribir(indiceAux+1, indiceToken);
                return true;
            }

        indiceToken = indiceAux;

        if (match("ESCRIBIR"))
            if (match("VARIABLE")) {
                String variableName = tokens.get(indiceToken - 1).getNombre();
                Symbol symbol = symbolTable.resolve(variableName);
                if(symbol == null) {
                    System.out.println("Advertencia: Variable '" + variableName + "' usada en ESCRIBIR pero no declarada");
                }
                generador.crearTuplaEscribir(indiceAux+1, indiceToken);
                return true;
            }

        indiceToken = indiceAux;
        return false;
    }

    //<Si> -> si <Comparacion> entonces <Enunciados> fin-si
    private boolean Si(){
        int indiceAux = indiceToken;
        int indiceTupla = generador.getTuplas().size();

        if(match("SI"))
            if(Comparacion())
                if(match("ENTONCES"))
                    if(Enunciados())
                        if(match("FINSI")) {
                            generador.conectarSi(indiceTupla);
                            return true;
                        }

        indiceToken = indiceAux;
        return false;
    }

    //<Mientras> -> mientras <Comparacion> <Enunciados> fin-mientras
    private boolean Mientras(){
        int indiceAux = indiceToken;
        int indiceTupla = generador.getTuplas().size();

        if(match("MIENTRAS"))
            if(Comparacion())
                if(Enunciados())
                    if(match("FINMIENTRAS")) {
                        generador.conectarMientras(indiceTupla);
                        return true;
                    }

        indiceToken = indiceAux;
        return false;
    }

    //<Repetir> -> repite ( VARIABLE , NUMERO , VARIABLE ) <Enunciados> fin-repite
    private boolean Repetir(){
        int indiceAux = indiceToken;

        if(match("REPITE"))
            if(match("PARENTESISIZQ")) {
                int indiceVarControl = indiceToken;
                if(match("VARIABLE"))
                    if(match("COMA")) {
                        int indiceInicio = indiceToken;
                        if(match("NUMERO"))
                            if(match("COMA")) {
                                int indiceFin = indiceToken;
                                if(match("VARIABLE"))
                                    if(match("PARENTESISDER")) {
                                        // Crear tupla de inicialización
                                        generador.crearTuplasRepite(indiceVarControl, indiceInicio, indiceFin);

                                        // Crear tupla de comparación
                                        int tuplaComparacion = generador.getTuplas().size();
                                        generador.crearComparacionRepite(indiceVarControl, indiceFin);

                                        if(Enunciados())
                                            if(match("FINREPITE")) {
                                                // Crear tupla de incremento
                                                generador.crearIncrementoRepite(indiceVarControl);

                                                // Conectar las tuplas del repite
                                                int tuplaInicio = tuplaComparacion - 1;
                                                generador.conectarRepite(tuplaInicio, tuplaComparacion);

                                                return true;
                                            }
                                    }
                            }
                    }
            }

        indiceToken = indiceAux;
        return false;
    }

    //<Comparacion> -> (<Valor> <Operador relacional> <Valor>)
    private boolean Comparacion(){
        int indiceAux = indiceToken;

        if(match("PARENTESISIZQ"))
            if(Valor())
                if(match("OPRELACIONAL"))
                    if(Valor())
                        if(match("PARENTESISDER")) {
                            generador.crearTuplaComparacion(indiceAux+1);
                            return true;
                        }

        indiceToken = indiceAux;
        return false;
    }

    private boolean match(String nombre){
        if(indiceToken < tokens.size() && tokens.get(indiceToken).getTipo().getNombre().equals(nombre)){
            System.out.println(nombre + ": "+ tokens.get(indiceToken).getNombre());

            indiceToken++;
            return true;
        }
        if(ex==null)
            ex = new SyntaxException(nombre,
                    indiceToken < tokens.size() ? tokens.get(indiceToken).getTipo().getNombre() : "EOF");

        return false;
    }

    public class SyntaxException extends Exception {
        public SyntaxException(String message){
            super(message);
        }

        public SyntaxException(String message1, String message2){
            super("Se esperaba un token '" + message1 +
                    "' y se encontro '" + message2 + "'");
        }
    }
}