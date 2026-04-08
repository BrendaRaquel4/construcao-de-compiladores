# Compiladores 2026

Repositório da disciplina de **Construção de Compiladores**, ministrada
pelo professor **Daniel Lucredio** na **UFSCar -- São Carlos**.

## Autores

-   Josué Martins da Conceição RA: 769613 (Turma A)
-   Brenda Raquel Maia RA:757891 (Turma B)

## Descrição

Este repositório implementa um **analisador léxico** capaz de
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

    construcao-de-compiladores/entrada.txt

> Importante: insira o código **antes de compilar o projeto**.

### 2. Arquivo de saída

O resultado da análise será gerado em:

    construcao-de-compiladores/saida.txt

## Compilação

Execute o seguinte comando dentro do diretório:

    construcao-de-compiladores/T1/algumalexico

``` bash
mvn package
```

## Execução

Após a compilação, execute o `.jar` gerado com o comando:

``` bash
java -jar "construcao-de-compiladores/T1/algumalexico/target/algumalexico-1.0-SNAPSHOT-jar-with-dependencies.jar" "construcao-de-compiladores/entrada.txt" "construcao-de-compiladores/saida.txt"
```

Sendo primeiro o caminho para o executavel, depois o primeiro argumento do código(a entrada) e o segundo argumento o caminho para o arquivo de saída. (modifique os caminhos para onde estão os arquivos)
