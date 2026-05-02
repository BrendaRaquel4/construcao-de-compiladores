package br.ufscar.dc.compiladores.alguma.semantico;

import br.ufscar.dc.compiladores.alguma.semantico.AlgumaParser.ProgramaContext;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;


//classe principal  igual a que o professor usa 
//apenas modificado para saida ser escrita em arquivo

public class Principal {
    public static void main(String args[]) throws IOException {
        // o primeiro arg eh o arq de entrada
        //seg arg eh o arq de saida
        String arquivoSaida = args[1];

        // Cria um objeto para escrever no arquivo
        try(PrintWriter pw = new PrintWriter(arquivoSaida)) {            
            CharStream cs = CharStreams.fromFileName(args[0]);
            AlgumaLexer lexer = new AlgumaLexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            AlgumaParser parser = new AlgumaParser(tokens);
            ProgramaContext arvore = parser.programa();
            AlgumaSemantico as = new AlgumaSemantico();
            as.visitPrograma(arvore);
            AlgumaSemanticoUtils.errosSemanticos.forEach((s) -> pw.println(s));
            pw.println("Fim da compilacao");
        } catch(FileNotFoundException fnfe) {
            System.err.println("O seguinte arquivo de saida nao existe:"+args[1]);
        }
    }
}
