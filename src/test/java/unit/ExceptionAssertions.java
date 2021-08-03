package unit;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class ExceptionAssertions {

    public static <E extends Throwable> void thenShouldThrowServiceException(Class<E> exceptionClass, Runnable method, String msgErrorExpected) {
        E exception = assertThrows(exceptionClass, method::run);
        assertEquals(msgErrorExpected, exception.getMessage());
    }

    public static <E extends Throwable, P1> void thenShouldThrowServiceException(Class<E> exceptionClass, P1 param1, Consumer<P1> method, String msgErrorExpected) {
        E exception = assertThrows(exceptionClass, () -> method.accept(param1));
        assertEquals(msgErrorExpected, exception.getMessage());
    }

    public static <E extends Throwable, P1, P2> void thenShouldThrowServiceException(Class<E> exceptionClass, P1 param1, P2 param2, BiConsumer<P1, P2> method, String msgErrorExpected) {
        E exception = assertThrows(exceptionClass, () -> method.accept(param1, param2));
        assertEquals(msgErrorExpected, exception.getMessage());
    }

}
