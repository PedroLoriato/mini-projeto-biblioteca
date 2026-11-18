package pedro.biblioteca.repository;

import pedro.biblioteca.domain.LeitorDomain;
import pedro.biblioteca.domain.TipoLeitor;

import java.util.List;
import java.util.Optional;

public interface LeitorRepository {
    List<LeitorDomain> findAll();
    List<LeitorDomain> findByTipo(TipoLeitor tipo);
    Optional<LeitorDomain> findById(Long id);
    LeitorDomain save(LeitorDomain curso);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByEmail(String email);
}