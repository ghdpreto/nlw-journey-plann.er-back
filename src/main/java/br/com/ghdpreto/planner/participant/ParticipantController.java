package br.com.ghdpreto.planner.participant;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("participants")
public class ParticipantController {

    @Autowired
    private ParticipantRepository participantRepository;

    @PostMapping("{id}/confirm")
    public ResponseEntity<ParticipantEntity> confirmParticipant(@PathVariable UUID id,
            @RequestBody ParticipantRequestPayload payload) {

        Optional<ParticipantEntity> participant = this.participantRepository.findById(id);

        if (participant.isPresent()) {
            ParticipantEntity rawParticipant = participant.get();

            rawParticipant.setIsConfirmed(true);
            rawParticipant.setName(payload.name());

            this.participantRepository.save(rawParticipant);

            return ResponseEntity.ok(rawParticipant);
        }

        return ResponseEntity.notFound().build();
    }

}
