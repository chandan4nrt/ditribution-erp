nrt/
├── src/main/java/com/ecomm/nrt/
│   ├── config/             # Security, Cloud (AWS/OIDC), & CORS configs
│   ├── controller/         # REST API Endpoints (Entry points)
│   ├── dto/                # Data Transfer Objects (Request/Response shapes)
│   │   ├── request/        # e.g., CheckoutRequest.java
│   │   └── response/       # e.g., InvoiceResponse.java
│   ├── entity/             # JPA Hibernate Entities (DB Tables)
│   ├── exception/          # Custom error handling (GlobalExceptionHandler)
│   ├── repository/         # Spring Data JPA interfaces (Postgres queries)
│   ├── service/            # Business Logic (The "Flattening" logic goes here)
│   │   └── impl/           # Concrete implementations of services
│   └── util/               # Helper classes (UUID gen, Date formatters)
├── src/main/resources/
│   ├── application.properties # Postgres credentials & JPA settings
│   └── static/             # (Optional) If serving any static assets
└── pom.xml                 # Dependencies (Postgres, JPA, Lombok)

run spring boot app in antigravity - powershell
./mvnw spring-boot:run


how to run in spring sts 
1- maven install
2 - run as spring boot app