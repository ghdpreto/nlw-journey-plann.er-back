package br.com.ghdpreto.planner.trip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ghdpreto.planner.participant.ParticipantService;

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

}
