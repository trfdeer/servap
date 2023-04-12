FROM maven:3-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -B -f pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:resolve
COPY src src
RUN mvn -B -s /usr/share/maven/ref/settings-docker.xml clean package

FROM jetty:11-jre17-alpine-eclipse-temurin AS runner
COPY --from=builder /app/target/servap.war /var/lib/jetty/webapps/ROOT.war
COPY .env /var/lib/jetty/.env
EXPOSE 8080
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["java","-jar","/usr/local/jetty/start.jar"]