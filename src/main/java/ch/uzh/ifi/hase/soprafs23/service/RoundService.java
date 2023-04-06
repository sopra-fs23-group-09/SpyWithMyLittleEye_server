package ch.uzh.ifi.hase.soprafs23.service;


import ch.uzh.ifi.hase.soprafs23.entity.Round;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoundService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final RoundRepository roundRepository;

    @Autowired
    public RoundService(@Qualifier("roundRepository") RoundRepository roundRepository) {
        this.roundRepository = roundRepository;
    }

    public void createRound(List<User> users){
        //TODO
    }
    public boolean checkKeyword(String keyword){
        //TODO
        return false;
    }

    public void setKeywordAndColor(Round round, String keyword, String color){
        round.setKeyword(keyword);
        round.setColor(color);
    }

    //What should this method do? award point? because in this case, why do we pass a boolean
    public void awardPoints(User user, String guess, boolean correct){
        //TODO
    }
    public void deleteRound(){
        //TODO
    }
}
