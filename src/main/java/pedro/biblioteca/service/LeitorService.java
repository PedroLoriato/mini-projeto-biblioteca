package pedro.biblioteca.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.biblioteca.domain.LeitorDomain;
import pedro.biblioteca.domain.TipoLeitor;
import pedro.biblioteca.dto.leitor.LeitorRequest;
import pedro.biblioteca.dto.leitor.LeitorResponse;
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

    public Optional<LeitorResponse> buscarPorId(Long id) {
        return repository.findById(id)
                .map(LeitorResponse::de);
    }

    public LeitorResponse criar(LeitorRequest request) {
        verificarEmailExistente(request.email());

        LeitorDomain leitor = request.toDomain();
        LeitorDomain leitorSalvo = repository.save(leitor);

        return LeitorResponse.de(leitorSalvo);
    }

    public Optional<LeitorResponse> atualizar(Long id, LeitorRequest request) {
        return repository.findById(id)
                .map(leitor -> {
                    if (!Objects.equals(leitor.getEmail(), request.email())) {
                        verificarEmailExistente(request.email());
                    }
                    leitor.setNome(request.nome());
                    leitor.setEmail(request.email());
                    leitor.setTipo(request.tipo());
                    return LeitorResponse.de(repository.save(leitor));
                });
    }

    public boolean remover(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    private void verificarEmailExistente(String email) {
        if (repository.existsByEmail(email)) {
            throw new IllegalStateException("Já existe um leitor com esse e-mail: " + email);
        }
    }
}