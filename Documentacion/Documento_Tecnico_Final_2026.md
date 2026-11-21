# Documento técnico definitivo — Esto Es Cachapas y Algo Más

**Asignatura:** Desarrollo de Aplicaciones Móviles Nativas  
**Estudiante:** Javier Andrés Barrera Cárdenas  
**Fecha:** 20 de noviembre de 2026  
**Universidad:** UCompensar

## 1. Introducción

En este proyecto seguí trabajando en Esto Es Cachapas y Algo Más, dado que mi intención es llevar el menú del negocio a un aplicativo donde el comprador pueda buscar los productos y registrar un pedido y, por su parte, el vendedor y el administrador encuentren sus propias herramientas. En esta fase revisé las funciones presentes en el repositorio y diferencié lo que está implementado de aquello que necesita integraciones externas.

## 2. Objetivo y alcance

El comprador puede revisar el menú, agregar productos al carrito y consultar el pedido registrado. El vendedor administra productos e identifica pedidos que deben atenderse. El administrador dispone de funciones para gestionar usuarios y revisar un reporte de ventas de demostración. El alcance actualmente **es local y académico**; no es una plataforma de comercio electrónico lista para producción.

## 3. Arquitectura revisada

| Archivo | Función |
|---|---|
| `MainActivity.kt` | Navegación, permisos por rol y cierre de sesión. |
| `Activities.kt` | Ingreso, registro y recuperación local de cuenta. |
| `ScreenFragment.kt` | Catálogo, carrito, pedidos, perfil, usuarios, productos y reportes. |
| `Store.kt` | Operaciones CRUD, reglas de autorización local y persistencia JSON en SharedPreferences. |
| `Domain.kt` | Modelos y reglas del negocio. |
| `BiometricGate.kt` | BiometricPrompt y Android Keystore para biometría local. |
| `res/layout` y `assets/menu.json` | Interfaz XML y menú inicial. |

## 4. Funcionamiento por roles

**Comprador:** registro, autenticación, consulta y búsqueda de catálogo, detalle de producto, carrito, simulación de pedido, historial y actualización de perfil.

**Vendedor:** panel de ventas, creación y edición de productos, baja lógica de productos y seguimiento a pedidos.

**Administrador:** panel, mantenimiento de usuarios y roles, gestión de productos y reporte de ventas **simuladas**.

## 5. Seguridad y gestión de usuarios

El repositorio usa el algoritmo PBKDF2WithHmacSHA256 con una sal aleatoria para generar el hash local de las contraseñas. Los permisos dependen del rol asignado en el almacén local, no de una autorización de servidor. La recuperación de contraseña muestra un código **dentro de la aplicación**: no envía correos. La biometría aprovecha AndroidX Biometric y Android Keystore sin almacenar una huella; debe validarse en un celular físico compatible.

## 6. Productos y compras

El catálogo inicial proviene de `app/src/main/assets/menu.json`. Los productos se filtran por categorías y se agregan al carrito con validación de cantidades y disponibilidad. La pantalla de compra permite seleccionar un resultado `Aprobado`, `Pendiente` o `Rechazado`, pero **no existe un cobro real** ni una confirmación de un proveedor certificado. Los cambios de productos, usuarios y pedidos permanecen en el almacenamiento local de la instalación.

## 7. Matriz de cumplimiento

| Requisito del docente | Estado verificable en código | Trabajo pendiente |
|---|---|---|
| Autenticación para tres roles | Implementada localmente | Migrar a Firebase Auth y reglas de servidor |
| Biometría | Código implementado | Probar en dispositivo físico |
| CRUD de usuarios en Firestore | No implementado | Configurar Firestore y reglas seguras |
| CRUD de productos con imágenes | CRUD local y foto seleccionable de galería (almacenamiento privado local) | Subir fotografías a Firebase Storage y verificar cámara |
| Carrito, órdenes e historial en Firestore | Locales | Sincronización y persistencia segura |
| Geolocalización para entrega | Captura de coordenadas bajo permiso en pantalla de pedido | Asociarlas a entregas habilitadas y sincronizar datos |
| Pagos certificados | Simulación | Pasarela en sandbox, backend y confirmación de transacción |
| ZIP sin errores de compilación | No verificado en esta revisión | Gradle Sync, compilación y empaquetado |
| Video de lanzamiento en celular | Pendiente | Grabar 1 minuto en dispositivo físico |
| Capturas en manual | Pendientes | Capturar pantallas reales de las tres cuentas |

## 8. Ampliaciones realizadas en el repositorio\n\nSe añadió la selección de una imagen de galería al formulario de productos y su copia al almacenamiento privado del dispositivo, junto con el campo `imagePath` de la entidad Product. También se incorporó una solicitud puntual de ubicación con permisos de Android en la pantalla de confirmación. La entrega a domicilio sigue bloqueada mientras no exista tarifa y cobertura validada. Estas ampliaciones requieren compilarse y probarse en un teléfono para considerarlas verificadas.\n\n## 9. Tecnologías

Kotlin 2.0.21, Android Gradle Plugin 8.7.3, Java 17, Android SDK 35, minSdk 26, XML, AppCompat, Fragments, Material Components y AndroidX Biometric, según los archivos de configuración existentes.

## 9. Pruebas antes de entregar

1. Importar el proyecto en Android Studio y completar Gradle Sync.
2. Compilar e instalar en un teléfono físico.
3. Verificar registro, inicio de sesión, recuperación local, cierre y biometría.
4. Revisar búsqueda, detalle, carrito, subtotal y creación de pedidos simulados.
5. Crear/editar un producto y confirmar que se refleje en el catálogo.
6. Gestionar usuarios y consultar reportes desde el rol administrador.
7. Probar las integraciones externas que realmente se implementen.
8. Tomar capturas originales y grabar video de máximo un minuto.

## 10. Conclusiones

Este trabajo me permitió organizar una experiencia de compra y darle herramientas a los distintos roles del negocio. El recorrido principal del aplicativo y los datos de prueba existen en el proyecto; sin embargo, para presentarlo como una tienda Android completamente funcional todavía se requieren servicios externos y pruebas en un equipo físico. Por esta razón, el alcance se documenta sin atribuirle operaciones que aún no realiza.

**Código:** https://github.com/Javier9905/EstoEsCachapas-Android

**Referencias:** [Android Biometric](https://developer.android.com/identity/sign-in/biometric-auth), [Firebase Firestore](https://firebase.google.com/docs/firestore), [Firebase Storage](https://firebase.google.com/docs/storage), [Permisos de ubicación](https://developer.android.com/develop/sensors-and-location/location/permissions).

> La versión diagramada en PDF, con portada artística, se entrega como archivo académico aparte. La documentación de GitHub no sustituye la compilación ni las pruebas requeridas.
