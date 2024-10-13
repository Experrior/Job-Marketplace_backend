package com.jobsearch.userservice.repositories

import com.jobsearch.userservice.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID>{
}