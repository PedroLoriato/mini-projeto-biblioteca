package pedro.biblioteca.dto.emprestimo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EmprestimoRequest(
        @NotNull(message = "O ID do livro é obrigatório")
        @Positive(message = "O ID do livro deve ser maior que zero")
        Long livroId,

        @NotNull(message = "O ID do leitor é obrigatório")
        @Positive(message = "O ID do leitor deve ser maior que zero")
        Long leitorId
) {
}