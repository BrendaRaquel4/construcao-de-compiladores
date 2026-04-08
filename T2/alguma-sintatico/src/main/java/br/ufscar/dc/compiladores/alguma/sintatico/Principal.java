package br.ufscar.dc.compiladores.alguma.sintatico;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

public class Principal {

    public static void main(String args[]) throws IOException {

        if (args.length < 2) {
            System.err.println("Uso: java -jar <jarfile> <arquivo_entrada> <arquivo_saida>");
            return;
        }

        CharStream cs = CharStreams.fromFileName(args[0]);
        AlgumaLexer lexer = new AlgumaLexer(cs);

        List<String> saidaTokens = new ArrayList<>();
        boolean erroLexico = false;
        String erroLexicoMsg = "";

        // Fazendo a analise lexica
        Token t;
        while ((t = lexer.nextToken()).getType() != Token.EOF) {
            String nomeToken = AlgumaLexer.VOCABULARY.getDisplayName(t.getType());
            String texto = t.getText();

            if ("CADEIA_INACABADA".equals(nomeToken)) {
                erroLexicoMsg = "Linha " + t.getLine() + ": cadeia literal nao fechada\nFim da compilacao";
                erroLexico = true;
                break;
            }

            if ("COMENTARIO_INACABADO".equals(nomeToken)) {
                erroLexicoMsg = "Linha " + t.getLine() + ": comentario nao fechado\nFim da compilacao";
                erroLexico = true;
                break;
            }

            if ("ERROR".equals(nomeToken)) {
                erroLexicoMsg = "Linha " + t.getLine() + ": " + texto + " - simbolo nao identificado\nFim da compilacao";
                erroLexico = true;
                break;
            }

            if (!nomeToken.equals("WS") && !nomeToken.equals("COMENTARIO")) {
                saidaTokens.add("<'" + texto + "'," + nomeToken + ">");
            }
        }


        // Escrevendo a saida no txt
        try (PrintWriter pw = new PrintWriter(new FileWriter(args[1]))) {

            // Se erro léxico → imprime e termina
            if (erroLexico) {
                pw.println(erroLexicoMsg);
                return;
            }


            // Faznedo a analise sintatica
            cs = CharStreams.fromFileName(args[0]);
            lexer = new AlgumaLexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            AlgumaParser parser = new AlgumaParser(tokens);

            // Remove listener padrão
            parser.removeErrorListeners();
            parser.addErrorListener(new MyCustomErrorListener(pw));

            try {
                parser.programa();

                //Imprimindo possiveis erros
                for (String s : saidaTokens) {
                    pw.println(s);
                }

            } catch (RuntimeException e) {
                // erro sintático já foi tratado e impresso
            }

        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo de saída.");
        }
    }
}