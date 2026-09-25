FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN javac MorseCodeServer.java

EXPOSE 10000

CMD ["java", "MorseCodeServer"]
