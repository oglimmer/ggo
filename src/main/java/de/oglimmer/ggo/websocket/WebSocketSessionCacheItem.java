package de.oglimmer.ggo.websocket;

import java.lang.ref.WeakReference;

import de.oglimmer.ggo.logic.Player;
import org.springframework.web.socket.WebSocketSession;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketSessionCacheItem {
    private String sessionId;
    private WeakReference<WebSocketSession> session;
    private Player player;
    private boolean disconnected;

    public WebSocketSessionCacheItem(String sessionId, WeakReference<WebSocketSession> session) {
        this.sessionId = sessionId;
        this.session = session;
        this.disconnected = false;
    }

    @Override
    public String toString() {
        return "WebSocketSessionCacheItem{" +
                "sessionId='" + sessionId + '\'' +
                ", player=" + (player != null ? player.getSide() : "null") +
                ", disconnected=" + disconnected +
                '}';
    }
}