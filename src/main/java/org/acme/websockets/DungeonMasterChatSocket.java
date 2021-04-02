package org.acme.websockets;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat/dungeonMaster/{dungeonName}/{userName}")
@ApplicationScoped
public class DungeonMasterChatSocket {
    @Inject
    SessionManager sessionManager;


    @OnOpen
    public void onOpen(Session session, @PathParam("dungeonName") String dungeonName, @PathParam("username") String username) {
        sessionManager.joinChat(session, dungeonName, username);
    }

    @OnClose
    public void onClose(Session session, @PathParam("dungeonName") String dungeonName, @PathParam("username") String username) {
        sessionManager.remove(dungeonName,username);
        broadcast("User " + username + " left", dungeonName);
    }

    @OnError
    public void onError(Session session, @PathParam("dungeonName") String dungeonName, @PathParam("username") String username, Throwable throwable) {
        sessionManager.remove(dungeonName, username);
        broadcast("User " + username + " left on error: " + throwable, dungeonName);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("dungeonName") String dungeonName, @PathParam("username") String username) {
        if (message.equalsIgnoreCase("_ready_")) {
            broadcast("User " + username + " joined", dungeonName);
        } else {
            broadcast(">> " + username + ": " + message, dungeonName);
        }
    }

    private void broadcast(String message, String dungeonName) {
        sessionManager.getDungeonPlayerMap().get(dungeonName).values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }

    private void whisperAsMaster(String message, String dungeonName, String playerName) {
        sessionManager.getDungeonPlayerMap().get(dungeonName).get(playerName).getAsyncRemote().sendObject(message, result -> {
            if (result.getException() != null) {
                System.out.println("Unable to send message: " + result.getException());
            }
        });
    }

    public void whisperAsPlayer(String message, String dungeonName, String fromPlayer, String toPlayer) {
        sessionManager.getDungeonRoomPlayerMap().get(dungeonName).values().forEach(map -> {
            if (map.containsKey(fromPlayer) && map.containsKey(toPlayer)) {
                map.get(toPlayer).getAsyncRemote().sendObject(message, result -> {
                    if (result.getException() != null) {
                        System.out.println("Unable to send message: " + result.getException());
                    }
                });
            }
        });
    }

    public void roomBroadcastAsMaster(String message, String dungeonName, String roomName) {
        sessionManager.getDungeonRoomPlayerMap().get(dungeonName).get(roomName).values().forEach((session) -> {
            session.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }

    public void roomBroadcastAsPlayer(String message, String dungeonName, String playerName){
        sessionManager.getDungeonRoomPlayerMap().get(dungeonName).values().forEach(map -> {
            if(map.containsKey(playerName)){
                map.values().forEach(session -> {
                    session.getAsyncRemote().sendObject(message, result -> {
                        if (result.getException() != null) {
                            System.out.println("Unable to send message: " + result.getException());
                        }
                    });
                });
            }
        });
    }
}