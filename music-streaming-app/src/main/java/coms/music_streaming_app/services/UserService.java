package coms.music_streaming_app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import coms.music_streaming_app.models.User;
import coms.music_streaming_app.repositories.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	 public List<User> getAllUsers() {
	        return userRepository.findAll();
	    }
	 

	
	

}
