package com.example.ChatApp2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;


public interface UserRepository extends JpaRepository<User,Integer> {

    @Modifying
    @Query(value = "UPDATE User u set " +
            "u.isOnline = false WHERE u.name= :username")
    public void deactivateUserByName(String username);

    @Modifying
    @Query(value = "DELETE User u WHERE u.isOnline = false")
    int deleteDeactivatedUsers();
}
