import java.util.ArrayList;

public class TraductorPseudo {
    private ArrayList<Token> tokens;
    private int indiceToken = 0;
    private StringBuilder codigoPython;
    private StringBuilder codigoC;
    private int nivelIndentacion = 0;
    private ArrayList<String> variablesDeclaradas;

    public TraductorPseudo() {
        codigoPython = new StringBuilder();
        codigoC = new StringBuilder();
        variablesDeclaradas = new ArrayList<>();
    }

    public void traducir(PseudoLexer lexer) throws Exception {
        tokens = lexer.getTokens();
        indiceToken = 0;
        codigoPython = new StringBuilder();
        codigoC = new StringBuilder();
        variablesDeclaradas = new ArrayList<>();
        nivelIndentacion = 0;

        if(Programa()) {
            if(indiceToken == tokens.size()) {
                System.out.println("\nTraducido correctamente.\n");
                return;
            }
        }
        throw new Exception("Error en la traducción");
    }

    public String getCodigoPython() {
        return codigoPython.toString();
    }

    public String getCodigoC() {
        return codigoC.toString();
    }

    private String getIndentacion() {
        return "    ".repeat(nivelIndentacion);
    }

    //<Programa> -> inicio-programa <Enunciados> fin-programa
    private boolean Programa() {
        if(match("INICIOPROGRAMA")) {
            // Encabezado Python
            codigoPython.append("# Programa traducido desde pseudocódigo\n");
            codigoPython.append("def main():\n");
            nivelIndentacion++;

            // Encabezado C
            codigoC.append("#include <stdio.h>\n\n");
            codigoC.append("int main() {\n");

            if(Enunciados()) {
                if(match("FINPROGRAMA")) {
                    // Finalización Python
                    codigoPython.append("\nif __name__ == '__main__':\n");
                    codigoPython.append("    main()\n");

                    // Finalización C
                    codigoC.append("    return 0;\n");
                    codigoC.append("}\n");
                    return true;
                }
            }
        }
        return false;
    }

    //<Enunciados> -> <Enunciado> <Enunciados>
    private boolean Enunciados() {
        int indiceAux = indiceToken;
        if(Enunciado()) {
            while(Enunciado());
            return true;
        }
        indiceToken = indiceAux;
        return false;
    }

    //<Enunciado> -> <Asignacion> | <Leer> | <Escribir> | <Si> | <Mientras> | <Declaracion> | <Repetir>
    private boolean Enunciado() {
        int indiceAux = indiceToken;

        if(Declaracion()) return true;

        indiceToken = indiceAux;
        if(tokens.get(indiceToken).getTipo().getNombre().equals("VARIABLE"))
            if(Asignacion()) return true;

        indiceToken = indiceAux;
        if(tokens.get(indiceToken).getTipo().getNombre().equals("LEER"))
            if(Leer()) return true;

        indiceToken = indiceAux;
        if(tokens.get(indiceToken).getTipo().getNombre().equals("ESCRIBIR"))
            if(Escribir()) return true;

        indiceToken = indiceAux;
        if(tokens.get(indiceToken).getTipo().getNombre().equals("SI"))
            if(Si()) return true;

        indiceToken = indiceAux;
        if(tokens.get(indiceToken).getTipo().getNombre().equals("MIENTRAS"))
            if(Mientras()) return true;

        indiceToken = indiceAux;
        if(tokens.get(indiceToken).getTipo().getNombre().equals("REPITE"))
            if(Repetir()) return true;

        indiceToken = indiceAux;
        return false;
    }

    // <Declaracion> -> variables <Lista_Variables>
    private boolean Declaracion() {
        if(match("DECLARAR")) {
            match("DOSPUNTOS");

            ArrayList<String> vars = new ArrayList<>();
            if(ListaVariables(vars)) {
                // Python: no necesita declaración explícita
                codigoPython.append(getIndentacion() + "# Variables: " + String.join(", ", vars) + "\n");

                // C: declarar como int
                codigoC.append("    int " + String.join(", ", vars) + ";\n");

                variablesDeclaradas.addAll(vars);
                return true;
            }
        }
        return false;
    }

