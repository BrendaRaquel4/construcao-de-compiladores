# Compiladores 2026

Repositório da disciplina de **Compiladores**,
ministrada pelo Professor Dr. **Daniel Lucrédio**
no Departamento de Computação da **UFSCar — São Carlos**.

---

## Autores

* **Josué Martins da Conceição** — RA: 769613 (Turma B)
* **Brenda Raquel Maia** — RA: 757891 (Turma A)

---

## Sobre o Projeto

Este repositório contém a implementação de um **compilador** para a
linguagem **LA (Linguagem Algorítmica)**, desenvolvida pelo professor Jander.

O projeto foi desenvolvido ao longo da disciplina e está dividido em
cinco etapas:

| Trabalho | Descrição                        |
| -------- | -------------------------------- |
| T1       | Analisador Léxico                |
| T2       | Analisador Sintático             |
| T3       | Analisador Semântico             |
| T4       | Continuação da Análise Semântica |
| T5       | Geração de Código                |

---

# Pré-requisitos

Antes de executar o projeto, instale:

* Java JDK
* Maven
* ANTLR

As dependências do projeto já estão configuradas no arquivo `pom.xml`.

---

# Estrutura do Projeto

```text
construcao-de-compiladores/
│
├── T1/
├── T2/
├── T3/
├── T4/
└── T5/
```

Cada diretório representa uma etapa do compilador.

---

# Entrada e Saída

## Arquivo de Entrada

O código-fonte da linguagem LA deve ser colocado em:

```text
entrada.txt
```

## Arquivo de Saída

O resultado da compilação/análise será gerado em:

```text
saida.txt
```

> Modifique os caminhos conforme a estrutura do seu ambiente.

---

# Compilação

Para compilar qualquer etapa do projeto, execute o comando abaixo dentro do diretório correspondente:

```bash
mvn package
```

---

# Compilação por Etapa

## T1 — Analisador Léxico

Diretório:

```text
construcao-de-compiladores/T1/algumalexico
```

---

## T2 — Analisador Sintático

Diretório:

```text
construcao-de-compiladores/T2/alguma-sintatico
```

---

## T3 — Analisador Semântico

Diretório:

```text
construcao-de-compiladores/T3/alguma-semantico
```

---

## T4 — Análise Semântica

Diretório:

```text
construcao-de-compiladores/T4/alguma-semantico
```

---

## T5 — Gerador de Código

Diretório:

```text
construcao-de-compiladores/T5/alguma-gerador
```

---

# Execução

A estrutura de execução é:

```bash
java -jar <executavel.jar> <arquivo_entrada> <arquivo_saida>
```

Onde:

* `<executavel.jar>` → caminho do arquivo `.jar`
* `<arquivo_entrada>` → código-fonte em LA
* `<arquivo_saida>` → arquivo de saída gerado

---

# Execução — T1

```bash
java -jar "construcao-de-compiladores/T1/algumalexico/target/algumalexico-1.0-SNAPSHOT-jar-with-dependencies.jar" "construcao-de-compiladores/T1/entrada.txt" "construcao-de-compiladores/T1/saida.txt"
```

---

# Execução — T2

```bash
java -jar "construcao-de-compiladores/T2/alguma-sintatico/target/alguma-sintatico-1.0-SNAPSHOT-jar-with-dependencies.jar" "construcao-de-compiladores/T2/entrada.txt" "construcao-de-compiladores/T2/saida.txt"
```

---

# Execução — T3

```bash
java -jar "construcao-de-compiladores/T3/alguma-semantico/target/alguma-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar" "construcao-de-compiladores/T3/entrada.txt" "construcao-de-compiladores/T3/saida.txt"
```

---

# Execução — T4

```bash
java -jar "construcao-de-compiladores/T4/alguma-semantico/target/alguma-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar" "construcao-de-compiladores/T4/entrada.txt" "construcao-de-compiladores/T4/saida.txt"
```

---

# Execução — T5

## Gerar saída em texto

```bash
java -jar "construcao-de-compiladores/T5/alguma-gerador/target/alguma-gerador-1.0-SNAPSHOT-jar-with-dependencies.jar" "construcao-de-compiladores/T5/entrada.txt" "construcao-de-compiladores/T5/saida.txt"
```

---

## Gerar código C

Também é possível gerar código em C da etapa do T5:

```bash
java -jar "construcao-de-compiladores/T5/alguma-gerador/target/alguma-gerador-1.0-SNAPSHOT-jar-with-dependencies.jar" "construcao-de-compiladores/T5/entrada.txt" "construcao-de-compiladores/T5/saida.c"
```

Depois de gerar o arquivo `.c`, compile com:

```bash
gcc saida.c -o saida
```

Será gerado um executável (`saida`).

Execute com:

```bash
./saida
```

---

# Observações

* Certifique-se de executar os comandos no diretório correto.
* Os caminhos dos arquivos podem variar conforme o sistema operacional.
* O projeto foi desenvolvido utilizando Maven para gerenciamento de dependências e build.

---

# Tecnologias Utilizadas

* Java
* Maven
* ANTLR
* GCC (na etapa de geração de código)

