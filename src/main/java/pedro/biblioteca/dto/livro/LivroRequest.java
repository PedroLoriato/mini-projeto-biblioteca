package pedro.biblioteca.dto.livro;

import jakarta.validation.constraints.*;
import pedro.biblioteca.domain.LivroDomain;

public record LivroRequest(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        String titulo,

        @NotBlank(message = "O autor é obrigatório")
        @Size(max = 100, message = "O autor deve ter no máximo 100 caracteres")
        String autor,

        @NotBlank(message = "O ISBN é obrigatório")
        @Pattern(regexp = "\\d{13}", message = "O ISBN deve conter exatamente 13 dígitos numéricos")
        String isbn,

        @NotNull(message = "O ano de publicação é obrigatório")
        @Min(value = 1450, message = "O ano de publicação deve ser a partir de 1450")
        Integer anoPublicacao,

        @NotNull(message = "A quantidade total é obrigatória")
        @Positive(message = "A quantidade total deve ser maior que zero")
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