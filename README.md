# Hat-api-gateway
Требуемая версия JAVA 17. 

* Конфигурация путей задана в `application.yaml`

## Локальный запуск

### Опция #1
В IDE запустите `rubber.dutch.hat.apigateway.ApiGatewayApplication`

### Опция #2
Запуск из консоли командой `./gradlew bootRun`

## Docker
* Сборка образа: `docker build -t hat-api-gateway .`
* Запуск контейнера: `docker run -p 9002:9002 hat-api-gateway`
