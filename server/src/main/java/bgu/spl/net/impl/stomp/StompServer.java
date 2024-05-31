package bgu.spl.net.impl.stomp;

import java.util.function.Supplier;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.Server;

public class StompServer {

    public static void main(String[] args) {
        // TODO: implement this

        boolean tpc = false;
        if(args[1].equals("tpc")) tpc = true;
        int port = Integer.parseInt(args[0]);
        if(tpc){
            Server.threadPerClient(port, () -> new StompMessagingProtocolImpl<>(), StompMessageEncoderDecoder::new).serve();
        }
        else{
            Server.reactor(Runtime.getRuntime().availableProcessors(), 
                     port, //port
                     () -> new StompMessagingProtocolImpl<>(), //protocol factory
                     StompMessageEncoderDecoder::new //message encoder decoder factory
             ).serve();
                 }
                  // you can use any server... 
//     Server.reactor(3, 
   ////         7777, //port
    //        () -> new StompMessagingProtocolImpl<>(), //protocol factory
    /////        StompMessageEncoderDecoder::new //message encoder decoder factory
   // ).serve();

    // Server.reactor(
    //         Runtime.getRuntime().availableProcessors(),
    //         7777, //port
    //         () ->  new RemoteCommandInvocationProtocol<>(feed), //protocol factory
    //         ObjectEncoderDecoder::new //message encoder decoder factory
    // ).serve();
        
    }
}
