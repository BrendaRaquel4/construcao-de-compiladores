package br.ufscar.dc.compiladores.alguma.semantico;


import br.ufscar.dc.compiladores.alguma.semantico.AlgumaParser.CmdChamadaContext;
import br.ufscar.dc.compiladores.alguma.semantico.AlgumaParser.Declaracao_globalContext;
import br.ufscar.dc.compiladores.alguma.semantico.AlgumaParser.Declaracao_localContext;
import br.ufscar.dc.compiladores.alguma.semantico.AlgumaParser.ParametroContext;
import br.ufscar.dc.compiladores.alguma.semantico.AlgumaParser.VariavelContext;
import br.ufscar.dc.compiladores.alguma.semantico.TabelaDeSimbolos.EntradaTabelaDeSimbolos;
import br.ufscar.dc.compiladores.alguma.semantico.TabelaDeSimbolos.TipoAlguma;
import br.ufscar.dc.compiladores.alguma.semantico.TabelaDeSimbolos.TipoEntrada;

public class AlgumaSemantico extends AlgumaBaseVisitor<Void> {
    Escopos pilhaDeTabelas = new Escopos();
    private EntradaTabelaDeSimbolos subRotinaAtual = null;

    @Override public Void visitPrograma(AlgumaParser.ProgramaContext ctx) { super.visitPrograma(ctx); return null; }

    @Override
    public Void visitDeclaracao_global(Declaracao_globalContext ctx) {
        TabelaDeSimbolos escopoGlobal = pilhaDeTabelas.obterEscopoAtual();
        String nomeSubRotina = ctx.IDENT().getText();
        if (escopoGlobal.existe(nomeSubRotina)) {
            AlgumaSemanticoUtils.adicionarErroSemantico(ctx.start, "identificador " + nomeSubRotina + " ja declarado anteriormente");
            return null;
        }

        if (ctx.start.getText().equals("funcao")) {
            TipoAlguma tipoRetorno = AlgumaSemanticoUtils.getTipo(ctx.tipo_estendido().getText());
            escopoGlobal.adicionarFuncao(nomeSubRotina, tipoRetorno);
        } else {
            escopoGlobal.adicionarProcedimento(nomeSubRotina);
        }
        subRotinaAtual = escopoGlobal.getEntrada(nomeSubRotina);

        pilhaDeTabelas.criarNovoEscopo();
        TabelaDeSimbolos escopoLocal = pilhaDeTabelas.obterEscopoAtual();

        if (ctx.parametros() != null) {
            for (ParametroContext pCtx : ctx.parametros().parametro()) {
                boolean ehVarParam = pCtx.VAR() != null;
                String nomeTipoParam = pCtx.tipo_estendido().getText();
                boolean ehPonteiro = nomeTipoParam.startsWith("^");
                if(ehPonteiro) nomeTipoParam = nomeTipoParam.substring(1);
                
                TipoAlguma tipoParam = TipoAlguma.INVALIDO;
                TabelaDeSimbolos tabelaDoTipoParam = null;

                if(AlgumaSemanticoUtils.ehTipoBasico(nomeTipoParam)) {
                    tipoParam = AlgumaSemanticoUtils.getTipo(nomeTipoParam);
                } else {
                    EntradaTabelaDeSimbolos entradaTipo = pilhaDeTabelas.obterEntrada(nomeTipoParam);
                    if(entradaTipo != null && entradaTipo.getTipoEntrada() == TipoEntrada.TIPO) {
                        tipoParam = entradaTipo.getTipo();
                        tabelaDoTipoParam = entradaTipo.getRegistro();
                    } else {
                        AlgumaSemanticoUtils.adicionarErroSemantico(pCtx.tipo_estendido().start, "tipo " + nomeTipoParam + " nao declarado");
                    }
                }

                if(tipoParam != TipoAlguma.INVALIDO) {
                    for(var idCtx : pCtx.identificador()){
                        String nomeParam = idCtx.IDENT(0).getText();
                        boolean ehVetorParam = !idCtx.dimensao().getText().isEmpty();
                        if(escopoLocal.existe(nomeParam)) {
                            AlgumaSemanticoUtils.adicionarErroSemantico(idCtx.start, "identificador " + nomeParam + " ja declarado anteriormente");
                        } else {
                            escopoLocal.adicionar(nomeParam, TipoEntrada.VARIAVEL, tipoParam, ehPonteiro, tabelaDoTipoParam, ehVetorParam, ehVarParam);
                            escopoGlobal.adicionarParametro(nomeSubRotina, escopoLocal.getEntrada(nomeParam));
                        }
                    }
                }
            }
        }
        
        for(var decl : ctx.declaracao_local()) visitDeclaracao_local(decl);
        for(var cmd : ctx.cmd()) visitCmd(cmd);
        pilhaDeTabelas.abandonarEscopo();
        subRotinaAtual = null;
        return null;
    }

