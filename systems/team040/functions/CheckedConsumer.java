package systems.team040.functions;

/**
 * Created this and the other 'checked' interface to allow writing lambdas that throw exceptions
 */
@FunctionalInterface
public interface CheckedConsumer<T, E extends  Exception> {
    void accept(T t) throws E;
}
