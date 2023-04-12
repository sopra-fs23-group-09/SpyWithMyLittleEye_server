package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class WebSocketService {
    private final UserRepository userRepository;
    private final UserService userService;
    @Autowired
    protected SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketService(UserRepository userRepository,
                            @Lazy UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public void sendMessageToSubscribers(String mapping, Object o) {
        System.out.println("Inside sendMessage");
        Set<SimpUser> users = getUsers();
        System.out.println(users.size());
        this.simpMessagingTemplate.convertAndSend(mapping, o);
    }

    @Autowired private SimpUserRegistry simpUserRegistry;

    public Set<SimpUser> getUsers() {
        return simpUserRegistry.getUsers();
    }
}
