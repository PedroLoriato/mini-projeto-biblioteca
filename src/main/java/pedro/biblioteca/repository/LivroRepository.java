package pedro.biblioteca.repository;

import pedro.biblioteca.domain.LivroDomain;

import java.util.List;
import java.util.Optional;

public interface LivroRepository {
    List<LivroDomain> findAll();

    Optional<LivroDomain> findById(Long id);

    LivroDomain save(LivroDomain curso);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByIsbn(String Isbn);
}
