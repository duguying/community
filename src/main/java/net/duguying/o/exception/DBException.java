package net.duguying.o.exception;

/**
 * Created by duguying on 15/11/25.
 */
public class DBException extends RuntimeException {
    public DBException(Exception cause) {
        super(cause);
    }
}
