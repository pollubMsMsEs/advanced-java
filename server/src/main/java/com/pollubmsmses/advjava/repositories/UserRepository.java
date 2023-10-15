package com.pollubmsmses.advjava.repositories;
import com.pollubmsmses.advjava.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);

}
