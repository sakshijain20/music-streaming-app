package coms.music_streaming_app.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import coms.music_streaming_app.models.User;

public interface UserRepository extends MongoRepository<User, String> {
	 Optional<User> findByUsername(String username);

	  Boolean existsByUsername(String username);

	  Boolean existsByEmail(String email);

}
