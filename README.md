# screenshot-web
## Build Command
```mvn clean package```
## Run Command
```java -jar servlet-java8-0.0.1-SNAPSHOT.jar```
### Port
```java -jar servlet-java8-0.0.1-SNAPSHOT.jar port=8081```
## URL
Screenshot
```http://127.0.0:8080/?title=windowTitle```
### Image Format
```http://127.0.0:8080/?title=windowTitle&format=jpeg```
### Image Dimension
```http://127.0.0:8080/?title=windowTitle&w=100&h=200```
### Image Dimension and offset x
```http://127.0.0:8080/?title=windowTitle&w=100&h=200&x=10```
### Image Dimension and offset x and offset y
```http://127.0.0:8080/?title=windowTitle&w=100&h=200&x=10&y=20```
