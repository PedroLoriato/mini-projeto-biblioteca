package pedro.biblioteca.dto.livro;

import pedro.biblioteca.domain.LivroDomain;

public record LivroResponse(
        Long id,
        String titulo,
        String autor,
        String isbn,
        Integer anoPublicacao,
        Integer quantidadeTotal,
        Integer quantidadeDisponivel
) {
    public static LivroResponse de(LivroDomain livro, int quantidadeDisponivel) {
        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getAnoPublicacao(),
                livro.getQuantidadeTotal(),
                quantidadeDisponivel
        );
    }
}