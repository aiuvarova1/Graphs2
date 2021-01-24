package exceptions;

public class IsAlreadyVisitedException extends RuntimeException {
    public IsAlreadyVisitedException(String message) {
        super(message);
    }

}
