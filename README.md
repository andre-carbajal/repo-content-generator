# Repository Content Generator

A Spring Boot web application that fetches and concatenates repository content from GitHub. This tool helps developers
quickly view and aggregate content from multiple files in a GitHub repository, making it easier to analyze codebases and
generate documentation.

## Features

- Web-based interface for entering GitHub repository URLs
- Support for recursive directory traversal

## Requirements

- Java 21 or higher
- Maven 3.6+
- GitHub Personal Access Token

## Dependencies

Key dependencies include:

- Spring Boot 3.4.5
- Jackson (for JSON processing)

## Configuration

The application uses YAML configuration. Key settings:

```yaml
github:
  token: ${GITHUB_TOKEN}  # Set via environment variable
```

## Getting Started

1. Set up your GitHub token:
   ```bash
   export GITHUB_TOKEN=your_github_personal_access_token
   ```

2. Start the application:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Access the web interface at `http://localhost:8080`

## How It Works

The application follows this process:

1. User submits a GitHub repository URL
2. Backend extracts owner and repository name
3. Application recursively fetches repository contents using GitHub API
4. Files matching include patterns (and not matching exclude patterns) are processed
5. Content is concatenated and returned to the frontend
6. Results are displayed in a copyable text area

## Security Considerations

- GitHub token is required and should be kept secure
- Token permissions should be limited to repository read access

## Rate Limiting

The application is subject to GitHub API rate limits:

- Without authentication: 60 requests per hour per IP address
- With authentication (using a token): 5,000 requests per hour per user

If you exceed these limits, you'll receive a 403 Forbidden error like this:

```
Error generating readme: 403 Forbidden: "{"message":"API rate limit exceeded... Check out the documentation for more details."}"
```

To avoid rate limiting issues:

- Always use an authenticated token
- Monitor your API usage through GitHub's API
- Consider implementing request caching for frequently accessed repositories
- For large repositories, plan your requests carefully to stay within limits

You can check your current rate limit status by calling:
`https://api.github.com/rate_limit`

## Running with Docker

You can easily run this application in a containerized environment using Docker and Docker Compose. This setup ensures
consistent builds and isolates dependencies, making it straightforward to deploy or test locally.

### Requirements

- Docker (latest version recommended)
- Docker Compose (v2 or higher)
- Java 21+ and Maven are not required on your host when using Docker

### Environment Variables

- **GITHUB_TOKEN**: A GitHub Personal Access Token is required for the application to access the GitHub API. Set this
  variable in your environment or in a `.env` file at the project root.

### Build and Run Instructions

1. **Build the Docker image**:
   ```bash
   docker build -t repo-content-generator:latest .
   ```
2. **Run the application**:
   ```bash
   docker run -d --name repo-content-generator -p 8083:8080 -e GITHUB_TOKEN=<your_github_personal_access_token> repo-content-generator:latest
    ```
   Optionally, you can create a `.env` file in the project root with the following content:
   ```env
    GITHUB_TOKEN=<your_github_personal_access_token>
    ```
   Then run:
    ```bash
   docker run -d --name repo-content-generator -p 8083:8080 --env-file .env repo-content-generator:latest
   ```
3. **Access the application**:
4. Open your web browser and navigate to `http://localhost:8083` to access the application.

## System Instructions

These are the system instructions I am using in my Claude Project:

```text
You are an expert technical writer specializing in writing documentation for software projects. You are tasked with writing a new README file for the given project.
Your goal is to create an informative documentation for software engineers who visit the following repository.

First, here's the name of the repository:
<name>
{NAME}
</name>

To give you context here is all of the current documentation and source code for the project
<src>
{SRC}
</src>

When writing the README, follow these guidelines:

1. Structure:
    - Begin with an attention-grabbing introduction
    - Include the following sections but don't limit yourself to just these
        - Project Requirements
        - Dependencies
        - Getting Started
            - In the 'Getting Started' section, you don't need to include instructions on how to clone the repo; these instructions are already provided elsewhere.
        - How to run the application
        - Relevant Code Examples
    - End with a conclusion that summarizes key points and encourages reader engagement

2. Tone and Style:
    - Write in a friendly, natural, and educational tone
    - Use clear, concise language
    - Incorporate relevant examples and analogies to explain complex concepts
    - Use lists when appropriate but don't overuse them 

3. Text Formatting:
    - The output of this document will be Markdown
    - Use headers (H1 for title, H2 for main sections, H3 for subsections)
    - Keep paragraphs short (3-5 sentences)
    - Proofread for grammar, spelling, and clarity
    - Avoid using any of the following words if possible {WORD_EXLUDE_LIST}

4. Code Formatting:
    - Use clean and concise code examples
    - Avoid including import statements or package declarations for brevity
    - Use consistent indentation (e.g., prefer spaces and tabs)
    - Break long lines of code for readability
    - If showing output, clearly separate it from the code
    - Include a brief explanation before and/or after each code block

5. Output:
    - Return the final README directly as Markdown text only.
    - Do not wrap the content with code fences like ```markdown or ``` at the beginning or end.
    - Only output the Markdown content, without any explanation or extra text.

Remember to tailor the content towards an audience of software developers.
```