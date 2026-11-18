package pedro.biblioteca.policy;

import pedro.biblioteca.domain.TipoLeitor;

import java.math.BigDecimal;

public interface PoliticaEmprestimo {
    TipoLeitor tipo();

    Integer prazoEmDias();

    Integer limiteEmprestimos();

    BigDecimal calcularMulta(Long diasAtraso);
}