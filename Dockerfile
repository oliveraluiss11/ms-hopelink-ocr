# Usa una imagen base de Java 17 de Corretto
FROM amazoncorretto:21-alpine

# Copia el archivo JAR de la aplicación al contenedor
COPY target/ms-ocr-0.0.1-SNAPSHOT.jar /app/ms-ocr.jar

# Establece el directorio de trabajo en /app
WORKDIR /app

# Ejecuta la aplicación al iniciar el contenedor
CMD ["java", "-jar", "ms-ocr.jar"]