package systems.team040.functions;

import java.sql.SQLException;

@FunctionalInterface
public interface CheckedFunction<T, R> {
    R apply(T t) throws SQLException;
}
