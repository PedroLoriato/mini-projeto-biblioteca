package pedro.biblioteca.dto.livro;

import pedro.biblioteca.domain.LivroDomain;

public record LivroRequest(
        String titulo,
        String autor,
        String isbn,
        Integer anoPublicacao,
        Integer quantidadeTotal
) {
    public LivroDomain toDomain() {
        return LivroDomain.builder()
                .titulo(this.titulo)
                .autor(this.autor)
                .isbn(this.isbn)
                .anoPublicacao(this.anoPublicacao)
                .quantidadeTotal(this.quantidadeTotal)
                .build();
    }
}