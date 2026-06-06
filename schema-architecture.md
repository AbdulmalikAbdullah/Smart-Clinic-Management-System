# Smart Clinic Management System Architecture

## Architecture Summary

The Smart Clinic Management System is built using Spring Boot and follows a three-tier architecture consisting of the presentation layer, application layer, and data layer.

The application uses both MVC and REST architectures. Thymeleaf templates are used to render the Admin and Doctor dashboards, while REST APIs provide JSON responses for modules such as Appointments, Patient Dashboard, and Patient Records. All requests are processed through controllers and routed to a shared service layer where business logic is implemented.

The application uses two databases. MySQL stores structured data such as Patients, Doctors, Appointments, and Admin information using JPA entities. MongoDB stores prescription data using document-based models. The service layer communicates with the appropriate repository depending on the type of data being accessed.

---

## Numbered Flow of Data and Control

1. Users access the system through Thymeleaf dashboards (AdminDashboard and DoctorDashboard) or REST API modules such as Appointments, PatientDashboard, and PatientRecord.

2. Requests are routed to either Thymeleaf Controllers for server-rendered pages or REST Controllers for JSON-based API responses.

3. Controllers delegate all business operations to the Service Layer, which contains the application's business logic and validation rules.

4. The Service Layer communicates with the appropriate Repository Layer to retrieve or store data.

5. MySQL Repositories access the MySQL database for relational data, while the MongoDB Repository accesses the MongoDB database for prescription documents.

6. Data retrieved from the databases is mapped into Java models. MySQL records are mapped to JPA entities, while MongoDB records are mapped to document models.

7. The models are returned to the presentation layer. Thymeleaf controllers render HTML views, while REST controllers return JSON responses to API clients.