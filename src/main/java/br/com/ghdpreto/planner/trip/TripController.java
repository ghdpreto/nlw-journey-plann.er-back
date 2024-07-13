package br.com.ghdpreto.planner.trip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ghdpreto.planner.participant.ParticipantCreateResponse;
import br.com.ghdpreto.planner.participant.ParticipantRequestPayload;
import br.com.ghdpreto.planner.participant.ParticipantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

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

        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

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

    @PutMapping("{id}")
    public ResponseEntity<TripEntity> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload) {

        Optional<TripEntity> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {
            TripEntity rawTrip = trip.get();
            rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setDestination(payload.destination());

            this.tripRepository.save(rawTrip);

            return ResponseEntity.ok(rawTrip);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("{id}/confirm")
    public ResponseEntity<TripEntity> confirmTrip(@PathVariable UUID id) {
        Optional<TripEntity> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {
            TripEntity rawTrip = trip.get();

            rawTrip.setIsConfirmed(true);

            this.tripRepository.save(rawTrip);
            /**
             * Notificando participantes referente a confirmacao do evento
             */
            this.participantService.triggerConfirmationEMailToParticipants(id);

            return ResponseEntity.ok(rawTrip);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id,
            @RequestBody ParticipantRequestPayload payload) {
        Optional<TripEntity> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {
            TripEntity rawTrip = trip.get();

            ParticipantCreateResponse participantResponse = this.participantService
                    .registerParticipantToEvent(payload.email(), rawTrip);

            if (rawTrip.getIsConfirmed()) {
                /**
                 * envia o email somente para esse participante
                 */
                this.participantService.triggerConfirmationEMailToParticipant(payload.email());
            }

            return ResponseEntity.ok(participantResponse);
        }

        return ResponseEntity.notFound().build();
    }

}
