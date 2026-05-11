package br.ufscar.dc.compiladores.alguma.semantico;

import java.util.LinkedList;
import java.util.List;

public class Escopos {
    private LinkedList<TabelaDeSimbolos> pilhaDeTabelas;
    public Escopos() {
        pilhaDeTabelas = new LinkedList<>();
        criarNovoEscopo();
    }
    public void criarNovoEscopo() {
        pilhaDeTabelas.push(new TabelaDeSimbolos());
    }
    public TabelaDeSimbolos obterEscopoAtual() {
        return pilhaDeTabelas.peek();
    }
    public List<TabelaDeSimbolos> percorrerEscoposAninhados() {
        return pilhaDeTabelas;
    }
    public void abandonarEscopo() {
        pilhaDeTabelas.pop();
    }
    public TabelaDeSimbolos.EntradaTabelaDeSimbolos obterEntrada(String nome) {
        for (TabelaDeSimbolos escopo : pilhaDeTabelas) {
            if (escopo.existe(nome)) {
                return escopo.getEntrada(nome);
            }
        }
        return null;
    }
}
