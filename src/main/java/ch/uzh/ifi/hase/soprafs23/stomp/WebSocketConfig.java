package ch.uzh.ifi.hase.soprafs23.stomp;

import ch.uzh.ifi.hase.soprafs23.service.GameService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
       //registry.addEndpoint("/ws").withSockJS();
       registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS()
               .setHeartbeatTime(10000);;
    }

    @Bean
    public GameService gameService() {
        return new GameService();
    }
}
