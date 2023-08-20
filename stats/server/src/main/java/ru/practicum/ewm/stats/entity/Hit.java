package ru.practicum.ewm.stats.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@Table(name = "hits", schema = "public")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @Column(name = "application", nullable = false)
    private String app;

    @Column(name = "uri", nullable = false)
    private String uri;

    @Column(name = "remote_ip", nullable = false)
    private String ip;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;
}