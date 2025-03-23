# E-Commerce Order Management System

## Overview
This is a scalable microservices-based Order Management System (OMS) for an e-commerce platform. It is designed to handle order placement, inventory management, and order status updates with a focus on concurrency handling, scalability, and resilience during traffic spikes.

## Features
- **Product Management**: Add new products, retrieve product details, and list all products.
- **Inventory Management**: Track and update stock levels for products.
- **Order Management**: Place orders, update order status, and retrieve order details.
- **Scalability**: Uses message queues (AWS SQS) for asynchronous processing of orders.
- **Transaction Handling**: Ensures consistency in order and inventory updates.

## Tech Stack
- **Backend**: Java (Spring Boot)
- **Database**: PostgreSQL (or any SQL DB)
- **Messaging Queue**: AWS SQS
- **Architecture**: Microservices
- **Logging & Monitoring**: SLF4J, CloudWatch

## API Endpoints

### Product APIs
- `POST /api/v1/products` - Add a new product.
- `POST /api/v1/products/bulk` - Add multiple products in bulk.
- `GET /api/v1/products/{productId}` - Get details of a specific product.
- `GET /api/v1/products` - List all products (pagination and filters to be added).

### Inventory APIs
- `GET /api/v1/inventory/products/{productId}` - Get stock details of a product.
- `PATCH /api/v1/inventory/products/{productId}` - Update stock for a product.
- `PUT /api/v1/inventory/products/{productId}` - Add stock to a product.

### Order APIs
- `POST /api/v1/orders` - Place an order.
- `GET /api/v1/orders/{id}` - Get order details by ID.
- `GET /api/v1/orders` - List all orders (pagination and filtering to be added).
- `PATCH /api/v1/orders/{orderId}/status` - Update the status of an order.

## Setup and Installation

### Prerequisites
- Java 17+
- PostgreSQL database
- AWS account for SQS (or local SQS-compatible alternative)
- Maven (for building the project)

### Steps to Run
1. Clone the repository:
   ```sh
   git clone https://github.com/rahulgoindi15/oms-be.git
   cd oms-be
   ```
2. Configure the application properties (`src/main/resources/application.properties`) with database and AWS credentials.
3. Build the application:
   ```sh
   mvn clean install
   ```
4. Run the application:
   ```sh
   mvn spring-boot:run
   ```

## Future Enhancements
- Implement pagination, filtering, and sorting for product and order retrieval.
- Introduce caching mechanisms for frequently accessed data.
- Optimize database indexes and queries for better performance.
- Add authentication and authorization mechanisms.

