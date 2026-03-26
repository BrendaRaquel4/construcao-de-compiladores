lexer grammar jandergrammar;

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

// LITERAIS E IDENTIFICADORES
NUM_INT       : [0-9]+;
NUM_REAL      : [0-9]+ '.' [0-9]+;
CADEIA_INACABADA    : '"' (~["\r\n"]);
CADEIA        : '"' (~[\r\n"\\])* '"';
IDENT         : [a-zA-Z] [a-zA-Z0-9_]*;

// COMENTÁRIOS E ESPAÇOS EM BRANCO
COMENTARIO_INACABADO    :'{' ~[\r\n}]*?('\r' |'\n'|'EOF');
COMENTARIO    : '{' ~[\r\n}]* '}' -> skip;
WS            : [ \t\r\n]+ -> skip;

// ERRO
ERROR         : . ;
