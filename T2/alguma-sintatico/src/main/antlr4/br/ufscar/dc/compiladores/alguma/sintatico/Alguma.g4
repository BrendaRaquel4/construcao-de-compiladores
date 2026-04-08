grammar Alguma;

programa
    : declaracoes 'algoritmo' corpo 'fim_algoritmo'
    ;

declaracoes
    : decl_local_global*
    ;

decl_local_global
    : declaracao_local
    | declaracao_global
    ;

declaracao_local
    : 'declare' variavel
    | 'constante' IDENT ':' tipo_basico '=' valor_constante
    | 'tipo' IDENT ':' tipo
    ;

variavel
    : identificador (',' identificador)* ':' tipo
    ;

identificador
    : IDENT ('.' IDENT)* dimensao
    ;

dimensao
    : ('[' exp_aritmetica ']')*
    ;

tipo
    : registro
    | tipo_estendido
    ;

registro
    : 'registro' variavel* 'fim_registro'
    ;

tipo_estendido
    : ('^')? tipo_basico_ident
    ;

tipo_basico_ident
    : tipo_basico
    | IDENT
    ;

tipo_basico
    : 'literal'
    | 'inteiro'
    | 'real'
    | 'logico'
    ;

valor_constante
    : CADEIA
    | NUM_INT
    | NUM_REAL
    | 'verdadeiro'
    | 'falso'
    ;

declaracao_global
    : 'procedimento' IDENT '(' parametros? ')' declaracao_local* cmd* 'fim_procedimento'
    | 'funcao' IDENT '(' parametros? ')' ':' tipo_estendido declaracao_local* cmd* 'fim_funcao'
    ;

parametros
    : parametro (',' parametro)*
    ;

parametro
    : ('var')? identificador (',' identificador)* ':' tipo_estendido
    ;

corpo
    : declaracao_local* cmd*
    ;

cmd
    : cmdLeia
    | cmdEscreva
    | cmdSe
    | cmdCaso
    | cmdPara
    | cmdEnquanto
    | cmdFaca
    | cmdAtribuicao
    | cmdChamada
    | cmdRetorne
    ;

cmdLeia
    : 'leia' '(' ('^')? identificador (',' ('^')? identificador)* ')'
    ;

cmdEscreva
    : 'escreva' '(' expressao (',' expressao)* ')'
    ;

cmdSe
    : 'se' expressao 'entao' cmd* ('senao' cmd*)? 'fim_se'
    ;

cmdCaso
    : 'caso' exp_aritmetica 'seja' selecao ('senao' cmd*)? 'fim_caso'
    ;

cmdPara
    : 'para' IDENT '<-' exp_aritmetica 'ate' exp_aritmetica 'faca' cmd* 'fim_para'
    ;

cmdEnquanto
    : 'enquanto' expressao 'faca' cmd* 'fim_enquanto'
    ;

cmdFaca
    : 'faca' cmd* 'ate' expressao
    ;

cmdAtribuicao
    : ('^')? identificador '<-' expressao
    ;

cmdChamada
    : IDENT '(' expressao (',' expressao)* ')'
    ;

cmdRetorne
    : 'retorne' expressao
    ;

selecao
    : item_selecao*
    ;

item_selecao
    : constantes ':' cmd*
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

exp_aritmetica
    : termo (op1 termo)*
    ;

termo
    : fator (op2 fator)*
    ;

fator
    : parcela (op3 parcela)*
    ;

op1
    : '+'
    | '-'
    ;

op2
    : '*'
    | '/'
    ;

op3
    : '%'
    ;

parcela
    : op_unario? parcela_unario
    | parcela_nao_unario
    ;

parcela_unario
    : ('^')? identificador
    | IDENT '(' expressao (',' expressao)* ')'
    | NUM_INT
    | NUM_REAL
    | '(' expressao ')'
    ;

parcela_nao_unario
    : '&' identificador
    | CADEIA
    ;

exp_relacional
    : exp_aritmetica (op_relacional exp_aritmetica)?
    ;

op_relacional
    : '='
    | '<>'
    | '>='
    | '<='
    | '>'
    | '<'
    ;

expressao
    : termo_logico (op_logico_1 termo_logico)*
    ;

termo_logico
    : fator_logico (op_logico_2 fator_logico)*
    ;

fator_logico
    : 'nao'? parcela_logica
    ;

parcela_logica
    : 'verdadeiro'
    | 'falso'
    | exp_relacional
    ;

op_logico_1
    : 'ou'
    ;

op_logico_2
    : 'e'
    ;

////////////////////////////////         algumalexico         /////////////////
// PALAVRAS-CHAVE
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


// OPERADORES MATEMÁTICOS E RELACIONAIS
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


// SÍMBOLOS RESERVADOS
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


// LITERAIS E IDENTIFICADORES
NUM_REAL : [0-9]+ '.' [0-9]+;
NUM_INT  : [0-9]+;
CADEIA_INACABADA    : '"' (~["\r\n])* ('\r' | '\n' | EOF);
CADEIA        : '"' (~[\r\n"\\])* '"';
IDENT         : [a-zA-Z] [a-zA-Z0-9_]*;

// COMENTÁRIOS E ESPAÇOS EM BRANCO
COMENTARIO_INACABADO : '{' ~[\r\n}]* ('\r' | '\n' | EOF);
COMENTARIO    : '{' ~[\r\n}]* '}' -> skip;
WS            : [ \t\r\n]+ -> skip;

// ERRO
ERROR         : . ;