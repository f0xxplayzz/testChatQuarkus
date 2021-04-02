package org.acme.websockets;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/server/chat")
@ApplicationScoped
public class ServerChatSocket {

}
