package br.com.ghdpreto.planner.participant;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ghdpreto.planner.trip.TripEntity;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    public void registerParticipantsToEvent(List<String> participantsToInvite, TripEntity trip) {
        List<ParticipantEntity> participants = participantsToInvite.stream()
                .map(email -> new ParticipantEntity(email, trip)).toList();
        /**
         * Salvando em massa os participant
         */
        this.participantRepository.saveAll(participants);

        System.out.println(participants.get(0).getId());
    }

    public ParticipantCreateResponse registerParticipantToEvent(String email, TripEntity trip) {
        ParticipantEntity participant = new ParticipantEntity(email, trip);

        this.participantRepository.save(participant);

        return new ParticipantCreateResponse(participant.getId());
    }

    public void triggerConfirmationEMailToParticipants(UUID tripId) {

    }

    public void triggerConfirmationEMailToParticipant(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'triggerConfirmationEMailToParticipant'");
    }
}
