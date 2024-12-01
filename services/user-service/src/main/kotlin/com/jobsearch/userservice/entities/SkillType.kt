package com.jobsearch.userservice.entities

enum class SkillType(val displayName: String) {
    // Programming Languages
    JAVA("Java"),
    KOTLIN("Kotlin"),
    PYTHON("Python"),
    JAVASCRIPT("JavaScript"),
    TYPESCRIPT("TypeScript"),
    C_SHARP("C#"),
    C_PLUS_PLUS("C++"),
    RUBY("Ruby"),
    GO("Go"),
    SWIFT("Swift"),
    PHP("PHP"),
    RUST("Rust"),
    SQL("SQL"),
    SCALA("Scala"),
    HTML("HTML"),
    CSS("CSS"),

    // Frameworks and Libraries
    SPRING_BOOT("Spring Boot"),
    SPRING("Spring"),
    HIBERNATE("Hibernate"),
    REACT("React"),
    ANGULAR("Angular"),
    VUE_JS("Vue.js"),
    DJANGO("Django"),
    FLASK("Flask"),
    RUBY_ON_RAILS("Ruby on Rails"),
    EXPRESS_JS("Express.js"),
    NODE_JS("Node.js"),
    DOT_NET(".NET"),
    LARAVEL("Laravel"),
    SYMFONY("Symfony"),

    // Databases
    MYSQL("MySQL"),
    POSTGRESQL("PostgreSQL"),
    MONGODB("MongoDB"),
    ORACLE("Oracle"),
    SQL_SERVER("SQL Server"),
    REDIS("Redis"),
    CASSANDRA("Cassandra"),
    ELASTICSEARCH("Elasticsearch"),

    // DevOps and CI/CD
    DOCKER("Docker"),
    KUBERNETES("Kubernetes"),
    JENKINS("Jenkins"),
    GIT("Git"),
    GITHUB("GitHub"),
    GITLAB("GitLab"),
    CI_CD("CI/CD"),
    ANSIBLE("Ansible"),
    TERRAFORM("Terraform"),
    AWS("AWS"),
    AZURE("Azure"),
    GOOGLE_CLOUD("Google Cloud"),

    // Testing
    SELENIUM("Selenium"),
    JUNIT("JUnit"),
    MOCKITO("Mockito"),
    CYPRESS("Cypress"),
    POSTMAN("Postman"),
    JEST("Jest"),
    MOCHA("Mocha"),

    // Others
    MACHINE_LEARNING("Machine Learning"),
    DATA_SCIENCE("Data Science"),
    BIG_DATA("Big Data"),
    CLOUD_COMPUTING("Cloud Computing"),
    AGILE("Agile"),
    SCRUM("Scrum"),
    PROJECT_MANAGEMENT("Project Management"),
    SYSTEM_DESIGN("System Design"),
    API_DEVELOPMENT("API Development"),
    GRAPHQL("GraphQL"),
    REST_API("REST API")
}