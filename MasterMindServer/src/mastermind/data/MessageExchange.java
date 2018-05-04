/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mastermind.data;

import java.io.Serializable;

/**
 *
 * @author lostone
 */
public class MessageExchange implements Serializable {
    public static enum MessageType { ERROR, SUCCESS, INFO, WARNING }
    /*Action : where it occurs..
     * MessageType  : self explaining
     * Message
    */
    private String action,messageType,message;

    public MessageExchange(String action, String messageType, String message) {
        this.action = action;
        this.messageType = messageType;
        this.message = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
}
