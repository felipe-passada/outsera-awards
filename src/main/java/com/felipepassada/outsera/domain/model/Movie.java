package com.felipepassada.outsera.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
@Data
@EqualsAndHashCode(of = {"title", "year"})
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
    private Set<Producer> producers = new HashSet<>();

    public void addProducer(Producer producer) {
        if (!this.producers.contains(producer)) {
            this.producers.add(producer);
        }
        if (!producer.getMovies().contains(this)) {
            producer.getMovies().add(this);
        }
    }
}
