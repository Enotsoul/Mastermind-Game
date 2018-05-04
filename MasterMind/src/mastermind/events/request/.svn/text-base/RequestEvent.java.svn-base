/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mastermind.events.request;

import java.util.EventObject;

/**
 *
 * @author Bram
 */
public class RequestEvent extends EventObject {

    private String command;
    private Object object;

    public RequestEvent(Object source, String command)
    {
        super(source);
        this.command=command;
        object=null;
    }

    public RequestEvent(Object source, String command, Object object)
    {
        super(source);
        this.command=command;
        this.object = object;
    }

    public String getCommand() {
        return command;
    }

    public Object getObject() {
        return object;
    }
    
}
