package br.com.ghdpreto.planner.participant;

import java.util.UUID;

import br.com.ghdpreto.planner.trip.TripEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "participants")
public class ParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(name = "is_confirmed")
    private Boolean isConfirmed;

    /**
     * Relacionamento
     * 1 trip pode ter N participant
     * 1 participant pode pertencer a 1 trip
     */
    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private TripEntity trip;

    public ParticipantEntity(String email, TripEntity trip) {
        this.email = email;
        this.trip = trip;
        this.isConfirmed = false;
        this.name = "";
    }
}
