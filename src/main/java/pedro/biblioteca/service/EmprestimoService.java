package pedro.biblioteca.service;

import org.springframework.stereotype.Service;
import pedro.biblioteca.domain.EmprestimoDomain;
import pedro.biblioteca.domain.LeitorDomain;
import pedro.biblioteca.domain.LivroDomain;
import pedro.biblioteca.domain.TipoLeitor;
import pedro.biblioteca.dto.emprestimo.EmprestimoRequest;
import pedro.biblioteca.dto.emprestimo.EmprestimoResponse;
import pedro.biblioteca.exception.ConflitoException;
import pedro.biblioteca.exception.RecursoNaoEncontradoException;
import pedro.biblioteca.policy.PoliticaEmprestimo;
import pedro.biblioteca.repository.EmprestimoRepository;
import pedro.biblioteca.repository.LeitorRepository;
import pedro.biblioteca.repository.LivroRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private final LeitorRepository leitorRepository;
    private final Clock clock;
    private final Map<TipoLeitor, PoliticaEmprestimo> politicas;

    public EmprestimoService(
            EmprestimoRepository emprestimoRepository,
            LivroRepository livroRepository,
            LeitorRepository leitorRepository,
            Clock clock,
            List<PoliticaEmprestimo> politicas
    ) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
        this.leitorRepository = leitorRepository;
        this.clock = clock;
        this.politicas = politicas.stream()
                .collect(Collectors.toMap(PoliticaEmprestimo::tipo, Function.identity()));
    }

    public EmprestimoResponse realizar(EmprestimoRequest request) {
        LivroDomain livro = livroRepository.findById(request.livroId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro", request.livroId()));

        LeitorDomain leitor = leitorRepository.findById(request.leitorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Leitor", request.leitorId()));

        LocalDate hoje = LocalDate.now(clock);
        PoliticaEmprestimo politica = politicas.get(leitor.getTipo());

        long emprestimosAtivosLivro = emprestimoRepository.countAtivosByLivroId(livro.getId());
        if (livro.getQuantidadeTotal() - emprestimosAtivosLivro <= 0) {
            throw new ConflitoException("Não há exemplares disponíveis para o livro '" + livro.getTitulo() + "'.");
        }

        if (emprestimoRepository.existsAtrasadoByLeitorId(leitor.getId(), hoje)) {
            throw new ConflitoException("O leitor possui empréstimo em atraso e não pode realizar novo empréstimo.");
        }

        long emprestimosAtivosLeitor = emprestimoRepository.countAtivosByLeitorId(leitor.getId());
        if (emprestimosAtivosLeitor >= politica.limiteEmprestimos()) {
            throw new ConflitoException(
                    "O leitor atingiu o limite máximo de " +
                            politica.limiteEmprestimos() +
                            " empréstimos ativos."
            );
        }

        EmprestimoDomain novoEmprestimo = EmprestimoDomain.builder()
                .livroId(livro.getId())
                .leitorId(leitor.getId())
                .dataEmprestimo(hoje)
                .dataPrevistaDevolucao(hoje.plusDays(politica.prazoEmDias()))
                .dataDevolucao(null)
                .multa(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .build();

        EmprestimoDomain salvo = emprestimoRepository.save(novoEmprestimo);
        return EmprestimoResponse.de(salvo, livro.getTitulo(), leitor.getNome());
    }

    public List<EmprestimoResponse> listar(Long leitorId, Boolean ativos) {
        List<EmprestimoDomain> filtrados = emprestimoRepository.findAll().stream()
                .filter(porLeitor(leitorId))
                .filter(porStatusAtivo(ativos))
                .toList();

        return toResponseList(filtrados);
    }

    public EmprestimoResponse buscarPorId(Long id) {
        EmprestimoDomain emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empréstimo", id));

        return toResponse(emprestimo);
    }

    public EmprestimoResponse devolver(Long id) {
        EmprestimoDomain emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empréstimo", id));

        if (!emprestimo.isAtivo()) {
            throw new ConflitoException("O empréstimo " + id + " já foi devolvido.");
        }

        LeitorDomain leitor = leitorRepository.findById(emprestimo.getLeitorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Leitor", emprestimo.getLeitorId()));

        LocalDate hoje = LocalDate.now(clock);
        PoliticaEmprestimo politica = politicas.get(leitor.getTipo());

        Long diasAtraso = Math.max(0, ChronoUnit.DAYS.between(emprestimo.getDataPrevistaDevolucao(), hoje));
        emprestimo.setDataDevolucao(hoje);
        emprestimo.setMulta(politica.calcularMulta(diasAtraso));

        EmprestimoDomain atualizado = emprestimoRepository.save(emprestimo);
        return toResponse(atualizado);
    }

    public List<EmprestimoResponse> listarAtrasados() {
        LocalDate hoje = LocalDate.now(clock);
        return toResponseList(emprestimoRepository.findAtrasados(hoje));
    }

    private Predicate<EmprestimoDomain> porLeitor(Long leitorId) {
        return Optional.ofNullable(leitorId)
                .<Predicate<EmprestimoDomain>>map(id ->
                        emprestimo -> emprestimo.getLeitorId().equals(id))
                .orElse(emprestimo -> true);
    }

    private Predicate<EmprestimoDomain> porStatusAtivo(Boolean ativos) {
        return Optional.ofNullable(ativos)
                .<Predicate<EmprestimoDomain>>map(
                        ativo -> emprestimo -> emprestimo.isAtivo() == ativo)
                .orElse(emprestimo -> true);
    }

    private EmprestimoResponse toResponse(EmprestimoDomain emprestimo) {
        String tituloLivro = livroRepository.findById(emprestimo.getLivroId())
                .map(LivroDomain::getTitulo)
                .orElse("Livro removido");

        String nomeLeitor = leitorRepository.findById(emprestimo.getLeitorId())
                .map(LeitorDomain::getNome)
                .orElse("Leitor removido");

        return EmprestimoResponse.de(emprestimo, tituloLivro, nomeLeitor);
    }

    private List<EmprestimoResponse> toResponseList(List<EmprestimoDomain> emprestimos) {
        if (emprestimos.isEmpty()) {
            return List.of();
        }

        Map<Long, String> titulosPorLivro = livroRepository.findAll().stream()
                .collect(Collectors.toMap(LivroDomain::getId, LivroDomain::getTitulo));

        Map<Long, String> nomesPorLeitor = leitorRepository.findAll().stream()
                .collect(Collectors.toMap(LeitorDomain::getId, LeitorDomain::getNome));

        return emprestimos.stream()
                .map(e -> EmprestimoResponse.de(
                        e,
                        titulosPorLivro.getOrDefault(e.getLivroId(), "Livro removido"),
                        nomesPorLeitor.getOrDefault(e.getLeitorId(), "Leitor removido")
                ))
                .toList();
    }
}