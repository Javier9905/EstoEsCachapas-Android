# Manual de usuario
## Esto Es Cachapas y Algo Más

**Aplicación móvil Android**  
**Autor:** Javier Andrés Barrera Cárdenas  
**Programa:** Ingeniería de Software · UCompensar

### Presentación

Esto Es Cachapas y Algo Más es una aplicación que reúne el menú del negocio en un solo lugar y permite organizar la consulta de productos y los pedidos. Su interfaz cambia según el perfil de acceso, dado que el comprador necesita encontrar los alimentos de una forma sencilla, mientras que el vendedor y el administrador cuentan con opciones para manejar la información del catálogo y del negocio.

El sistema utiliza tres perfiles: **comprador, vendedor y administrador**.

## 1. Inicio de sesión

Al abrir la aplicación aparece la pantalla de acceso, donde se escriben el correo y la contraseña. También se encuentran las opciones de registro y recuperación de contraseña.

| Perfil | Correo de demostración |
|---|---|
| Comprador | `comprador@cachapas.demo` |
| Vendedor | `vendedor@cachapas.demo` |
| Administrador | `admin@cachapas.demo` |

**Contraseña para las tres cuentas de demostración:** `Cachapas2026!`.

Estas cuentas permiten recorrer las funciones de cada perfil. El registro de una cuenta nueva corresponde al perfil comprador.

## 2. Perfil comprador

### Consultar el menú

Desde **Inicio** se muestran productos destacados del negocio y se puede ingresar a **Catálogo**. Allí se encuentran las categorías disponibles, una opción de búsqueda y los productos con sus respectivos precios.

Al seleccionar **Ver producto**, la aplicación muestra el nombre, la descripción, el valor y las unidades disponibles.

### Agregar productos al carrito

Dentro del detalle se indica la cantidad deseada y se selecciona **Agregar al carrito**. En **Carrito** aparecen los artículos elegidos, el precio por unidad y el total, en ese caso se puede aumentar o disminuir la cantidad, retirar un producto o vaciar el carrito.

### Registrar un pedido

El botón de confirmación permite revisar los artículos antes de continuar. Se diligencian los datos de contacto, se selecciona la modalidad de recogida disponible y se aceptan las condiciones de la demostración.

En esta versión se puede elegir un resultado de pago **Aprobado**, **Pendiente** o **Rechazado**. Estos estados son de prueba y no producen cargos bancarios.

### Consultar pedidos

En **Pedidos** se encuentra el historial de la cuenta. Cada registro presenta su número, fecha, total, estado de pago y detalle de los artículos.

### Perfil y cuenta

La sección **Perfil** reúne los datos de contacto, la dirección y las opciones de seguridad. Allí también se puede activar la biometría cuando el teléfono tiene un sensor compatible y una huella configurada.

## 3. Perfil vendedor

El vendedor entra a un panel desde el cual consulta los productos del menú y los pedidos registrados.

### Gestión de productos

En **Productos**, el botón **Crear producto** abre un formulario con los campos de nombre, categoría, precio, existencias y descripción. Es posible elegir una fotografía desde la galería del teléfono, de modo que el artículo tenga una referencia visual.

Un producto existente se modifica desde **Editar producto**. La opción **Eliminar del menú** lo desactiva del catálogo, conservando los pedidos donde haya sido incluido.

### Gestión de pedidos

En **Pedidos** se consultan los registros y, cuando corresponde, se utilizan los controles para avanzar por los estados del proceso: recibido, en preparación, listo para recoger y entregado.

## 4. Perfil administrador

El administrador dispone de un panel para consultar la información general del negocio.

En **Usuarios** se visualizan las cuentas registradas, sus perfiles y estados. Las opciones de administración permiten crear usuarios, editar datos, cambiar roles y desactivar accesos.

En **Productos** se consulta el catálogo y en **Pedidos** se revisan las solicitudes registradas. La sección **Reporte** presenta un resumen de los resultados de prueba, incluidos totales y productos vendidos en la simulación.

## 5. Acceso biométrico y recuperación de contraseña

La autenticación por huella se activa desde **Perfil** después de iniciar sesión normalmente. El teléfono presenta el cuadro de autenticación propio de Android; la aplicación no guarda imágenes de huellas digitales.

La recuperación de contraseña de la versión de demostración utiliza un código mostrado dentro de la misma aplicación. Ese código no llega por correo electrónico.

## 6. Ubicación y direcciones

En la pantalla del pedido existe una opción para consultar la ubicación del teléfono. Si se concede el permiso, el sistema presenta las coordenadas obtenidas y permite revisar la dirección. La dirección también puede escribirse manualmente.

La modalidad habilitada para completar pedidos de prueba es **recogida en el local**; la información sobre domicilios se muestra como referencia y no incluye una tarifa calculada.

## 7. Información sobre los datos

Las cuentas, el catálogo, el carrito y los pedidos de esta versión utilizan almacenamiento local. Por eso, la información se conserva en el dispositivo donde se usa la aplicación y no se sincroniza automáticamente con otros teléfonos.

Las operaciones con dinero son simuladas y los resultados aparecen únicamente para mostrar el recorrido de una compra.

---

**Esto Es Cachapas y Algo Más**  
Una forma de consultar el menú y organizar los pedidos desde Android.
