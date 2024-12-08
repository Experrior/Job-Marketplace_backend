package com.jobsearch.jobservice.config


import org.springframework.beans.factory.annotation.Autowired
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
import java.util.*
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "masterEntityManager",
    transactionManagerRef = "masterTransactionManager",
    basePackages = [
        "com.jobsearch.jobservice.repositories"
    ]
)
open class MasterDBConfig {

    @Autowired
    private val env: Environment? = null

    @Bean(name=["masterSource"])
    open fun productDataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(
            "org.postgresql.Driver"
        )
        dataSource.url = "jdbc:postgresql://172.22.0.1:5432/JobMarketDB"
//        dataSource.url = env?.getProperty("spring.datasource.url")
        dataSource.username = "admin"
        dataSource.password = "test"

        return dataSource
    }

    @Bean(name = ["masterEntityManager"])
    open fun productEntityManager(): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = productDataSource()
        em.setPackagesToScan(
            *arrayOf("com.jobsearch.jobservice.entities")
        )
        val vendorAdapter = HibernateJpaVendorAdapter()
        em.jpaVendorAdapter = vendorAdapter
        val properties = HashMap<String, Any?>()
//        properties["hibernate.hbm2ddl.auto"] = env.getProperty("hibernate.hbm2ddl.auto")
        properties["hibernate.dialect"] = env?.getProperty("hibernate.dialect")
        em.setJpaPropertyMap(properties)
        return em
    }

    @Bean(name = ["masterTransactionManager"])
    open fun productTransactionManager(): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = productEntityManager().getObject()
        return transactionManager
    }
}