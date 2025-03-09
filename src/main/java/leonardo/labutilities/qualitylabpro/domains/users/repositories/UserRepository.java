package leonardo.labutilities.qualitylabpro.domains.users.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import leonardo.labutilities.qualitylabpro.domains.analytics.models.Analytic;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByUsername(String name);

	boolean existsByEmail(String email);

	UserDetails getReferenceOneByUsername(String username);

	User findOneByUsername(String username);

	User findOneByEmail(String email);

	User findOneByUsernameOrEmail(String username, String email);

	@Query("SELECT a FROM users u JOIN u.validatedAnalytics a WHERE u.id = :id")
	List<Analytic> findAnalyticsByUserValidatedId(Long id);

	boolean existsByUsernameOrEmail(String username, String email);

	UserDetails getReferenceByUsernameAndEmail(String userName, String email);

	boolean existsByUsernameAndEmail(String userName, String email);

	@Transactional
	@Modifying
	@Query("UPDATE users u SET u.password = :newPassword WHERE u.username = :username")
	void setPasswordWhereByUsername(@Param("username") String username,
			@Param("newPassword") String newPassword);

	@Transactional
	@Modifying
	@Query("UPDATE users u SET u.password = :newPassword  WHERE u.email = :email")
	void setPasswordWhereByEmail(String email, String newPassword);

}
