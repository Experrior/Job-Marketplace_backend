package com.jobsearch.jobservice.entities.specifications


import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.requests.JobFilterRequest
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.util.*

object JobSpecifications {
    fun getJobsByFilter(filter: JobFilterRequest): Specification<Job> {
        return Specification { root, _, criteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")))

            filter.location?.let {
                predicates.add(criteriaBuilder.equal(root.get<String>("location"), it))
            }
            filter.requiredExperience?.let {
                predicates.add(criteriaBuilder.equal(root.get<String>("requiredExperience"), it))
            }
            filter.companyId?.let {
                predicates.add(criteriaBuilder.equal(root.get<UUID>("companyId"), it))
            }
            filter.hasSalary?.let {
                if (it) {
                    predicates.add(criteriaBuilder.isNotNull(root.get<Int>("salary")))
                } else {
                    predicates.add(criteriaBuilder.isNull(root.get<Int>("salary")))
                }
            }
            filter.minSalary?.let {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("salary"), it))
            }
            filter.maxSalary?.let {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("salary"), it))
            }

            // TODO("Add filter for required skills")

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }
}