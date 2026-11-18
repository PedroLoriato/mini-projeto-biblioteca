## Sistema de Empréstimos da Biblioteca do Campus

> Mini projeto desenvolvido para a disciplina de **Tópicos Especiais II**, consistindo numa **API REST** para o
> gerenciamento de empréstimos da biblioteca do campus.

---

### Tecnologias Utilizadas

- **Linguagem:** Java 21
- **Framework:** Spring Framework 4.1.2
- **Arquitetura:** API REST

---

**Integrante:** Pedro Henrique Loriato

---

### Como Executar

#### Pré-requisitos

- **Git** instalado para clonar o repositório.
- **JDK 21** instalado e configurado na variável de ambiente `JAVA_HOME`.
- **Maven 3.9+** (opcional, pois o projeto inclui o Maven Wrapper).

#### Passos para Execução

1. Clone o repositório diretamente na branch `master` e acesse o diretório raiz do projeto pelo terminal:
   ```bash
   git clone -b master [https://github.com/PedroLoriato/mini-projeto-biblioteca.git](https://github.com/PedroLoriato/mini-projeto-biblioteca.git)
   cd biblioteca
   ```

2. Execute a aplicação utilizando o Maven Wrapper:
    - **Linux / macOS:**
      ```bash
      ./mvnw spring-boot:run
      ```
    - **Windows (PowerShell ou CMD):**
      ```cmd
      ./mvnw spring-boot:run
      ```

3. A API estará disponível em `http://localhost:8080`.

#### Carga Inicial e Testes

- Ao iniciar, a classe `CargaInicialConfig` popula automaticamente o repositório em memória com **3 livros**, **2
  leitores** (um `ALUNO` e um `PROFESSOR`) e **1 empréstimo já em atraso**, permitindo testar imediatamente as regras de
  bloqueio por atraso, cálculo de multa e listagem de pendências.
- Para testar todos os cenários de sucesso e erro, importe o arquivo `biblioteca.postman_collection.json` no Postman
  (variável `base_url` configurada para `http://localhost:8080`).

---

### Lista de Endpoints

#### Livros (`/livros`)

| Método   | Endpoint       | Uso                                                                            |
|:---------|:---------------|:-------------------------------------------------------------------------------|
| `POST`   | `/livros`      | Cadastra um novo livro no acervo.                                              |
| `GET`    | `/livros`      | Lista os livros cadastrados, com filtros opcionais `?autor=` e `?disponivel=`. |
| `GET`    | `/livros/{id}` | Busca um livro específico pelo ID, exibindo a quantidade disponível calculada. |
| `PUT`    | `/livros/{id}` | Atualiza os dados cadastrais de um livro existente.                            |
| `DELETE` | `/livros/{id}` | Remove um livro do acervo, desde que não possua empréstimos ativos.            |

#### Leitores (`/leitores`)

| Método   | Endpoint         | Uso                                                                   |
|:---------|:-----------------|:----------------------------------------------------------------------|
| `POST`   | `/leitores`      | Cadastra um novo leitor (`ALUNO` ou `PROFESSOR`).                     |
| `GET`    | `/leitores`      | Lista os leitores cadastrados, com filtro opcional `?tipo=`.          |
| `GET`    | `/leitores/{id}` | Busca os dados de um leitor específico pelo ID.                       |
| `PUT`    | `/leitores/{id}` | Atualiza os dados cadastrais de um leitor existente.                  |
| `DELETE` | `/leitores/{id}` | Remove um leitor do sistema, desde que não possua empréstimos ativos. |

#### Empréstimos (`/emprestimos`)

| Método | Endpoint                      | Uso                                                                                |
|:-------|:------------------------------|:-----------------------------------------------------------------------------------|
| `POST` | `/emprestimos`                | Registra um novo empréstimo validando disponibilidade, atrasos e limite do leitor. |
| `GET`  | `/emprestimos`                | Lista os empréstimos registrados, com filtros opcionais `?leitorId=` e `?ativos=`. |
| `GET`  | `/emprestimos/{id}`           | Busca os detalhes de um empréstimo específico pelo ID.                             |
| `POST` | `/emprestimos/{id}/devolucao` | Registra a devolução de um empréstimo ativo e calcula eventual multa por atraso.   |
| `GET`  | `/emprestimos/atrasados`      | Lista todos os empréstimos ativos com data prevista de devolução vencida.          |

---

### Principais Decisões de Projeto

- **Adoção do pacote `domain` em vez de `model`:** A camada de domínio foi nomeada como `domain` para representar
  explicitamente as entidades centrais e as regras do negócio, mantendo-as normalizadas (armazenando apenas
  identificadores de relacionamento e tipos wrapper) e isoladas dos contratos externos da API.
- **Uso do `enum` `TipoLeitor`:** O perfil do leitor foi modelado como um tipo enumerado (`ALUNO` e `PROFESSOR`),
  garantindo segurança de tipos em tempo de compilação, validação automática na desserialização das requisições e
  filtragem consistente nas consultas.
- **Separação de DTOs em subpacotes por recurso (`dto.livro`, `dto.leitor`, `dto.emprestimo`):** Os objetos de entrada
  (`*Request`) e saída (`*Response`) foram segregados em subpacotes dedicados a cada recurso. Isso evita o vazamento de
  entidades de domínio, isola as anotações de validação sintática nos registros de entrada e permite enriquecer as
  respostas na camada `Service` com informações derivadas (como `quantidadeDisponivel`, título do livro e nome do
  leitor).
- **Criação do pacote `policy` com o padrão Strategy:** As regras variáveis por perfil de leitor (prazo de devolução,
  limite de empréstimos simultâneos e valor diário de multa) foram encapsuladas na interface `PoliticaEmprestimo` e nas
  implementações `PoliticaAluno` e `PoliticaProfessor`. Essa abordagem elimina condicionais acopladas no
  `EmprestimoService`, facilita a extensão para novos perfis e permite externalizar os parâmetros no `application.yml`.
- **Regra de negócio para `anoPublicacao`:** A validação do ano de publicação foi dividida conforme a responsabilidade
  de cada camada: o `LivroRequest` verifica a obrigatoriedade e o limite histórico mínimo (`@NotNull` e `@Min(1450)`),
  enquanto o `LivroService` utiliza o bean `Clock` injetado para impedir o cadastro ou atualização de livros com ano
  superior ao ano corrente, lançando `NegocioException` (HTTP 422) padronizada no formato RFC 9457 (`ProblemDetail`).