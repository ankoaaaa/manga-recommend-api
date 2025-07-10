# Stage 1: ビルド環境
# MavenとJava 17を使ってプロジェクトをビルドする
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: 実行環境
# 最小限のJava実行環境に、ビルドしたjarファイルだけをコピーする
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]