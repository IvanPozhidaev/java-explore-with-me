package ru.practicum.ewm.model;

import lombok.*;
import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "users", schema = "public")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "name", nullable = false)
    private String name;
}