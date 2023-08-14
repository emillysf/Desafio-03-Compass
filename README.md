# Desafio-03-Compass

This application provides a solution for searching and managing posts sourced from an external API. Leveraging an asynchronous approach, the application fetches posts and enhances them with detailed comment information. Furthermore, it meticulously maintains an accurate log of processing updates, allowing users to track the evolution of posts over time.

## Prerequisites

To run this application, you need to have Intellij IDEA installed. This IDE provides the necessary environment for seamless execution and development of the application.

## Configuration and Execution

- The application runs on port 8080.
- It employs an embedded H2 database for storage.
- The Spring configuration, `spring.jpa.hibernate.ddl-auto`, is set to "create-drop," facilitating an agile database schema approach.
  

## External Data Source

The external data source is available at: [https://jsonplaceholder.typicode.com](https://jsonplaceholder.typicode.com/). The posts fetched from this source serve as the foundation for the enrichment and management process.

## Post States

Posts can assume various states, each reflecting a specific stage of the process. These states are pivotal for tracking the trajectory and evolution of each post.

## API - Functionality

The application offers a comprehensive set of functionalities through a user-friendly API:

1. **Process Post**: Executes processing for a new post. Endpoint: `POST /posts/{postId}`.

2. **Deactivate Post**: Allows deactivation of an active post. Endpoint: `DELETE /posts/{postId}`.

3. **Reprocess Post**: Re-processes an active or deactivated post. Endpoint: `PUT /posts/{postId}`.

4. **Query Posts**: Provides a list of posts, including state history and enriched data. Endpoint: `GET /posts`.