    @Override
    public Void visitCmdRetorne(AlgumaParser.CmdRetorneContext ctx) {
        if (subRotinaAtual == null || subRotinaAtual.getTipoEntrada() != TipoEntrada.FUNCAO) {
            AlgumaSemanticoUtils.adicionarErroSemantico(ctx.start, "comando retorne nao permitido nesse escopo");
        } else {
            TipoAlguma tipoRetornoEsperado = subRotinaAtual.getTipoRetorno();
            TipoAlguma tipoRetornoVerificado = AlgumaSemanticoUtils.verificarTipo(pilhaDeTabelas, ctx.expressao());
            if (tipoRetornoVerificado != tipoRetornoEsperado && !AlgumaSemanticoUtils.ehTipoInteiroEmReal(tipoRetornoEsperado, tipoRetornoVerificado)) {
                AlgumaSemanticoUtils.adicionarErroSemantico(ctx.start, "tipo de retorno incompativel");
            }
        }
        return super.visitCmdRetorne(ctx);
    }
    
    @Override public Void visitCorpo(AlgumaParser.CorpoContext ctx) { pilhaDeTabelas.criarNovoEscopo(); super.visitCorpo(ctx); pilhaDeTabelas.abandonarEscopo(); return null; }
    
public void declaracaoVariavel(VariavelContext ctx){
    TabelaDeSimbolos escopoAtual = pilhaDeTabelas.obterEscopoAtual();
    String nomeTipo = ctx.tipo().getText();
    TipoAlguma tipoVar = TipoAlguma.INVALIDO;
    TabelaDeSimbolos tabelaDoTipo = null;
    boolean ehPonteiro = false;

    if (ctx.tipo().tipo_estendido() != null &&
        ctx.tipo().tipo_estendido().getText().startsWith("^")) {

        ehPonteiro = true;
        nomeTipo = nomeTipo.substring(1);
    }

    if (ctx.tipo().registro() != null) {

        tipoVar = TipoAlguma.REGISTRO;

        pilhaDeTabelas.criarNovoEscopo();

        for (var v : ctx.tipo().registro().variavel()) {
            declaracaoVariavel(v);
        }

        tabelaDoTipo = pilhaDeTabelas.obterEscopoAtual();
        pilhaDeTabelas.abandonarEscopo();

    } else {

        if (AlgumaSemanticoUtils.ehTipoBasico(nomeTipo)) {

            tipoVar = AlgumaSemanticoUtils.getTipo(nomeTipo);

        } else {

            EntradaTabelaDeSimbolos entrada =
                pilhaDeTabelas.obterEntrada(nomeTipo);

            if (entrada != null &&
                entrada.getTipoEntrada() == TipoEntrada.TIPO) {

                tipoVar = entrada.getTipo();
                tabelaDoTipo = entrada.getRegistro();

            } else {

                AlgumaSemanticoUtils.adicionarErroSemantico(
                    ctx.tipo().start,
                    "tipo " + nomeTipo + " nao declarado"
                );

                tipoVar = TipoAlguma.INVALIDO;
            }
        }
    }

    for (var idCtx : ctx.identificador()) {

        String nomeVariavel = idCtx.IDENT(0).getText();

        if (pilhaDeTabelas.obterEntrada(nomeVariavel) != null) {

            AlgumaSemanticoUtils.adicionarErroSemantico(
                idCtx.start,
                "identificador " + nomeVariavel +
                " ja declarado anteriormente"
            );

        } else {

            boolean ehVetor =
                !idCtx.dimensao().getText().isEmpty();

            escopoAtual.adicionar(
                nomeVariavel,
                TipoEntrada.VARIAVEL,
                tipoVar,
                ehPonteiro,
                tabelaDoTipo,
                ehVetor,
                false
            );
        }
    }
}   
    
