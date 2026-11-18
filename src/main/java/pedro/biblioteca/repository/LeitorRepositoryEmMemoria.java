package pedro.biblioteca.repository;

import org.springframework.stereotype.Repository;
import pedro.biblioteca.domain.LeitorDomain;
import pedro.biblioteca.domain.TipoLeitor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LeitorRepositoryEmMemoria implements LeitorRepository {
    private final Map<Long, LeitorDomain> dados = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();

    @Override
    public LeitorDomain save(LeitorDomain leitor) {
        if (Objects.isNull(leitor.getId())) {
            leitor.setId(sequencia.incrementAndGet());
        }
        dados.put(leitor.getId(), leitor);
        return leitor;
    }

    @Override
    public Optional<LeitorDomain> findById(Long id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<LeitorDomain> findAll() {
        return List.copyOf(dados.values());
    }

    @Override
    public List<LeitorDomain> findByTipo(TipoLeitor tipo) {
        if (Objects.isNull(tipo)) {
            return List.of();
        }
        return dados.values().stream()
                .filter(leitor -> Objects.equals(leitor.getTipo(), tipo))
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
    public Boolean existsByEmail(String email) {
        return dados.values().stream()
                .anyMatch(leitor -> leitor.getEmail().equalsIgnoreCase(email));
    }
}