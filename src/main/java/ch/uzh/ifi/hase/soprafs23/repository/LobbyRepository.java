package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;

import java.util.HashMap;
import java.util.Map;


public class LobbyRepository {
    private static Map<Integer, Lobby> lobbyRepositoryByID = new HashMap<>();
    private static Map<Integer, Lobby> lobbyRepositoryByAccessCode = new HashMap<>();

    private LobbyRepository(){ }

    public static void addLobby(Lobby lobby){
        lobbyRepositoryByID.put(lobby.getId(), lobby);
        lobbyRepositoryByAccessCode.put(lobby.getAccessCode(), lobby);
    }
    public static Lobby getLobbyById(int lobbyId){
        return lobbyRepositoryByID.get(lobbyId);
    }
    public static Lobby getLobbyByAccessCode(int accessCode){
        return lobbyRepositoryByAccessCode.get(accessCode);
    }
    public static void deleteLobby(int lobbyId){
        Lobby l = lobbyRepositoryByID.get(lobbyId);
        lobbyRepositoryByID.remove(lobbyId);
        lobbyRepositoryByAccessCode.remove(l.getAccessCode());
    }



}
