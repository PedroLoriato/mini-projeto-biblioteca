package pedro.biblioteca.dto.livro;

public record LivroRequest(
        String titulo,
        String autor,
        String isbn,
        Integer anoPublicacao,
        Integer quantidadeTotal
) {
}
