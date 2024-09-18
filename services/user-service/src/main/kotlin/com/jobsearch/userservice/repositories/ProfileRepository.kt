package com.jobsearch.userservice.repositories

import com.jobsearch.userservice.entities.Profile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProfileRepository : JpaRepository<Profile, UUID> {
}