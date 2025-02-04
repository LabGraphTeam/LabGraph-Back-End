package leonardo.labutilities.qualitylabpro.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import leonardo.labutilities.qualitylabpro.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByUsername(String name);

	boolean existsByEmail(String email);

	UserDetails getReferenceOneByUsername(String username);

	User findOneByUsername(String username);

	User findOneByEmail(String email);

	User findOneByUsernameOrEmail(String username, String email);

	boolean existsByUsernameOrEmail(String username, String email);

	UserDetails getReferenceByUsernameAndEmail(String userName, String Email);

	boolean existsByUsernameAndEmail(String userName, String Email);

	@Transactional
	@Modifying
	@Query("UPDATE users u SET u.password = :newPassword WHERE u.username = :username")
	void setPasswordWhereByUsername(String username, String newPassword);

	@Transactional
	@Modifying
	@Query("UPDATE users u SET u.password = :newPassword  WHERE u.email = :username")
	void setPasswordWhereByEmail(String email, String newPassword);

}
