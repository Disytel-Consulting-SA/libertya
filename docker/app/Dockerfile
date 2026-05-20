# Imagen de aplicación Libertya
#
# Prerequisito: el ZIP del release debe estar disponible en el contexto de build.
# Ejemplo de build:
#   docker build --build-arg ZIP_FILE=ServidorOXP_V26.05.zip -t libertya/libertya-app:26.05 .
#
# El ZIP debe descomprimirse con un directorio raíz llamado "ServidorOXP".

FROM ubuntu:22.04

ARG ZIP_FILE=ServidorOXP.zip

# Java 8 (mismo que producción; JBoss de esta era no es compatible con Java 11+)
RUN apt-get update && apt-get install -y --no-install-recommends \
        openjdk-8-jdk-headless \
        unzip \
        curl \
        netcat-openbsd \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
ENV OXP_HOME=/ServidorOXP
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Copiar e instalar el ZIP del release
COPY ${ZIP_FILE} /tmp/libertya.zip
RUN unzip -q /tmp/libertya.zip -d / \
    && rm /tmp/libertya.zip \
    && find ${OXP_HOME} -name "*.sh" -exec chmod +x {} \;

# Copiar entrypoint
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Puerto web (HTTP), SSL y JNP/RMI
EXPOSE 8080 8443 1099

ENTRYPOINT ["/docker-entrypoint.sh"]
