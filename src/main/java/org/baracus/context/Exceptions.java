package org.baracus.context;

public class Exceptions {
    /**
     * Bean instanciation fucked up. Thrown when the instantiation of
     * a bean caised an errer
     */
    final static class IntantiationException extends RuntimeException {
        IntantiationException(Throwable reason) {
            super(reason);
        }
    }


    /**
     * Registration of a bean failed.
     */
    final static class RegistrationException extends RuntimeException {
        RegistrationException(Throwable reason) {
            super(reason);
        }
    }

    final static class IncompatibleTypesException extends RuntimeException {
        IncompatibleTypesException(String message) {
            super(message);
        }
    }

    /**
     * Injection of a bean caused an error
     */
    final static class InjectionException extends RuntimeException {
        InjectionException(Throwable reason) {
            super(reason);
        }

        InjectionException(String msg, Throwable reason) {
            super(msg, reason);
        }
    }
}