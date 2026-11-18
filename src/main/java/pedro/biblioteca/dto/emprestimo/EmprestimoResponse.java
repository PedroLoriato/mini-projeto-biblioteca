package pedro.biblioteca.dto.emprestimo;

import pedro.biblioteca.domain.EmprestimoDomain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmprestimoResponse(
        Long id,
        Long livroId,
        String titulo,
        Long leitorId,
        String leitor,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        LocalDate dataDevolucao,
        BigDecimal multa
) {
    public static EmprestimoResponse de(EmprestimoDomain emprestimo, String tituloLivro, String nomeLeitor) {
        return new EmprestimoResponse(
                emprestimo.getId(),
                emprestimo.getLivroId(),
                tituloLivro,
                emprestimo.getLeitorId(),
                nomeLeitor,
                emprestimo.getDataEmprestimo(),
                emprestimo.getDataPrevistaDevolucao(),
                emprestimo.getDataDevolucao(),
                emprestimo.getMulta()
        );
    }
}