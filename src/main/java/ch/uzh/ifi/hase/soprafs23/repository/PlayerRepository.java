package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("playerRepository")
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByUsername(String username);
    Player findByToken(String token);
    List<Player> findTop15ByOrderByHighScoreDesc();
    List<Player> findTop15ByOrderByGamesWonDesc();
}