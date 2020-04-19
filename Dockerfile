FROM gradle:jdk8-alpine as GRADLE_BUILDER
WORKDIR /home/gradle/src
COPY . /home/gradle/src
USER root
RUN chown -R gradle .
USER gradle
RUN gradle build --info

FROM openjdk:8
VOLUME /tmp
EXPOSE 8080
COPY /src/main/resources/application.properties ./application.properties
COPY --from=GRADLE_BUILDER /home/gradle/src/build/libs/XeroFinancialsImporter-1.0.jar /
ENTRYPOINT ["java","-Xmx512m","-jar","XeroFinancialsImporter-1.0.jar"]
