grammar Alguma;

// Regra principal do programa
programa
    : declaracoes* ALGORITMO corpo FIM_ALGORITMO
    ;

// Declarações globais e locais
declaracoes
    : declaracao_local
    | declaracao_global
    ;

declaracao_local: 'declare' var1=variavel
		| 'constante' var2=IDENT ':' tipo_basico '=' valor_constante
		| 'tipo' var3=IDENT ':' tipo;

// Variáveis com possíveis dimensões
variavel
    : identificador (',' identificador)* ':' tipo
    ;

identificador
    : IDENT ('.' IDENT)* dimensao
    ;

dimensao
    : ('[' exp_aritmetica ']')*
    ;

// Tipos básicos e registros
tipo
    : registro
    | tipo_estendido
    ;

registro
    : REGISTRO (variavel)* FIM_REGISTRO
    ;

tipo_estendido
    : ('^')? tipo_basico_ident
    ;

tipo_basico_ident
    : tipo_basico
    | IDENT
    ;

tipo_basico
    : LITERAL
    | INTEIRO
    | REAL
    | LOGICO
    | REGISTRO
    ;

valor_constante
    : CADEIA
    | NUM_INT
    | NUM_REAL
    | VERDADEIRO
    | FALSO
    ;

// Declarações de procedimentos e funções
declaracao_global: 'procedimento' IDENT '(' (parametros)? ')' (declaracao_local)* (cmd)* 'fim_procedimento'
		| 'funcao' IDENT '(' (parametros)? ')' ':' tipo_estendido (declaracao_local)* (cmd)* 'fim_funcao';

parametros
    : parametro (',' parametro)*
    ;

parametro
    : (VAR)? identificador (',' identificador)* ':' tipo_estendido
    ;

// Corpo com comandos
corpo
    : (declaracao_local)* (cmd)*
    ;

cmd: cmdLeia | cmdEscreva | cmdSe | cmdCaso | cmdPara | cmdEnquanto
	| cmdFaca | cmdAtribuicao | cmdChamada | cmdRetorne;

cmdLeia
    : LEIA '(' ('^')? identificador (',' ('^')? identificador)* ')'
    ;

cmdEscreva
    : ESCREVA '(' expressao (',' (expressao))* ')'
    ;

cmdSe
    : SE expressao ENTAO (cmd)* (SENAO (cmd)*)? FIM_SE
    ;

cmdCaso
    : CASO exp_aritmetica SEJA selecao (SENAO (cmd)*)? FIM_CASO
    ;

cmdPara
    : PARA IDENT '<-' exp_aritmetica ATE exp_aritmetica FACA (cmd)* FIM_PARA
    ;

cmdEnquanto
    : ENQUANTO expressao FACA (cmd)* FIM_ENQUANTO
    ;

cmdFaca
    : FACA (cmd)* ATE expressao
    ;

cmdAtribuicao
    : ('^')? identificador '<-' expressao
    ;

cmdChamada
    : IDENT '(' expressao (',' expressao)* ')'
    ;

cmdRetorne
    : RETORNE expressao
    ;

// Seleção de casos
selecao
    : (item_selecao)*
    ;

item_selecao
    : constantes ':' (cmd)*
    ;

constantes
    : numero_intervalo (',' numero_intervalo)*
    ;

numero_intervalo
    : op_unario? NUM_INT ('..' op_unario? NUM_INT)?
    ;

op_unario
    : '-'
    ;

// EXPRESSÃO ARITMÉTICA
exp_aritmetica
    : termo (op1 termo)*
    ;

termo: fatores+=fator (op2 fatores+=fator)*;

fator: parcelas+=parcela (op3 parcelas+=parcela)*;

// Operadores aritméticos
op1:'+' | '-';

op2: '*' | '/';

op3: '%';

parcela: (op_unario)? parcela_unario | parcela_nao_unario;
 
 //ROTULANDO REGRAS para conseguir usar o ctx mais facilmente   nos codigos
