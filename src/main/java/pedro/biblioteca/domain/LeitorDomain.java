package pedro.biblioteca.domain;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeitorDomain {
    private Long id;
    private String nome;
    private String email;
    private TipoLeitor tipo;

    public LeitorDomain(
            String nome,
            String email,
            TipoLeitor tipo
    ) {
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
    }
}