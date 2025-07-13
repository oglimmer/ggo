package de.oglimmer.ggo.websocket;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.oglimmer.ggo.atmospheremvc.game.Player;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum WebSocketSessionCache {
    INSTANCE;

    private final List<WebSocketSessionCacheItem> items = Collections.synchronizedList(new ArrayList<>());
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<WebSocketSessionCacheItem> getItems() {
        return new ArrayList<>(items);
    }

    public void connect(WebSocketSession session) {
        WebSocketSessionCacheItem item = getItem(session.getId());
        if (item == null) {
            item = new WebSocketSessionCacheItem(session.getId(), new WeakReference<>(session));
            items.add(item);
            log.debug("connect {}", item);
        } else if (item.getSession().get() != session) {
            item.setSession(new WeakReference<>(session));
            log.debug("re-connect {}", item);
        }
        item.setDisconnected(false);

        updateOtherPlayer(item);
        updateLastConnectionTime(item);
    }

    private void updateLastConnectionTime(WebSocketSessionCacheItem item) {
        Player actingPlayer = item.getPlayer();
        if (actingPlayer != null) {
            actingPlayer.setLastConnection(new Date());
        }
    }

    public void disconnect(String sessionId) {
        items.stream().filter(i -> i.getSessionId().equals(sessionId)).forEach(i -> i.setDisconnected(true));
        WebSocketSessionCacheItem item = getItem(sessionId);
        if (item != null) {
            updateOtherPlayer(item);
            updateLastConnectionTime(item);
        }
    }

    private void updateOtherPlayer(WebSocketSessionCacheItem item) {
        Player actingPlayer = item.getPlayer();
        if (actingPlayer != null) {
            Player remainingPlayer = actingPlayer.getGame().getOtherPlayer(actingPlayer);
            if (remainingPlayer != null) {
                remainingPlayer.updateUI();
            }
        }
    }

    private WebSocketSessionCacheItem getItem(String sessionId) {
        Optional<WebSocketSessionCacheItem> findFirst = items.stream()
                .filter(i -> i.getSessionId().equals(sessionId))
                .findFirst();
        if (findFirst.isPresent()) {
            log.debug("get item for sessionId {}", sessionId);
            return findFirst.get();
        }
        log.debug("NO Session for sessionId {}", sessionId);
        return null;
    }

    public boolean isConnected(Player player) {
        WebSocketSessionCacheItem item = getItem(player);
        if (item != null) {
            return !item.isDisconnected();
        }
        return false;
    }

    public void registerPlayer(Player player, String sessionId) {
        WebSocketSessionCacheItem disconnectedPlayerItem = getItem(player);
        if (disconnectedPlayerItem != null) {
            remove(disconnectedPlayerItem.getSessionId());
        }
        WebSocketSessionCacheItem connectedSessionItem = getItem(sessionId);
        if (connectedSessionItem != null && connectedSessionItem.getPlayer() != player) {
            connectedSessionItem.setPlayer(player);
            log.debug("set on {} player {}", sessionId, player.getSide());
        }
    }

    public void remove(String sessionId) {
        for (Iterator<WebSocketSessionCacheItem> it = items.iterator(); it.hasNext();) {
            WebSocketSessionCacheItem i = it.next();
            if (i.getSessionId().equals(sessionId)) {
                it.remove();
            }
        }
    }

    public WebSocketSession get(Player player) {
        WebSocketSessionCacheItem findFirst = getItem(player);
        if (findFirst != null) {
            WebSocketSession session = findFirst.getSession().get();
            if (session != null) {
                log.debug("get Session for player {} = {}", player.getSide(), session.getId());
                return session;
            }
        }
        log.debug("NO Session for player {}", player.getSide());
        return null;
    }

    public WebSocketSessionCacheItem getItem(Player player) {
        Optional<WebSocketSessionCacheItem> findFirst = items.stream()
                .filter(i -> i.getPlayer() == player)
                .findFirst();
        if (findFirst.isPresent()) {
            return findFirst.get();
        }
        return null;
    }

    public void sendMessage(Player player, JsonNode message) {
        WebSocketSession session = get(player);
        if (session != null && session.isOpen()) {
            try {
                String messageText = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(messageText));
                log.debug("Sent message to player {}: {}", player.getSide(), messageText);
            } catch (IOException e) {
                log.error("Failed to send message to player {}", player.getSide(), e);
            }
        } else {
            log.warn("Discard messages as player {} not connected", player.getSide());
        }
    }
}