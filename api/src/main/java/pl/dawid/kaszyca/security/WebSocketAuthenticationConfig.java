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
import java.util.Objects;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    TokenProvider tokenProvider;

    private static final String SESSION_ID = "simpSessionId";
    private static final String DESTINATION = "simpDestination";

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
                StompHeaderAccessor accessor = getAccessor(message);
                switch (Objects.requireNonNull(accessor.getCommand())) {
                    case CONNECT:
                        checkConnectAuthorization(accessor);
                        break;
                    case DISCONNECT:
                        removeActiveUserFromList(accessor);
                        break;
                    case SUBSCRIBE:
                        checkSubscribeAuthorization(accessor, message);
                        break;
                    case SEND:
                        checkSendAuthorization(accessor, message);
                        break;
                    default:
                        return message;
                }
                return message;
            }
        });
    }

    private StompHeaderAccessor getAccessor(Message<?> message) {
        return MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    }

    private void checkConnectAuthorization(StompHeaderAccessor accessor) {
        try {
            Authentication authentication = setAuthenticationToAccessorFromTokenInHeader(accessor);
            String sessionId = (String) accessor.getHeader(SESSION_ID);
            addUser(sessionId, authentication.getName());
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace.", e);
        }
    }

    public void addUser(String sessionId, String login) {
        if (!userLogin.containsKey(sessionId) && !userLogin.containsValue(login))
            userLogin.put(sessionId, login);
    }

    private void removeActiveUserFromList(StompHeaderAccessor accessor) {
        String sessionId = (String) accessor.getHeader(SESSION_ID);
        removeUserBySessionId(sessionId);
    }

    public void removeUserBySessionId(String sessionId) {
        userLogin.remove(sessionId, userLogin.get(sessionId));
    }

    private void checkSubscribeAuthorization(StompHeaderAccessor accessor, Message<?> message) {
        String sessionId = (String) accessor.getHeader(SESSION_ID);
        String login = ((String) Objects.requireNonNull(message.getHeaders().get(DESTINATION))).split("/")[3];
        if (!checkUserBySessionIdAndLogin(sessionId, login)) {
            throw new LoginFromTokenDoNotMatchToWebSocketChannelException();

        }
    }

    public boolean checkUserBySessionIdAndLogin(String sessionId, String login) {
        return userLogin.get(sessionId).equals(login);
    }

    private void checkSendAuthorization(StompHeaderAccessor accessor, Message<?> message) {
        try {
            Authentication authentication = setAuthenticationToAccessorFromTokenInHeader(accessor);
            String app = ((String) Objects.requireNonNull(message.getHeaders().get(DESTINATION))).split("/")[1];
            String login = ((String) Objects.requireNonNull(message.getHeaders().get(DESTINATION))).split("/")[4];
            if (!authentication.getName().equals(login) && app.equals("app")) {
                throw new LoginFromTokenDoNotMatchToWebSocketChannelException();
            }
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace.", e);
        }
    }

    private Authentication setAuthenticationToAccessorFromTokenInHeader(StompHeaderAccessor accessor) {
        List<String> authorization = accessor.getNativeHeader("X-Authorization");
        log.info("X-Authorization: {}", authorization);
        String accessToken = Objects.requireNonNull(authorization).get(0).split(" ")[1];
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        accessor.setUser(authentication);
        return authentication;
    }
}
