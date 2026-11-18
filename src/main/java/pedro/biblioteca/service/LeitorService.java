package pedro.biblioteca.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.biblioteca.domain.LeitorDomain;
import pedro.biblioteca.domain.TipoLeitor;
import pedro.biblioteca.dto.leitor.LeitorRequest;
import pedro.biblioteca.dto.leitor.LeitorResponse;
import pedro.biblioteca.exception.ConflitoException;
import pedro.biblioteca.exception.RecursoNaoEncontradoException;
import pedro.biblioteca.repository.EmprestimoRepository;
import pedro.biblioteca.repository.LeitorRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeitorService {

    private final LeitorRepository repository;
    private final EmprestimoRepository emprestimoRepository;

    public List<LeitorResponse> listar(TipoLeitor tipo) {
        return Optional.ofNullable(tipo)
                .map(repository::findByTipo)
                .orElseGet(repository::findAll)
                .stream()
                .map(LeitorResponse::de)
                .toList();
    }

    public LeitorResponse buscarPorId(Long id) {
        return LeitorResponse.de(buscarEntidadePorId(id));
    }

    public LeitorResponse criar(LeitorRequest request) {
        verificarEmailExistente(request.email());
        LeitorDomain leitorSalvo = repository.save(request.toDomain());
        return LeitorResponse.de(leitorSalvo);
    }

    public LeitorResponse atualizar(Long id, LeitorRequest request) {
        LeitorDomain leitor = buscarEntidadePorId(id);
        validarAtualizacaoEmail(leitor.getEmail(), request.email());

        leitor.setNome(request.nome());
        leitor.setEmail(request.email());
        leitor.setTipo(request.tipo());

        return LeitorResponse.de(repository.save(leitor));
    }

    public void remover(Long id) {
        buscarEntidadePorId(id);
        verificarSemEmprestimosAtivos(id);
        repository.deleteById(id);
    }

    private LeitorDomain buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Leitor", id));
    }

    private void validarAtualizacaoEmail(String emailAtual, String novoEmail) {
        if (!Objects.equals(emailAtual, novoEmail)) {
            verificarEmailExistente(novoEmail);
        }
    }

    private void verificarEmailExistente(String email) {
        if (repository.existsByEmail(email)) {
            throw new ConflitoException("Já existe um leitor cadastrado com o e-mail " + email);
        }
    }

    private void verificarSemEmprestimosAtivos(Long leitorId) {
        if (emprestimoRepository.countAtivosByLeitorId(leitorId) > 0) {
            throw new ConflitoException("Não é possível remover o leitor " + leitorId + " pois possui empréstimos ativos.");
        }
    }
}