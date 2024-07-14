ALTER TABLE participants
ADD CONSTRAINT fk_participants_trip
FOREIGN KEY (trip_id)
REFERENCES trips(id)
ON DELETE CASCADE;