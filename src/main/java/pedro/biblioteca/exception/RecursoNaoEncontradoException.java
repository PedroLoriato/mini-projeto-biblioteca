package pedro.biblioteca.exception;

public class RecursoNaoEncontradoException extends NegocioException {
    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " " + id + " não encontrado");
    }
}