package pedro.biblioteca.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoDomain {

    private Long id;
    private Long livroId;
    private Long leitorId;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private BigDecimal multa;

    public boolean isAtivo() {
        return this.dataDevolucao == null;
    }

    public boolean isAtrasado(LocalDate dataReferencia) {
        return isAtivo() && this.dataPrevistaDevolucao.isBefore(dataReferencia);
    }
}