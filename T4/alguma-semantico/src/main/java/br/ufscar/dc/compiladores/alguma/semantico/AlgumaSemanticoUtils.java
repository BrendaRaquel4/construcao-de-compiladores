package br.ufscar.dc.compiladores.alguma.semantico;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import br.ufscar.dc.compiladores.alguma.semantico.TabelaDeSimbolos.EntradaTabelaDeSimbolos;
import br.ufscar.dc.compiladores.alguma.semantico.TabelaDeSimbolos.TipoAlguma;
import br.ufscar.dc.compiladores.alguma.semantico.TabelaDeSimbolos.TipoEntrada;


class InfoIdentificador {
    public TipoAlguma tipo; public boolean ehPonteiro; public boolean ehVetor; public TabelaDeSimbolos tabelaDoRegistro;
    public InfoIdentificador(TipoAlguma tipo, boolean ehPonteiro, boolean ehVetor, TabelaDeSimbolos tabela) {
        this.tipo = tipo; this.ehPonteiro = ehPonteiro; this.ehVetor = ehVetor; this.tabelaDoRegistro = tabela;
    }
}

public class AlgumaSemanticoUtils {
    public static List<String> errosSemanticos = new ArrayList<>();
    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        String erro = String.format("Linha %d: %s", linha, mensagem);
        if(!errosSemanticos.contains(erro)) errosSemanticos.add(erro);
    }
    public static boolean ehTipoBasico(String tipoVar){ return tipoVar.equals("literal") || tipoVar.equals("inteiro") || tipoVar.equals("real") || tipoVar.equals("logico"); }
    public static TipoAlguma getTipo(String tipoStr) {
        if (tipoStr.startsWith("^")) tipoStr = tipoStr.substring(1);
        switch(tipoStr){
            case "literal": return TipoAlguma.LITERAL;
            case "inteiro": return TipoAlguma.INTEIRO;
            case "real": return TipoAlguma.REAL;
            case "logico": return TipoAlguma.LOGICO;
            default: return TipoAlguma.INVALIDO;
        }
    }
    public static boolean ehTipoNumeral(TipoAlguma t1, TipoAlguma t2){ return (t1 == TipoAlguma.INTEIRO && t2 == TipoAlguma.REAL) || (t1 == TipoAlguma.REAL && t2 == TipoAlguma.INTEIRO); }
    public static boolean ehTipoInteiroEmReal(TipoAlguma t1, TipoAlguma t2){ return t1 == TipoAlguma.REAL && t2 == TipoAlguma.INTEIRO; }

    public static InfoIdentificador verificarIdentificador(Escopos pilhaDeTabelas, AlgumaParser.IdentificadorContext ctx) {
        String nomeCompleto = ctx.getText();
        String nomeBase = ctx.IDENT(0).getText();
        EntradaTabelaDeSimbolos entradaBase = pilhaDeTabelas.obterEntrada(nomeBase);
        if (entradaBase == null) {
            adicionarErroSemantico(ctx.start, "identificador " + nomeCompleto + " nao declarado");
            return new InfoIdentificador(TipoAlguma.INVALIDO, false, false, null);
        }
        boolean acessoVetor = !ctx.dimensao().getText().isEmpty();
        if (acessoVetor) {
            if (!entradaBase.isVetor()) {
                adicionarErroSemantico(ctx.start, "identificador " + nomeBase + " nao e um vetor");
                return new InfoIdentificador(TipoAlguma.INVALIDO, false, false, null);
            }
            for (var exp : ctx.dimensao().exp_aritmetica()) {
                if (verificarTipo(pilhaDeTabelas, exp) != TipoAlguma.INTEIRO) adicionarErroSemantico(exp.start, "indice de vetor deve ser inteiro");
            }
        }
        TipoAlguma tipoResultante = entradaBase.getTipo();
        boolean ehPonteiroResultante = entradaBase.isPonteiro();
        boolean ehVetorResultante = acessoVetor ? false : entradaBase.isVetor();
        TabelaDeSimbolos tabelaDoTipo = entradaBase.getRegistro();
        if (ctx.IDENT().size() > 1) {
             if (tipoResultante != TipoAlguma.REGISTRO) {
                adicionarErroSemantico(ctx.start, "identificador " + nomeBase + " nao e um registro");
                return new InfoIdentificador(TipoAlguma.INVALIDO, false, false, null);
             }
             for (int i = 1; i < ctx.IDENT().size(); i++) {
                String nomeCampo = ctx.IDENT(i).getText();
                if (tabelaDoTipo == null || !tabelaDoTipo.existe(nomeCampo)) {
                    adicionarErroSemantico(ctx.start, "identificador " + nomeCompleto + " nao declarado");
                    return new InfoIdentificador(TipoAlguma.INVALIDO, false, false, null);
                }
                EntradaTabelaDeSimbolos entradaCampo = tabelaDoTipo.getEntrada(nomeCampo);
                tipoResultante = entradaCampo.getTipo();
                ehPonteiroResultante = entradaCampo.isPonteiro();
                tabelaDoTipo = entradaCampo.getRegistro();
            }
        }
        return new InfoIdentificador(tipoResultante, ehPonteiroResultante, ehVetorResultante, tabelaDoTipo);
    }
    
    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.ExpressaoContext ctx) {
        TipoAlguma tipoResultante = verificarTipo(pilhaDeTabelas, ctx.termo_logico(0));
        for(int i = 1; i < ctx.termo_logico().size(); i++) {
            TipoAlguma proximoTipo = verificarTipo(pilhaDeTabelas, ctx.termo_logico(i));
            if(tipoResultante != TipoAlguma.LOGICO || proximoTipo != TipoAlguma.LOGICO) return TipoAlguma.INVALIDO;
        }
        return tipoResultante;
    }
    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.Termo_logicoContext ctx) {
        TipoAlguma tipoResultante = verificarTipo(pilhaDeTabelas, ctx.fator_logico(0));
        for(int i = 1; i < ctx.fator_logico().size(); i++) {
            TipoAlguma proximoTipo = verificarTipo(pilhaDeTabelas, ctx.fator_logico(i));
            if(tipoResultante != TipoAlguma.LOGICO || proximoTipo != TipoAlguma.LOGICO) return TipoAlguma.INVALIDO;
        }
        return tipoResultante;
    }
    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.Fator_logicoContext ctx) { return verificarTipo(pilhaDeTabelas, ctx.parcela_logica()); }
    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.Parcela_logicaContext ctx) { if(ctx.pl1 != null) return TipoAlguma.LOGICO; return verificarTipo(pilhaDeTabelas, ctx.pl2); }
    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.Exp_relacionalContext ctx) {
        TipoAlguma tipo1 = verificarTipo(pilhaDeTabelas, ctx.exp_aritmetica(0));
        if (ctx.op_relacional() == null) return tipo1;
        TipoAlguma tipo2 = verificarTipo(pilhaDeTabelas, ctx.exp_aritmetica(1));
        if (tipo1 == TipoAlguma.INVALIDO || tipo2 == TipoAlguma.INVALIDO) return TipoAlguma.INVALIDO;
        if (ehTipoNumeral(tipo1, tipo2) || (tipo1 == tipo2)) return TipoAlguma.LOGICO;
        return TipoAlguma.INVALIDO;
    }
    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.Exp_aritmeticaContext ctx) {
        TipoAlguma tipoResultante = verificarTipo(pilhaDeTabelas, ctx.termo(0));
        for (int i = 1; i < ctx.termo().size(); i++) {
            TipoAlguma proximoTipo = verificarTipo(pilhaDeTabelas, ctx.termo(i));
            if (proximoTipo == TipoAlguma.INVALIDO) return TipoAlguma.INVALIDO;
            if (ehTipoNumeral(tipoResultante, proximoTipo)) tipoResultante = TipoAlguma.REAL;
            else if (tipoResultante == TipoAlguma.LITERAL && proximoTipo == TipoAlguma.LITERAL) tipoResultante = TipoAlguma.LITERAL;
            else if (tipoResultante != proximoTipo) return TipoAlguma.INVALIDO;
        }
        return tipoResultante;
    }
    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.TermoContext ctx) {
        TipoAlguma tipoResultante = verificarTipo(pilhaDeTabelas, ctx.fator(0));
        for (int i = 1; i < ctx.fator().size(); i++) {
            TipoAlguma proximoTipo = verificarTipo(pilhaDeTabelas, ctx.fator(i));
            if (proximoTipo == TipoAlguma.INVALIDO) return TipoAlguma.INVALIDO;
            if (ehTipoNumeral(tipoResultante, proximoTipo)) tipoResultante = TipoAlguma.REAL;
            else if(tipoResultante != proximoTipo) return TipoAlguma.INVALIDO;
        }
        return tipoResultante;
    }
    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.FatorContext ctx) { return verificarTipo(pilhaDeTabelas, ctx.parcela(0)); }
    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.ParcelaContext ctx) {
        if (ctx.parcela_unario() != null) return verificarTipo(pilhaDeTabelas, ctx.parcela_unario());
        else return verificarTipo(pilhaDeTabelas, ctx.parcela_nao_unario());
    }
    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.Parcela_nao_unarioContext ctx) {
        if (ctx.pn1 != null) {
            if (ctx.getText().startsWith("&")) return TipoAlguma.ENDERECO;
            return verificarIdentificador(pilhaDeTabelas, ctx.pn1).tipo;
        } else { return TipoAlguma.LITERAL; }
    }
    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.Parcela_unarioContext ctx) {
        if (ctx.p1 != null) return verificarIdentificador(pilhaDeTabelas, ctx.p1).tipo;
        if (ctx.p3 != null) return TipoAlguma.INTEIRO;
        if (ctx.p4 != null) return TipoAlguma.REAL;
        if (ctx.p5 != null) return verificarTipo(pilhaDeTabelas, ctx.p5);

        if (ctx.p2 != null) {
            String nomeSubRotina = ctx.p2.getText();
            EntradaTabelaDeSimbolos entrada = pilhaDeTabelas.obterEntrada(nomeSubRotina);
            if (entrada == null) {
                adicionarErroSemantico(ctx.p2, "identificador " + nomeSubRotina + " nao declarado");
                return TipoAlguma.INVALIDO;
            }
            if (entrada.getTipoEntrada() != TipoEntrada.FUNCAO) {
                adicionarErroSemantico(ctx.p2, "identificador " + nomeSubRotina + " nao e uma funcao");
                return TipoAlguma.INVALIDO;
            }
            verificarParametros(pilhaDeTabelas, entrada, ctx.expressao(), ctx.p2);
            return entrada.getTipoRetorno();
        }
        return TipoAlguma.INVALIDO;
    }

    public static void verificarParametros(Escopos pilhaDeTabelas, EntradaTabelaDeSimbolos subRotina, List<AlgumaParser.ExpressaoContext> args, Token tokenChamada) {
        List<EntradaTabelaDeSimbolos> paramsEsperados = subRotina.getParametros();
        if (paramsEsperados.size() != args.size()) {
            adicionarErroSemantico(tokenChamada, "incompatibilidade de parametros na chamada de " + subRotina.nome);
            return;
        }
        for (int i = 0; i < args.size(); i++) {
            EntradaTabelaDeSimbolos paramEsperado = paramsEsperados.get(i);
            TipoAlguma tipoFornecido = verificarTipo(pilhaDeTabelas, args.get(i));

            if (paramEsperado.isVar()) {
                // Lógica de verificação para parâmetros 'var' (passados por referência)
                AlgumaParser.IdentificadorContext idCtx = null;
                try { idCtx = args.get(i).termo_logico(0).fator_logico(0).parcela_logica().exp_relacional().exp_aritmetica(0).termo(0).fator(0).parcela(0).parcela_unario().p1; } catch(Exception e) {}
                if (idCtx == null) {
                    adicionarErroSemantico(args.get(i).start, "argumento nao pode ser uma expressao para parametro var");
                    continue;
                }
            }
            // Para chamadas de função, os tipos devem ser exatos. Conversão implícita não é permitida.
            if (tipoFornecido != paramEsperado.getTipo()) {
                adicionarErroSemantico(tokenChamada, "incompatibilidade de parametros na chamada de " + subRotina.nome);
                break; // Para o erro na primeira incompatibilidade e evita mensagens duplicadas
            }
        }
    }
}
