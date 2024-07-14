package br.com.ghdpreto.planner.link;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ghdpreto.planner.trip.TripEntity;

@Service
public class LinkService {

    @Autowired
    private LinkRepository linkRepository;

    public LinkResponse registerLink(LinkRequestPayload payload, TripEntity trip) {

        LinkEntity link = new LinkEntity(payload.title(), payload.url(), trip);

        this.linkRepository.save(link);

        return new LinkResponse(link.getId());

    }

    public List<LinkData> getAllLinksFromId(UUID tripId) {
        return this.linkRepository.findByTripId(tripId).stream()
                .map(link -> new LinkData(link.getTitle(), link.getUrl())).toList();
    }
}