    // NOVO MÉTODO
    public void declaracaoConstante(Declaracao_localContext ctx) {
        TabelaDeSimbolos escopoAtual = pilhaDeTabelas.obterEscopoAtual();
        String nomeConst = ctx.var2.getText();

        // Verificação hierárquica
        if (pilhaDeTabelas.obterEntrada(nomeConst) != null) {
            AlgumaSemanticoUtils.adicionarErroSemantico(ctx.var2, "identificador " + nomeConst + " ja declarado anteriormente");
        } else {
            TipoAlguma tipoConst = AlgumaSemanticoUtils.getTipo(ctx.tipo_basico().getText());
            // O valor não é armazenado pois não é usado na análise semântica, mas poderia ser.
            escopoAtual.adicionarConstante(nomeConst, tipoConst, null);
        }
    }

    public void declaracaoTipo(Declaracao_localContext ctx){
        TabelaDeSimbolos escopoAtual = pilhaDeTabelas.obterEscopoAtual();
        String nomeTipo = ctx.var3.getText();
        if (pilhaDeTabelas.obterEntrada(nomeTipo) != null) {
            AlgumaSemanticoUtils.adicionarErroSemantico(ctx.start, "tipo " + nomeTipo + " ja declarado anteriormente");
            return;
        }
        AlgumaParser.TipoContext tipoCtx = ctx.tipo();
        if (tipoCtx.registro() != null) {
            pilhaDeTabelas.criarNovoEscopo();
            for(var variavelRegistro : tipoCtx.registro().variavel()) declaracaoVariavel(variavelRegistro);
            TabelaDeSimbolos tabelaDoRegistro = pilhaDeTabelas.obterEscopoAtual();
            pilhaDeTabelas.abandonarEscopo();
            escopoAtual.adicionar(nomeTipo, TipoEntrada.TIPO, TipoAlguma.REGISTRO, false, tabelaDoRegistro, false, false);
        }
    }
    
    @Override public Void visitDeclaracao_local(Declaracao_localContext ctx){
        if (ctx.var1 != null) {
            declaracaoVariavel(ctx.var1);
        } else if (ctx.var2 != null) {
            declaracaoConstante(ctx);
        } else if (ctx.var3 != null) {
            declaracaoTipo(ctx);
        }
        return null;
    }
    
    @Override public Void visitCmdLeia(AlgumaParser.CmdLeiaContext ctx){ for (var v : ctx.identificador()) AlgumaSemanticoUtils.verificarIdentificador(pilhaDeTabelas, v); return null; }
    @Override public Void visitCmdEscreva(AlgumaParser.CmdEscrevaContext ctx){ for (var e : ctx.expressao()) AlgumaSemanticoUtils.verificarTipo(pilhaDeTabelas, e); return null; }
    
