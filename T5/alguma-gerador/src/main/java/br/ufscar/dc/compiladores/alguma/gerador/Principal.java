package br.ufscar.dc.compiladores.alguma.gerador;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class Principal {
    public static void main(String[] args) throws IOException {

        try (PrintWriter pw = new PrintWriter(args[1])) {
            CharStream cs = CharStreams.fromFileName(args[0]);

            AlgumaLexer lexer = new AlgumaLexer(cs);
            //criado uma lista para armazenar os possiveis erros 
            List<String> erros = new ArrayList<>();
            lexer.removeErrorListeners();
            lexer.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?,?> r, Object o,
                                        int line, int charPos, String msg, RecognitionException e) {
                    erros.add(String.format("Linha %d:%d %s", line, charPos, msg));
                }
            });

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            AlgumaParser parser = new AlgumaParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?,?> r, Object o,
                                        int line, int charPos, String msg, RecognitionException e) {
                    erros.add(String.format("Linha %d:%d %s", line, charPos, msg));
                }
            });

            ParseTree arvore = parser.programa();

            AlgumaSemantico sem = new AlgumaSemantico();
            sem.visit(arvore);
            erros.addAll(AlgumaSemanticoUtils.errosSemanticos);

            //se tiver algum erro, eh printado no arquivo
            if (!erros.isEmpty()) {
                erros.forEach(pw::println);
                pw.println("Fim da compilacao");
                return;
            }

            AlgumaGeradorC ger = new AlgumaGeradorC(sem.pilhaDeTabelas);
            ger.visit(arvore);
            pw.print(ger.getSaida());
        }
    }
}
