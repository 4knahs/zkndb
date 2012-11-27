/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zkndb.exceptions;

/**
 *
 * @author arinto
 */
public class BadAclFormatException extends Exception{
    public BadAclFormatException(){super();}
    public BadAclFormatException(String message){super(message);}
    public BadAclFormatException(String message,Throwable cause){super(message,cause);}
    public BadAclFormatException(Throwable cause){super(cause);}
}
