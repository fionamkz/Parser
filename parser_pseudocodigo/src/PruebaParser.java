import java.io.FileReader;
import java.io.IOException;
import java.io.File;

public class PruebaParser {
    public static void main(String[] arg) throws LexicalException, PseudoParser.SyntaxException{
        String entrada = leerPrograma("programa1.txt");

        // DEBUG: Verificar que se leyó el archivo
        System.out.println("*** Contenido del archivo ***");
        System.out.println(entrada);
        System.out.println("*** Longitud: " + entrada.length() + " ***\n");

        PseudoLexer lexer = new PseudoLexer();
        lexer.analizar(entrada);

        System.out.println("*** Analisis lexico ***\n");
        System.out.println("Total de tokens: " + lexer.getTokens().size());

        for(Token t: lexer.getTokens()){
            System.out.println(t);
        }

        System.out.println("\n*** Analisis sintactico ***\n");

        PseudoParser parser = new PseudoParser();
        parser.analizar(lexer);
    }

    private static String leerPrograma(String nombre){
        String entrada = "";

        try{
            FileReader reader = new FileReader(nombre);
            int caracter;

            while((caracter = reader.read())!= -1)
                entrada += (char) caracter;

            reader.close();
            return entrada;
        } catch(IOException e){
            System.out.println("ERROR: No se pudo leer el archivo '" + nombre + "'");
            System.out.println("Mensaje: " + e.getMessage());
            return "";
        }
    }
}