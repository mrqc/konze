# Konze

## tl;dr
Text-to-SQL database connection management framework for agents, with 
* permission management
* connection timeout management
* connection profiles
* data change auditing
* query logging.

First release of Konze supports PostgreSQL databases, but support for MySQL and other databases is planned for the future.

---

## What is Konze?

A Text-to-SQL database connection management framework for agents. When you want to provide agents access to a database, Konze ensures that
* a connection for an agent gets only those permissions that are necessary for the agent to do its job
* a connection follows specific timeout to prevent agents from keeping connections open indefinitely and overloading the database
* you can define profiles for connections which can be reused across agents, so you don't have to define the same connection parameters for each agent
* data changes are audited, so you can track which agent made which changes to the database (Konze adds triggers to the database to log changes)
* queries are logged to agent log files, so you can see what queries agents are running against the database (Konze logs queries to a file)

Example config yaml file:
```
profiles:
  - name: admin-agent
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
      #- all privileges
      query:
        row-limit: 1000
        execution-timeout: 60s
        execution-logging: true
        execution-log: ./logs/execution.log
      audit:
        log: ./logs/audit.log
        schema-adaption:
          - update-trigger: true
          - delete-trigger: true
          - insert-trigger: true
        monitoring:
          slow-query-threshold: 500ms
          slow-query-logging: true
          slow-query-log: ./logs/slow-queries.log
          
configuration:
  pool:
    dataSourceClassName: com.zaxxer.hikari.HikariDataSource
    jdbcUrl: jdbc:mysql://localhost:3306/mydb
    username: user
    password: password
    autoCommit: true
    connectionTimeout: 30000
    idleTimeout: 600000
    keepaliveTime: 0
    maxLifetime: 1800000
    connectionTestQuery: SELECT 1
    minimumIdle: 10
    maximumPoolSize: 20
    poolName: MyHikariPool
    initializationFailTimeout: 1
    readOnly: false
    connectionInitSql: SELECT 1
    transactionIsolation: TRANSACTION_READ_COMMITTED
    validationTimeout: 5000
    leakDetectionThreshold: 2000
    schema: public

```
