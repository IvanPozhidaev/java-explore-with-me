package ru.practicum.ewm.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "requests", schema = "public")
public class RequestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @Column(name = "event_id", nullable = false)
    private Long event;
    @Column(name = "requester_id", nullable = false)
    private Long requester;
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "status")
    private RequestStatus status;
}