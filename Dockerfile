# --- ETAPA 1: Compilación (Build) ---
# Usamos Eclipse Temurin con Maven para compilar
FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos pom y descargamos dependencias para aprovechar el caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos fuentes y compilamos
COPY src ./src
RUN mvn clean package -DskipTests

# --- ETAPA 2: Ejecución (Runtime) ---
# Usamos el JRE de Eclipse Temurin sobre Alpine Linux (muy ligero)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiamos el .jar desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Ejecutamos la app
ENTRYPOINT ["java", "-jar", "app.jar"]