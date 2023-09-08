package fr.nil.backedflow.repositories;

import fr.nil.backedflow.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User,UUID> {

    @Transactional
    @Modifying
    @Query("update User u set u.firstName = ?1, u.lastName = ?2, u.mail = ?3, u.password = ?4, u.avatar = ?6 where u.id = ?5")
    void updateUser(@NonNull String firstName, String lastName, String mail, String password, UUID id, String avatarURL);
    List<User> findAll();
    Optional<User> findUserById(UUID id);
    Optional<User> findByMail(String email);
    boolean existsByMail(String email);
    void deleteByMail(String email);
}

