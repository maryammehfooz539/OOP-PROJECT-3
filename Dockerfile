# Dockerfile: tells a hosting service (Render, Railway, Fly.io ...) how to build and start Gemora.
# It compiles the Java files and starts Main, which starts the website on the port the host gives us (PORT).
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . .
RUN mkdir -p out && javac -encoding UTF-8 -cp "lib/*" -d out $(find src -name "*.java")
EXPOSE 8080
CMD ["java", "-Djava.awt.headless=true", "-cp", "out:lib/*", "com.jewellery.Main"]
