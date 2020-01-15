package eye2web.modelmapper.exception;

public class NoArgsConstructorException extends Exception {

    public NoArgsConstructorException(final String className) {
        super(String.format("Constructor with zero arguments is does not exist: '%s'", className));
    }
}
