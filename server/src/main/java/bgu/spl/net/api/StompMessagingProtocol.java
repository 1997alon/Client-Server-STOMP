package bgu.spl.net.api;

import bgu.spl.net.srv.Connections;

import java.net.Socket;
import java.util.HashMap;
public interface StompMessagingProtocol<T>  {


    /**
	 * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
	**/
    void start(int connectionId, Connections<T> connections);
    
    void process(T message, int connectionId);
	
	/**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();

    void addConnections(Connections<T> connections);
}
