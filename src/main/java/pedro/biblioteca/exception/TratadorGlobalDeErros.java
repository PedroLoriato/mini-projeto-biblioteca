package pedro.biblioteca.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class TratadorGlobalDeErros extends ResponseEntityExceptionHandler {

    private final Clock clock;

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return criarProblemDetail(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado",
                ex.getMessage(),
                "recurso-nao-encontrado"
        );
    }

    @ExceptionHandler(ConflitoException.class)
    public ProblemDetail tratarConflito(ConflitoException ex) {
        return criarProblemDetail(
                HttpStatus.CONFLICT,
                "Conflito de dados",
                ex.getMessage(),
                "conflito"
        );
    }

    @ExceptionHandler(NegocioException.class)
    public ProblemDetail tratarNegocio(NegocioException ex) {
        return criarProblemDetail(
                HttpStatus.UNPROCESSABLE_CONTENT,
                "Regra de negócio violada",
                ex.getMessage(),
                "regra-negocio"
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        Map<String, String> erros = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        erro -> Objects.requireNonNullElse(erro.getDefaultMessage(), "inválido"),
                        (primeira, segunda) -> primeira
                ));

        ProblemDetail problema = ex.getBody();
        problema.setTitle("Erro de validação");
        problema.setDetail("Um ou mais campos estão inválidos");
        problema.setType(criarTipoErro("validacao"));
        problema.setProperty("erros", erros);
        problema.setProperty("timestamp", Instant.now(clock));

        return ResponseEntity.badRequest().body(problema);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        ProblemDetail problema = criarProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Requisição malformada",
                "Corpo da requisição inválido ou valor não permitido (ex.: tipo de leitor deve ser ALUNO ou PROFESSOR).",
                "requisicao-malformada"
        );

        return ResponseEntity.badRequest().body(problema);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        String nomeParametro = (ex instanceof MethodArgumentTypeMismatchException mismatch)
                ? mismatch.getName()
                : ex.getPropertyName();

        Object valorInvalido = ex.getValue();
        Class<?> tipoEsperado = ex.getRequiredType();

        String detalhe;
        if (tipoEsperado != null && tipoEsperado.isEnum()) {
            String valoresAceitos = Arrays.stream(tipoEsperado.getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            detalhe = String.format(
                    "Valor inválido '%s' para o parâmetro '%s'. Valores aceitos: [%s].",
                    valorInvalido, nomeParametro, valoresAceitos
            );
        } else {
            detalhe = String.format(
                    "Valor inválido '%s' para o parâmetro '%s'.",
                    valorInvalido, nomeParametro
            );
        }

        ProblemDetail problema = criarProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Parâmetro inválido",
                detalhe,
                "parametro-invalido"
        );

        return ResponseEntity.badRequest().body(problema);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail tratarErroInesperado(Exception ex) {
        log.error("Erro inesperado", ex);
        return criarProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                "erro-interno"
        );
    }

    private ProblemDetail criarProblemDetail(HttpStatus status, String title, String detail, String slug) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detail);
        problema.setTitle(title);
        problema.setType(criarTipoErro(slug));
        problema.setProperty("timestamp", Instant.now(clock));
        return problema;
    }

    private URI criarTipoErro(String slug) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/erros/{slug}")
                .buildAndExpand(slug)
                .toUri();
    }
}