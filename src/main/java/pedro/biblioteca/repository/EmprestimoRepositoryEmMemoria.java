package pedro.biblioteca.repository;

import org.springframework.stereotype.Repository;
import pedro.biblioteca.domain.EmprestimoDomain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class EmprestimoRepositoryEmMemoria implements EmprestimoRepository {

    private final Map<Long, EmprestimoDomain> dados = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    @Override
    public EmprestimoDomain save(EmprestimoDomain emprestimo) {
        if (emprestimo.getId() == null) {
            emprestimo.setId(sequencia.getAndIncrement());
        }
        dados.put(emprestimo.getId(), emprestimo);
        return emprestimo;
    }

    @Override
    public Optional<EmprestimoDomain> findById(Long id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<EmprestimoDomain> findAll() {
        return new ArrayList<>(dados.values());
    }

    @Override
    public Long countAtivosByLivroId(Long livroId) {
        return dados.values().stream()
                .filter(e -> e.getLivroId().equals(livroId) && e.isAtivo())
                .count();
    }

    @Override
    public Long countAtivosByLeitorId(Long leitorId) {
        return dados.values().stream()
                .filter(e -> e.getLeitorId().equals(leitorId) && e.isAtivo())
                .count();
    }

    @Override
    public Map<Long, Long> countAtivosPorLivro() {
        return dados.values().stream()
                .filter(EmprestimoDomain::isAtivo)
                .collect(Collectors.groupingBy(EmprestimoDomain::getLivroId, Collectors.counting()));
    }

    @Override
    public Boolean existsAtrasadoByLeitorId(Long leitorId, LocalDate dataReferencia) {
        return dados.values().stream()
                .anyMatch(e -> e.getLeitorId().equals(leitorId) && e.isAtrasado(dataReferencia));
    }

    @Override
    public List<EmprestimoDomain> findAtrasados(LocalDate dataReferencia) {
        return dados.values().stream()
                .filter(e -> e.isAtrasado(dataReferencia))
                .toList();
    }
}