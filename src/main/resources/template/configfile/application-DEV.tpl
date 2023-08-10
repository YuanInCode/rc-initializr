spring:
  redis:
    host: localhost
    port: 6379
    timeout: 3000
    database: 1
    lettuce:
      pool:
        max-active: 20
        max-wait: 1000
  datasource:
    username: sa
    password:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:quartz_extend_demo
  h2:
    console:
      enabled: true
      settings:
        trace: true
        web-allow-others: true
      path: /h2-console
  quartz:
    auto-startup: true
    job-store-type: jdbc
    startup-delay: 1s
    overwrite-existing-jobs: true
    properties:
      org:
        quartz:
          scheduler:
            instanceName: ${quartzSchedulerName}
            instanceId: AUTO
          jobStore:
            class: org.quartz.impl.jdbcjobstore.JobStoreTX
            driverDelegateClass: org.quartz.impl.jdbcjobstore.StdJDBCDelegate
            tablePrefix: QRTZ_
            isClustered: true
            clusterCheckinInterval: 10000
            useProperties: false
            dataSource: myDS
          threadPool:
            class: org.quartz.simpl.SimpleThreadPool
            threadCount: 20
            threadPriority: 5
            threadsInheritContextClassLoaderOfInitializingThread: true
          dataSource:
            myDS:
              driver: org.h2.Driver
              URL: jdbc:h2:mem:quartz_extend_demo
              user: sa
              password:
              provider: hikaricp
              validationQuery: select 1