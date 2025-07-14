package de.oglimmer.ggo.db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class GameNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private Instant createdOn;
    @Column
    private Instant confirmed;
    @Column(nullable = false)
    private String confirmId;

}