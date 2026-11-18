package pedro.biblioteca.repository;

import org.springframework.stereotype.Repository;
import pedro.biblioteca.domain.LivroDomain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LivroRepositoryEmMemoria implements LivroRepository {
    private final Map<Long, LivroDomain> dados = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();

    @Override
    public LivroDomain save(LivroDomain livro) {
        if (Objects.isNull(livro.getId())) {
            livro.setId(sequencia.incrementAndGet());
        }
        dados.put(livro.getId(), livro);
        return livro;
    }

    @Override
    public Optional<LivroDomain> findById(Long id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<LivroDomain> findAll() {
        return List.copyOf(dados.values());
    }

    @Override
    public List<LivroDomain> findByAutor(String autor) {
        String termoBusca = autor.toLowerCase();

        return dados.values().stream()
                .filter(livro -> Objects.nonNull(livro.getAutor()) &&
                        livro.getAutor().toLowerCase().contains(termoBusca))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        dados.remove(id);
    }

    @Override
    public Boolean existsById(Long id) {
        return dados.containsKey(id);
    }

    @Override
    public Boolean existsByIsbn(String isbn) {
        return dados.values().stream()
                .anyMatch(livro -> livro.getIsbn().equalsIgnoreCase(isbn));
    }
}
