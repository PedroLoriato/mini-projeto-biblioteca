# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [1.0.0] - 2026-11-18

### Adicionado

- CRUD completo para gerenciamento de **Livros** (`/livros`), com cálculo dinâmico de `quantidadeDisponivel`, validação
  de ano de publicação via `Clock` e filtros por autor e disponibilidade.
- CRUD completo para gerenciamento de **Leitores** (`/leitores`), utilizando o enum `TipoLeitor` (`ALUNO` e `PROFESSOR`)
  e bloqueio de remoção quando há empréstimos ativos.
- Módulo de **Empréstimos** (`/emprestimos`) com registro de empréstimos, devoluções, cálculo automático de multas e
  consulta de empréstimos em atraso.
- Padrão **Strategy** (`PoliticaEmprestimo`) para regras de prazo, limite simultâneo e multa diária por perfil de
  leitor, parametrizadas via `application.yml`.
- Validação de requisições com Jakarta Bean Validation e tratamento global de exceções padronizado conforme a **RFC
  9457** (`ProblemDetail`).
- Carga inicial automática em memória (`CargaInicialConfig`), coleção de testes (`biblioteca.postman_collection.json`) e
  documentação no `README.md`.

---

## Colaboradores

- [Pedro Henrique Loriato](https://github.com/PedroLoriato)