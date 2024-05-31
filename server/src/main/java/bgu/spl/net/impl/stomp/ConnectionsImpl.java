package bgu.spl.net.impl.stomp;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.NonBlockingConnectionHandler;

public class ConnectionsImpl<T> implements Connections<T> {
public static volatile int msgCounter = 0;
public HashMap<String, Vector<int[]>> topics = new HashMap<>(); // for each topic there is his queue of all his subscribes.
public HashMap<Integer, ConnectionHandler<T>> activeUsersIDToHandler = new HashMap<>(); // all the active users and their's handlers.
public HashMap<ConnectionHandler<T>, Integer> activeUsersHandlerToID = new HashMap<>(); // all the active users and their's handlers.
public HashMap<T, Integer> messageToIDHandler = new HashMap<>(); // map between msg to client for connect later between client and user.

protected List<String> users = new ArrayList<>(); // all the users.
public HashMap<StompUser, Integer> userToClientID = new HashMap<>(); // connect between user to clientID.
public HashMap<Integer, StompUser> clientIDtoUser = new HashMap<>(); // connect between clientID to user.
public HashMap<String, StompUser> connectBetweenUserToObjectUser = new HashMap<>();
public static volatile int counterUniqueID = 0;
public boolean closingChannel = false;
public ConnectionsImpl(){
}
@Override
public boolean send(int connectionId, T msg, T receipt) {
    // TODO Auto-generated method stub
    // we can assume that the message is string only and then...
    // here we get the full msg and we send it to the function send in handler for continue the process.
    ConnectionHandler<T> handler = activeUsersIDToHandler.get(connectionId);
    if(msg != null) handler.send(msg);
    if(receipt != null) handler.send(receipt);
    return false;
}

@Override
public void send(String channel, T msg, int connecstionId) {
    // TODO Auto-generated method stub
    // we assume that when the client sent a msg in the frame "send" the hashmap of the msg and the handlerID is not removed yet.
    Vector<int[]> subscribes = topics.get(channel);
    for(int[] v: subscribes){
            ConnectionHandler<T> handler = activeUsersIDToHandler.get(v[1]);
            String response = "MESSAGE\nsubscription:" +  v[0] + "\nmessage-id:" + (msgCounter) +
                        "\ndestination:/" + channel + "\n\n" + (String)msg + "\n" + '\u0000';
            handler.send((T)response);
    }
    msgCounter++;
    
}

@Override
public void disconnect(int connectionId) {
    // TODO Auto-generated method stub
    // disconnect becasue of bug or an error.
    ConnectionHandler<T> handler = activeUsersIDToHandler.get(connectionId);
    activeUsersHandlerToID.remove(handler);
    activeUsersIDToHandler.remove(connectionId);
    StompUser user;
    if(clientIDtoUser.containsKey(connectionId)){
        for(String topic: topics.keySet()){
            Vector<int[]> v = topics.get(topic);
            for(int[] index: v){
                if(index[1] == connectionId){
                    v.remove(index);
                    break;
                }
            }
        }
        user = clientIDtoUser.get(connectionId);
        user.disconnect();
        clientIDtoUser.remove(connectionId);
        userToClientID.remove(user);
        user.uniqueIDReset();
    }
    if(handler instanceof NonBlockingConnectionHandler){
        closingChannel = true;
        return;
    } 
    try {
        handler.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

}
public int addActiveUser(ConnectionHandler<T> c){ // add connections between client id to handler.
    activeUsersIDToHandler.put(counterUniqueID, c);
    activeUsersHandlerToID.put(c, counterUniqueID++);
    return (counterUniqueID - 1);
}

public void removeActiveUserById(int id){
    ConnectionHandler<T> c = activeUsersIDToHandler.get(id);
    activeUsersHandlerToID.remove(c);
    activeUsersIDToHandler.remove(id);
}

private void connectBetweenUserToClient(T message, StompUser user){
    int clientId = messageToIDHandler.get(message);
    userToClientID.put(user, clientId);
    clientIDtoUser.put(clientId, user);
}


public boolean CONNECT(T message, int connectionId){
    String msg = (String) message;
    String[] split = msg.split("login:", 2);
    String find = split[1];
    String userName = "";
    int j = 0;
    while(find.charAt(j) != '\n'){
        userName += find.charAt(j++);
    }
    String response = "";
    split = msg.split("passcode:", 2);
    find = split[1];
    String password = "";
    j =0;
    while(find.charAt(j) != '\n'){
        password = password + find.charAt(j++);
    }
    String version = "version:1.2";
    j = 0;
    split = msg.split("host:", 2);
    find = split[1];
    String host = "";
    while(find.charAt(j) != '\n'){
        host += find.charAt(j++);
    }
    if(!host.equals("stomp.cs.bgu.ac.il")){
        String res = "the host is not right";
        ERROR(connectionId, message, (T)response);
        return true;
    }
    T receipt = RECEIPT(message, connectionId);
    if(users.contains(userName)){
        StompUser u = connectBetweenUserToObjectUser.get(userName);
        if(u.password.equals(password)){
            if(userToClientID.containsValue(connectionId)){
                response = "CONNECTED\nthe client is already logged in, log out before trying again\n" + version + "\n\n" + '\u0000';
            }
            else if(u.connect){
                response = "User already logged in";
                ERROR(connectionId, message, (T)response);
                return true;
            }
            else{ // assume that the active connection does not happpen because everytime user log out, the connection is deleting.
                response = "CONNECTED\n" + version + "\n\n" + '\u0000';
                u.connected();
            }
        }
        else{
            response = "Wrong password";
            ERROR(connectionId, message, (T)response);
            return true;
        }
        u.uniqueID = connectionId;
    }
    else{
        StompUser member = new StompUser<>(userName, password);
        member.connected();
        response = "CONNECTED\n" + version + "\n\n" + '\u0000';
        // add him to all the hashmaps.
        connectBetweenUserToObjectUser.put(userName, member);
        users.add(userName); //  add the user Name to the list of users.
        member.uniqueID = connectionId;
    }
    
    // add the connection between user to clientID.
    connectBetweenUserToClient(message, connectBetweenUserToObjectUser.get(userName));
    send(connectionId, (T)response, (T)receipt);
    return false;
}

public T RECEIPT(T msg, int connectionId){
    // need to change in every frame that here it will find  a receipt
    String message = (String) msg;
    String response = "";
    if(message.contains("receipt:")){
        String[] split = message.split("receipt:", 2);
        String find = split[1];
        int j = 0;
        String receipt = "";
        while(find.charAt(j) != '\n'){
            receipt += find.charAt(j++);
        }
        response = "RECEIPT\nreceipt-id:" + receipt + "\n\n" + '\u0000';
        return (T)response;
    }
    return null;
}

public boolean SUBSCRIBE(T message, int connectionId){
    String msg = (String) message;
    String[] split = msg.split("destination:/", 2);
    String topic = "";
    int j = 0;
    String find = split[1];
    while(find.charAt(j) != '\n' && find.charAt(j) != '/'){
        topic = topic + find.charAt(j++);
    }
    // if the topic doesnt exist, we create one with this topic.
    if(!topics.containsKey(topic)){
        Vector<int[]> channel = new Vector<>();
        topics.put(topic, channel);
        // we add the user to this channel later.
    }
    split = msg.split("id:", 2);
    find = split[1];
    j = 0;
    String uniqueID = "";
    while(find.charAt(j) != '\n'){
        uniqueID = uniqueID + find.charAt(j++);
    }
    int subscriptionID = Integer.parseInt(uniqueID); // id to save for subsrcibe.
    int[] arr = {subscriptionID, connectionId}; // the left one is the unique number for subscription and the right one is the clientId.
    // if user wants to subscribe to a channel but he is already subscribed.
    if(topics.get(topic).contains(arr)){
        ERROR(connectionId, message, (T)("you are already subscribed to this channel."));
    }
    Vector<int[]> channel = topics.get(topic);
    channel.add(arr); // we add to the channel the subscribe.
    T receipt = RECEIPT((T)msg, connectionId);
    send(connectionId, null, (T)receipt);
    return false;
}

public boolean UNSUBSCRIBE(T message, int connectionId){
    String msg = (String) message;
    String[] split = msg.split("id:", 2);
    String find = split[1];
    int j = 0;
    String uniqueId = "";
    while(find.charAt(j) != '\n'){
        uniqueId = uniqueId + find.charAt(j++);
    }
    int sunscriptionID = Integer.parseInt(uniqueId); // receipt to send back.
    // we search which channel the user wants to unsubscribe.
    for(Vector<int[]> v : topics.values()){
        for(int[] i: v){
            if(i[0] == sunscriptionID && i[1] == connectionId){
                T receipt = RECEIPT(message, connectionId);
                v.remove(i);
                send(connectionId, null, receipt);
                return false;
            }
        }
    }
    String response = "you sent an unrecognized id";
    ERROR(connectionId, message, (T)response);
    return true;
}

public void addMsg(T nextMessage, Integer id) {
    messageToIDHandler.put(nextMessage, id);
}

public boolean SEND(T message, int connectionId){
    // need to check if the client subsrcibe to the channel he wants to send a msg.
    // we remove the connection between the msg to the client id in the function "send" later.
    String msg = (String) message;
    String destination = "";
    String[] split = msg.split("destination:/", 2);
    String find = split[1];
    int j = 0;
    String topic = "";
    while(find.charAt(j) != '\n'){
        topic += find.charAt(j++);
    }
    if(!topics.containsKey(topic)){
        ERROR(connectionId, message, (T)("There is no such topic"));
        return true;
    }
    Vector<int[]> vector = topics.get(topic);
    for(int[] index: vector){ // check that the sender is subscribe to the channel.
        if(index[1] == connectionId){
            send(topic, (T)message, connectionId);
            T receipt = RECEIPT(message, connectionId);
            send(connectionId, null, receipt);
            return false;
        }
    }
    ERROR(connectionId, message, (T)("you are not subscribed to this channel"));
    return true;
}

public void DISCONNECT(T message, int connectionId){
    String msg = (String) message;
    T receipt = RECEIPT(message, connectionId);
    send(connectionId, null, receipt);
    ConnectionHandler<T> handler = activeUsersIDToHandler.get(connectionId);
    StompUser user = clientIDtoUser.get(connectionId);
    // remove the user from all the channels.
    for(String topic: topics.keySet()){
        Vector<int[]> v = topics.get(topic);
        for(int[] index: v){
            if(index[1] == connectionId){
                v.remove(index);
                break;
            }
        }
    }
    // remove all the connections from the maps.
    try {
        user.disconnect();
        clientIDtoUser.remove(connectionId);
        userToClientID.remove(user);
        activeUsersHandlerToID.remove(handler);
        activeUsersIDToHandler.remove(connectionId);
        user.uniqueIDReset();
    } catch (Exception e) {
    }
    
    if(handler instanceof NonBlockingConnectionHandler){
        closingChannel = true;
        return;
    } 
    try {
        handler.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void ERROR(int connectionId, T msg, T response){
    String res = (String)response;
    String message = (String)msg;
    // check about the receipt!!!
    String error = "";
    T  receipt = RECEIPT(msg, connectionId);
    if(receipt != null){
        error = "ERROR\nreceipt:" + receipt + "message:" + msgCounter + "\nthe message:\n"
                + message + "\n-----\n" + res + "\n\n" + '\u0000';
    }
    else{
        error = "ERROR\nmessage:" + msgCounter + "\nthe message:\n" + message + "\n-----\n" + res
                + "\n\n" + '\u0000';
    }
    msgCounter++;
    send(connectionId, (T)error, receipt);
    disconnect(connectionId);
}

}
