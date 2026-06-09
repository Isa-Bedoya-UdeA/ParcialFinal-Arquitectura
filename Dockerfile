# ============================================================
# Stage 1: Build del JAR con Maven
# ============================================================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos primero solo el pom.xml para cachear la descarga
# de dependencias. Mientras el pom no cambie, esta capa se
# reutiliza en builds sucesivos.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Ahora sí copiamos el código fuente y compilamos.
COPY src ./src
RUN mvn clean package -DskipTests -Dmaven.compiler.release=17

# ============================================================
# Stage 2: Imagen final, solo JRE (más liviana que el JDK)
# ============================================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# El nombre del JAR lo define el artifactId + version del pom.xml.
# artifactId=parcialFinal, version=0.0.1-SNAPSHOT
COPY --from=build /app/target/parcialFinal-0.0.1-SNAPSHOT.jar parcialFinal-0.0.1-SNAPSHOT.jar

# Puerto por defecto de Spring Boot (no hay server.port en application.properties)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "parcialFinal-0.0.1-SNAPSHOT.jar"]
