package org.acme.websockets;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
@Singleton
public class ServerChatClient {

    final URI serverUri = new URI("/chat/all/server");

    Session session = ContainerProvider.getWebSocketContainer().connectToServer(this,serverUri);

    @Inject
    SessionManager sessionManager;

    public ServerChatClient() throws URISyntaxException, IOException, DeploymentException {
    }

    @OnOpen
    public void open(Session session){
    }

    @OnMessage
    void message(String msg){
    }

    public void sendMessage(String msg){
        session.getAsyncRemote().sendText(msg);
    }
}
