package ru.practicum.ewm.model;

import lombok.*;
import javax.persistence.*;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "compilations", schema = "public")
public class CompilationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "pinned")
    private Boolean pinned;
    @OneToMany
    @JoinTable(
            name = "events_compilations",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<EventModel> events;
}