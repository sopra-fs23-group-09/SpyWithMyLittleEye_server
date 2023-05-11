package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "profilePicture", target = "profilePicture")
    Player convertPlayerPostDTOtoEntity(PlayerPostDTO playerPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "creationDate", target = "creationDate")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "profilePicture", target = "profilePicture")
    PlayerGetDTO convertEntityToPlayerGetDTO(Player player);

    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "profilePicture", target = "profilePicture")
    Player convertPlayerPutDTOtoEntity(PlayerPutDTO playerPutDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "accessCode", target = "accessCode")
    @Mapping(source = "hostId", target = "hostId")
    @Mapping(source = "players", target = "playerNames")
    @Mapping(source = "amountRounds", target = "amountRounds")
    LobbyGetDTO convertLobbyToLobbyGetDTO(Lobby lobby);

    @Mapping(source = "id", target = "id")
    GameStartedGetDTO convertGameToGameStartedGetDTO(Game game);

    @Mapping(source = "keyword", target = "keyword")
    @Mapping(source = "roundOverStatus", target = "roundOverStatus")
    @Mapping(source = "playerPoints", target = "playerPoints")
    @Mapping(source = "currentRoundNr", target = "currentRoundNr")
    @Mapping(source = "hostId", target = "hostId")
    RoundGetDTO convertGameToRoundGetDTO(Game game);
}
