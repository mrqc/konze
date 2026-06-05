# Konze

## tl;dr
Providing controllable database connections to support: 
* historization of data / recovery of data
* permission management
* connection timeout management
* connection profiles management
* prompt and query logging.

for your java/kotline application

Features:

| Status      | Feature                                                                  |
|-------------|--------------------------------------------------------------------------|
| in progress | built-in Text-to-SQL database connection management framework for agents |
| in progress | support for PostgreSQL databases                                           |
| planned     | support for MySQL and other databases                                           |



First release of Konze supports PostgreSQL databases, but support for MySQL and other databases is planned for the future.

## What is Konze?

Konze is a database connection management framework built on Hikari. It provides the ability to manage different pools
for your java/kotline applications. As example it enables to maintain AI agents in a controllable way, ensuring that 
agents have the necessary permissions to do their job, while also preventing them from keeping connections open indefinitely 
and overloading the database. Konze also provides features such as connection profiles, query logging, and data historization 
to help you manage your database connections effectively. It also comes with a Text-to-SQL database access. 
When you want to provide access to a database, Konze ensures that
* a connection gets only those permissions that are necessary for the agent to do its job
* a connection follows specific timeout to prevent it from keeping connections open indefinitely and overloading the database
* you can define profiles for connections which can be reused, so you don't have to define the same connection parameters for each usage
* queries are logged to log files, so you can see which queries are running against the database (Konze logs queries to a file)
* data is historized, so you can recover data if it gets accidentally deleted or updated data that it shouldn't have.

Example config yaml file:
```yaml
- konze-db-example:
  profiles:
    example-profile:
      permissions:
        - select
        - insert
        - update
        - delete
        - truncate
        - references
        - trigger
        - maintain
        - usage
        - create
        - connect
        - temporary
        - execute
        - all privileges

      configuration:
        query:
          executionTimeout: 60s
          executionLogging: true
          executionLog: ./logs/execution.log
        monitoring:
          slowQueryThreshold: 500
          slowQueryLogging: true
          slowQueryLog: ./logs/slow-queries.log
      schemaDiscoveryEndpoint:
        enabled: true
        endpoint: /schema-discovery
        rateLimiting: 100
      pool:
        dataSourceClassName: com.zaxxer.hikari.HikariDataSource
        jdbcUrl: jdbc:postgresql://localhost:5432/konze_db
        autoCommit: true
        connectionTimeout: 30000
        idleTimeout: 600000
        keepaliveTime: 0
        maxLifetime: 1800000
        connectionTestQuery: select 1
        minimumIdle: 10
        maximumPoolSize: 20
        poolName: MyHikariPool
        initializationFailTimeout: 1
        readOnly: false
        connectionInitSql: select 1
        transactionIsolation: TRANSACTION_READ_COMMITTED
        validationTimeout: 5000
        leakDetectionThreshold: 2000
        schema: public

  databaseAdministration:
    access:
      driver: net.masterstudios.konze.driver.postgres.PostgresDatabaseDriver
      jdbcUrl: jdbc:postgresql://localhost:5432/konze_db
      username: konze_user
      password: konze_password
    schema:
      updateTrigger: true
      deleteTrigger: true
      insertTrigger: true

```
