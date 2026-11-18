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
            LivroDomain livro1 = criarLivro(
                    "Código Limpo",
                    "Robert C. Martin",
                    "9788576082675",
                    2009,
                    2
            );
            LivroDomain livro2 = criarLivro(
                    "Arquitetura Limpa",
                    "Robert C. Martin",
                    "9788550804606",
                    2019,
                    1
            );
            LivroDomain livro3 = criarLivro(
                    "Domain-Driven Design",
                    "Eric Evans",
                    "9788550800653",
                    2016,
                    3
            );

            livroRepository.save(livro1);
            livroRepository.save(livro2);
            livroRepository.save(livro3);

            LeitorDomain aluna = criarLeitor(
                    "Ana Souza",
                    "ana.souza@ifes.edu.br",
                    TipoLeitor.ALUNO
            );
            LeitorDomain professor = criarLeitor(
                    "Carlos Mendes",
                    "carlos.mendes@ifes.edu.br",
                    TipoLeitor.PROFESSOR
            );

            leitorRepository.save(aluna);
            leitorRepository.save(professor);

            LocalDate hoje = LocalDate.now(clock);

            EmprestimoDomain emprestimoAtrasado = EmprestimoDomain.builder()
                    .livroId(livro1.getId())
                    .leitorId(aluna.getId())
                    .dataEmprestimo(hoje.minusDays(10))
                    .dataPrevistaDevolucao(hoje.minusDays(3))
                    .dataDevolucao(null)
                    .multa(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                    .build();

            emprestimoRepository.save(emprestimoAtrasado);

            log.info(
                    "Carga inicial concluída: 3 livros, 2 leitores e 1 empréstimo atrasado (id={}).",
                    emprestimoAtrasado.getId());
        };
    }

    private LivroDomain criarLivro(String titulo, String autor, String isbn, int anoPublicacao, int quantidadeTotal) {
        LivroDomain livro = new LivroDomain();
        livro.setTitulo(titulo);
        livro.setAutor(autor);
        livro.setIsbn(isbn);
        livro.setAnoPublicacao(anoPublicacao);
        livro.setQuantidadeTotal(quantidadeTotal);
        return livro;
    }

    private LeitorDomain criarLeitor(String nome, String email, TipoLeitor tipo) {
        LeitorDomain leitor = new LeitorDomain();
        leitor.setNome(nome);
        leitor.setEmail(email);
        leitor.setTipo(tipo);
        return leitor;
    }
}