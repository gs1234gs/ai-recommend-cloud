# syntax=docker/dockerfile:1.6
# ====================== 阶段1：构建编译阶段 ======================
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

# ---------- 1. 复制所有 pom.xml，最大化利用 Docker 构建缓存 ----------
# 根 pom
COPY pom.xml .

# common-utils-cloud (公共工具模块)
COPY common-utils-cloud/pom.xml ./common-utils-cloud/
COPY common-utils-cloud/aliyun-oss-spring-boot-autoconfigure/pom.xml ./common-utils-cloud/aliyun-oss-spring-boot-autoconfigure/
COPY common-utils-cloud/aliyun-oss-spring-boot-starter/pom.xml ./common-utils-cloud/aliyun-oss-spring-boot-starter/
COPY common-utils-cloud/common-utils-spring-boot-autoconfigure/pom.xml ./common-utils-cloud/common-utils-spring-boot-autoconfigure/
COPY common-utils-cloud/common-utils-spring-boot-starter/pom.xml ./common-utils-cloud/common-utils-spring-boot-starter/
COPY common-utils-cloud/db-table-spring-boot-autoconfigure/pom.xml ./common-utils-cloud/db-table-spring-boot-autoconfigure/
COPY common-utils-cloud/db-table-spring-boot-starter/pom.xml ./common-utils-cloud/db-table-spring-boot-starter/
COPY common-utils-cloud/gorse-sdk-spring-boot-autoconfigure/pom.xml ./common-utils-cloud/gorse-sdk-spring-boot-autoconfigure/
COPY common-utils-cloud/gorse-sdk-spring-boot-starter/pom.xml ./common-utils-cloud/gorse-sdk-spring-boot-starter/
COPY common-utils-cloud/microservice-security-spring-boot-autoconfigure/pom.xml ./common-utils-cloud/microservice-security-spring-boot-autoconfigure/
COPY common-utils-cloud/microservice-security-spring-boot-starter/pom.xml ./common-utils-cloud/microservice-security-spring-boot-starter/
COPY common-utils-cloud/public-pojo-spring-boot-autoconfigure/pom.xml ./common-utils-cloud/public-pojo-spring-boot-autoconfigure/
COPY common-utils-cloud/public-pojo-spring-boot-starter/pom.xml ./common-utils-cloud/public-pojo-spring-boot-starter/
COPY common-utils-cloud/redis-utils-spring-boot-autoconfigure/pom.xml ./common-utils-cloud/redis-utils-spring-boot-autoconfigure/
COPY common-utils-cloud/redis-utils-spring-boot-starter/pom.xml ./common-utils-cloud/redis-utils-spring-boot-starter/
COPY common-utils-cloud/web-client-spring-boot-autoconfigure/pom.xml ./common-utils-cloud/web-client-spring-boot-autoconfigure/
COPY common-utils-cloud/web-client-spring-boot-starter/pom.xml ./common-utils-cloud/web-client-spring-boot-starter/

# gsy-ai-cloud (AI 服务)
COPY gsy-ai-cloud/pom.xml ./gsy-ai-cloud/
COPY gsy-ai-cloud/ai-app/pom.xml ./gsy-ai-cloud/ai-app/
COPY gsy-ai-cloud/ai-common-util/pom.xml ./gsy-ai-cloud/ai-common-util/
COPY gsy-ai-cloud/ai-pojo/pom.xml ./gsy-ai-cloud/ai-pojo/
COPY gsy-ai-cloud/ai-server/pom.xml ./gsy-ai-cloud/ai-server/

# gsy-behavior-cloud (行为服务)
COPY gsy-behavior-cloud/pom.xml ./gsy-behavior-cloud/
COPY gsy-behavior-cloud/behavior-app/pom.xml ./gsy-behavior-cloud/behavior-app/
COPY gsy-behavior-cloud/behavior-common-utils/pom.xml ./gsy-behavior-cloud/behavior-common-utils/
COPY gsy-behavior-cloud/behavior-pojo/pom.xml ./gsy-behavior-cloud/behavior-pojo/
COPY gsy-behavior-cloud/behavior-server/pom.xml ./gsy-behavior-cloud/behavior-server/

# gsy-gateway-cloud (API 网关)
COPY gsy-gateway-cloud/pom.xml ./gsy-gateway-cloud/
COPY gsy-gateway-cloud/gateway-server-app/pom.xml ./gsy-gateway-cloud/gateway-server-app/

