import java.io.FileReader;
import java.io.IOException;

public class PruebaTuplas {
    public static void main(String[] arg) {
        try {
            String entrada = leerPrograma("programa4.txt");

            System.out.println("=".repeat(80));
            System.out.println("GENERADOR DE CÓDIGO INTERMEDIO (TUPLAS)");
            System.out.println("=".repeat(80));

            System.out.println("\n*** PROGRAMA ORIGINAL ***\n");
            System.out.println(entrada);
            System.out.println("\n" + "=".repeat(80));

            PseudoLexer lexer = new PseudoLexer();
            lexer.analizar(entrada);

            System.out.println("\n*** ANÁLISIS LÉXICO ***\n");
            System.out.println("Total de tokens: " + lexer.getTokens().size());

            for(Token t: lexer.getTokens()){
                System.out.println(t);
            }

            System.out.println("\n" + "=".repeat(80));
            System.out.println("\n*** ANÁLISIS SINTÁCTICO CON GENERACIÓN DE TUPLAS ***\n");

            PseudoParser parser = new PseudoParser();
            parser.analizar(lexer);

            System.out.println("\n" + "=".repeat(80));
            System.out.println("\n*** TUPLAS GENERADAS ***\n");
            System.out.println("Formato: (Tipo, SaltoVerdadero, SaltoFalso, [parámetros])\n");

            int i = 0;
            for(Tupla t: parser.getTuplas()) {
                System.out.println(i + ": " + t);
                i++;
            }

            System.out.println("\n" + "=".repeat(80));
            System.out.println("\nGENERACIÓN DE TUPLAS COMPLETADA EXITOSAMENTE");
            System.out.println("=".repeat(80));

        } catch(LexicalException e) {
            System.err.println("ERROR LÉXICO: " + e.getMessage());
        } catch(PseudoParser.SyntaxException e) {
            System.err.println("ERROR SINTÁCTICO: " + e.getMessage());
        } catch(Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String leerPrograma(String nombre) {
        String entrada = "";

        try {
            FileReader reader = new FileReader(nombre);
            int caracter;

            while((caracter = reader.read()) != -1)
                entrada += (char) caracter;

            reader.close();
            return entrada;
        } catch(IOException e) {
            System.err.println("ERROR: No se pudo leer el archivo '" + nombre + "'");
            return "";
        }
    }
}