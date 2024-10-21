package service;

/**
 * Indicates there was an error connecting to a service
 */
public class ServiceException extends Exception {
    final private int statusCode;

    public ServiceException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int StatusCode() {
        return statusCode;
    }
}
