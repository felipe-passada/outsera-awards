package com.felipepassada.outsera.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "producers")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "movies")
@NoArgsConstructor
@AllArgsConstructor
public class Producer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "producers")
    private Set<Movie> movies;
}
