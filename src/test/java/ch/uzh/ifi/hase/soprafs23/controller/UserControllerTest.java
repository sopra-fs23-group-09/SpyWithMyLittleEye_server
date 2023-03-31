package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.ONLINE);
        user.setToken("token");
        user.setCreationDate(new Date(0L));
        user.setBirthday(new Date(0L));

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        String creationD = dFormat.format(user.getCreationDate());
        String birthd = dFormat.format(user.getBirthday());
        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())))
                .andExpect(jsonPath("$[0].creationDate", is(creationD)))
                .andExpect(jsonPath("$[0].birthday", is(birthd)))
                .andExpect(jsonPath("$[0].id", is(user.getId().intValue())));
    }

    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(new Date(0L));

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("password");
        userPostDTO.setUsername("testUsername");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        String creationD = dFormat.format(user.getCreationDate());

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())))
                .andExpect(jsonPath("$.creationDate", is(creationD)))
                .andExpect(jsonPath("$.birthday").value(IsNull.nullValue()));
    }

    @Test
    public void createUser_duplicateUsername_conflict() throws Exception{
        String errorMessage = "Reason";
        ResponseStatusException e = new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        given(userService.createUser(Mockito.any())).willThrow(e);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("password");
        userPostDTO.setUsername("testUsername");

        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString((userPostDTO)));

        mockMvc.perform(postRequest)
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(errorMessage)));
    }

    @Test
    public void editUser_success() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setBirthday(new Date(0L));
        userPutDTO.setUsername("newUsername");

        MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isNoContent());
    }

    @Test
    public void editUser_notFound() throws Exception{
        String errorMessage = "Reason";
        ResponseStatusException e = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        doThrow(e).when(userService).updateUser(Mockito.any(), Mockito.anyString(), Mockito.anyLong());

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setBirthday(new Date(0L));
        userPutDTO.setUsername("newUsername");

        MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(errorMessage)));
    }

    @Test
    public void getUser_withId_success() throws Exception{
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(new Date(0L));

        List<User> allUsers = Collections.singletonList(user);
        given(userService.getUsers()).willReturn(allUsers);

        MockHttpServletRequestBuilder getRequest = get("/users/"+user.getId());

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        String creationD = dFormat.format(user.getCreationDate());
        System.out.println(creationD);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())))
                .andExpect(jsonPath("$.creationDate", is(creationD)))
                .andExpect(jsonPath("$.birthday").value(IsNull.nullValue()));
    }
    @Test
    public void getUser_withId_notFound() throws Exception{
        given(userService.getUsers()).willReturn(Collections.emptyList());

        MockHttpServletRequestBuilder getRequest = get("/users/1");

        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException));
    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}