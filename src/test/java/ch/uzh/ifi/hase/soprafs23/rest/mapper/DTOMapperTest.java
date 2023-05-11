package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPostDTO;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
    @Test
    public void testCreateUser_fromUserPostDTO_toUser_success() {
        // create PlayerPostDTO
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPassword("password");
        playerPostDTO.setUsername("username");

        // MAP -> Create player
        Player player = DTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);

        // check content
        assertEquals(playerPostDTO.getPassword(), player.getPassword());
        assertEquals(playerPostDTO.getUsername(), player.getUsername());
    }

    @Test
    public void testGetUser_fromUser_toUserGetDTO_success() {
        // create Player
        Player player = new Player();
        player.setUsername("firstname@lastname");
        player.setStatus(PlayerStatus.ONLINE);
        player.setToken("1");
        player.setCreationDate(new Date());
        player.setBirthday(new Date());

        // MAP -> Create PlayerGetDTO
        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player);

        // check content
        assertEquals(player.getId(), playerGetDTO.getId());
        assertEquals(player.getUsername(), playerGetDTO.getUsername());
        assertEquals(player.getStatus(), playerGetDTO.getStatus());
        assertEquals(player.getBirthday(), playerGetDTO.getBirthday());
        assertEquals(player.getCreationDate(), playerGetDTO.getCreationDate());
    }
}
