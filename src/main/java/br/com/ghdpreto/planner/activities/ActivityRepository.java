package br.com.ghdpreto.planner.activities;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, UUID> {

    List<ActivityEntity> findByTripId(UUID tripId);

}
