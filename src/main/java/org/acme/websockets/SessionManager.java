package org.acme.websockets;

import javax.inject.Singleton;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class SessionManager {
    private Map<String, Map<String,Session>> dungeonPlayerMap = new HashMap<>();
    private Map<String,Map<String,Map<String,Session>>> dungeonRoomPlayerMap = new HashMap<>();

    public void joinChat(Session session, String dungeonName, String playerName){
        if(dungeonPlayerMap.get(dungeonName) == null){
            Map<String,Session> playerMap = new HashMap<>();
            playerMap.put(playerName,session);
            dungeonPlayerMap.put(dungeonName,playerMap);
        } else {
            dungeonPlayerMap.get(dungeonName).put(playerName,session);
        }
    }

    public void changeRoom(String dungeonName, String oldRoom, String newRoom, String playerName){
        Session session = dungeonPlayerMap.get(dungeonName).get(playerName);
        if(oldRoom != null){
            dungeonRoomPlayerMap.get(dungeonName).get(oldRoom).remove(playerName);
        }
        if(dungeonRoomPlayerMap.get(dungeonName).get(newRoom) != null){
            dungeonRoomPlayerMap.get(dungeonName).get(newRoom).put(playerName,session);
        }else{
            Map<String,Map<String,Session>> map = new HashMap<>();
            Map<String,Session> sessionMap = new HashMap<>();
            sessionMap.put(playerName,session);
            map.put(newRoom,sessionMap);
        }
    }

    public void setRoomFirstTime(String playerName, String dungeonName, String roomName){
        Session session = dungeonPlayerMap.get(dungeonName).get(playerName);
        if(dungeonRoomPlayerMap.get(dungeonName) == null){
            Map<String,Session> playerMap = new HashMap<>();
            playerMap.put(playerName,session);
            Map<String,Map<String,Session>> roomMap = new HashMap<>();
            roomMap.put(roomName,playerMap);
            dungeonRoomPlayerMap.put(dungeonName,roomMap);
        }else if(dungeonRoomPlayerMap.get(dungeonName).get(roomName) == null){
            Map<String,Session> playerMap = new ConcurrentHashMap<>();
            playerMap.put(playerName,session);
            dungeonRoomPlayerMap.get(dungeonName).put(roomName,playerMap);
        } else if(!dungeonRoomPlayerMap.get(dungeonName).get(roomName).containsKey(playerName)){
            dungeonRoomPlayerMap.get(dungeonName).get(roomName).put(playerName,session);
        }
    }

    public void remove(String dungeonName, String playerName){
        dungeonPlayerMap.get(dungeonName).remove(playerName);
        if(dungeonRoomPlayerMap.get(dungeonName) != null) {
            dungeonRoomPlayerMap.get(dungeonName).values().forEach(map -> {
                if (map.containsKey(playerName)) {
                    map.remove(playerName);
                }
            });
        }
    }

    public Map<String, Map<String,Session>> getDungeonPlayerMap(){
        return dungeonPlayerMap;
    }

    public Map<String,Map<String,Map<String,Session>>> getDungeonRoomPlayerMap(){
        return dungeonRoomPlayerMap;
    }
}