parcela_unario: ('^')? p1=identificador
		| p2=IDENT '(' expressao (',' expressao)* ')'
		| p3=NUM_INT
		| p4=NUM_REAL
		| '(' p5=expressao ')';

parcela_nao_unario: '&' pn1=identificador | pn2=CADEIA;

// EXPRESSÃO RELACIONAL e LÓGICA
exp_relacional
    : exp_aritmetica (op_relacional exp_aritmetica)?
    ;

op_relacional: 
              '=' 
            | '<>' 
            | '>=' 
            | '<=' 
            | '>' 
            | '<';

expressao
    : termo_logico (op_logico_1 termo_logico)*
    ;

termo_logico
    : fator_logico (op_logico_2 fator_logico)*
    ;

fator_logico
    : ('NAO')? parcela_logica
    ;

parcela_logica: 
      pl1=( 'verdadeiro' | 'falso' ) 
    | pl2=exp_relacional;


op_logico_1 : OU;

op_logico_2
    : E
    ;

// Tokens literais e símbolos
ALGORITMO     : 'algoritmo';
DECLARE       : 'declare';
FIM_ALGORITMO : 'fim_algoritmo';
LEIA          : 'leia';
ESCREVA       : 'escreva';
LITERAL       : 'literal';
INTEIRO       : 'inteiro';
REAL          : 'real';
LOGICO        : 'logico';
E             : 'e';
OU            : 'ou';
NAO           : 'nao';
SE            : 'se';
ENTAO         : 'entao';
SENAO         : 'senao';
FIM_SE        : 'fim_se';
CASO          : 'caso';
SEJA          : 'seja';
FIM_CASO      : 'fim_caso';
PARA          : 'para';
ATE           : 'ate';
FACA          : 'faca';
FIM_PARA      : 'fim_para';
ENQUANTO      : 'enquanto';
FIM_ENQUANTO  : 'fim_enquanto';
REGISTRO      : 'registro';
FIM_REGISTRO  : 'fim_registro';
TIPO          : 'tipo';
PROCEDIMENTO  : 'procedimento';
FIM_PROCEDIMENTO : 'fim_procedimento';
VAR           : 'var';
FUNCAO        : 'funcao';
FIM_FUNCAO    : 'fim_funcao';
RETORNE       : 'retorne';
CONSTANTE     : 'constante';
FALSO         : 'falso';
VERDADEIRO    : 'verdadeiro';

DOIS_PONTOS   : ':';
ABRE_PAR      : '(';
FECHA_PAR     : ')';
VIRGULA       : ',';
INTERVALO     : '..';
ATRIBUICAO    : '<-';
PONTEIRO      : '^';
REFERENCIA    : '&';
PONTO         : '.';
ABRE_COLCH    : '[';
FECHA_COLCH   : ']';

MAIS          : '+';
MENOS         : '-';
MULT          : '*';
DIV           : '/';
MOD           : '%';
MENOR         : '<';
MAIOR         : '>';
MENOR_IGUAL   : '<=';
MAIOR_IGUAL   : '>=';
IGUAL         : '=';
DIFERENTE     : '<>';

NUM_INT       : ('0'..'9')+;
NUM_REAL      : ('0'..'9')+ ('.' ('0'..'9')+)? ;
CADEIA_INACABADO    : '"' (~["\r\n])*? ('\r'|'\n'|'EOF');
CADEIA        : '"' (~[\r\n"\\])* '"';
IDENT         : [a-zA-Z] [a-zA-Z0-9_]*;

// COMENTÁRIOS E ESPAÇOS EM BRANCO
COMENTARIO_INACABADO    :'{' ~[\r\n}]*?('\r' |'\n'|'EOF');
COMENTARIO    : '{' ~[\r\n}]* '}' -> skip;
WS            : [ \t\r\n]+ -> skip;

// ERRO
ERROR         : . ;