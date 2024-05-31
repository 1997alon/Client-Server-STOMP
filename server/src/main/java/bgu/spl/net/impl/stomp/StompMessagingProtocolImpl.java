package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.Connections;

import java.net.Socket;
import java.util.HashMap;
public class StompMessagingProtocolImpl<T> implements StompMessagingProtocol<T> {
    Connections<T> connections;
    Integer connectionID = null;
    boolean shouldTerminate = false;
    Socket socket;
    /**
     *
     */
    public StompMessagingProtocolImpl(){

    }
    
    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.connections = connections;
        this.connectionID = connectionId;
        this.socket = socket;
        // now we have the client ID for knowing to who to send.
    }

    @Override
    public void process(T message, int connectionId) { // maybe we should combine the methods "start" and "process" to only "process" with the arguments: "message, connections and id".
            String msg = (String) message;
            String[] split = msg.split("\n", 2);
            String frame = split[0];
            if(frame.equals("CONNECT")){
                shouldTerminate = ((ConnectionsImpl<T>)connections).CONNECT(message, connectionId);
                return;
            }
            if(!((ConnectionsImpl<T>)connections).clientIDtoUser.containsKey(connectionId)){
                ((ConnectionsImpl<T>)connections).ERROR(connectionId, message, (T)("you are not connected!!"));
                return;
            }
            StompUser user = ((ConnectionsImpl<T>)connections).clientIDtoUser.get(connectionId);
            if(user == null || !user.getConnect()) return; // the client tries to subscribe when he is not connecting.
            else if(frame.equals("SUBSCRIBE")){
                shouldTerminate = ((ConnectionsImpl<T>)connections).SUBSCRIBE(message, connectionId);
            }
            else if(frame.equals("UNSUBSCRIBE")){
                shouldTerminate = ((ConnectionsImpl<T>)connections).UNSUBSCRIBE(message, connectionId);
            }
            else if(frame.equals("DISCONNECT")){
                ((ConnectionsImpl<T>)connections).DISCONNECT(message, connectionId);
                shouldTerminate = true;
            }
            else if(frame.equals("SEND")){
                shouldTerminate = ((ConnectionsImpl<T>)connections).SEND(message, connectionId);
            }
        
    }

    @Override
    public boolean shouldTerminate() {
        // TODO Auto-generated method stub
        return shouldTerminate;
    }
    
    public void addConnections(Connections<T> con){
        connections = con;
    }

    
}
