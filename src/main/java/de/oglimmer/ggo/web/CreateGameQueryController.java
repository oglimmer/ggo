package de.oglimmer.ggo.web;

import de.oglimmer.ggo.db.GameNotification;
import de.oglimmer.ggo.db.GameNotificationsDao;
import de.oglimmer.ggo.email.EmailService;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Games;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@AllArgsConstructor
@Controller
public class CreateGameQueryController extends BaseController {

    private GameNotificationsDao gameNotificationsDao;
    private EmailService emailService;

    @GetMapping("/CreateGameQuery")
    public ResponseEntity<Result> show(HttpServletResponse response) {
        Game game = Games.<Game>getGames().createGame();
        Player player = game.createPlayer();
        response.addCookie(new Cookie("playerId", player.getId()));
        List<GameNotification> gameNotifications = gameNotificationsDao.allConfirmed();
        gameNotifications.forEach(rec -> emailService.notifyGameCreatedRealtime(rec.getEmail(), rec.getConfirmId()));
        int numberOfNotifications = gameNotifications.size();
        return ResponseEntity.ok(new Result(game.getId(), player.getId(), numberOfNotifications));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private String gameId;
        private String playerId;
        private int numberOfNotifications;
    }

}