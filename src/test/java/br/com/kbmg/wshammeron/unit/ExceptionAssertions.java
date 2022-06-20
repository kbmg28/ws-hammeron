package br.com.kbmg.wshammeron.unit;

import br.com.kbmg.wshammeron.exception.ServiceException;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class ExceptionAssertions {

    public static <E extends Throwable> void thenShouldThrowException(Class<E> exceptionClass, Runnable method) {
        E exception = assertThrows(exceptionClass, method::run);
    }

    public static <E extends Throwable> void thenShouldThrowException(Class<E> exceptionClass, Runnable method, String msgErrorExpected) {
        E exception = assertThrows(exceptionClass, method::run);
        assertEquals(msgErrorExpected, exception.getMessage());
    }

    public static <E extends Throwable, P1> void thenShouldThrowException(Class<E> exceptionClass, P1 param1, Consumer<P1> method) {
        assertThrows(exceptionClass, () -> method.accept(param1));
    }

    public static <E extends Throwable, P1> void thenShouldThrowException(Class<E> exceptionClass, P1 param1, Consumer<P1> method, String msgErrorExpected) {
        E exception = assertThrows(exceptionClass, () -> method.accept(param1));
        assertEquals(msgErrorExpected, exception.getMessage());
    }

    public static <E extends Throwable, P1, P2> void thenShouldThrowException(Class<E> exceptionClass, P1 param1, P2 param2, BiConsumer<P1, P2> method) {
        assertThrows(exceptionClass, () -> method.accept(param1, param2));
    }

    public static <E extends Throwable, P1, P2> void thenShouldThrowException(Class<E> exceptionClass, P1 param1, P2 param2, BiConsumer<P1, P2> method, String msgErrorExpected) {
        E exception = assertThrows(exceptionClass, () -> method.accept(param1, param2));
        assertEquals(msgErrorExpected, exception.getMessage());
    }

    public static void thenShouldThrowServiceException(Runnable method) {
        thenShouldThrowException(ServiceException.class, method);
    }
    public static void thenShouldThrowServiceException(Runnable method, String msgErrorExpected) {
        thenShouldThrowException(ServiceException.class, method, msgErrorExpected);
    }

    public static <P1> void thenShouldThrowServiceException(P1 param1, Consumer<P1> method) {
        thenShouldThrowException(ServiceException.class, param1, method);
    }

    public static <P1> void thenShouldThrowServiceException(P1 param1, Consumer<P1> method, String msgErrorExpected) {
        thenShouldThrowException(ServiceException.class, param1, method, msgErrorExpected);
    }

    public static <P1, P2> void thenShouldThrowServiceException(P1 param1, P2 param2, BiConsumer<P1, P2> method) {
        thenShouldThrowException(ServiceException.class, param1, param2, method);
    }

    public static <P1, P2> void thenShouldThrowServiceException(P1 param1, P2 param2, BiConsumer<P1, P2> method, String msgErrorExpected) {
        thenShouldThrowException(ServiceException.class, param1, param2, method, msgErrorExpected);
    }

}
