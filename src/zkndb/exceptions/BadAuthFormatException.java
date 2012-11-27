/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zkndb.exceptions;

/**
 *
 * @author arinto
 */
public class BadAuthFormatException extends Exception{
    public BadAuthFormatException(){super();}
    public BadAuthFormatException(String message){super(message);}
    public BadAuthFormatException(String message,Throwable cause){super(message,cause);}
    public BadAuthFormatException(Throwable cause){super(cause);}
}