# gsy-general-api-cloud (通用 API 模块)
COPY gsy-general-api-cloud/pom.xml ./gsy-general-api-cloud/
COPY gsy-general-api-cloud/ai-server-api/pom.xml ./gsy-general-api-cloud/ai-server-api/
COPY gsy-general-api-cloud/behavior-server-api/pom.xml ./gsy-general-api-cloud/behavior-server-api/
COPY gsy-general-api-cloud/goods-server-api/pom.xml ./gsy-general-api-cloud/goods-server-api/
COPY gsy-general-api-cloud/order-server-api/pom.xml ./gsy-general-api-cloud/order-server-api/
COPY gsy-general-api-cloud/recommend-server-api/pom.xml ./gsy-general-api-cloud/recommend-server-api/
COPY gsy-general-api-cloud/review-server-api/pom.xml ./gsy-general-api-cloud/review-server-api/
COPY gsy-general-api-cloud/statistical-server-api/pom.xml ./gsy-general-api-cloud/statistical-server-api/
COPY gsy-general-api-cloud/sys-server-api/pom.xml ./gsy-general-api-cloud/sys-server-api/
COPY gsy-general-api-cloud/upload-server-api/pom.xml ./gsy-general-api-cloud/upload-server-api/

# gsy-goods-cloud (商品服务)
COPY gsy-goods-cloud/pom.xml ./gsy-goods-cloud/
COPY gsy-goods-cloud/goods-app/pom.xml ./gsy-goods-cloud/goods-app/
COPY gsy-goods-cloud/goods-common-utils/pom.xml ./gsy-goods-cloud/goods-common-utils/
COPY gsy-goods-cloud/goods-pojo/pom.xml ./gsy-goods-cloud/goods-pojo/
COPY gsy-goods-cloud/goods-server/pom.xml ./gsy-goods-cloud/goods-server/

# gsy-order-cloud (订单服务)
COPY gsy-order-cloud/pom.xml ./gsy-order-cloud/
COPY gsy-order-cloud/order-app/pom.xml ./gsy-order-cloud/order-app/
COPY gsy-order-cloud/order-common-utils/pom.xml ./gsy-order-cloud/order-common-utils/
COPY gsy-order-cloud/order-pojo/pom.xml ./gsy-order-cloud/order-pojo/
COPY gsy-order-cloud/order-server/pom.xml ./gsy-order-cloud/order-server/

# gsy-review-cloud (评论服务)
COPY gsy-review-cloud/pom.xml ./gsy-review-cloud/
COPY gsy-review-cloud/review-app/pom.xml ./gsy-review-cloud/review-app/
COPY gsy-review-cloud/review-common-utils/pom.xml ./gsy-review-cloud/review-common-utils/
COPY gsy-review-cloud/review-pojo/pom.xml ./gsy-review-cloud/review-pojo/
COPY gsy-review-cloud/review-server/pom.xml ./gsy-review-cloud/review-server/

# gsy-system-cloud (系统服务)
COPY gsy-system-cloud/pom.xml ./gsy-system-cloud/
COPY gsy-system-cloud/system-app/pom.xml ./gsy-system-cloud/system-app/
COPY gsy-system-cloud/sys-common-utils/pom.xml ./gsy-system-cloud/sys-common-utils/
COPY gsy-system-cloud/sys-pojo/pom.xml ./gsy-system-cloud/sys-pojo/
COPY gsy-system-cloud/sys-user-server/pom.xml ./gsy-system-cloud/sys-user-server/

# gsy-upload-cloud (上传服务，独立 jar 模块)
COPY gsy-upload-cloud/pom.xml ./gsy-upload-cloud/

# ---------- 2. 复制全部源代码 ----------
COPY . .

# ---------- 3. 打包指定模块 ----------
# MODULE: 要打包的模块路径，如 gsy-gateway-cloud/gateway-server-app
# -pl 指定模块
# -am (--also-make) 自动构建该模块依赖的所有上游模块（通过 Maven reactor 机制）
# 不能使用 dependency:go-offline，因为内部 SNAPSHOT 模块尚未安装到本地仓库
ARG MODULE
RUN --mount=type=cache,target=/root/.m2 \
    mvn package -DskipTests -pl ${MODULE} -am

# ====================== 阶段2：运行镜像 ======================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 时区设置
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 从构建阶段复制打包好的 jar
ARG MODULE
COPY --from=builder /build/${MODULE}/target/*.jar app.jar

# Spring Boot 会自动读取 SPRING_PROFILES_ACTIVE 环境变量，默认 prod
ENV SPRING_PROFILES_ACTIVE=prod

# JVM 容器优化参数
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=70.0", \
    "-jar", "app.jar"]