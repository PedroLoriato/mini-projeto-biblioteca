package pedro.biblioteca.policy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pedro.biblioteca.domain.TipoLeitor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PoliticaProfessor implements PoliticaEmprestimo {

    private final Integer prazoDias;
    private final Integer limiteAtivos;
    private final BigDecimal multaDiaria;

    public PoliticaProfessor(
            @Value("${biblioteca.emprestimo.prazo-dias.professor}") Integer prazoDias,
            @Value("${biblioteca.emprestimo.limite-ativos.professor}") Integer limiteAtivos,
            @Value("${biblioteca.emprestimo.multa-diaria.professor}") BigDecimal multaDiaria
    ) {
        this.prazoDias = prazoDias;
        this.limiteAtivos = limiteAtivos;
        this.multaDiaria = multaDiaria;
    }

    @Override
    public TipoLeitor tipo() {
        return TipoLeitor.PROFESSOR;
    }

    @Override
    public Integer prazoEmDias() {
        return prazoDias;
    }

    @Override
    public Integer limiteEmprestimos() {
        return limiteAtivos;
    }

    @Override
    public BigDecimal calcularMulta(Long diasAtraso) {
        if (diasAtraso <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return multaDiaria.multiply(BigDecimal.valueOf(diasAtraso))
                .setScale(2, RoundingMode.HALF_UP);
    }
}