# Compiladores 2026

Repositório da disciplina de **Construção de Compiladores**, ministrada
pelo Professor Dr. **Daniel Lucredio** no Departamento de Computação | UFSCar - São Carlos.

## Autores

-   Josué Martins da Conceição RA: 769613 (Turma B)
-   Brenda Raquel Maia RA:757891 (Turma A)

## Descrição

Este repositório implementa um **compilador** capaz de
interpretar a linguagem **LA**, desenvolvida pelo professor Jander.

## Pré-requisitos

Para executar o projeto, é necessário ter instalado:

-   Java
-   Maven (para compilação do projeto)
-   ANTLR

As dependências do projeto já estão definidas no arquivo `pom.xml`.

## Como utilizar

### 1. Arquivo de entrada

O código-fonte a ser analisado deve ser colocado no arquivo:

    construcao-de-compiladores/.../entrada.txt

> Importante: insira o código **antes de compilar o projeto**.

### 2. Arquivo de saída

O resultado da análise será gerado em:

    construcao-de-compiladores/.../saida.txt

## 3. Compilar
Para compilar cada parte do projeto separado, execute o comando nos diretórios especifícos abaixo:
``` bash
mvn package
```

-  ## Para compilar T1

Execute o comando dentro do diretório:

    construcao-de-compiladores/T1/algumalexico

-  ## Para compilar T2

Execute o comando dentro do diretório:

    construcao-de-compiladores/T2/alguma-sintatico


-  ## Para compilar T3

Execute o comando dentro do diretório:

    construcao-de-compiladores/T3/alguma-semantico



-  ## Execução - T1

Após a compilação, execute o `.jar` gerado com o comando:

``` bash
java -jar "construcao-de-compiladores/T1/algumalexico/target/algumalexico-1.0-SNAPSHOT-jar-with-dependencies.jar" "construcao-de-compiladores/T1/entrada.txt" "construcao-de-compiladores/T1/saida.txt"
```

-  ## Execução - T2

Após a compilação, execute o `.jar` gerado com o comando:

``` bash
java -jar "construcao-de-compiladores/T2/alguma-sintatico/target/alguma-sintatico-1.0-SNAPSHOT-jar-with-dependencies.jar" "construcao-de-compiladores/T2/entrada.txt" "construcao-de-compiladores/T2/saida.txt"
```

-  ## Execução - T3

Após a compilação, execute o `.jar` gerado com o comando:

``` bash
java -jar "construcao-de-compiladores/T3/alguma-semantico/target/alguma-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar" "construcao-de-compiladores/T3/entrada.txt" "construcao-de-compiladores/T3/saida.txt"
```

Sendo primeiro o caminho para o executavel (para ambas as execuções), depois o primeiro argumento do código(a entrada) e o segundo argumento o caminho para o arquivo de saída. (modifique os caminhos para os diretorios onde estão os arquivos)
