# Web

La aplicación Angular usa el Gateway para el login y el registro reales. En
desarrollo llama a `http://127.0.0.1:8082/api`; en producción usa `/api` mediante
`environment.prod.ts`. Inicia Discovery, Auth y Gateway antes de usar estas
pantallas.

Desde esta carpeta:

```powershell
npm install
npm start -- --host 127.0.0.1
```

Abre `http://127.0.0.1:4200/login`. El catálogo y los pedidos de esta rama
principal siguen utilizando datos simulados en Angular; sus adaptadores al
backend todavía no están implementados. En un despliegue, el servidor web debe
reenviar `/api/**` al Gateway.

Para verificar los cambios, ejecuta `npm test -- --watch=false` y `npm run build`.
