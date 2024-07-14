package br.com.ghdpreto.planner.activities;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ghdpreto.planner.trip.TripEntity;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public ActivityResponse registerActivity(ActivityRequestPayload payload, TripEntity trip) {
        ActivityEntity activity = new ActivityEntity(payload.title(), payload.occurs_at(), trip);

        this.activityRepository.save(activity);

        return new ActivityResponse(activity.getId());

    }

    public List<ActivityData> getAllActivitiesFromEvent(UUID id) {
        return this.activityRepository.findByTripId(id).stream()
                .map(activity -> new ActivityData(activity.getId(), activity.getTitle(), activity.getOccursAt()))
                .toList();

    }
}
