mvn clean package -Dmaven.test.skip=true -f pom.xml && mvn clean package -Dmaven.test.skip=true && del /a /f /q target\*.original && rd /s /Q target\classes  && rd /s /Q target\generated-sources  && rd /s /Q target\maven-status   && rd /s /Q target\maven-archiver
@echo off
@pause
