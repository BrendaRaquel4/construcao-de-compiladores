package br.ufscar.dc.compiladores.alguma.semantico;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import br.ufscar.dc.compiladores.alguma.semantico.TabelaDeSimbolos.TipoAlguma;

public class AlgumaSemanticoUtils {
    public static List<String> errosSemanticos = new ArrayList<>();

    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        errosSemanticos.add(String.format("Linha %d: %s", linha, mensagem));
    }

    public static boolean ehTipoBasico(String tipoVar) {
        return tipoVar.equals("literal") ||
               tipoVar.equals("inteiro") ||
               tipoVar.equals("real")    ||
               tipoVar.equals("registro")    ||
               tipoVar.equals("logico");
    }

    public static boolean ehTipoNumeral(TipoAlguma a, TipoAlguma b) {
        // inteiro↔real (qualquer direção) é permitido
        return (a == TipoAlguma.INTEIRO && b == TipoAlguma.REAL) ||
               (a == TipoAlguma.REAL    && b == TipoAlguma.INTEIRO);
    }

    public static boolean ehTipoInteiroEmReal(TipoAlguma a, TipoAlguma b) {
        // permite atribuir um inteiro a uma variável real
        return (a == TipoAlguma.REAL && b == TipoAlguma.INTEIRO);
    }

    // ---------------------- EXPRESSÃO LÓGICA ----------------------

    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.ExpressaoContext ctx) {
        TipoAlguma ret = null;
        for (var tl : ctx.termo_logico()) {
            TipoAlguma aux = verificarTipo(pilhaDeTabelas, tl);
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != TipoAlguma.INVALIDO) {
                ret = TipoAlguma.INVALIDO;
            }
        }
        return ret;
    }

    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.Termo_logicoContext ctx) {
        TipoAlguma ret = null;
        for (var fl : ctx.fator_logico()) {
            TipoAlguma aux = verificarTipo(pilhaDeTabelas, fl);
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != TipoAlguma.INVALIDO) {
                ret = TipoAlguma.INVALIDO;
            }
        }
        return ret;
    }

    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.Fator_logicoContext ctx) {
        return verificarTipo(pilhaDeTabelas, ctx.parcela_logica());
    }

    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.Parcela_logicaContext ctx) {
        if (ctx.pl1 != null) {
            return TipoAlguma.LOGICO;
        } else {
            return verificarTipo(pilhaDeTabelas, ctx.pl2);
        }
    }

    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.Exp_relacionalContext ctx) {
        if (ctx.op_relacional() != null) {
            return TipoAlguma.LOGICO;
        } else {
            return verificarTipo(pilhaDeTabelas, ctx.exp_aritmetica(0));
        }
    }

    // ---------------------- EXPRESSÃO ARITMÉTICA ----------------------

    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.Exp_aritmeticaContext ctx) {
        TipoAlguma tipo = null;
        for (var ta : ctx.termo()) {
            TipoAlguma aux = verificarTipo(pilhaDeTabelas, ta);
            if (tipo == null) {
                tipo = aux;
            } else if (tipo != aux && aux != TipoAlguma.INVALIDO) {
                // Se for int↔real, converte para REAL
                if (ehTipoNumeral(tipo, aux)) {
                    tipo = TipoAlguma.REAL;
                }
                // Se qualquer dos dois for LITERAL combinado com algo NÃO LITERAL, é INVÁLIDO
                else if ((tipo == TipoAlguma.LITERAL && aux != TipoAlguma.LITERAL) ||
                         (aux == TipoAlguma.LITERAL && tipo != TipoAlguma.LITERAL)) {
                    tipo = TipoAlguma.INVALIDO;
                }
                // Se ambos são LITERAL → mantém LITERAL
                else if (tipo == TipoAlguma.LITERAL && aux == TipoAlguma.LITERAL) {
                    tipo = TipoAlguma.LITERAL;
                }
                // Caso contrário (por exemplo, real+real ou inteiro+inteiro), se iguais, mantém
                else if (tipo == aux) {
                    // mantém “tipo”
                } else {
                    tipo = TipoAlguma.INVALIDO;
                }
            }
        }
        return tipo;
    }

    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.TermoContext ctx) {
        TipoAlguma tipo = null;
        for (var fa : ctx.fator()) {
            TipoAlguma aux = verificarTipo(pilhaDeTabelas, fa);
            if (tipo == null) {
                tipo = aux;
            } else if (tipo != aux && aux != TipoAlguma.INVALIDO) {
                if (ehTipoNumeral(tipo, aux)) {
                    tipo = TipoAlguma.REAL;
                } else if ((tipo == TipoAlguma.LITERAL && aux != TipoAlguma.LITERAL) ||
                           (aux == TipoAlguma.LITERAL && tipo != TipoAlguma.LITERAL)) {
                    tipo = TipoAlguma.INVALIDO;
                } else if (tipo == TipoAlguma.LITERAL && aux == TipoAlguma.LITERAL) {
                    tipo = TipoAlguma.LITERAL;
                } else if (tipo == aux) {
                    // mantém "tipo"
                } else {
                    tipo = TipoAlguma.INVALIDO;
                }
            }
        }
        return tipo;
    }

    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.FatorContext ctx) {
        TipoAlguma tipo = null;
        for (var pa : ctx.parcela()) {
            TipoAlguma aux = verificarTipo(pilhaDeTabelas, pa);
            if (tipo == null) {
                tipo = aux;
            } else if (tipo != aux && aux != TipoAlguma.INVALIDO) {
                if (ehTipoNumeral(tipo, aux)) {
                    tipo = TipoAlguma.REAL;
                } else if ((tipo == TipoAlguma.LITERAL && aux != TipoAlguma.LITERAL) ||
                           (aux == TipoAlguma.LITERAL && tipo != TipoAlguma.LITERAL)) {
                    tipo = TipoAlguma.INVALIDO;
                } else if (tipo == TipoAlguma.LITERAL && aux == TipoAlguma.LITERAL) {
                    tipo = TipoAlguma.LITERAL;
                } else if (tipo == aux) {
                    // mantém "tipo"
                } else {
                    tipo = TipoAlguma.INVALIDO;
                }
            }
        }
        return tipo;
    }

    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.ParcelaContext ctx) {
        if (ctx.parcela_unario() != null) {
            return verificarTipo(pilhaDeTabelas, ctx.parcela_unario());
        } else {
            return verificarTipo(pilhaDeTabelas, ctx.parcela_nao_unario());
        }
    }

    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.Parcela_nao_unarioContext ctx) {
        if (ctx.pn1 != null) { // &identificador (referência)
            return verificarTipo(pilhaDeTabelas.obterEscopoAtual(), ctx.pn1.getText());
        } else { // pn2 = CADEIA
            return TipoAlguma.LITERAL;
        }
    }

    public static TipoAlguma verificarTipo(Escopos pilhaDeTabelas, AlgumaParser.Parcela_unarioContext ctx) {
        // p1 = identificador
        if (ctx.p1 != null) {
            String nomeId = ctx.p1.getText();
            for (TabelaDeSimbolos esc : pilhaDeTabelas.percorrerEscoposAninhados()) {
                if (esc.existe(nomeId)) {
                    return verificarTipo(esc, nomeId);
                }
            }
            return TipoAlguma.INVALIDO;
        }
        // p2 = chamada de funcao; para simplificar, assumimos chamada válida sempre retorna REAL
        if (ctx.p2 != null) {
            return TipoAlguma.REAL;
        }
        // p3 = NUM_INT
        if (ctx.p3 != null) {
            return TipoAlguma.INTEIRO;
        }
        // p4 = NUM_REAL
        if (ctx.p4 != null) {
            return TipoAlguma.REAL;
        }
        // p5 = '(' expressao ')'
        return verificarTipo(pilhaDeTabelas, ctx.p5);
    }

    public static TipoAlguma verificarTipo(TabelaDeSimbolos tabela, String nomeVar) {
        return tabela.verificar(nomeVar);
    }
}
