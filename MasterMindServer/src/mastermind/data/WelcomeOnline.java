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
public class WelcomeOnline implements java.io.Serializable {
    private OnlinePlayer me;

    public WelcomeOnline(OnlinePlayer me) {
        this.me = me;
    }

    public OnlinePlayer getMe() {
        return me;
    }

    public void setMe(OnlinePlayer me) {
        this.me = me;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WelcomeOnline other = (WelcomeOnline) obj;
        if (this.me != other.me && (this.me == null || !this.me.equals(other.me))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.me != null ? this.me.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "WelcomeOnline{" + "me=" + me + '}';
    }

    

}
