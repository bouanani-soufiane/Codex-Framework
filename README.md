# Codex Framework

Codex is a lightweight Java framework that provides dependency injection, custom ORM, data mapping, and automated database table creation to facilitate the development of scalable and maintainable applications.

## Features

- **Dependency Injection**: Streamline object creation and dependency management.
- **Custom ORM**: Simplify database interactions with an intuitive ORM.
- **Data Mapping**: Effortlessly map database tables to Java objects.
- **Automated Table Creation**: Automatically generate database tables from your Java classes.

## Getting Started

### Prerequisites

- Java 8 or higher
- Maven

### Installation

1. Clone the repository:

   ```bash
   git clone git@github.com:bouanani-soufiane/Codex-Framework.git
   cd codex
   ```


## Usage

### Dependency Injection

Codex uses [Burningwave](https://github.com/burningwave/core) for classpath scanning to enable dependency injection.

### Example

```java
// soon..
```

### Resources that helps me

- This [article](https://dev.to/jjbrt/how-to-create-your-own-dependency-injection-framework-in-java-4eaj) was very helpful in the development of Codex dependency injection.

## How to Make EntityManager

### 1. Start with Schema Generation:
- **Step 1.1:** Implement the `SchemaGenerator` to create tables without any constraints initially.
- **Step 1.2:** Use the `ConstraintManager` to add foreign keys and other constraints after the tables have been created.

### 2. Develop Query Execution:
- **Step 2.1:** Implement the `CRUDHandler` to handle basic Create, Read, Update, and Delete operations.
- **Step 2.2:** Extend functionality by developing the `QueryExecutor` to support custom queries and more complex database interactions.

### 3. Build Entity Mapping:
- **Step 3.1:** Create the `EntityMapper` to map database rows to entity objects.
- **Step 3.2:** Implement the `RowMapper` to handle the conversion between entities and their corresponding database rows.

### 4. Integrate Transaction Management:
- **Step 4.1:** Develop the `TransactionManager` to ensure that all database operations are atomic, consistent, and properly isolated.
- **Step 4.2:** Implement mechanisms to handle transaction rollbacks in case of errors or failures.

### 5. Handle Errors Gracefully:
- **Step 5.1:** Implement robust error-handling mechanisms to manage exceptions and ensure the system remains stable.
- **Step 5.2:** Ensure that any foreign key constraint violations and other common database errors are caught and managed appropriately.

---

## Contributing

We welcome contributions to this project. Please follow these steps:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature-name`).
3. Commit your changes (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/your-feature-name`).
5. Open a pull request.

Please ensure your code follows our coding standards and includes tests for new features.

---

By Bouanani Soufiane