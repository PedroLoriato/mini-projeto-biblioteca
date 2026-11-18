package pedro.biblioteca.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.biblioteca.domain.LivroDomain;
import pedro.biblioteca.dto.livro.LivroRequest;
import pedro.biblioteca.dto.livro.LivroResponse;
import pedro.biblioteca.exception.ConflitoException;
import pedro.biblioteca.exception.NegocioException;
import pedro.biblioteca.exception.RecursoNaoEncontradoException;
import pedro.biblioteca.repository.EmprestimoRepository;
import pedro.biblioteca.repository.LivroRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository repository;
    private final EmprestimoRepository emprestimoRepository;
    private final Clock clock;

    public List<LivroResponse> listar(String autor, Boolean disponivel) {
        Map<Long, Long> ativosPorLivro = emprestimoRepository.countAtivosPorLivro();

        return Optional.ofNullable(autor)
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty))
                .map(repository::findByAutor)
                .orElseGet(repository::findAll)
                .stream()
                .map(livro -> LivroResponse.de(livro, calcularQuantidadeDisponivel(livro, ativosPorLivro)))
                .filter(porDisponibilidade(disponivel))
                .toList();
    }

    public LivroResponse buscarPorId(Long id) {
        return toResponse(buscarEntidadePorId(id));
    }

    public LivroResponse criar(LivroRequest request) {
        validarAnoPublicacao(request.anoPublicacao());
        verificarIsbnExistente(request.isbn());

        LivroDomain livroSalvo = repository.save(request.toDomain());
        return LivroResponse.de(livroSalvo, livroSalvo.getQuantidadeTotal());
    }

    public LivroResponse atualizar(Long id, LivroRequest request) {
        validarAnoPublicacao(request.anoPublicacao());

        LivroDomain livro = buscarEntidadePorId(id);
        validarAtualizacaoIsbn(livro.getIsbn(), request.isbn());

        livro.setTitulo(request.titulo());
        livro.setAutor(request.autor());
        livro.setIsbn(request.isbn());
        livro.setAnoPublicacao(request.anoPublicacao());
        livro.setQuantidadeTotal(request.quantidadeTotal());

        return toResponse(repository.save(livro));
    }

    public void remover(Long id) {
        buscarEntidadePorId(id);
        verificarSemEmprestimosAtivos(id);
        repository.deleteById(id);
    }

    private LivroDomain buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro", id));
    }

    private LivroResponse toResponse(LivroDomain livro) {
        Integer ativos = emprestimoRepository.countAtivosByLivroId(livro.getId()).intValue();
        Integer disponivel = Math.max(0, livro.getQuantidadeTotal() - ativos);
        return LivroResponse.de(livro, disponivel);
    }

    private Integer calcularQuantidadeDisponivel(LivroDomain livro, Map<Long, Long> ativosPorLivro) {
        Integer ativos = ativosPorLivro.getOrDefault(livro.getId(), 0L).intValue();
        return Math.max(0, livro.getQuantidadeTotal() - ativos);
    }

    private Predicate<LivroResponse> porDisponibilidade(Boolean disponivel) {
        if (disponivel == null) {
            return livro -> true;
        }
        return livro -> (livro.quantidadeDisponivel() > 0) == disponivel;
    }

    private void validarAnoPublicacao(Integer anoPublicacao) {
        Integer anoAtual = LocalDate.now(clock).getYear();
        if (anoPublicacao > anoAtual) {
            throw new NegocioException(
                    "O ano de publicação (" + anoPublicacao + ") não pode ser maior que o ano atual (" + anoAtual + ")."
            );
        }
    }

    private void validarAtualizacaoIsbn(String isbnAtual, String novoIsbn) {
        if (!Objects.equals(isbnAtual, novoIsbn)) {
            verificarIsbnExistente(novoIsbn);
        }
    }

    private void verificarIsbnExistente(String isbn) {
        if (repository.existsByIsbn(isbn)) {
            throw new ConflitoException("Já existe um livro cadastrado com o ISBN " + isbn);
        }
    }

    private void verificarSemEmprestimosAtivos(Long livroId) {
        if (emprestimoRepository.countAtivosByLivroId(livroId) > 0) {
            throw new ConflitoException(
                    "Não é possível remover o livro " + livroId + " pois possui empréstimos ativos."
            );
        }
    }
}