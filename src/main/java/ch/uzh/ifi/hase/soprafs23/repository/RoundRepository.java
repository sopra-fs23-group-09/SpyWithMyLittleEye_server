package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("roundRepository")
public interface RoundRepository extends JpaRepository<Round, Long>{
    //probably need more methods for findBy
}
