# Konze

| :exclamation:  Under Development as per 27th may 2026   |
|----------------------------------------------|

## tl;dr
Text-to-SQL database connection management framework for agents, with 
* recovery of data
* permission management
* connection timeout management
* connection profiles management
* data change auditing
* prompt and query logging.

First release of Konze supports PostgreSQL databases, but support for MySQL and other databases is planned for the future.

## What is Konze?

A Text-to-SQL database connection management framework for agents. When you want to provide agents access to a database, Konze ensures that
* a connection for an agent gets only those permissions that are necessary for the agent to do its job
* a connection follows specific timeout to prevent agents from keeping connections open indefinitely and overloading the database
* you can define profiles for connections which can be reused across agents, so you don't have to define the same connection parameters for each agent
* data changes are audited, so you can track which agent made which changes to the database (Konze adds triggers to the database to log changes)
* queries are logged to agent log files, so you can see what queries agents are running against the database (Konze logs queries to a file)

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
        audit:
          log: ./logs/audit.log
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