    @Override
    public Void visitCmdAtribuicao(AlgumaParser.CmdAtribuicaoContext ctx) { 
        InfoIdentificador infoLhs = AlgumaSemanticoUtils.verificarIdentificador(pilhaDeTabelas, ctx.identificador());
        if (infoLhs.tipo == TipoAlguma.INVALIDO) return null;
        TipoAlguma tipoRhs = AlgumaSemanticoUtils.verificarTipo(pilhaDeTabelas, ctx.expressao());
        boolean isLhsDereferenced = ctx.getText().startsWith("^");
        String nomeLhs = ctx.identificador().getText();
        if (isLhsDereferenced) { 
            if (!infoLhs.ehPonteiro) { AlgumaSemanticoUtils.adicionarErroSemantico(ctx.start, "identificador " + nomeLhs + " nao e um ponteiro"); return null; }
            TipoAlguma tipoBaseLhs = infoLhs.tipo;
            if (!tipoRhs.equals(tipoBaseLhs) && !AlgumaSemanticoUtils.ehTipoInteiroEmReal(tipoBaseLhs, tipoRhs)) AlgumaSemanticoUtils.adicionarErroSemantico(ctx.start, "atribuicao nao compativel para ^" + nomeLhs);
        } else {
            if (tipoRhs == TipoAlguma.ENDERECO) {
                if (!infoLhs.ehPonteiro) AlgumaSemanticoUtils.adicionarErroSemantico(ctx.start, "atribuicao nao compativel para " + nomeLhs);
                else {
                    AlgumaParser.Parcela_nao_unarioContext pnuCtx = ctx.expressao().termo_logico(0).fator_logico(0).parcela_logica().exp_relacional().exp_aritmetica(0).termo(0).fator(0).parcela(0).parcela_nao_unario();
                    if(pnuCtx != null && pnuCtx.pn1 != null) { 
                        InfoIdentificador infoRhs = AlgumaSemanticoUtils.verificarIdentificador(pilhaDeTabelas, pnuCtx.pn1);
                        if (infoRhs.tipo != TipoAlguma.INVALIDO && infoLhs.tipo != infoRhs.tipo) AlgumaSemanticoUtils.adicionarErroSemantico(ctx.start, "atribuicao nao compativel para " + nomeLhs);
                    }
                }
            } else {
                if (infoLhs.ehPonteiro) { AlgumaSemanticoUtils.adicionarErroSemantico(ctx.start, "atribuicao nao compativel para " + nomeLhs); return null; }
                if (infoLhs.tipo == TipoAlguma.REGISTRO && tipoRhs == TipoAlguma.REGISTRO) return null;
                if (!tipoRhs.equals(infoLhs.tipo) && !AlgumaSemanticoUtils.ehTipoInteiroEmReal(infoLhs.tipo, tipoRhs)) AlgumaSemanticoUtils.adicionarErroSemantico(ctx.start, "atribuicao nao compativel para " + nomeLhs);
            }
        }
        return null;
    }

    @Override public Void visitCmdSe(AlgumaParser.CmdSeContext ctx) { AlgumaSemanticoUtils.verificarTipo(pilhaDeTabelas, ctx.expressao()); super.visitCmdSe(ctx); return null; }

    @Override
    public Void visitCmdEnquanto(AlgumaParser.CmdEnquantoContext ctx) {
        AlgumaSemanticoUtils.verificarTipo(pilhaDeTabelas, ctx.expressao());
        super.visitCmdEnquanto(ctx);
        return null;
    }

    @Override
    public Void visitCmdFaca(AlgumaParser.CmdFacaContext ctx) {
        super.visitCmdFaca(ctx);
        AlgumaSemanticoUtils.verificarTipo(pilhaDeTabelas, ctx.expressao());
        return null;
    }

    @Override
    public Void visitCmdPara(AlgumaParser.CmdParaContext ctx) {
        for (var exp : ctx.exp_aritmetica()) AlgumaSemanticoUtils.verificarTipo(pilhaDeTabelas, exp);
        super.visitCmdPara(ctx);
        return null;
    }

    @Override
    public Void visitCmdCaso(AlgumaParser.CmdCasoContext ctx) {
        AlgumaSemanticoUtils.verificarTipo(pilhaDeTabelas, ctx.exp_aritmetica());
        super.visitCmdCaso(ctx);
        return null;
    }
    
    @Override
    public Void visitCmdChamada(CmdChamadaContext ctx) {
        String nomeProc = ctx.IDENT().getText();
        EntradaTabelaDeSimbolos entrada = pilhaDeTabelas.obterEntrada(nomeProc);
        if(entrada == null) { AlgumaSemanticoUtils.adicionarErroSemantico(ctx.start, "identificador " + nomeProc + " nao declarado"); return null; }
        if(entrada.getTipoEntrada() != TipoEntrada.PROCEDIMENTO) { AlgumaSemanticoUtils.adicionarErroSemantico(ctx.start, "identificador " + nomeProc + " nao e um procedimento"); return null; }
        AlgumaSemanticoUtils.verificarParametros(pilhaDeTabelas, entrada, ctx.expressao(), ctx.start);
        return null;
    }
}