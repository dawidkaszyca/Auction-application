package pl.dawid.kaszyca.security;


import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import pl.dawid.kaszyca.exception.LoginFromTokenDoNotMatchToWebSocketChannelException;
import pl.dawid.kaszyca.security.jwt.TokenProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    TokenProvider tokenProvider;

    /**
     * @key - websocket session id
     * @value - login with url to send with simpMessagingTemplate
     * before put it's always check if key and value are unique
     */
    Map<String, String> userLogin;

    public WebSocketAuthenticationConfig() {
        this.userLogin = new HashMap<>();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand())) {
                    try {
                        List<String> authorization = accessor.getNativeHeader("X-Authorization");
                        log.info("X-Authorization: {}", authorization);
                        String accessToken = authorization.get(0).split(" ")[1];
                        Authentication authentication = tokenProvider.getAuthentication(accessToken);
                        accessor.setUser(authentication);
                        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                            String sessionId = (String) accessor.getHeader("simpSessionId");
                            addUser(sessionId, authentication.getName());
                        }
                        if (StompCommand.SEND.equals(accessor.getCommand())) {
                            String app = ((String) message.getHeaders().get("simpDestination")).split("/")[1];
                            String login = ((String) message.getHeaders().get("simpDestination")).split("/")[4];
                            if (!authentication.getName().equals(login) && app.equals("app")) {
                                throw new LoginFromTokenDoNotMatchToWebSocketChannelException();
                            }
                        }
                    } catch (JwtException | IllegalArgumentException e) {
                        log.info("Invalid JWT token.");
                        log.trace("Invalid JWT token trace.", e);
                    }
                }
                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String sessionId = (String) accessor.getHeader("simpSessionId");
                    String login = ((String) message.getHeaders().get("simpDestination")).split("/")[3];
                    if (!checkUserBySessionIdAndLogin(sessionId, login)) {
                        throw new LoginFromTokenDoNotMatchToWebSocketChannelException();
                    }
                }
                if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                    String sessionId = (String) accessor.getHeader("simpSessionId");
                    removeUserBySessionId(sessionId);
                }
                return message;
            }
        });
    }

    public void addUser(String sessionId, String login) {
        if (!userLogin.containsKey(sessionId) && !userLogin.containsValue(login))
            userLogin.put(sessionId, login);
    }

    public void removeUserBySessionId(String sessionId) {
        userLogin.remove(sessionId, userLogin.get(sessionId));
    }

    public boolean checkUserBySessionIdAndLogin(String sessionId, String login) {
        return userLogin.get(sessionId).equals(login);
    }
}
