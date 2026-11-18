package pedro.biblioteca.dto.leitor;

import pedro.biblioteca.domain.LeitorDomain;
import pedro.biblioteca.domain.TipoLeitor;

public record LeitorResponse(
        Long id,
        String nome,
        String email,
        TipoLeitor tipo
) {
    public static LeitorResponse de(LeitorDomain leitor) {
        return new LeitorResponse(
                leitor.getId(),
                leitor.getNome(),
                leitor.getEmail(),
                leitor.getTipo()
        );
    }
}