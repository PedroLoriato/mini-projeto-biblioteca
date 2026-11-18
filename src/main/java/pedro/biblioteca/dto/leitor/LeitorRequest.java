package pedro.biblioteca.dto.leitor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pedro.biblioteca.domain.LeitorDomain;
import pedro.biblioteca.domain.TipoLeitor;

public record LeitorRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve ter um formato válido")
        String email,

        @NotNull(message = "O tipo de leitor é obrigatório (ALUNO ou PROFESSOR)")
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