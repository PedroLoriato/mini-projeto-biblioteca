package pedro.biblioteca.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.biblioteca.domain.LeitorDomain;
import pedro.biblioteca.domain.TipoLeitor;
import pedro.biblioteca.dto.leitor.LeitorRequest;
import pedro.biblioteca.dto.leitor.LeitorResponse;
import pedro.biblioteca.exception.ConflitoException;
import pedro.biblioteca.exception.RecursoNaoEncontradoException;
import pedro.biblioteca.repository.LeitorRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeitorService {

    private final LeitorRepository repository;

    public List<LeitorResponse> listar(TipoLeitor tipo) {
        return Optional.ofNullable(tipo)
                .map(repository::findByTipo)
                .orElseGet(repository::findAll)
                .stream()
                .map(LeitorResponse::de)
                .toList();
    }

    public LeitorResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(LeitorResponse::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Leitor", id));
    }

    public LeitorResponse criar(LeitorRequest request) {
        verificarEmailExistente(request.email());

        LeitorDomain leitor = request.toDomain();
        LeitorDomain leitorSalvo = repository.save(leitor);

        return LeitorResponse.de(leitorSalvo);
    }

    public LeitorResponse atualizar(Long id, LeitorRequest request) {
        LeitorDomain leitor = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Leitor", id));

        if (!Objects.equals(leitor.getEmail(), request.email())) {
            verificarEmailExistente(request.email());
        }

        leitor.setNome(request.nome());
        leitor.setEmail(request.email());
        leitor.setTipo(request.tipo());

        return LeitorResponse.de(repository.save(leitor));
    }

    public void remover(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Leitor", id);
        }
        repository.deleteById(id);
    }

    private void verificarEmailExistente(String email) {
        if (repository.existsByEmail(email)) {
            throw new ConflitoException("Já existe um leitor cadastrado com o e-mail " + email);
        }
    }
}