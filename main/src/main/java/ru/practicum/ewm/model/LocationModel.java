package ru.practicum.ewm.model;

import lombok.*;
import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "locations", schema = "public")
public class LocationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @Column(name = "lat", nullable = false)
    private float lat;
    @Column(name = "lon", nullable = false)
    private float lon;
}