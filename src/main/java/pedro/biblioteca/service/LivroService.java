package pedro.biblioteca.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.biblioteca.domain.LivroDomain;
import pedro.biblioteca.dto.livro.LivroRequest;
import pedro.biblioteca.dto.livro.LivroResponse;
import pedro.biblioteca.mapper.LivroMapper;
import pedro.biblioteca.repository.LivroRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository repository;
    private final LivroMapper mapper;

    public List<LivroResponse> listar() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public Optional<LivroResponse> buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse);
    }

    public LivroResponse criar(LivroRequest request) {
        if (repository.existsByIsbn(request.isbn())) {
            throw new IllegalStateException("Já existe um livro com esse isbn: " + request.isbn());
        }

        LivroDomain livro = mapper.toDomain(request);
        LivroDomain livroSalvo = repository.save(livro);

        return mapper.toResponse(livroSalvo);
    }

    public Optional<LivroResponse> atualizar(Long id, LivroRequest request) {
        return repository.findById(id)
                .map(livro -> {
                    livro.setTitulo(request.titulo());
                    livro.setAutor(request.autor());
                    livro.setIsbn(request.isbn());
                    livro.setAnoPublicacao(request.anoPublicacao());
                    livro.setQuantidadeTotal(request.quantidadeTotal());
                    return mapper.toResponse(repository.save(livro));
                });
    }

    public boolean remover(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}