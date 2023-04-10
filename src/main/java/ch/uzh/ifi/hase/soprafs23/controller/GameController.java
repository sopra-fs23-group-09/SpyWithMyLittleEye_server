package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.spilpi.GuessIn;
import ch.uzh.ifi.hase.soprafs23.spilpi.GuessOut;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GameController {

    private final UserService userService;

    GameController(UserService userService){
        this.userService = userService;
    }

    @MessageMapping("/guess")
    @SendTo("/game/guesses")
    public GuessOut handleGuess(GuessIn guessIn) throws Exception{
        //String username = "hallo";
        User user = userService.getUser(guessIn.getId());
        String username = user.getUsername();
        String guess = HtmlUtils.htmlEscape(guessIn.getGuess());
        return new GuessOut(username, guess);
    }
}
