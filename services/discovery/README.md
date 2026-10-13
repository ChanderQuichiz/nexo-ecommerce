# Discovery Service

Eureka Server para los servicios de Nexo. Se inicia con `mvn spring-boot:run`
desde esta carpeta y escucha en `http://127.0.0.1:8761`.

Auth, Orders y Gateway se registran con `EUREKA_URL`, cuyo valor local es
`http://localhost:8761/eureka/`. Para despliegues en contenedores, configura
esa URL y `DISCOVERY_BIND_ADDRESS` según la red del entorno.
