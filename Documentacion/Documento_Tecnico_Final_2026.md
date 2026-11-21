# Documento técnico
## Esto Es Cachapas y Algo Más — Android

**Desarrollo de Aplicaciones Móviles Nativas**  
**Javier Andrés Barrera Cárdenas**  
**Ingeniería de Software · UCompensar**

## 1. Descripción del sistema

Esto Es Cachapas y Algo Más es una aplicación Android orientada a la consulta de un menú de alimentos y la administración de pedidos mediante tres perfiles. El comprador utiliza el catálogo y el carrito, mientras que el vendedor gestiona los productos y el administrador consulta usuarios, permisos e información del negocio.

El funcionamiento del aplicativo se organiza a partir de pantallas independientes, modelos de datos y un repositorio local que mantiene la información dentro del dispositivo.

## 2. Entorno tecnológico

| Elemento | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| Entorno | Android Studio |
| Interfaz | XML, Material Components y Fragments |
| SDK de compilación | Android 35 |
| Versión mínima | Android API 26 |
| Java | Java 17 |
| Acceso biométrico | AndroidX Biometric y Android Keystore |
| Persistencia | SharedPreferences con representación JSON |
| Recursos iniciales | Archivo local `assets/menu.json` |

## 3. Organización del código

La estructura diferencia las actividades de acceso, la navegación, los modelos y las reglas del negocio, dado que cada una de estas partes cumple una función específica dentro de la aplicación.

| Componente | Descripción |
|---|---|
| `Activities.kt` | Pantallas de ingreso, registro y recuperación de acceso. |
| `MainActivity.kt` | Navegación principal y menú correspondiente a cada rol. |
| `ScreenFragment.kt` | Interfaces del catálogo, carrito, pedidos, productos, usuarios y reportes. |
| `Domain.kt` | Entidades de usuarios, productos y pedidos; validaciones de cantidades y estados. |
| `Store.kt` | Operaciones de lectura y escritura de datos locales, sesiones y reglas del negocio. |
| `BiometricGate.kt` | Autenticación biométrica mediante componentes de Android. |
| `res/layout` | Diseño XML de las pantallas. |
| `assets/menu.json` | Información inicial del menú. |

## 4. Modelo de información

### Usuarios

Cada usuario tiene un identificador, nombre, correo, rol y estado de acceso. Los perfiles disponibles son comprador, vendedor y administrador. Las contraseñas locales se procesan mediante PBKDF2WithHmacSHA256 con una sal aleatoria.

### Productos

Los artículos del catálogo contienen identificador, nombre, categoría, precio, inventario, descripción y estado de disponibilidad. También pueden asociar una fotografía seleccionada de la galería y guardada en el almacenamiento privado de la aplicación.

### Carrito y pedidos

El carrito conserva productos y cantidades asociadas al comprador. En la confirmación se calculan importes a partir de los precios registrados y se genera un pedido con su fecha, número de referencia, productos, datos de contacto y estado.

## 5. Funciones por perfil

**Comprador.** Dispone de registro e inicio de sesión, consulta del menú, filtros de categoría, detalle de alimentos, administración del carrito, confirmación de pedidos de prueba, historial y actualización del perfil.

**Vendedor.** Accede al panel del negocio, crea y edita productos, modifica el inventario, selecciona fotografías y consulta los pedidos para realizar cambios de estado autorizados.

**Administrador.** Gestiona cuentas y roles, consulta productos y pedidos y visualiza reportes que resumen los registros de la aplicación.

## 6. Navegación

La aplicación utiliza una actividad principal que incorpora diferentes fragmentos según la pantalla seleccionada. El menú inferior se adapta a la identidad del usuario, de manera que el comprador encuentra las opciones relacionadas con sus compras y el negocio accede a sus funciones administrativas.

Los permisos de acceso se verifican mediante el rol de la sesión local.

## 7. Catálogo y fotografías

El menú inicial se carga desde un archivo JSON incluido con la aplicación. La búsqueda permite filtrar productos por nombre y categoría; el detalle reúne precio, descripción y disponibilidad.

La imagen de un artículo se selecciona desde la galería mediante el selector de archivos de Android y se copia al almacenamiento privado del dispositivo. La ruta local queda asociada al producto y se utiliza para presentar la fotografía en las pantallas del catálogo y administración.

## 8. Ubicación y pedidos

La aplicación permite solicitar una ubicación puntual desde la confirmación del pedido. El permiso depende de la aprobación del usuario en Android y las coordenadas se muestran como referencia para la dirección escrita.

El registro de pedidos utiliza recogida en el local. Para representar los posibles resultados del proceso de compra, el sistema muestra los estados de pago **Aprobado**, **Pendiente** y **Rechazado**, todos ellos **simulados**. No existe una conexión bancaria ni se realiza un cobro.

Los pedidos registrados pueden consultarse desde el historial y siguen las reglas de transición definidas en el dominio del aplicativo.

## 9. Seguridad y tratamiento de información

Las contraseñas locales se almacenan como hashes con sal. La biometría utiliza las funciones proporcionadas por Android y su almacén seguro de claves, sin conservar huellas digitales.

La información de usuarios, productos, carritos y pedidos se guarda localmente; en consecuencia, no se intercambia automáticamente entre diferentes dispositivos. La aplicación bloquea el tráfico sin cifrar mediante su configuración de Android.

## 10. Consideraciones de uso

La aplicación constituye un entorno de demostración de compras. Los precios y estados de pago se utilizan para representar el recorrido del pedido y los reportes corresponden a operaciones simuladas.

Este alcance permite consultar el menú, registrar compras de prueba y administrar la información desde los tres roles, manteniendo diferenciadas las funciones de cada usuario.

## Referencias técnicas

- [Arquitectura de aplicaciones Android](https://developer.android.com/topic/architecture)
- [Autenticación biométrica en Android](https://developer.android.com/identity/sign-in/biometric-auth)
- [Permisos de ubicación](https://developer.android.com/develop/sensors-and-location/location/permissions)

---

**Esto Es Cachapas y Algo Más · Aplicación Android**
