package coms.music_streaming_app.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import coms.music_streaming_app.models.ERole;
import coms.music_streaming_app.models.Role;


public interface RoleRepository extends MongoRepository<Role, String> {
	Optional<Role> findByName(ERole name);
}
