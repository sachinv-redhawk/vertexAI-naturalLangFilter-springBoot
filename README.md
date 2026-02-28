# Student Agent Project

## Project Summary

This project is a Spring Boot application that demonstrates how to use natural language processing (NLP) with Google Gemini (Generative AI) to automatically generate database filters for student records. Users can query the student database using natural language, and the system will convert these queries into structured filters using the Gemini API, enabling advanced search and sorting capabilities.

### Key Features
- **RESTful API for Students:** Provides endpoints to filter and search student records.
- **NLP-Powered Filtering:** Converts user questions into database filters using Gemini generative AI.
- **Automatic Filter Generation:** Supports both manual filter input and natural language queries.
- **H2 In-Memory Database:** Stores student data for demonstration and testing.
- **Spring Data JPA:** Handles database operations and dynamic queries.
- **Integration with Google Gemini API:** Uses REST API calls with Bearer token authentication to generate filters from user queries.

### Technologies Used
- Spring Boot (WebMVC, WebFlux, Data JPA)
- H2 Database
- Lombok
- Jackson Databind
- Google Gemini Generative AI (via REST API)

### How It Works
1. **User Query:** The user sends a natural language question about students (e.g., "Show me Computer Science students with GPA above 3.5").
2. **Gemini API Call:** The application sends the question to the Gemini API using a REST call with a Bearer token.
3. **Filter Generation:** Gemini returns a JSON filter object, which is parsed and used to query the student database.
4. **Results:** The filtered student records are returned to the user.
