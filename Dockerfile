FROM amazoncorretto:11-alpine-jdk
COPY target/konum-1.0-SNAPSHOT.jar konum.jar
ENTRYPOINT ["java","-jar","/konum.jar"]