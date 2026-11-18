package pedro.biblioteca.exception;

public class ConflitoException extends NegocioException {
    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}