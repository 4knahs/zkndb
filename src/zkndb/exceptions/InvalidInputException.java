/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zkndb.exceptions;

/**
 *
 * @author 4knahs
 */
public class InvalidInputException extends Exception{
    public InvalidInputException(){super();}
    public InvalidInputException(String message){super(message);}
    public InvalidInputException(String message,Throwable cause){super(message,cause);}
    public InvalidInputException(Throwable cause){super(cause);}
}
