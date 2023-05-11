package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WebSocketService {

    private final Logger log = LoggerFactory.getLogger(WebSocketService.class);
    private final PlayerRepository playerRepository;
    private final PlayerService playerService;
    @Autowired
    protected SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketService(PlayerRepository playerRepository,
                            @Lazy PlayerService playerService) {
        this.playerRepository = playerRepository;
        this.playerService = playerService;
    }

    public void sendMessageToSubscribers(String mapping, Object o) {
        log.info("Sending {} to {}", o, mapping);
        this.simpMessagingTemplate.convertAndSend(mapping, o);
    }
}
