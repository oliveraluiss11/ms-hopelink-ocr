FROM ubuntu:latest

# Actualizar índices de paquetes e instalar tesseract-ocr, Java y dependencias
RUN apt-get update && \
    apt-get install -y tesseract-ocr openjdk-21-jdk

# Crear directorio de trabajo
WORKDIR /app

# Copia el JAR de la aplicación
COPY target/ms-ocr-*.jar /app/ms-ocr-app.jar

# Copiar ruta de ficheros de Tesseract
COPY ./src/main/resources/tessdata /tessdata

# Otorgar permisos a la carpeta de Tesseract
RUN chmod -R 755 /tessdata

# Ejecutar la aplicación
CMD ["java", "-jar", "/app/ms-ocr-app.jar"]
