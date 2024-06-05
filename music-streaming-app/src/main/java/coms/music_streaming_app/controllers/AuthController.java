package coms.music_streaming_app.controllers;

import coms.music_streaming_app.models.*;
import coms.music_streaming_app.models.payload.response.MessageResponse;
import coms.music_streaming_app.repositories.*;
import coms.music_streaming_app.security.service.*;
import coms.music_streaming_app.payload.request.LoginRequest;
import coms.music_streaming_app.payload.request.SignupRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import coms.music_streaming_app.repositories.RoleRepository;
import coms.music_streaming_app.repositories.UserRepository;
import coms.music_streaming_app.security.jwt.JwtUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	  AuthenticationManager authenticationManager;

	  @Autowired
	  UserRepository userRepository;

	  @Autowired
	  RoleRepository roleRepository;

	  @Autowired
	  PasswordEncoder encoder;

	  @Autowired
	  JwtUtils jwtUtils;
	  
//	  @GetMapping("/hello")
//	  public String test() {
//		  return "Hello";
//	  }
	  
	  @PostMapping("/signin")
	  public ResponseEntity<?> authenticateUser( @RequestBody LoginRequest loginRequest) 
	  {
		  
		  //System.out.println(loginRequest.getPassword());

	    Authentication authentication = authenticationManager.authenticate(
	        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    String jwt = jwtUtils.generateJwtToken(authentication);
	    
	    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
	    List<String> roles = userDetails.getAuthorities().stream()
	        .map(item -> item.getAuthority())
	        .collect(Collectors.toList());
	    
	    Map<String,String> response = new HashMap<>();
	    
	    response.put("status","200");
	    response.put("jwt", jwt);
	    
	     response.put("role0", roles.get(0));
	    response.put("username", userDetails.getUsername());
	    response.put("email", userDetails.getEmail());
	    
	    return new ResponseEntity<>(response, HttpStatus.OK);
	  }

	  @PostMapping("/signup")
	  public ResponseEntity<?> registerUser( @RequestBody SignupRequest signUpRequest) {
	    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
	      return ResponseEntity
	          .badRequest()
	          .body(new MessageResponse("Error: Username is already taken!"));
	    }

	    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
	      return ResponseEntity
	          .badRequest()
	          .body(new MessageResponse("Error: Email is already in use!"));
	    }

	    // Create new user's account
	    User user = new User(signUpRequest.getUsername(), 
	               signUpRequest.getEmail(),
	               encoder.encode(signUpRequest.getPassword()));

	    Set<String> strRoles = signUpRequest.getRoles();
	    Set<Role> roles = new HashSet<>();

	    if (strRoles == null) {
	      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
	          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	      roles.add(userRole);
	    } else {
	      strRoles.forEach(role -> {
	        switch (role) {
	        case "admin":
	          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
	              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	          roles.add(adminRole);

	          break;
	        default:
	          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
	              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	          roles.add(userRole);
	        }
	      });
	    }

	    user.setRoles(roles);
	    userRepository.save(user);
	    Map<String,String> response = new HashMap<>();
	    response.put("status", "200");
	    response.put("username", signUpRequest.getUsername());
	    System.out.println(signUpRequest.getRoles());
	    return new ResponseEntity<>(response, HttpStatus.OK);
	  
	  
	  
	}

}
