package pedro.biblioteca.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.biblioteca.domain.LivroDomain;
import pedro.biblioteca.dto.livro.LivroRequest;
import pedro.biblioteca.dto.livro.LivroResponse;
import pedro.biblioteca.repository.LivroRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository repository;

    public List<LivroResponse> listar(String autor) {
        return Optional.ofNullable(autor)
                .filter(Predicate.not(String::isBlank))
                .map(repository::findByAutor)
                .orElseGet(repository::findAll)
                .stream()
                .map(LivroResponse::de)
                .toList();
    }

    public Optional<LivroResponse> buscarPorId(Long id) {
        return repository.findById(id)
                .map(LivroResponse::de);
    }

    public LivroResponse criar(LivroRequest request) {
        verificarIsbnExistente(request.isbn());

        LivroDomain livro = request.toDomain();
        LivroDomain livroSalvo = repository.save(livro);

        return LivroResponse.de(livroSalvo);
    }

    public Optional<LivroResponse> atualizar(Long id, LivroRequest request) {
        return repository.findById(id)
                .map(livro -> {
                    if (!Objects.equals(livro.getIsbn(), request.isbn())) {
                        verificarIsbnExistente(request.isbn());
                    }
                    livro.setTitulo(request.titulo());
                    livro.setAutor(request.autor());
                    livro.setIsbn(request.isbn());
                    livro.setAnoPublicacao(request.anoPublicacao());
                    livro.setQuantidadeTotal(request.quantidadeTotal());
                    return LivroResponse.de(repository.save(livro));
                });
    }

    public boolean remover(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    private void verificarIsbnExistente(String isbn) {
        if (repository.existsByIsbn(isbn)) {
            throw new IllegalStateException("Já existe um livro com esse isbn: " + isbn);
        }
    }
}