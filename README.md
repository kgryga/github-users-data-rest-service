# github-users-data-rest-service

This project contains REST service for getting some of GitHub user data. It also stores request API count for the particular user in the database.  

## Requirements
- Java 17
- Gradle 
- MongoDB

### Database
This service requires MongoDB database that is locally running with the following credentials:

_username_: _mongo_ <br/>
_password_: _pass_ <br/>

### MongoDB from Docker
To create and run MongoDB from Docker, use the following commands:
1. Pull down the pre-built mongo image from the Docker repository:
```
docker pull mongo
```
2. Start a Docker container:
```
docker run
-d
--name mongo-db
-p 27017:27017
-e MONGO_INITDB_ROOT_USERNAME=mongo
-e MONGO_INITDB_ROOT_PASSWORD=pass
mongo
```

## Startup
Start the MongoDb database and then use the following command from terminal in the project's directory: 
```
./gradlew bootRun
```     
to run the application.

## Usage

Application is running on the localhost:8080 so getting user data is possible using the following template of curl request:

```
curl -X GET -ki http://localhost:8080/users/{login} 
```
where _login_ is GitHub user login.

### Responses
Possible API responses:
- **200** OK with body:
```
{
    "id": "...",
    "login": "...",
    "name": "…",
    "type": "...",
    "avatarUrl": „”,
    "createdAt": "..."
    "calculations": "..."
}
```
where _calculations_ is float value equals to:
```
6 / user_git_hub_followers_number * (2 + user_git_hub_public_repos_number)
```
- **404** NOT FOUND when no users found with requested login 
- **500** INTERNAL SERVER ERROR when unexpected error occurred
- **502** GATEWAY ERROR when GitHub API Client doesn't respond

## TODO
- add Swagger API documentation
- create docker-compose
