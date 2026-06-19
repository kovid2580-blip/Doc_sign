FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY doc_signature/ .

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java","-jar","target/doc_signature-0.0.1-SNAPSHOT.jar"]
