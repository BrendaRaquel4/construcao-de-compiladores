package br.ufscar.dc.compiladores.alguma.sintatico;

import java.io.PrintWriter;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

public class MyCustomErrorListener extends BaseErrorListener {

    private final PrintWriter pw;

    public MyCustomErrorListener(PrintWriter pw) {
        this.pw = pw;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {

        String textoErro = ((Token) offendingSymbol).getText();

        if ("<EOF>".equals(textoErro)) {
            textoErro = "EOF";
        }

        pw.println("Linha " + line + ": erro sintatico proximo a " + textoErro);
        pw.println("Fim da compilacao");

        // Interrompe o parsing imediatamente
        throw new RuntimeException("Erro sintático encontrado");
    }
}