package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "creationDate", target = "creationDate")
    @Mapping(source = "birthday", target = "birthday")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "username", target = "username")
    User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "accessCode", target = "accessCode")
    @Mapping(source = "hostId", target = "hostId")
    @Mapping(source = "players", target = "playerNames")
    @Mapping(source = "amountRounds", target = "amountRounds")
    LobbyGetDTO convertLobbyToLobbyGetDTO(Lobby lobby);

    @Mapping(source = "id", target = "id")
    LobbyStartedGetDTO convertLobbyToLobbyStartedGetDTO(Lobby lobby);
}
