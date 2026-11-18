package pedro.biblioteca.domain;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LivroDomain {
    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private Integer anoPublicacao;
    private Integer quantidadeTotal;

    public LivroDomain(
            String titulo,
            String autor,
            String isbn,
            Integer anoPublicacao,
            Integer quantidadeTotal
    ) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.anoPublicacao = anoPublicacao;
        this.quantidadeTotal = quantidadeTotal;
    }
}