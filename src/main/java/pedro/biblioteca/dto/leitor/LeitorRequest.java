package pedro.biblioteca.dto.leitor;

import pedro.biblioteca.domain.LeitorDomain;
import pedro.biblioteca.domain.TipoLeitor;

public record LeitorRequest(
        String nome,
        String email,
        TipoLeitor tipo
) {
    public LeitorDomain toDomain() {
        return LeitorDomain.builder()
                .nome(this.nome)
                .email(this.email)
                .tipo(this.tipo)
                .build();
    }
}