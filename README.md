# FullStack Blogging Application

A simple blogging application built with Spring Boot, featuring user authentication and post management. Includes Docker, Kubernetes, and CI/CD pipeline setup.

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.3.2, Spring Security
- **Frontend**: Thymeleaf, Bootstrap 5
- **Database**: H2 (embedded)
- **DevOps**: Docker, Kubernetes, Terraform, Jenkins

## Features

- User registration and login
- Create and view posts
- Responsive UI
- Cloud-ready deployment

## Quick Start

### Run Locally

```bash
# Build and run
./mvnw spring-boot:run

# Access at http://localhost:8080
```

### Run with Docker

```bash
# Build
./mvnw clean package -DskipTests
docker build -t bloggingapp:latest .

# Run
docker run -d -p 8080:8080 bloggingapp:latest
```

### Deploy to Kubernetes

```bash
# Deploy
kubectl apply -f deployment-service.yml

# Check status
kubectl get pods
kubectl get svc
```

## Project Structure

```
├── src/main/java/com/example/twitterapp/
│   ├── controller/     # API endpoints
│   ├── model/          # User & Post entities
│   ├── service/        # Business logic
│   ├── repository/     # Data access
│   └── config/         # Security config
├── src/main/resources/templates/   # HTML views
├── EKS_Terraform/      # AWS EKS infrastructure
├── Dockerfile          # Container config
├── deployment-service.yml  # K8s manifests
├── jen.groovy          # Jenkins pipeline
└── pom.xml             # Maven config
```

## Endpoints

| Endpoint | Description |
|----------|-------------|
| `/login` | Login page |
| `/register` | Registration page |
| `/` | Home - view all posts |
| `/add` | Create new post |

## CI/CD Pipeline

The Jenkins pipeline (`jen.groovy`) handles:
- Build & Test
- Code analysis (SonarQube)
- Security scanning (Trivy)
- Docker build & push
- Kubernetes deployment

## AWS Infrastructure

Create EKS cluster using Terraform:

```bash
cd EKS_Terraform
terraform init
terraform apply
```

## License

MIT
