package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;

import java.util.HashMap;
import java.util.Map;

public class LobbyRepository {
    private Map<Integer, Lobby> lobbyRepositoryByID;
    private Map<Integer, Lobby> lobbyRepositoryByAccessCode;

    private static LobbyRepository INSTANCE = new LobbyRepository();

    private LobbyRepository() {
        lobbyRepositoryByID = new HashMap<>();
        lobbyRepositoryByAccessCode = new HashMap<>();
    }

    ////// External interface

    public static void reset() {
        INSTANCE = new LobbyRepository();
    }

    public static void addLobby(Lobby lobby) {
        INSTANCE.addLobby_internal(lobby);
    }

    public static Lobby getLobbyById(int lobbyId) {
        return INSTANCE.getLobbyById_internal(lobbyId);
    }

    public static Lobby getLobbyByAccessCode(int accessCode) {
        return INSTANCE.getLobbyByAccessCode_internal(accessCode);
    }

    public static void deleteLobby(int lobbyId) {
        INSTANCE.deleteLobby_internal(lobbyId);
    }

    ////// INTERNALS

    private void addLobby_internal(Lobby lobby) {
        lobbyRepositoryByID.put(lobby.getId(), lobby);
        lobbyRepositoryByAccessCode.put(lobby.getAccessCode(), lobby);
    }

    private Lobby getLobbyById_internal(int lobbyId) {
        return lobbyRepositoryByID.get(lobbyId);
    }

    public Lobby getLobbyByAccessCode_internal(int accessCode) {
        return lobbyRepositoryByAccessCode.get(accessCode);
    }

    private void deleteLobby_internal(int lobbyId) {
        Lobby l = lobbyRepositoryByID.get(lobbyId);
        if (l == null)
            return;
        lobbyRepositoryByID.remove(lobbyId);
        lobbyRepositoryByAccessCode.remove(l.getAccessCode());
    }
}
