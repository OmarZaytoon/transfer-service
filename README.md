# transfer-service

## About
Transfer Service is a transactions management service, responsible about trasfering amount between two accounts including currency exhange

## Requirements
- java 21
- postgrssql db
- Docker Installed

## How To Run 
you can run the jar through the following command ```java -jar transfer-0.0.1.jar```
## How To Use
the service contains two simple api you can explore more on swagger after running the project at http://localhost:8080/swagger-ui/index.html
## How to build
### to Build Jar
you can build using the following command ```mvn clean install package```
### To Build Image 
you can use the following command ```docker build -t transfer-app:latest .```
to save the image you can use ```docker build -t transfer-app:latest .```
## How To Deploy 
- load transfer-app.tar on docker using the following command ```docker load -i transfer-app.tar```
- start up the using the following command ```docker-compose up -d```
-  expose app at 8080

## Logs
Application logs will be stored in the ./logs directory, mounted from the container.
