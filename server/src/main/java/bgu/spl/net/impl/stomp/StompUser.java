package bgu.spl.net.impl.stomp;
import java.util.HashMap;

import bgu.spl.net.srv.ConnectionHandler;

public class StompUser<T> {

    protected Integer uniqueID;
    protected String password;
    protected String userName;
    protected boolean connect = false;
    public StompUser(String _userName, String _password){
        userName = _userName;
        password = _password;
    }

    public void connected(){ 
        connect = true;
    }
    public void disconnect(){
        connect = false;
    }
    public boolean getConnect(){
        return connect;
    } 
    public void uniqueIDReset(){
        uniqueID = null;
    }
    
}
