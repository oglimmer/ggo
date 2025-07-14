package de.oglimmer.ggo.db;

import de.oglimmer.ggo.random.RandomString;
import de.oglimmer.ggo.util.GridGameOneProperties;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class GameNotificationsDao {

    private GameNotificationRepository gameNotificationRepository;
    private GridGameOneProperties properties;

    public List<GameNotification> allConfirmed() {
        if (properties.isEmailDisabled()) {
            return List.of();
        }
        return gameNotificationRepository.findAllByConfirmedIsNotNull();
    }

    public GameNotification addEmail(String email) {
        String confirmId = RandomString.getRandomStringHex(32);
        GameNotification gameNotification = new GameNotification();
        gameNotification.setEmail(email);
        gameNotification.setConfirmId(confirmId);
        return gameNotificationRepository.save(gameNotification);
    }

    public void unregisterEmail(String confirmId) {
        gameNotificationRepository.deleteByConfirmId(confirmId);
    }

    @Transactional
    public void confirmEmail(String confirmId) {
        Optional<GameNotification> allByConfirmIdAndConfirmedIsNull = gameNotificationRepository.findAllByConfirmIdAndConfirmedIsNull(confirmId);
        allByConfirmIdAndConfirmedIsNull.ifPresent(gameNotification -> gameNotification.setConfirmed(Instant.now()));
    }

    public GameNotification getByConfirmId(String confirmId) {
        Optional<GameNotification> allByConfirmId = gameNotificationRepository.findAllByConfirmId(confirmId);
        return allByConfirmId.get();
    }

    public Collection<GameNotification> all() {
        return gameNotificationRepository.findAll();
    }

}
