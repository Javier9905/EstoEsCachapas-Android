# Firebase: configuración aún pendiente

Estas reglas son **plantillas de seguridad**, **no están desplegadas** y la app todavía usa persistencia local. No se puede declarar el requisito Firebase cumplido con solo añadir estos archivos.

## Servicios que deben configurarse

1. Crear un proyecto Firebase, registrar la aplicación Android `co.cachapas.app` y descargar `google-services.json` en `app/` **fuera de GitHub**.
2. Activar Email/Password en Firebase Authentication; usar UIDs reales y recuperación segura por correo (no el código de demostración que se muestra en pantalla).
3. Crear Cloud Firestore y Cloud Storage; revisar las reglas de este directorio antes de publicarlas. Definir índices y límites de consultas según el esquema.
4. Implementar un backend privilegiado (Firebase Cloud Functions o equivalente) que gestione asignación de roles mediante *custom claims*. Nunca se debe permitir al cliente promoverse a administrador.
5. Implementar desde el backend la creación de pedidos, cálculo confiable de totales, reserva de stock, expiraciones e idempotencia.
6. Integrar la pasarela de pagos certificada con credenciales de prueba guardadas como secretos **solo en el servidor**. Verificar mediante webhook firmado o consulta autenticada; no aceptar estados enviados por el teléfono como confirmación.
7. Migrar las funciones del `Store.kt` local a repositorios que utilicen Firebase Auth, Firestore y Storage, contemplando conectividad, fallos, control de roles y pruebas con Emulator Suite.
8. Crear fotografías desde cámara/galería si se requiere, comprimirlas y subirlas a `product-images/{sellerId}/{fileName}` solo tras autenticar.
9. Validar el flujo de domicilios con costo, zonas de cobertura y geolocalización, y volver a realizar pruebas de todos los roles.

## Estado del repositorio

- **Hecho:** prototipo local, selección de fotografías y permiso GPS, documentación de los tres roles.
- **Sin hacer:** despliegue de reglas, conexión y persistencia con Firebase, backend de pagos, pruebas en dispositivo físico.
- **Verificación de compilación:** consultar [GitHub Actions](../../actions).

**No publiques tokens, claves privadas ni contraseñas de cuentas reales.** Las reglas deben revisarse frente a un modelo final de datos, y los roles privilegiados se asignan solamente desde un servicio confiable.
