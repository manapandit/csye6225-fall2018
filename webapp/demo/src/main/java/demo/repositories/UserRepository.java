package demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import demo.models.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
	
	@Query("SELECT id FROM User u WHERE u.email=:email")
	Optional<Integer> findIdByUserName(@Param("email") String email);
	
// 	@Query("SELECT email FROM User u WHERE u.email=:email")
// 	Optional<String> findIdByUserEmail(@Param("email") String email);
	
	
	
}
