package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;

import java.util.HashMap;
import java.util.Map;


public class LobbyRepository {
    private Map<Integer, Lobby> lobbyRepositoryByID = new HashMap<>();
    private Map<Integer, Lobby> lobbyRepositoryByAccessCode = new HashMap<>();
    private static LobbyRepository INSTANCE = new LobbyRepository();

    private LobbyRepository(){ }

    public static void addLobby(Lobby lobby){
        INSTANCE.lobbyRepositoryByID.put(lobby.getId(), lobby);
        INSTANCE.lobbyRepositoryByAccessCode.put(lobby.getAccessCode(), lobby);
    }
    private void addLobby_internal(Lobby lobby){
        lobbyRepositoryByID.put(lobby.getId(), lobby);
        lobbyRepositoryByAccessCode.put(lobby.getAccessCode(), lobby);
    }
    public static Lobby getLobbyById(int lobbyId){
        return INSTANCE.lobbyRepositoryByID.get(lobbyId);
    }
    private Lobby getLobbyById_internal(int lobbyId){
        return lobbyRepositoryByID.get(lobbyId);
    }
    public static Lobby getLobbyByAccessCode(int accessCode){
        return INSTANCE.lobbyRepositoryByAccessCode.get(accessCode);
    }
    public Lobby getLobbyByAccessCode_internal(int accessCode){
        return lobbyRepositoryByAccessCode.get(accessCode);
    }
    public static void deleteLobby(int lobbyId){
        Lobby l = INSTANCE.lobbyRepositoryByID.get(lobbyId);
        if (l == null) return;
        INSTANCE.lobbyRepositoryByID.remove(lobbyId);
        INSTANCE.lobbyRepositoryByAccessCode.remove(l.getAccessCode());
    }
    private void deleteLobby_internal(int lobbyId){
        Lobby l = lobbyRepositoryByID.get(lobbyId);
        if (l == null) return;
        lobbyRepositoryByID.remove(lobbyId);
        lobbyRepositoryByAccessCode.remove(l.getAccessCode());
    }
}
