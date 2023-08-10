server:
  # todo: 指定端口
  port: 8080

spring:
  profiles:
    active: DEV
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Shanghai
  application:
    name: ${appName}
  banner:
    location: classpath:rc-banner.txt