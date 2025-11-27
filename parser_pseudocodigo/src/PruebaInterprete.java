import java.io.FileReader;
import java.io.IOException;

public class PruebaInterprete {

    public static String leerPrograma(String nombre) {
        String entrada = "";
        try {
            FileReader reader = new FileReader(nombre);
            int caracter;
            while ((caracter = reader.read()) != -1)
                entrada += (char) caracter;
            reader.close();
            return entrada;
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            System.exit(1);
            return "";
        }
    }

    public static void main(String[] args) {
        try {
            String entrada = leerPrograma("programa4.txt");

            PseudoLexer lexer = new PseudoLexer();
            lexer.analizar(entrada);

            System.out.println("*** Análisis léxico ***\n");
            for (Token t : lexer.getTokens()) {
                System.out.println(t);
            }

            System.out.println("\n*** Análisis sintáctico ***\n");

            PseudoParser parser = new PseudoParser();
            parser.analizar(lexer);

            System.out.println("\n*** Tabla de símbolos ***\n");
            System.out.println(parser.getSymbolTable());

            System.out.println("\n*** Tuplas generadas ***\n");
            int i = 0;
            for (Tupla t : parser.getTuplas()) {
                System.out.println(i + ": " + t);
                i++;
            }

            System.out.println("\n*** Ejecución del programa ***\n");

            PseudoInterprete interprete = new PseudoInterprete(parser.getSymbolTable());
            interprete.interpretar(parser.getTuplas());

        } catch (LexicalException e) {
            System.err.println("ERROR LÉXICO: " + e.getMessage());
        } catch (PseudoParser.SyntaxException e) {
            System.err.println("ERROR SINTÁCTICO: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}