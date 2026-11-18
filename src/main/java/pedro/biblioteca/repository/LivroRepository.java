package pedro.biblioteca.repository;

import pedro.biblioteca.domain.LivroDomain;

import java.util.List;
import java.util.Optional;

public interface LivroRepository {
    List<LivroDomain> findAll();
    List<LivroDomain> findByAutor(String autor);
    Optional<LivroDomain> findById(Long id);
    LivroDomain save(LivroDomain curso);
    void deleteById(Long id);

    Boolean existsById(Long id);

    Boolean existsByIsbn(String isbn);
}
