package br.ufscar.dc.compiladores.alguma.semantico;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import br.ufscar.dc.compiladores.alguma.semantico.TabelaDeSimbolos.TipoAlguma;

// Construído usando Visitor
public class AlgumaSemantico extends AlgumaBaseVisitor<Void> {

    private Escopos pilhaDeTabelas = new Escopos();

    @Override
    public Void visitPrograma(AlgumaParser.ProgramaContext ctx) {
        AlgumaSemanticoUtils.errosSemanticos.clear();
        pilhaDeTabelas.criarNovoEscopo(); // abre escopo global
        super.visitPrograma(ctx);
        pilhaDeTabelas.abandonarEscopo();
        return null;
    }

    @Override
    public Void visitCorpo(AlgumaParser.CorpoContext ctx) {
        pilhaDeTabelas.criarNovoEscopo(); // abre escopo do corpo
        super.visitCorpo(ctx);
        pilhaDeTabelas.abandonarEscopo();
        return null;
    }

    @Override
    public Void visitDeclaracao_local(AlgumaParser.Declaracao_localContext ctx) {
        TabelaDeSimbolos escopoAtual = pilhaDeTabelas.obterEscopoAtual();

        // Caso 1: “declare variavel”
        if (ctx.DECLARE() != null) {
            AlgumaParser.VariavelContext varCtx = ctx.var1;
            String strTipoVar = varCtx.tipo().getText().toLowerCase();

            // 1.1) tipo não declarado?
            if (!AlgumaSemanticoUtils.ehTipoBasico(strTipoVar)) {
                String erro = "tipo " + strTipoVar + " nao declarado";
                AlgumaSemanticoUtils.adicionarErroSemantico(varCtx.tipo().start, erro);
                // cadastra tudo como INVALIDO para evitar cascata
                for (AlgumaParser.IdentificadorContext idCtx : varCtx.identificador()) {
                    String nomeVar = idCtx.getText();
                    Token t = idCtx.start;
                    if (escopoAtual.existe(nomeVar)) {
                        String erroRedeclara = "identificador " + nomeVar + " ja declarado anteriormente";
                        AlgumaSemanticoUtils.adicionarErroSemantico(t, erroRedeclara);
                    } else {
                        escopoAtual.adicionar(nomeVar, TipoAlguma.INVALIDO);
                    }
                }
            } else {
                // 1.2) converte para enum
                TipoAlguma tipoVar = TipoAlguma.INVALIDO;
                switch (strTipoVar) {
                    case "inteiro" -> tipoVar = TipoAlguma.INTEIRO;
                    case "real" -> tipoVar = TipoAlguma.REAL;
                    case "literal" -> tipoVar = TipoAlguma.LITERAL;
                    case "logico" -> tipoVar = TipoAlguma.LOGICO;
                    case "registro" -> tipoVar = TipoAlguma.REGISTRO;
                    default -> {
                    }
                }
                // Nunca irá acontecer, pois o analisador sintático
                // não permite
                // 1.3) adiciona cada identificador, checando redeclaração
                for (AlgumaParser.IdentificadorContext idCtx : varCtx.identificador()) {
                    String nomeVar = idCtx.getText();
                    Token t = idCtx.start;
                    if (escopoAtual.existe(nomeVar)) {
                        String erroRedeclara = "identificador " + nomeVar + " ja declarado anteriormente";
                        AlgumaSemanticoUtils.adicionarErroSemantico(t, erroRedeclara);
                    } else {
                        escopoAtual.adicionar(nomeVar, tipoVar);
                    }
                }
            }

        // Caso 2: “constante IDENT : tipo_basico = valor”
        } else if (ctx.CONSTANTE() != null) {
            TerminalNode idNode = (TerminalNode) ctx.var2;
            Token tokenConst = idNode.getSymbol();
            String nomeConst = idNode.getText();
            String strTipoConst = ctx.tipo_basico().getText().toLowerCase();

            // 2.1) tipo básico válido?
            if (!AlgumaSemanticoUtils.ehTipoBasico(strTipoConst)) {
                String erro = "tipo " + strTipoConst + " nao declarado";
                AlgumaSemanticoUtils.adicionarErroSemantico(ctx.tipo_basico().start, erro);
                if (escopoAtual.existe(nomeConst)) {
                    String erroRedeclara = "identificador " + nomeConst + " ja declarado anteriormente";
                    AlgumaSemanticoUtils.adicionarErroSemantico(tokenConst, erroRedeclara);
                } else {
                    escopoAtual.adicionar(nomeConst, TipoAlguma.INVALIDO);
                }
            } else {
                TipoAlguma tipoConst = TipoAlguma.INVALIDO;
                switch (strTipoConst) {
                    case "inteiro" -> tipoConst = TipoAlguma.INTEIRO;
                    case "registro" -> tipoConst = TipoAlguma.REGISTRO;
                    case "real" -> tipoConst = TipoAlguma.REAL;
                    case "literal" -> tipoConst = TipoAlguma.LITERAL;
                    case "logico" -> tipoConst = TipoAlguma.LOGICO;
                }
                if (escopoAtual.existe(nomeConst)) {
                    String erroRedeclara = "identificador " + nomeConst + " ja declarado anteriormente";
                    AlgumaSemanticoUtils.adicionarErroSemantico(tokenConst, erroRedeclara);
                } else {
                    escopoAtual.adicionar(nomeConst, tipoConst);
                }
            }

        // Caso 3: “tipo IDENT : tipo”
        } else if (ctx.TIPO() != null) {
            TerminalNode idNode = (TerminalNode) ctx.var3;
            Token tokenTipo = idNode.getSymbol();
            String nomeTipo = idNode.getText();
            AlgumaParser.TipoContext tipoCtx = ctx.tipo();

            boolean tipoValido = false;
            if (tipoCtx.registro() != null) {
                tipoValido = true;
            } else if (tipoCtx.tipo_estendido() != null) {
                AlgumaParser.Tipo_estendidoContext teCtx = tipoCtx.tipo_estendido();
                if (teCtx.PONTEIRO() != null) {
                    String apontado = teCtx.tipo_basico_ident().getText().toLowerCase();
                    if (AlgumaSemanticoUtils.ehTipoBasico(apontado)) {
                        tipoValido = true;
                    } else {
                        for (TabelaDeSimbolos esc : pilhaDeTabelas.percorrerEscoposAninhados()) {
                            if (esc.existe(apontado)) {
                                tipoValido = true;
                                break;
                            }
                        }
                    }
                } else {
                    String nomeIdTipo = teCtx.getText().toLowerCase();
                    if (AlgumaSemanticoUtils.ehTipoBasico(nomeIdTipo)) {
                        tipoValido = true;
                    } else {
                        for (TabelaDeSimbolos esc : pilhaDeTabelas.percorrerEscoposAninhados()) {
                            if (esc.existe(nomeIdTipo)) {
                                tipoValido = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (!tipoValido) {
                String erro = "tipo " + tipoCtx.getText().toLowerCase() + " nao declarado";
                AlgumaSemanticoUtils.adicionarErroSemantico(tokenTipo, erro);
                if (escopoAtual.existe(nomeTipo)) {
                    String erroRedeclara = "identificador " + nomeTipo + " ja declarado anteriormente";
                    AlgumaSemanticoUtils.adicionarErroSemantico(tokenTipo, erroRedeclara);
                } else {
                    escopoAtual.adicionar(nomeTipo, TipoAlguma.INVALIDO);
                }
            } else {
                if (escopoAtual.existe(nomeTipo)) {
                    String erroRedeclara = "identificador " + nomeTipo + " ja declarado anteriormente";
                    AlgumaSemanticoUtils.adicionarErroSemantico(tokenTipo, erroRedeclara);
                } else {
                    if (tipoCtx.registro() != null) {
                        escopoAtual.adicionar(nomeTipo, TipoAlguma.REGISTRO);
                    } else {
                        // Alias de tipo existente ou tipo básico: marcamos INVALIDO para não quebrar navegação
                        escopoAtual.adicionar(nomeTipo, TipoAlguma.INVALIDO);
                    }
                }
            }
        }

        return super.visitDeclaracao_local(ctx);
    }

    @Override
    public Void visitCmdLeia(AlgumaParser.CmdLeiaContext ctx) {
        for (AlgumaParser.IdentificadorContext idCtx : ctx.identificador()) {
            String nomeId = idCtx.getText();
            boolean encontrado = false;
            for (TabelaDeSimbolos esc : pilhaDeTabelas.percorrerEscoposAninhados()) {
                if (esc.existe(nomeId)) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                String erro = "identificador " + nomeId + " nao declarado";
                AlgumaSemanticoUtils.adicionarErroSemantico(idCtx.start, erro);
            }
        }
        return null;
    }

    @Override
    public Void visitCmdEscreva(AlgumaParser.CmdEscrevaContext ctx) {
        for (AlgumaParser.ExpressaoContext exprCtx : ctx.expressao()) {
            visitExpressao(exprCtx);
        }
        return null;
    }

    @Override
    public Void visitCmdAtribuicao(AlgumaParser.CmdAtribuicaoContext ctx) {
        // 1) Checa existência do identificador à esquerda
        String nomeVar = ctx.identificador().getText();
        Token tokenVar = ctx.identificador().start;
        boolean encontrado = false;
        TabelaDeSimbolos escEncontrado = null;
        for (TabelaDeSimbolos esc : pilhaDeTabelas.percorrerEscoposAninhados()) {
            if (esc.existe(nomeVar)) {
                encontrado = true;
                escEncontrado = esc;
                break;
            }
        }
        if (!encontrado) {
            String erro = "identificador " + nomeVar + " nao declarado";
            AlgumaSemanticoUtils.adicionarErroSemantico(tokenVar, erro);
        } else {
            // 2) Verifica compatibilidade de tipos
            TipoAlguma tipoExpressao = AlgumaSemanticoUtils.verificarTipo(pilhaDeTabelas, ctx.expressao());
            TipoAlguma tipoId = escEncontrado.verificar(nomeVar);

            if (tipoExpressao == TipoAlguma.INVALIDO
                    || !(tipoExpressao.equals(tipoId)
                         || AlgumaSemanticoUtils.ehTipoInteiroEmReal(tipoId, tipoExpressao))) {
                String erro = "atribuicao nao compativel para " + nomeVar;
                AlgumaSemanticoUtils.adicionarErroSemantico(tokenVar, erro);
            }
        }
        return null;
    }

    @Override
    public Void visitExp_aritmetica(AlgumaParser.Exp_aritmeticaContext ctx) {
        AlgumaSemanticoUtils.verificarTipo(pilhaDeTabelas, ctx);
        return super.visitExp_aritmetica(ctx);
    }

    @Override
    public Void visitParcela_unario(AlgumaParser.Parcela_unarioContext ctx) {
        if (ctx.p1 != null) {
            String nomeId = ctx.p1.getText();
            boolean encontrado = false;
            for (TabelaDeSimbolos esc : pilhaDeTabelas.percorrerEscoposAninhados()) {
                if (esc.existe(nomeId)) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                String erro = "identificador " + nomeId + " nao declarado";
                AlgumaSemanticoUtils.adicionarErroSemantico(ctx.p1.start, erro);
            }
        }
        return super.visitParcela_unario(ctx);
    }
}
