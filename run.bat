@ECHO OFF

gradlew assemble
docker-compose up -d
gradlew bootRun
pause