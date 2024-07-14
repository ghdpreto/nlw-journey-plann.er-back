package br.com.ghdpreto.planner.link;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<LinkEntity, UUID> {

    List<LinkEntity> findByTripId(UUID tripId);

}
