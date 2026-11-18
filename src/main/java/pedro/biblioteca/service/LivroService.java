package pedro.biblioteca.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.biblioteca.domain.LivroDomain;
import pedro.biblioteca.dto.livro.LivroRequest;
import pedro.biblioteca.dto.livro.LivroResponse;
import pedro.biblioteca.exception.ConflitoException;
import pedro.biblioteca.exception.NegocioException;
import pedro.biblioteca.exception.RecursoNaoEncontradoException;
import pedro.biblioteca.repository.LivroRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository repository;
    private final Clock clock;

    public List<LivroResponse> listar(String autor) {
        return Optional.ofNullable(autor)
                .filter(Predicate.not(String::isBlank))
                .map(repository::findByAutor)
                .orElseGet(repository::findAll)
                .stream()
                .map(LivroResponse::de)
                .toList();
    }

    public LivroResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(LivroResponse::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro", id));
    }

    public LivroResponse criar(LivroRequest request) {
        validarAnoPublicacao(request.anoPublicacao());
        verificarIsbnExistente(request.isbn());

        LivroDomain livro = request.toDomain();
        LivroDomain livroSalvo = repository.save(livro);

        return LivroResponse.de(livroSalvo);
    }

    public LivroResponse atualizar(Long id, LivroRequest request) {
        validarAnoPublicacao(request.anoPublicacao());

        LivroDomain livro = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro", id));

        if (!Objects.equals(livro.getIsbn(), request.isbn())) {
            verificarIsbnExistente(request.isbn());
        }

        livro.setTitulo(request.titulo());
        livro.setAutor(request.autor());
        livro.setIsbn(request.isbn());
        livro.setAnoPublicacao(request.anoPublicacao());
        livro.setQuantidadeTotal(request.quantidadeTotal());

        return LivroResponse.de(repository.save(livro));
    }

    public void remover(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Livro", id);
        }
        repository.deleteById(id);
    }

    private void verificarIsbnExistente(String isbn) {
        if (repository.existsByIsbn(isbn)) {
            throw new ConflitoException("Já existe um livro cadastrado com o ISBN " + isbn);
        }
    }

    private void validarAnoPublicacao(Integer anoPublicacao) {
        int anoAtual = LocalDate.now(clock).getYear();
        if (anoPublicacao > anoAtual) {
            throw new NegocioException("O ano de publicação (" + anoPublicacao + ") não pode ser maior que o ano atual (" + anoAtual + ").");
        }
    }
}