# 🌿 Vivero La Vega — Backend
 
API REST para la gestión de un vivero. Desarrollada con **Spring Boot 4** y **Spring Security** con autenticación JWT.
 
---
 
## 🚀 Tecnologías
 
| Tecnología | Versión | Uso |
|---|---|---|
| Spring Boot | 4.0.6 | Framework principal |
| Spring Security | 6.x | Autenticación y autorización |
| Spring Data JPA | 4.x | Persistencia |
| PostgreSQL | 15+ | Base de datos |
| JWT (jjwt) | 0.11.5 | Tokens de autenticación |
| Lombok | latest | Reducción de boilerplate |
| Maven | 3.8+ | Gestión de dependencias |
 
---
 
## 📋 Requisitos previos
 
- Java 17+
- Maven 3.8+
- PostgreSQL 15+
---
 
## ⚙️ Configuración
 
Crea un archivo `src/main/resources/application-dev.properties` con tus datos:
 
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vivero_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
 
El `application.properties` principal usa variables de entorno para producción:
 
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:4200}
```
 
---
 
## ▶️ Ejecución en desarrollo
 
```bash
# Con Maven Wrapper
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
 
# O compilar y ejecutar
./mvnw clean package -DskipTests
java -jar target/vivero-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```
 
La API estará disponible en `http://localhost:8080`.
 
---
 
## 🐳 Docker
 
```bash
# Build de la imagen
docker build -t vivero-api .
 
# Correr con variables de entorno
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/vivero_db \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=password \
  -e CORS_ALLOWED_ORIGINS=https://tu-dominio.com \
  vivero-api
```
 
O usando Docker Compose desde la raíz del proyecto:
 
```bash
docker-compose up -d --build
```
 
---
 
## 📁 Estructura del proyecto
 
```
src/main/java/com/vivero/vivero_backend/
├── api/
│   ├── config/          # JWT filter, Security config
│   ├── controller/      # REST controllers
│   ├── dto/             # Data Transfer Objects
│   ├── model/           # Entidades JPA
│   ├── repository/      # Spring Data repositories
│   └── service/         # Lógica de negocio
└── ViveroBackendApplication.java
```
 
---
 
## 🔗 Endpoints principales
 
### Autenticación
| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| POST | `/api/auth/login` | Login y obtención de JWT | No |
| POST | `/api/auth/register` | Registrar usuario | JWT |
 
### Ventas
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/ventas` | Listar ventas |
| GET | `/api/ventas/:id` | Obtener venta por ID |
| POST | `/api/ventas` | Crear venta |
| PATCH | `/api/ventas/:id` | Actualizar venta |
| DELETE | `/api/ventas/:id` | Eliminar venta |
 
### Productos
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/productos` | Listar productos |
| GET | `/api/productos/:id` | Obtener producto por ID |
| POST | `/api/productos` | Crear producto |
| PATCH | `/api/productos/:id` | Actualizar producto |
| DELETE | `/api/productos/:id` | Eliminar producto |
 
### Clientes
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/clientes` | Listar clientes |
| GET | `/api/clientes/:id` | Obtener cliente por ID |
| POST | `/api/clientes` | Crear cliente |
| PATCH | `/api/clientes/:id` | Actualizar cliente |
| DELETE | `/api/clientes/:id` | Eliminar cliente |
 
### Usuarios
| Método | Ruta | Descripción | Roles |
|---|---|---|---|
| GET | `/api/usuarios` | Listar usuarios | ADMIN |
| GET | `/api/usuarios/:id` | Obtener usuario | ADMIN |
| POST | `/api/usuarios` | Crear usuario | ADMIN |
| PATCH | `/api/usuarios/:id` | Actualizar rol/password | ADMIN |
| DELETE | `/api/usuarios/:id` | Eliminar usuario | ADMIN |
 
### Estadísticas
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/estadisticas/resumen` | Resumen general |
| GET | `/api/estadisticas/ventas-por-mes` | Ventas agrupadas por mes |
| GET | `/api/estadisticas/top-productos` | Top 5 productos más vendidos |
 
---
 
## 🔐 Seguridad
 
La autenticación usa **JWT Bearer tokens**. El token incluye el claim `rol` para el control de acceso por roles.
 
### Permisos por endpoint
 
| Endpoints | Roles permitidos |
|---|---|
| `/api/ventas/**` | ADMIN, USER, EMPLOYEE |
| `/api/productos/**` | ADMIN, USER |
| `/api/clientes/**` | ADMIN, USER |
| `/api/estadisticas/**` | ADMIN, USER |
| `/api/usuarios/**` | ADMIN |
| `/api/auth/login` | Público |
| `/api/auth/register` | ADMIN |
 
### Reglas de contraseña
 
Las contraseñas deben cumplir:
- Mínimo 8 caracteres
- Al menos una letra mayúscula
- Al menos una letra minúscula
- Al menos un carácter especial (`!@#$%^&*...`)
---
 
## 🧪 Tests
 
```bash
# Correr todos los tests
./mvnw test
 
# Test específico
./mvnw test -Dtest=PasswordValidationTest
./mvnw test -Dtest=VentaServiceTest
```
 
Los tests usan H2 en memoria. Crea `src/test/resources/application.properties`:
 
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
cors.allowed-origins=http://localhost:4200
```
 
---
 
## 🌐 Deploy en VPS (Hostinger)
 
La API está desplegada en `https://devxsota.cloud` con:
 
- Docker Compose para orquestación
- PostgreSQL en contenedor
- Nginx como reverse proxy
- SSL via Certbot
Para actualizar en producción:
 
```bash
# En el VPS
cd ~/vivero
git -C vivero pull
docker-compose up -d --build
```
 
