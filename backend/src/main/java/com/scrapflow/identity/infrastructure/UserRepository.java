package com.scrapflow.identity.infrastructure;

import com.scrapflow.identity.domain.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByEmail(String email);
  Optional<User> findByEmailVerificationTokenHash(String emailVerificationTokenHash);
  Optional<User> findByPasswordResetTokenHash(String passwordResetTokenHash);
  boolean existsByEmail(String email);
}
