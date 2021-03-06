/**
 * 
 */
package com.in28minutes.rest.webservices.restfulwebservices.user;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.net.URI;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.in28minutes.rest.webservices.restfulwebservices.user.exception.UserNotFoundExcepetion;

/**
 * @author ANUPAMBA
 *
 */
@RestController
public class UserJPAResource {
	
	@Autowired
	private UserDaoService userService;
	
	@Autowired
	private UserRepositoy userRepo;
	
	@Autowired
	private PostRepositoy postRepo;

	/***
	 * retrieveAllUser() 
	 * @GetMapping(path="/user/{id}")
	 * */
	@GetMapping(path="/jpa/users")
	public List<User> retrieveAllUser() {
		return userRepo.findAll();
	}
	
	
	/***saveUser(User)
	 * @PostMapping(path="/create-user")
	 * 
	 * */
	@PostMapping(path="/jpa/users")
	private ResponseEntity saveUser(@Valid @RequestBody User user) {
		User savedUser = userRepo.save(user);
		
		/** 
		 * 201 -because of code "ResponseEntity.created" Created response back to client --  ResponseEntity.created(location).build()
		 * Also sending URL / location for newly created user -- in Header 
		 * 
		 * Sending Response for API call appending User id to actual API call URI */
		URI location = ServletUriComponentsBuilder
		.fromCurrentRequest()
		.path("/{id}")
		.buildAndExpand(savedUser.getId()).toUri();
		
		return ResponseEntity.created(location).build();
	}
	
	
	
	
	/***retrieveSpecificUser(int id)
	 * @GetMapping(path="/user/{id}")
	 * 
	 * */
	@GetMapping(path="/jpa/user/{id}")
	private Resource retrieveSpecificUser(@PathVariable int id){
		
		Optional<User> user = userRepo.findById(id);
		
		if(!user.isPresent() ) {
			throw new UserNotFoundExcepetion(String.format("User with id - %s do not exist's", id));
		}
		
		//HATEOAS
		Resource<User> resource = new Resource(user.get());
		
		ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUser());
		resource.add(linkTo.withRel("all-users"));
		
		return resource;
	}
	
	
	/***retrieveSpecificUser(int id)
	 * @GetMapping(path="/user/{id}")
	 * 
	 * */
	@DeleteMapping(path="/jpa/user/{id}")
	private void deleteSpecificUser(@PathVariable int id){
		
		userRepo.deleteById(id);
	}
	
	/***retrieveSpecificUser(int id)
	 * @GetMapping(path="/user/{id}")
	 * 
	 * */
	@GetMapping(path="/jpa/user/{id}/posts")
	private List<Post> retrieveSpecificUsersPost(@PathVariable int id){
		
		Optional<User> user = userRepo.findById(id);
		
		if(!user.isPresent() ) {
			throw new UserNotFoundExcepetion(String.format("User with id - %s do not exist's", id));
		}
		
		//HATEOAS
		/*Resource<User> resource = new Resource(user.get().getPosts());
		
		ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUser());
		resource.add(linkTo.withRel("all-users"));
		*/
		return user.get().getPosts();
	}
	
	
	
	/***saveUser(User)
	 * @PostMapping(path="/create-user")
	 * 
	 * */
	@PostMapping("/jpa/user/{id}/post")
	private ResponseEntity<Object> savePost(@PathVariable int id, @RequestBody Post post) {
		Optional<User> userOptional = userRepo.findById(id);
		
		if(!userOptional.isPresent() ) {
			throw new UserNotFoundExcepetion(String.format("User with id - %s do not exist's", id));
		}
		
		
		post.setUser(userOptional.get());
		postRepo.save(post);
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(post.getId()).toUri();
				
				return ResponseEntity.created(location).build();
		
	}
	
	
}