    private boolean ListaVariables(ArrayList<String> vars) {
        if(tokens.get(indiceToken).getTipo().getNombre().equals("VARIABLE")) {
            String varName = tokens.get(indiceToken).getNombre();
            vars.add(varName);
            match("VARIABLE");

            if(match("COMA")) {
                return ListaVariables(vars);
            }
            return true;
        }
        return false;
    }

    //<Asignacion> -> VARIABLE = <Expresion>
    private boolean Asignacion() {
        int indiceAux = indiceToken;
        String variable = tokens.get(indiceToken).getNombre();

        if(match("VARIABLE")) {
            if(match("IGUAL")) {
                String expr = "";
                if(Expresion(expr)) {
                    // Python
                    codigoPython.append(getIndentacion() + variable + " = " + obtenerExpresion(indiceAux + 2) + "\n");

                    // C
                    codigoC.append("    " + variable + " = " + obtenerExpresion(indiceAux + 2) + ";\n");
                    return true;
                }
            }
        }
        indiceToken = indiceAux;
        return false;
    }

    private String obtenerExpresion(int inicio) {
        StringBuilder expr = new StringBuilder();
        int i = inicio;

        while(i < indiceToken && !tokens.get(i).getTipo().getNombre().matches("LEER|ESCRIBIR|SI|MIENTRAS|REPITE|FINSI|FINMIENTRAS|FINREPITE|FINPROGRAMA")) {
            String tipo = tokens.get(i).getTipo().getNombre();
            String valor = tokens.get(i).getNombre();

            if(tipo.equals("OPARITMETICO") || tipo.equals("NUMERO") || tipo.equals("VARIABLE")) {
                expr.append(valor);
                if(tipo.equals("OPARITMETICO") || i < indiceToken - 1) {
                    expr.append(" ");
                }
            }
            i++;
        }

        return expr.toString().trim();
    }

    private boolean Expresion(String resultado) {
        int indiceAux = indiceToken;

        if(Valor()) {
            if(match("OPARITMETICO")) {
                if(Valor()) {
                    return true;
                }
            }
        }

        indiceToken = indiceAux;
        if(Valor()) return true;

        indiceToken = indiceAux;
        return false;
    }

    private boolean Valor() {
        if(match("VARIABLE")) return true;
        if(match("NUMERO")) return true;
        return false;
    }

    //<Leer> -> leer VARIABLE
    private boolean Leer() {
        int indiceAux = indiceToken;

        if(match("LEER")) {
            if(tokens.get(indiceToken).getTipo().getNombre().equals("VARIABLE")) {
                String variable = tokens.get(indiceToken).getNombre();
                match("VARIABLE");

                // Python
                codigoPython.append(getIndentacion() + variable + " = int(input())\n");

                // C
                codigoC.append("    scanf(\"%d\", &" + variable + ");\n");
                return true;
            }
        }
        indiceToken = indiceAux;
        return false;
    }

    //<Escribir> -> escribir CADENA , VARIABLE | escribir CADENA | escribir VARIABLE
    private boolean Escribir() {
        int indiceAux = indiceToken;

        if(match("ESCRIBIR")) {
            if(tokens.get(indiceToken).getTipo().getNombre().equals("CADENA")) {
                String cadena = tokens.get(indiceToken).getNombre();
                match("CADENA");

                if(match("COMA")) {
                    if(tokens.get(indiceToken).getTipo().getNombre().equals("VARIABLE")) {
                        String variable = tokens.get(indiceToken).getNombre();
                        match("VARIABLE");

                        // Python
                        codigoPython.append(getIndentacion() + "print(\"" + cadena + "\", " + variable + ")\n");

                        // C
                        codigoC.append("    printf(\"" + cadena + " %d\\n\", " + variable + ");\n");
                        return true;
                    }
                } else {
                    // Solo cadena
                    codigoPython.append(getIndentacion() + "print(\"" + cadena + "\")\n");
                    codigoC.append("    printf(\"" + cadena + "\\n\");\n");
                    return true;
                }
            }
        }

        indiceToken = indiceAux;
        if(match("ESCRIBIR")) {
            if(tokens.get(indiceToken).getTipo().getNombre().equals("VARIABLE")) {
                String variable = tokens.get(indiceToken).getNombre();
                match("VARIABLE");

                // Python
                codigoPython.append(getIndentacion() + "print(" + variable + ")\n");

                // C
                codigoC.append("    printf(\"%d\\n\", " + variable + ");\n");
                return true;
            }
        }

        indiceToken = indiceAux;
        return false;
    }

