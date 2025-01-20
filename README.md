# Contents API System

## 1. Introduction
To implement a content API system. Users can like the content and view the content through the content API.

## 2. Content API System

The Content API provides the following functionalities:
- **Retrieve Content List**
- **Retrieve Content Details**
- **Search for Content**
- **Filter Content**
- **Add New Content**
- **Update Existing Content**
- **Delete Content**

## 3. Technical Requirements

The system's key technical requirements are as follows:
- **Programming Language**: Kotlin
- **Framework**: Spring
- **Database**: Relational database
- **Test Code**: Write test cases to ensure system accuracy.
- **Caching System**: Use a caching system for performance optimization.
- **Logging System**: Implement a logging system to record system events.

## 4. Interface

### 4.1 Content API

The Content API provides the following endpoints:

| Feature                   | Method | Endpoint                          | Description                                         |
|---------------------------|--------|-----------------------------------|-----------------------------------------------------|
| Retrieve Content List      | GET    | `/contents`                      | Retrieves a list of all content.                    |
| Retrieve Content Details   | GET    | `/contents/{contentId}`          | Retrieves detailed information for specific content. |
| Search for Content         | GET    | `/contents?q={query}`            | Searches for content based on the given query.      |
| Filter Content             | GET    | `/contents?category={category}`  | Filters content based on the given category.        |
| Add New Content            | POST   | `/contents`                      | Adds new content to the system.                     |
| Update Existing Content    | PATCH  | `/contents/{contentId}`          | Updates the details of specific content.            |
| Delete Content             | DELETE | `/contents/{contentId}`          | Deletes specific content from the system.           |

## 5. Constraints

The system operates under the following constraints:
- Each content item must have a unique ID.
- A user can only like a piece of content once.

## 6. Application Architecture

The application is designed with the following structure:
- **Controller Layer**: Handles HTTP requests and responses.
- **Service Layer**: Contains business logic and interacts with the repository layer.
- **Repository Layer**: Communicates with the database for data persistence.
- **Domain Model**: Represents the core entities and their relationships.
- **Utility Layer**: Provides reusable helpers such as caching, logging, and validation utilities.

## 7. How to Run Tests

To execute the test cases:
1. Navigate to the project root directory.
2. Run the following command in the terminal:
   ```bash
   ./gradlew test
   ```
3. Test results will be displayed in the terminal.

## 8. Checking Test Coverage

To check the test coverage:

1. Generate the test coverage report by running:
   ```bash
   ./gradlew jacocoTestReport
   ```
2. The coverage report can be found in the `build/reports/jacoco/test/html` directory.

## 9. Running the Server Application

To run the server application:

1. Build the project using Gradle:
   ```bash
   ./gradlew build
   ```
2. Start the application:
   ```bash
   java -jar build/libs/aroundme.jar
   ```
3. The server will start, and you can access the API at `http://localhost:8080`.