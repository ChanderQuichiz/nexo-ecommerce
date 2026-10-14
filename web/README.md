# Web

La aplicación Angular consume los microservicios a través del Gateway. En
desarrollo llama a `http://127.0.0.1:8082/api`; en producción usa `/api`
mediante `environment.prod.ts`. Inicia Discovery, Auth, Catalog, Orders,
Notifications, RabbitMQ y Gateway antes de usar estas pantallas.

Desde esta carpeta:

```powershell
npm install
npm start -- --host 127.0.0.1
```

Abre `http://127.0.0.1:4200/login`. El frontend usa Auth para iniciar sesión y
registrarse, Catalog para productos e inventario, Orders para checkout,
historial y pagos, y Notifications para descargar recibos PDF. En un
despliegue, el servidor web debe reenviar `/api/**` al Gateway.

Para verificar los cambios, ejecuta `npm test -- --watch=false` y `npm run build`.
