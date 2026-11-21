# Esto Es Cachapas y Algo Más — Android

Proyecto académico para **Desarrollo de Aplicaciones Móviles Nativas** (UCompensar), presentado por **Javier Andrés Barrera Cárdenas**.

Aplicación Android en Kotlin con interfaz XML y tres perfiles: **Comprador, Vendedor y Administrador**. Incluye catálogo precargado de cachapas y otros productos, búsqueda, carrito, registro local de pedidos, gestión local de productos y usuarios, reportes simulados y código para acceso biométrico mediante AndroidX Biometric.

## Estado del proyecto — 21 de noviembre de 2026

**Prototipo académico local. No es una aplicación de comercio electrónico lista para producción.**

- Implementado en el código: navegación por rol, registro/ingreso local, CRUD local, carrito, historial, reportes de prueba, soporte de biometría, selector de fotografías guardadas en el dispositivo y captura puntual de coordenadas con permiso.
- **Pendiente:** Firebase Authentication, Cloud Firestore, Firebase Storage, subida de imágenes a Firebase Storage, geolocalización asociada a pedidos con domicilio habilitado y pasarela de pagos certificada.
- **No verificado aquí:** compilación actual en Android Studio, funcionamiento en teléfono físico y grabación del video de lanzamiento. Se añadió una comprobación automática en `.github/workflows/android-ci.yml`, pendiente de comprobar que se ejecute correctamente en GitHub Actions.
- El selector de pago actual solo simula `Aprobado / Pendiente / Rechazado`; **no hace cobros**.
- El archivo `APK/Cachapas.apk` pertenece a la actividad anterior y no demuestra cumplimiento de esta entrega final.

## Cómo abrir el proyecto

1. Descarga o clona el repositorio.
2. Abre la carpeta raíz en **Android Studio**.
3. Espera a que finalice **Gradle Sync**.
4. Conecta un teléfono Android compatible y ejecuta la app.
5. Revisa los logs/errores de compilación y prueba todas las funciones relevantes.

Configuración de referencia: **Kotlin 2.0.21, AGP 8.7.3, Java 17, compileSdk 35, minSdk 26**.

## Cuentas locales de demostración

| Rol | Correo |
|---|---|
| Comprador | `comprador@cachapas.demo` |
| Vendedor | `vendedor@cachapas.demo` |
| Administrador | `admin@cachapas.demo` |

Contraseña en el entorno de prueba: `Cachapas2026!`. Nunca reutilices esta contraseña en un sistema real.

## Documentación de la actividad final

- [Documento técnico definitivo (Markdown)](Documentacion/Documento_Tecnico_Final_2026.md)
- [Manual de usuario (Markdown)](Documentacion/Manual_Usuario_Final_2026.md)
- [Guion del video de lanzamiento](Documentacion/Guion_Video_Final_2026.md)
- Documentos PDF y DOCX diagramados: forman parte de la entrega académica; la versión con portada artística se entrega aparte en la plataforma.
- La carpeta `Documentacion` conserva, además, los archivos de la **actividad 4**.

## Entregables solicitados por el docente

1. Documento técnico definitivo coherente con lo implementado.
2. Manual de usuario con **capturas reales** del aplicativo de los tres roles.
3. ZIP completo de Android Studio **compilable y comprobado**.
4. Repositorio GitHub público con código fuente y comentarios explicativos.
5. Video de lanzamiento de máximo **un minuto**, capturado en un dispositivo físico (no emulador).

Los requisitos de Firestore, Storage, GPS y pasarela deben implementarse antes de declarar que el proyecto final los cumple.

> Esta documentación separa las funciones existentes de las pendientes. No se presentan como pruebas las capturas, compilaciones o integraciones que todavía no se han realizado.
