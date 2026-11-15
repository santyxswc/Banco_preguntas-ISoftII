# syntax=docker/dockerfile:1
# Imagen de los microservicios del Banco de Preguntas (RNF #3).
# Un solo Dockerfile sirve para los siete servicios: la etapa de compilación es común y la
# etapa final copia el jar del servicio indicado en SERVICE.
#
#   docker build --build-arg SERVICE=question-service -t bp-question-service .
#
# Con Docker Compose no hace falta invocarlo a mano: `docker compose up -d --build`.

# ---------- Etapa 1: compilación (Maven + JDK 21) ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Primero los pom.xml: mientras no cambien, la descarga de dependencias queda en la caché de capas
COPY pom.xml .
COPY shared-contracts/pom.xml shared-contracts/
COPY user-service/pom.xml user-service/
COPY question-service/pom.xml question-service/
COPY validation-service/pom.xml validation-service/
COPY lifecycle-service/pom.xml lifecycle-service/
COPY review-service/pom.xml review-service/
COPY notification-service/pom.xml notification-service/
COPY api-gateway/pom.xml api-gateway/
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -DskipTests dependency:go-offline || true

# Código fuente de todos los módulos y empaquetado (las pruebas corren en el CI, no en la imagen)
COPY shared-contracts/src shared-contracts/src
COPY user-service/src user-service/src
COPY question-service/src question-service/src
COPY validation-service/src validation-service/src
COPY lifecycle-service/src lifecycle-service/src
COPY review-service/src review-service/src
COPY notification-service/src notification-service/src
COPY api-gateway/src api-gateway/src
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -DskipTests package \
    && mkdir /jars \
    && for s in user-service question-service validation-service lifecycle-service review-service notification-service api-gateway; do \
         cp "$s/target/$s-1.0.0-SNAPSHOT.jar" "/jars/$s.jar"; \
       done

# ---------- Etapa 2: ejecución (solo JRE, usuario sin privilegios) ----------
FROM eclipse-temurin:21-jre-alpine AS runtime
ARG SERVICE
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /jars/${SERVICE}.jar app.jar
USER app
# Límite de memoria relativo al del contenedor
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC"
ENTRYPOINT ["java", "-jar", "app.jar"]
