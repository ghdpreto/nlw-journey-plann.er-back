package br.com.ghdpreto.planner.trip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ghdpreto.planner.activity.ActivityData;
import br.com.ghdpreto.planner.activity.ActivityEntity;
import br.com.ghdpreto.planner.activity.ActivityRequestPayload;
import br.com.ghdpreto.planner.activity.ActivityResponse;
import br.com.ghdpreto.planner.activity.ActivityService;
import br.com.ghdpreto.planner.link.LinkData;
import br.com.ghdpreto.planner.link.LinkRepository;
import br.com.ghdpreto.planner.link.LinkRequestPayload;
import br.com.ghdpreto.planner.link.LinkResponse;
import br.com.ghdpreto.planner.link.LinkService;
import br.com.ghdpreto.planner.participant.ParticipantCreateResponse;
import br.com.ghdpreto.planner.participant.ParticipantData;
import br.com.ghdpreto.planner.participant.ParticipantRequestPayload;
import br.com.ghdpreto.planner.participant.ParticipantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("trips")
public class TripController {
    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private LinkService linkService;

    // #region TRIPS
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
    // #endregion

    // #region ACTIVITIES
    @PostMapping("{id}/activities")
    public ResponseEntity<ActivityResponse> registrerActivity(@PathVariable UUID id,
            @RequestBody ActivityRequestPayload payload) {
        Optional<TripEntity> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {

            TripEntity rawTrip = trip.get();
            ActivityResponse activityResponse = this.activityService.registerActivity(payload, rawTrip);

            return ResponseEntity.ok(activityResponse);

        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("{id}/activities")
    public ResponseEntity<List<ActivityData>> getActivities(@PathVariable UUID id) {
        List<ActivityData> activitiesList = this.activityService.getAllActivitiesFromEvent(id);

        return ResponseEntity.ok(activitiesList);
    }
    // #endregion

    // #region INVITE
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

    @GetMapping("{id}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id) {

        List<ParticipantData> participantList = this.participantService.getAllParticipantsFromEvent(id);
        return ResponseEntity.ok(participantList);
    }
    // #endregion

    // #region LINKS
    @PostMapping("{id}/links")
    public ResponseEntity<LinkResponse> registrerLink(@PathVariable UUID id,
            @RequestBody LinkRequestPayload payload) {

        Optional<TripEntity> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {

            TripEntity rawTrip = trip.get();
            LinkResponse linkResponse = this.linkService.registerLink(payload, rawTrip);

            return ResponseEntity.ok(linkResponse);

        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("{id}/links")
    public ResponseEntity<List<LinkData>> getLinks(@PathVariable UUID id) {
        List<LinkData> linksList = this.linkService.getAllLinksFromId(id);

        return ResponseEntity.ok(linksList);
    }

}
