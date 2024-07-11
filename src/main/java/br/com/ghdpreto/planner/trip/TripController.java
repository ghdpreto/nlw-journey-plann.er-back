package br.com.ghdpreto.planner.trip;

import java.util.Optional;
import java.util.UUID;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ghdpreto.planner.participant.ParticipantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("trips")
public class TripController {
    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ParticipantService participantService;

    @PostMapping("")
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {
        TripEntity newTrip = new TripEntity(payload);

        this.tripRepository.save(newTrip);

        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip.getId());

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripEntity> getTripDetails(@PathVariable UUID id) {

        Optional<TripEntity> trip = this.tripRepository.findById(id);

        /**
         * Condicional if()
         * trip.map(ResponseEntity::ok).
         */
        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
