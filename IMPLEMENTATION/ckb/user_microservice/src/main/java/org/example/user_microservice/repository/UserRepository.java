package org.example.user_microservice.repository;

import org.example.user_microservice.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByUsername(String username);
    boolean existsByUsername(@Param("username") String username);
    @Query("SELECT u.role FROM UserModel u WHERE u.username = :username")
    Integer getUserRole(@Param("username") String username);

    @Query("SELECT u.subscription FROM UserModel u WHERE u.username = :user")
    List<String> findNamesBySubscription(String user);
}
