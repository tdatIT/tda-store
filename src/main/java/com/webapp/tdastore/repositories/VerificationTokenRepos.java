package com.webapp.tdastore.repositories;

import com.webapp.tdastore.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepos extends JpaRepository<VerificationToken, Long> {
}