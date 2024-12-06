package com.jobsearch.jobservice.entities.specifications


import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.entities.enums.ExperienceLevel
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
            filter.requiredSkills?.let { requiredSkillNames ->
                val skillPredicates = requiredSkillNames.map { skillName ->
                    criteriaBuilder.isTrue(
                        criteriaBuilder.function(
                            "jsonb_path_exists",
                            Boolean::class.java,
                            root.get<Any>("requiredSkills"),
                            criteriaBuilder.literal("""$[*] ? (@.name == "$skillName")""")
                        )
                    )
                }
                predicates.add(criteriaBuilder.and(*skillPredicates.toTypedArray()))
            }
            filter.workLocation?.let {
                predicates.add(criteriaBuilder.equal(root.get<String>("workLocation"), it))
            }
            filter.employmentType?.let {
                predicates.add(criteriaBuilder.equal(root.get<String>("employmentType"), it))
            }
            filter.experienceLevel?.let {
                val experienceLevel = ExperienceLevel.valueOf(it.uppercase())
                predicates.add(criteriaBuilder.equal(root.get<ExperienceLevel>("experienceLevel"), experienceLevel))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }
}