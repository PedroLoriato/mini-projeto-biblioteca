package pedro.biblioteca.repository;

import pedro.biblioteca.domain.EmprestimoDomain;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EmprestimoRepository {
    EmprestimoDomain save(EmprestimoDomain emprestimo);

    Optional<EmprestimoDomain> findById(Long id);

    List<EmprestimoDomain> findAll();

    Long countAtivosByLivroId(Long livroId);

    Long countAtivosByLeitorId(Long leitorId);

    Map<Long, Long> countAtivosPorLivro();

    Boolean existsAtrasadoByLeitorId(Long leitorId, LocalDate dataReferencia);

    List<EmprestimoDomain> findAtrasados(LocalDate dataReferencia);
}