package ru.shubert.jobportal.model;

/**
 * Simple wrapper for any exception may occur then IEntity instantion fails.
 * @see ru.shubert.jobportal.service.IService#instantiate(Class)
 *
 */
public class  EntityInstantiationException extends Exception{
    static final long serialVersionUID = 1L;

    public EntityInstantiationException(String message) {
        super(message);
    }

    public EntityInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityInstantiationException(Throwable cause) {
        super(cause);
    }
}
