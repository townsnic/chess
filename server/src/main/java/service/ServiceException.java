package service;

/**
 * Indicates there was an error connecting to a service
 */
public class ServiceException extends Exception {
    public ServiceException(String message) { super(message); }
}
