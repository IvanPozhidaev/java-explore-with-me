package ru.practicum.ewm.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "events", schema = "public")
public class EventModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "annotation", nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryModel category;
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private UserModel initiator;
    @ManyToOne/*(cascade = CascadeType.ALL)*/
    @JoinColumn(name = "location_id", nullable = false)
    private LocationModel location;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @OneToMany
    @JoinColumn(name = "event_id")
    private List<RequestModel> allRequests;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Column(name = "paid")
    private Boolean paid;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private EventState state;

    public long countConfirmedRequests() {
        long result = 0L;
        if (allRequests != null) {
            for (RequestModel rm : allRequests) {
                if (rm.getStatus().equals(RequestStatus.CONFIRMED)) {
                    result++;
                }
            }
        }
        return result;
    }
}