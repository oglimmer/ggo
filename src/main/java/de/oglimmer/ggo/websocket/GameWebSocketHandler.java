package de.oglimmer.ggo.websocket;

import java.util.Date;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.oglimmer.ggo.websocket.com.CommandMessage;
import de.oglimmer.ggo.websocket.com.MessageQueue;
import de.oglimmer.ggo.logic.Games;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.debug("WebSocket connection established: {}", session.getId());
        WebSocketSessionCache.INSTANCE.connect(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.debug("Received message: {}", message.getPayload());
        
        try {
            CommandMessage commandMessage = objectMapper.readValue(message.getPayload(), CommandMessage.class);
            log.debug("Parsed command: {}", commandMessage);
            
            Game game = Games.<Game>getGames().getGameByPlayerId(commandMessage.getPid());
            if (game == null) {
                // @TODO: send 'game not exists'
                log.warn("Game not found for player ID: {}", commandMessage.getPid());
                return;
            }
            
            Player player = game.getPlayerById(commandMessage.getPid());
            if (player == null) {
                log.warn("Player not found with ID: {}", commandMessage.getPid());
                return;
            }
            
            player.setLastAction(new Date());
            player.setLastConnection(new Date());
            
            if ("join".equals(commandMessage.getCmd())) {
                WebSocketSessionCache.INSTANCE.registerPlayer(player, session.getId());
            }
            
            game.getCurrentPhase().execCmd(player, commandMessage.getCmd(), commandMessage.getParam());
            game.getCurrentPhase().updateMessages();
            game.getCurrentPhase().updateModalDialgs();
            
            MessageQueue messages = new MessageQueue(game);
            messages.process();
            
        } catch (Exception e) {
            log.error("Error processing message: {}", message.getPayload(), e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.debug("WebSocket connection closed: {} with status: {}", session.getId(), status);
        WebSocketSessionCache.INSTANCE.disconnect(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket transport error for session: {}", session.getId(), exception);
        WebSocketSessionCache.INSTANCE.disconnect(session.getId());
    }
}