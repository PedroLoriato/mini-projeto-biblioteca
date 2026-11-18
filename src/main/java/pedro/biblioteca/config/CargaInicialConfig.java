package pedro.biblioteca.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pedro.biblioteca.domain.EmprestimoDomain;
import pedro.biblioteca.domain.LeitorDomain;
import pedro.biblioteca.domain.LivroDomain;
import pedro.biblioteca.domain.TipoLeitor;
import pedro.biblioteca.repository.EmprestimoRepository;
import pedro.biblioteca.repository.LeitorRepository;
import pedro.biblioteca.repository.LivroRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Configuration
public class CargaInicialConfig {

    @Bean
    public CommandLineRunner carregarDadosIniciais(
            LivroRepository livroRepository,
            LeitorRepository leitorRepository,
            EmprestimoRepository emprestimoRepository,
            Clock clock
    ) {
        return args -> {
            List<LivroDomain> livros = List.of(
                    criarLivro(
                            "Código Limpo",
                            "Robert C. Martin",
                            "9788576082675",
                            2009,
                            2
                    ),
                    criarLivro(
                            "Arquitetura Limpa",
                            "Robert C. Martin",
                            "9788550804606",
                            2019,
                            1
                    ),
                    criarLivro(
                            "Domain-Driven Design",
                            "Eric Evans",
                            "9788550800653",
                            2016,
                            3
                    )
            );
            livros.forEach(livroRepository::save);

            List<LeitorDomain> leitores = List.of(
                    criarLeitor("Ana Souza", "ana.souza@ifes.edu.br", TipoLeitor.ALUNO),
                    criarLeitor("Carlos Mendes", "carlos.mendes@ifes.edu.br", TipoLeitor.PROFESSOR)
            );
            leitores.forEach(leitorRepository::save);

            LocalDate hoje = LocalDate.now(clock);

            EmprestimoDomain emprestimoAtrasado = EmprestimoDomain.builder()
                    .livroId(livros.getFirst().getId())
                    .leitorId(leitores.getFirst().getId())
                    .dataEmprestimo(hoje.minusDays(10))
                    .dataPrevistaDevolucao(hoje.minusDays(3))
                    .dataDevolucao(null)
                    .multa(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                    .build();

            emprestimoRepository.save(emprestimoAtrasado);

            log.info(
                    "Carga inicial concluída: {} livros, {} leitores e 1 empréstimo atrasado.",
                    livros.size(),
                    leitores.size()
            );
        };
    }

    private LivroDomain criarLivro(
            String titulo,
            String autor,
            String isbn,
            Integer anoPublicacao,
            Integer quantidadeTotal
    ) {
        return LivroDomain.builder()
                .titulo(titulo)
                .autor(autor)
                .isbn(isbn)
                .anoPublicacao(anoPublicacao)
                .quantidadeTotal(quantidadeTotal)
                .build();
    }

    private LeitorDomain criarLeitor(String nome, String email, TipoLeitor tipo) {
        return LeitorDomain.builder()
                .nome(nome)
                .email(email)
                .tipo(tipo)
                .build();
    }
}