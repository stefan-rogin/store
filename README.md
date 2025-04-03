# Store web service
Demo project for evaluation. It's a simplistic representation of a store, having products with prices. The project is built with SpringBoot and it's limited to exposing a REST web service, without any UI.

## Setup

The pre-required packages for building the application from source code are JDK 17 and Maven 3.6. Build, run and test the project, using your favorite IDE. By default, the application is available at http://localhost:8080 once started. The application comes with a pre-defined set of products for integration tests and demo.

## Overview

Generally, endpoinds respond with the serialized resource being created/updated.

### Endpoints

```
GET /products                           - Lists store products, in pages
GET /products/{id}                      - Retrieve a specific product by its UUID
GET /products/search?searchTerm=...     - Searches for products by name
POST /products                          - Creates a new product
PUT /products/{id}                      - Creates or updates a new product with given UUID
PATCH /products/{id}/price              - Changes a product's price
PATCH /products/{id}/name               - Changes a product's name
DELETE /products/{id}                   - Removes a product with given ID if it exists
GET /                                   - Redirects to /products
```
### Authentication and authorization

The project creates default profiles and roles and exposes no functionality to add more.
There are two demo profiles constructed automatically:

- `user` with role *User* (password `user`)
- `admin` with roles *User* and *Administrator* (password `admin`)
 
All endpoints require authenticated sessions. `GET` endpoints require *User* role, while `POST`, `PUT`, `PATCH` and `DELETE` require *Administrator* role. Unauthenticated requests are redirected to SpringBoot's default `/login`. Passwords are stored encrypted.

Authorization is web method-based, centralized in `store.security.config.SecurityConfig`. There is a secondary `TestSecurityConfig` used by the integration tests profile.

### Tests

Controllers have integration tests that use the start-up data set for harness. Services and other classes are verified with unit tests, mocking the DB where needed. Use the regular `mvn test` for running the entire test suite.

### Error handling and logging

Errors handling is centralized in a `GlobalExceptionHandler` class that extends Spring's `ResponseEntityExceptionHandler`, as it provides functionality specialized for web services. Method `handleExceptionInternal` is overriden to log encountered errors. A catch-all Exception handler is implemented as a last resort interceptor for any unhandled runtime exceptions, that will log, then generate a `ProblemDetail` response, but without passing any sensitive information. 

Actions changing the DB are traced in a separate `audit.log` file, logging method called, user and arguments.

### Other remarks
- For supporting assignable IDs for products, UUID was preferred for this entity's identifier.
- Utf-8 is configured as charset encoding through application.properties.
- The service only accepts application/json requests (except login/logout and /)
- For a deeper dive, there are comments and class briefs in the code.

## Examples

### Login
```
curl -X POST http://localhost:8080/login -d "username=user&password=user" \
-w "\n Status: %{http_code}\n" --cookie-jar cookies.txt 

curl -X POST http://localhost:8080/login -d "username=admin&password=admin" \
 -w "\n Status: %{http_code}\n" --cookie-jar cookies.txt 
```
### User actions
- Get the default list of products.
```
curl -X GET http://localhost:8080/products -H "Content-Type: application/json" \
-w "\n Status: %{http_code}\n" --cookie cookies.txt
```
- Get products, setting page size and sort criteria.
```
curl -X GET http://localhost:8080/products?size=20\&sort=id -H "Content-Type: application/json" \
-w "\n Status: %{http_code}\n" --cookie cookies.txt
```
- Get a specific product.
```
curl -X GET http://localhost:8080/products/d0246c1d-d1a8-4801-980b-be3d1694de9b \
 -H "Content-Type: application/json" \
-w "\n Status: %{http_code}\n" --cookie cookies.txt
```
- Perform a search. Same as listing products, it implements parameterized pagination.
```
curl -X GET http://localhost:8080/products/search?searchTerm=annibale \
 -H "Content-Type: application/json" \
-w "\n Status: %{http_code}\n" --cookie cookies.txt
```
### Administrator actions
- Create a new product.
```
curl -X POST http://localhost:8080/products \
--cookie cookies.txt \
-H "Content-Type: application/json" \
-w "\n Status: %{http_code}\n" \
-d '{
  "name": "Surface Laptop",
  "price": {
    "amount": 1334.49,
    "currency": "EUR"
  }
}'
```
- Update or insert (upsert) a product for a specified Id. The operation is idempotent.
```
curl -X PUT http://localhost:8080/products/e4ec0272-2b9f-4b51-98e6-b337632fcfee \
-H "Content-Type: application/json" --cookie cookies.txt \
-w "\n Status: %{http_code}\n" \
-d '{
  "name": "Tomato",
  "price": {
    "amount": 0.89,
    "currency": "EUR"
  }
}'
```
- Change a product's price.
```
curl -X PATCH http://localhost:8080/products/d0246c1d-d1a8-4801-980b-be3d1694de9b/price \
-H "Content-Type: application/json" --cookie cookies.txt \
-w "\n Status: %{http_code}\n" \
-d '{
  "amount": 8.49,
  "currency": "EUR"
}'
```
- Change a product's name.
```
curl -X PATCH http://localhost:8080/products/d0246c1d-d1a8-4801-980b-be3d1694de9b/name \
-H "Content-Type: application/json" --cookie cookies.txt \
-w "\n Status: %{http_code}\n" \
-d '{
  "name": "Premium Cat Food",
  "currency": "EUR"
}'
```
- Delete a product
```
curl -X DELETE http://localhost:8080/products/2c5cebfc-b2a9-4220-8b51-32b095f5876f \
 -H "Content-Type: application/json" \
-w "\n Status: %{http_code}\n" --cookie cookies.txt
```