    //<Si> -> si <Comparacion> entonces <Enunciados> fin-si
    private boolean Si() {
        int indiceAux = indiceToken;

        if(match("SI")) {
            int inicioComp = indiceToken;
            if(Comparacion()) {
                String comparacion = obtenerComparacion(inicioComp);

                if(match("ENTONCES")) {
                    // Python
                    codigoPython.append(getIndentacion() + "if " + comparacion + ":\n");
                    nivelIndentacion++;

                    // C
                    codigoC.append("    if (" + comparacion + ") {\n");

                    if(Enunciados()) {
                        if(match("FINSI")) {
                            // Python
                            nivelIndentacion--;

                            // C
                            codigoC.append("    }\n");
                            return true;
                        }
                    }
                }
            }
        }
        indiceToken = indiceAux;
        return false;
    }

    //<Mientras> -> mientras <Comparacion> <Enunciados> fin-mientras
    private boolean Mientras() {
        int indiceAux = indiceToken;

        if(match("MIENTRAS")) {
            int inicioComp = indiceToken;
            if(Comparacion()) {
                String comparacion = obtenerComparacion(inicioComp);

                // Python
                codigoPython.append(getIndentacion() + "while " + comparacion + ":\n");
                nivelIndentacion++;

                // C
                codigoC.append("    while (" + comparacion + ") {\n");

                if(Enunciados()) {
                    if(match("FINMIENTRAS")) {
                        // Python
                        nivelIndentacion--;

                        // C
                        codigoC.append("    }\n");
                        return true;
                    }
                }
            }
        }
        indiceToken = indiceAux;
        return false;
    }

    //<Repetir> -> repite ( VARIABLE , NUMERO , VARIABLE ) <Enunciados> fin-repite
    private boolean Repetir() {
        int indiceAux = indiceToken;

        if(match("REPITE")) {
            if(match("PARENTESISIZQ")) {
                String varControl = tokens.get(indiceToken).getNombre();
                if(match("VARIABLE")) {
                    if(match("COMA")) {
                        String inicio = tokens.get(indiceToken).getNombre();
                        if(match("NUMERO")) {
                            if(match("COMA")) {
                                String fin = tokens.get(indiceToken).getNombre();
                                if(match("VARIABLE")) {
                                    if(match("PARENTESISDER")) {
                                        // Python
                                        codigoPython.append(getIndentacion() + "for " + varControl + " in range(" + inicio + ", " + fin + " + 1):\n");
                                        nivelIndentacion++;

                                        // C
                                        codigoC.append("    for (" + varControl + " = " + inicio + "; " + varControl + " <= " + fin + "; " + varControl + "++) {\n");

                                        if(Enunciados()) {
                                            if(match("FINREPITE")) {
                                                // Python
                                                nivelIndentacion--;

                                                // C
                                                codigoC.append("    }\n");
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        indiceToken = indiceAux;
        return false;
    }

    //<Comparacion> -> (<Valor> <Operador relacional> <Valor>)
    private boolean Comparacion() {
        int indiceAux = indiceToken;

        if(match("PARENTESISIZQ")) {
            if(Valor()) {
                if(match("OPRELACIONAL")) {
                    if(Valor()) {
                        if(match("PARENTESISDER")) {
                            return true;
                        }
                    }
                }
            }
        }
        indiceToken = indiceAux;
        return false;
    }

    private String obtenerComparacion(int inicio) {
        StringBuilder comp = new StringBuilder();

        for(int i = inicio; i < indiceToken; i++) {
            String tipo = tokens.get(i).getTipo().getNombre();
            String valor = tokens.get(i).getNombre();

            if(!tipo.equals("PARENTESISIZQ") && !tipo.equals("PARENTESISDER")) {
                comp.append(valor).append(" ");
            }
        }

        return comp.toString().trim();
    }

    private boolean match(String nombre) {
        if(indiceToken < tokens.size() && tokens.get(indiceToken).getTipo().getNombre().equals(nombre)) {
            indiceToken++;
            return true;
        }
        return false;
    }
}