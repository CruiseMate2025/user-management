
# SonarScanner is typically the cleanest and most recommended way to analyze:
mvn sonar:sonar -Dsonar.host.url=http://localhost:9090 -Dsonar.token=$SONAR_TOKEN
# Default Project key 
`-Dsonar.projectKey=org.genc.app:user-mangement-service`
### To start the application from commandprompt 
`java -Dspring.profiles.active=dev -jar target\user-mangement-service-0.0.1-SNAPSHOT.jar`
## To  generate  jar  by skipping tests
`mvn clean package -DskipTests`

### curl commands  from API gateway

##### Register  new  user
```declarative
curl --location 'http://localhost:8080/api/v1/userservice/register' \
--header 'accept: */*' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6IlJPTEVfQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc2MTg5NTc1MiwiZXhwIjoxNzYxODk2NjUyLCJpc3MiOiJnZW5jX2NvaG9ydCIsImF1ZCI6WyJHZW5DIl19.aj9prZ1q60ISU1iNwvdERvOgAt67YoyGv4ICR6bMQfc' \
--header 'Content-Type: application/json' \
--data-raw '{
  "username": "gencuser2",
  "password": "dev123",
  "email": "gencuser2@cts.com",
  "firstName": "gencuser1",
  "lastName": "DECohort",
  "phone": "9867198672",
  "roleType": "ROLE_DEV"
}'
```
##### Login for existing  user
```declarative
curl --location 'http://localhost:8080/api/v1/userservice/login' \
--header 'accept: */*' \
--header 'Content-Type: application/json' \
--data '{
  "username": "admin",
  "password": "admin123"
}'
```




### Steps  to add  jwt  security  to application
Note: Root package should be in org.genc.appname Spring  security steps


Step 1: Add spring security starter pack and Jwt dependencies
##### Add  the Spring  starter packs
```declarative
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
```
##### Add  the Jwt dependencies
```declarative
<!-- Add new jjwt dependencies -->
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-api</artifactId>
			<version>0.12.3</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-impl</artifactId>
			<version>0.12.3</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-jackson</artifactId>
			<version>0.12.3</version>
			<scope>runtime</scope>
		</dependency>
```

Step 2: Add the User Role Entity,RoleType enum & RoleRepo , UserRepo

Step 3: Create config, filter ,security and util packages and place the respective (OpenAPIConfig,SecurityConfig,JWTAutheticationFilter, CustomAuthenticationEntryPoint,JwtUtil) 
files

Step 4: Add the UseDetailsImpl class CustomUserDetailsService, Authcontroller,AuthRequestDTO

Step 5: Add the CustomUserDetails & AuthResponse in DTO package

Step 6: Add the RoleService, RoleServiceImpl, RoleRequestDTO, RoleResponseDTO & custom Exceptions

Step 7: Set the seed data in startup

Step 8: Use the openssl keys in app.properties
`openssl rand -base64 32`


### Run the  below command  to  create the  project  and bind it
```declarative
mvn sonar:sonar -Dsonar.host.url=http://localhost:9090  -Dsonar.token=$SONAR_TOKEN
-Dsonar.projectKey=org.genc.app:user-mangement-service
```