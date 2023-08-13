package ru.practicum.ewm.model;

import lombok.*;
import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "categories", schema = "public")
public class CategoryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
}