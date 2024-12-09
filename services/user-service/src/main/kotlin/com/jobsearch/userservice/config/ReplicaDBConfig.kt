package com.jobsearch.userservice.config


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "replicaEntityManager",
    transactionManagerRef = "replicaTransactionManager",
    basePackages = [
        "com.jobsearch.userservice.replica_repositories",
    ]
)
open class ReplicaDBConfig(
    @Value("\${spring.datasource.replica.url}") private val dbUrl: String,
    @Value("\${spring.datasource.replica.username}") private val dbUsername: String,
    @Value("\${spring.datasource.replica.password}") private val dbPassword: String,
    @Value("\${spring.datasource.replica.driver-class-name}") private val dbDriver: String,
    @Value("\${spring.jpa.master.hibernate.dialect}") private val dbDialect: String,
) {

    @Autowired
    private val env: Environment? = null

    @Bean(name = ["replicaSource"])
    open fun productDataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(
            "org.postgresql.Driver"
        )
        dataSource.url = "jdbc:postgresql://172.22.0.1:5434/JobMarketDB"
//        dataSource.url = env?.getProperty("spring.datasource.replica1.url")
        dataSource.username = "admin"
        dataSource.password = "test"

        return dataSource
    }

    @Bean(name = ["replicaEntityManager"])
    open fun productEntityManager(): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = productDataSource()
        em.setPackagesToScan(
            *arrayOf("com.jobsearch.userservice.entities")
        )

        val vendorAdapter = HibernateJpaVendorAdapter()
        em.jpaVendorAdapter = vendorAdapter
        val properties = HashMap<String, Any?>()
//        properties["hibernate.hbm2ddl.auto"] = env.getProperty("hibernate.hbm2ddl.auto")
        properties["hibernate.dialect"] = env?.getProperty("hibernate.dialect")
        em.setJpaPropertyMap(properties)
        return em
    }

    @Bean(name = ["replicaTransactionManager"])
    open fun productTransactionManager(): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = productEntityManager().getObject()
        return transactionManager
    }

}
