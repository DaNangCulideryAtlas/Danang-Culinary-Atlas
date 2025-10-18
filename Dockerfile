# Sử dụng một image OpenJDK gọn nhẹ
FROM openjdk:17-jdk-slim

# Đặt thư mục làm việc
WORKDIR /app

# Lệnh ARG để khai báo một biến có thể được truyền vào lúc build
ARG JAR_FILE=target/*.jar

# Sao chép file .jar (đã được build sẵn từ job CI) vào container
COPY ${JAR_FILE} app.jar

# Expose port
EXPOSE 8081

# Lệnh khởi động ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]