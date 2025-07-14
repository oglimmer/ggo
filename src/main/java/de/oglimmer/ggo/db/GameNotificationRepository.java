package de.oglimmer.ggo.db;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface GameNotificationRepository extends ListCrudRepository<GameNotification, Long> {

    List<GameNotification> findAllByConfirmedIsNotNull();

    void deleteByConfirmId(String confirmId);

    Optional<GameNotification> findAllByConfirmIdAndConfirmedIsNull(String confirmId);

    Optional<GameNotification>  findAllByConfirmId(String confirmId);
}