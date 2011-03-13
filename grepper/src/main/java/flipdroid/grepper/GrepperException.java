package flipdroid.grepper;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/13/11
 * Time: 1:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class GrepperException extends Exception {
    public GrepperException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public GrepperException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public GrepperException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public GrepperException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
