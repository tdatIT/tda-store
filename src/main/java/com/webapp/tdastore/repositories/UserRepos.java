package com.webapp.tdastore.repositories;

import com.webapp.tdastore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepos extends JpaRepository<User,Long> {

}
