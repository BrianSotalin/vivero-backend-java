# --- ETAPA 1: Compilación (Build) ---
# Usamos una imagen de Maven con Java 17 para compilar el código
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copiamos el archivo de configuración de Maven (pom.xml) y descargamos dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente y generamos el archivo .jar (saltamos los tests para ir más rápido)
COPY src ./src
RUN mvn clean package -DskipTests

# --- ETAPA 2: Ejecución (Runtime) ---
# Usamos una imagen mucho más pequeña que solo tiene el JRE de Java 17
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copiamos solo el archivo .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto en el que corre tu app de Spring
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
