package com.felipepassada.outsera.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "movies")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "producers")
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "movie_year", nullable = false)
    private Integer year;

    @Column(nullable = false)
    private String studio;

    @Column(nullable = false)
    private Boolean winner;

    @ManyToMany
    @JoinTable(
        name = "produced_movies",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "producer_id")
    )
    private Set<Producer> producers;
}
