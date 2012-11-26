package zkndb.exceptions;

/**
 *
 * @author arinto
 */
public class InvalidDbEntryException extends Exception{
    public InvalidDbEntryException(){super();}
    public InvalidDbEntryException(String message){super(message);}
    public InvalidDbEntryException(String message,Throwable cause){super(message,cause);}
    public InvalidDbEntryException(Throwable cause){super(cause);}
}
