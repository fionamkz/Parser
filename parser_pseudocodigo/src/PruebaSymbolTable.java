import java.io.FileReader;
import java.io.IOException;

public class PruebaSymbolTable {
    public static void main(String[] arg) throws LexicalException, PseudoParser.SyntaxException {
        String entrada = leerPrograma("programa3.txt");
        PseudoLexer lexer = new PseudoLexer();
        lexer.analizar(entrada);

        System.out.println("*** Análisis léxico ***\n");

        for(Token t: lexer.getTokens()){
            System.out.println(t);
        }

        System.out.println("\n*** Análisis sintáctico con tabla de símbolos ***\n");

        PseudoParser parser = new PseudoParser();
        parser.analizar(lexer);
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
            return "";
        }
    }
}