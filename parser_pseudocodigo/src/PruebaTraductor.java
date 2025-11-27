import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PruebaTraductor {
    public static void main(String[] args) {
        try {
            //lee el programa en pseudocódigo
            String entrada = leerPrograma("programa1.txt");

            System.out.println("TRADUCTOR DE PSEUDOCÓDIGO A PYTHON Y C");

            System.out.println("-PROGRAMA ORIGINAL EN PSEUDOCÓDIGO-\n");
            System.out.println(entrada);
            System.out.println("\n" + "=".repeat(60) + "\n");

            //análisis léxico
            PseudoLexer lexer = new PseudoLexer();
            lexer.analizar(entrada);

            System.out.println("*** ANÁLISIS LÉXICO ***\n");
            System.out.println("Total de tokens: " + lexer.getTokens().size());
            for(Token t : lexer.getTokens()) {
                System.out.println(t);
            }
            System.out.println("\n" + "=".repeat(60) + "\n");

            // Traducción
            TraductorPseudo traductor = new TraductorPseudo();
            traductor.traducir(lexer);

            // Mostrar código Python
            System.out.println("-CÓDIGO TRADUCIDO A PYTHON-\n");
            System.out.println(traductor.getCodigoPython());
            System.out.println("=".repeat(60) + "\n");

            // Mostrar código C
            System.out.println("-CÓDIGO TRADUCIDO A C-\n");
            System.out.println(traductor.getCodigoC());
            System.out.println("=".repeat(60) + "\n");

            // Guardar archivos
            guardarArchivo("programa_traducido.py", traductor.getCodigoPython());
            guardarArchivo("programa_traducido.c", traductor.getCodigoC());


        } catch(LexicalException e) {
            System.err.println("ERROR LÉXICO: " + e.getMessage());
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

    private static void guardarArchivo(String nombre, String contenido) {
        try {
            FileWriter writer = new FileWriter(nombre);
            writer.write(contenido);
            writer.close();
        } catch(IOException e) {
            System.err.println("ERROR: No se pudo guardar el archivo '" + nombre + "'");
        }
    }
}
