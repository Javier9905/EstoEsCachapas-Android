package co.cachapas.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Cada destino infla su layout XML; el repositorio concentra permisos y reglas locales. */
class ScreenFragment: Fragment() {
 companion object {
  fun newInstance(route: String, id: String?) = ScreenFragment().apply { arguments = Bundle().apply { putString("route",route);putString("id",id) } }
 }
 private val host get() = requireActivity() as MainActivity
 private val store get() = Store.get(requireContext())
 private val route get() = requireArguments().getString("route")!!
 private val recordId get() = arguments?.getString("id")
 private lateinit var root: View
 private var filter = "Todos"; private var query = ""
 override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
  val layout = when(route) {
   "home" -> R.layout.fragment_home;"catalog" -> R.layout.fragment_catalog;"detail" -> R.layout.fragment_detail
   "cart" -> R.layout.fragment_cart;"checkout" -> R.layout.fragment_checkout;"orders" -> R.layout.fragment_orders
   "order_detail" -> R.layout.fragment_order_detail;"profile" -> R.layout.fragment_profile;"panel" -> R.layout.fragment_panel
   "products" -> R.layout.fragment_products;"product_form" -> R.layout.fragment_product_form;"users" -> R.layout.fragment_users
   "user_form" -> R.layout.fragment_user_form;"report" -> R.layout.fragment_report;"poster" -> R.layout.fragment_poster;else -> R.layout.fragment_policies
  }
  return inflater.inflate(layout,container,false).also { root=it }
 }
 private fun <T: View> v(id: Int): T = root.findViewById(id)
 private fun set(id: Int, s: String) { v<TextView>(id).text = s }
 private fun input(id: Int) = v<EditText>(id).text.toString()
 private fun click(id: Int, block: () -> Unit) { v<View>(id).setOnClickListener { host.attempt(block) } }
 private fun go(dest: String, id: String? = null) { host.navigate(dest,id) }
 private fun confirm(text: String, action: () -> Unit) { MaterialAlertDialogBuilder(requireContext()).setMessage(text).setNegativeButton("Cancelar",null).setPositiveButton("Confirmar") { _,_ -> host.attempt(action) }.show() }
 private fun button(parent: LinearLayout, text: String, action: () -> Unit) {
  parent.addView(MaterialButton(requireContext()).apply { this.text=text; layoutParams=LinearLayout.LayoutParams(-1,-2);setOnClickListener { host.attempt(action) } })
 }
 private fun card(parent: LinearLayout, tag: String, title: String, body: String, actions: List<Pair<String, () -> Unit>> = emptyList()) {
  val card = layoutInflater.inflate(R.layout.item_card,parent,false)
  card.findViewById<TextView>(R.id.cardTag).text = tag
  card.findViewById<TextView>(R.id.cardTitle).text = title
  card.findViewById<TextView>(R.id.cardBody).text = body
  val row = card.findViewById<LinearLayout>(R.id.cardActions)
  actions.forEach { (label,action) -> button(row,label,action) }
  parent.addView(card)
 }
 private fun empty(parent: LinearLayout, text: String = getString(R.string.empty)) { parent.addView(TextView(requireContext()).apply { this.text=text;textSize=16f;setPadding(0,24,0,24) }) }
 private fun spinner(id: Int, values: List<String>, selected: Int = 0) { v<Spinner>(id).apply { adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,values);setSelection(selected) } }
 private fun date(time: Long) = SimpleDateFormat("dd MMM yyyy, HH:mm",Locale.forLanguageTag("es-CO")).format(Date(time))
 override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
  super.onViewCreated(view,savedInstanceState)
  if(store.current == null) { host.logout(false);return }
  host.attempt {
   when(route) {
    "home" -> home();"catalog" -> catalog(savedInstanceState);"detail" -> detail();"cart" -> cart();"checkout" -> checkout()
    "orders" -> orders();"order_detail" -> orderDetail();"profile" -> profile();"panel" -> panel();"products" -> products()
    "product_form" -> productForm();"users" -> users();"user_form" -> userForm();"report" -> report()
    "poster" -> click(R.id.back) { host.back() }
    else -> { click(R.id.poster) { go("poster") };click(R.id.back) { host.back() } }
   }
  }
 }
 override fun onSaveInstanceState(outState: Bundle) { outState.putString("filter",filter);outState.putString("query",query);super.onSaveInstanceState(outState) }
 private fun productCard(parent: LinearLayout,p: Product) {
  card(parent,p.category.uppercase(),p.name,Rules.money(p.price)+" · "+if(p.stock>0) "Disponible" else "Agotado",listOf("Ver producto" to { go("detail",p.id) }))
 }
 private fun home() {
  set(R.id.greeting,"Hola, ${store.current!!.name.substringBefore(' ')}")
  val parent = v<LinearLayout>(R.id.items)
  store.products.filter { it.active && it.category=="Cachapas" }.take(3).forEach { productCard(parent,it) }
  if(parent.childCount==0) empty(parent,"Explora el menú para encontrar productos disponibles.")
  click(R.id.menu) { go("catalog") };click(R.id.orders) { go("orders") };click(R.id.poster) { go("poster") }
 }
 private fun catalog(saved: Bundle?) {
  filter=saved?.getString("filter") ?: "Todos";query=saved?.getString("query").orEmpty()
  v<EditText>(R.id.search).setText(query)
  val chips = v<ChipGroup>(R.id.categories)
  (listOf("Todos")+Rules.categories).forEach { category ->
   chips.addView(Chip(requireContext()).apply {
    id = View.generateViewId();text=category;isCheckable=true;isChecked=filter==category;minHeight=(48*resources.displayMetrics.density).toInt()
    setOnClickListener { filter=category;renderCatalog() }
   })
  }
  v<EditText>(R.id.search).doAfterTextChanged { query=it.toString();renderCatalog() }
  renderCatalog()
 }
 private fun renderCatalog() {
  val list = store.products.filter { it.active && (filter=="Todos" || it.category==filter) && it.name.contains(query,true) }
  set(R.id.count,"${list.size} productos y presentaciones")
  val parent=v<LinearLayout>(R.id.items);parent.removeAllViews();list.forEach { productCard(parent,it) }
  if(list.isEmpty()) empty(parent,"No encontramos productos con esa búsqueda.")
 }
 private fun detail() {
  val p=store.products.find { it.id==recordId } ?: error("Producto no disponible.")
  set(R.id.title,p.name);set(R.id.category,p.category);set(R.id.price,Rules.money(p.price));set(R.id.description,p.description)
  set(R.id.stock,if(p.active) "Disponibilidad de prueba: ${p.stock} unidades" else "Producto desactivado")
  v<EditText>(R.id.quantity).setText("1")
  v<View>(R.id.add).isEnabled=p.active && p.stock>0
  click(R.id.add) { val q=input(R.id.quantity).toIntOrNull() ?: 0;require(q>0) { "Escribe una cantidad válida." };store.add(p.id,q);host.message("${p.name} se agregó al carrito.") }
  click(R.id.back) { host.back() }
 }
 private fun cart() {
  val parent=v<LinearLayout>(R.id.items);parent.removeAllViews()
  val lines=store.cartLines()
  lines.forEach { l ->
   val p=store.products.first { it.id==l.productId }
   card(parent,"${l.quantity} UNIDADES"+(if(!p.active) " · NO DISPONIBLE" else ""),l.name,Rules.money(l.price)+" por unidad · Subtotal "+Rules.money(l.price*l.quantity),listOf(
    "+ Agregar una unidad" to { store.updateCart(l.productId,l.quantity+1);cart() },
    "− Quitar una unidad" to { store.updateCart(l.productId,l.quantity-1);cart() },
    "Eliminar del carrito" to { confirm("¿Retirar ${l.name}?") { store.updateCart(l.productId,0);cart() } }))
  }
  if(lines.isEmpty()) empty(parent,"Tu carrito está vacío. Elige un producto del menú para empezar.")
  set(R.id.total,"Total "+Rules.money(lines.sumOf { it.price*it.quantity }))
  v<View>(R.id.checkout).isEnabled=lines.isNotEmpty();v<View>(R.id.clear).isEnabled=lines.isNotEmpty()
  click(R.id.checkout) { go("checkout") };click(R.id.clear) { confirm("¿Vaciar el carrito?") { store.clearCart();cart() } }
 }
 private fun checkout() {
  val u=store.current!!;val lines=store.cartLines();require(lines.isNotEmpty()) { "El carrito está vacío." }
  set(R.id.summary,lines.joinToString("\n") { "${it.quantity} × ${it.name}: ${Rules.money(it.price*it.quantity)}" }+"\n\nTotal a confirmar: "+Rules.money(Rules.total(lines))+"\nRecogida: $0")
  v<EditText>(R.id.contact).setText(u.name);v<EditText>(R.id.phone).setText(u.phone);v<EditText>(R.id.address).setText(u.address)
  spinner(R.id.deliveryMode,listOf("Recogida en el local", "Domicilio por confirmar"))
  v<Spinner>(R.id.deliveryMode).onItemSelectedListener=object: AdapterView.OnItemSelectedListener {
   override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
    val pickup=position==0;v<View>(R.id.address).isEnabled=!pickup;v<View>(R.id.pay).isEnabled=pickup
    set(R.id.deliveryInfo,if(pickup) "Recogida sin costo adicional. Confirma la ubicación del local con el negocio." else "El domicilio necesita cobertura y tarifa confirmadas. Elige recogida para completar esta demostración.")
   }
   override fun onNothingSelected(parent: AdapterView<*>?) = Unit
  }
  spinner(R.id.paymentResult,listOf("Aprobado", "Pendiente", "Rechazado"))
  click(R.id.pay) {
   require(v<Spinner>(R.id.deliveryMode).selectedItemPosition==0) { "La tarifa de domicilio aún no está definida." }
   require(v<CheckBox>(R.id.terms).isChecked) { "Confirma las condiciones del pedido de prueba." }
   val order=store.checkout(input(R.id.contact),input(R.id.phone),input(R.id.note),v<Spinner>(R.id.paymentResult).selectedItem.toString())
   v<View>(R.id.pay).isEnabled=false
   host.navigate("orders",root=true);go("order_detail",order.id)
  }
  click(R.id.back) { host.back() }
 }
 private fun orders() {
  val u=store.current!!;set(R.id.subtitle,if(u.role==Role.BUYER) "Tus pedidos y pagos de prueba" else "Pedidos recibidos por el negocio")
  val parent=v<LinearLayout>(R.id.items);val orders=store.visibleOrders()
  orders.forEach { o -> card(parent,o.status.uppercase(),o.id,"${date(o.created)}\n${Rules.money(o.total)} · Pago ${o.payment.lowercase()}",listOf("Consultar pedido" to { go("order_detail",o.id) })) }
  if(orders.isEmpty()) empty(parent,"Aún no hay pedidos. Completa una compra de prueba para ver el recorrido.")
 }
 private fun orderDetail() {
  val o=store.visibleOrders().find { it.id==recordId } ?: error("Pedido no disponible para esta cuenta.")
  set(R.id.title,o.id);set(R.id.status,"${o.status} · Pago: ${o.payment} (prueba)")
  set(R.id.summary,o.lines.joinToString("\n") { "${it.quantity} × ${it.name}\n${Rules.money(it.price*it.quantity)}" }+"\n\nTotal: "+Rules.money(o.total)+"\n"+date(o.created))
  set(R.id.contact,"Recoge: ${o.contact}\nTeléfono: ${o.phone}\n${o.address}\nObservaciones: ${o.note.ifBlank { "Sin observaciones" }}")
  val actions=v<LinearLayout>(R.id.actions);actions.removeAllViews()
  if(store.current!!.role==Role.BUYER && o.status=="Sin confirmar") button(actions,"Cancelar intento pendiente") { confirm("¿Cancelar este intento sin pago confirmado?") { store.changeOrder(o.id,"Cancelado");orderDetail() } }
  if(store.current!!.role!=Role.BUYER && o.payment=="Aprobado") {
   val next=when(o.status) { "Recibido" -> "En preparación";"En preparación" -> "Listo para recoger";"Listo para recoger" -> "Entregado";else -> null }
   if(next!=null) button(actions,"Marcar $next") { confirm("¿Cambiar el estado a $next?") { store.changeOrder(o.id,next);orderDetail() } }
   if(o.status=="Recibido") button(actions,"Cancelar y reintegrar de prueba") { confirm("¿Cancelar el pedido? Se repone el inventario y se registra un reintegro simulado.") { store.changeOrder(o.id,"Cancelado");orderDetail() } }
  }
  click(R.id.back) { host.back() }
 }
 private fun profile() {
  val u=store.current!!;set(R.id.role,"${u.role.label} · ${u.email}")
  v<EditText>(R.id.name).setText(u.name);v<EditText>(R.id.email).setText(u.email);v<EditText>(R.id.phone).setText(u.phone);v<EditText>(R.id.address).setText(u.address)
  fun status() { set(R.id.bioStatus,if(BiometricGate(requireContext()).enabled(u.id)) "Biometría activada en este dispositivo." else "El ingreso con contraseña siempre está disponible.") }
  status()
  click(R.id.save) { store.profile(input(R.id.name),input(R.id.email),input(R.id.phone),input(R.id.address));host.message("Perfil actualizado.") }
  click(R.id.biometric) { BiometricGate(requireContext()).enable(host,u.id) { error -> if(error==null) { status();host.message("Biometría activada. Vuelve a abrir la app para probar el acceso.") } else host.message(error) } }
  click(R.id.orders) { go("orders") };click(R.id.policies) { go("policies") }
  click(R.id.logout) { confirm("¿Cerrar la sesión? Se desactivará el acceso biométrico de esta cuenta.") { host.logout(true) } }
 }
 private fun panel() {
  val u=store.current!!;val paid=store.orders.filter { it.payment=="Aprobado" && it.status!="Cancelado" }
  set(R.id.title,if(u.role==Role.ADMIN) "Administración" else "Panel de ventas")
  set(R.id.metrics,"${store.products.count { it.active }} productos activos\n${store.orders.count { it.status in listOf("Recibido","En preparación","Listo para recoger") }} pedidos por atender\n${Rules.money(paid.sumOf { it.total })} en ventas simuladas")
  val parent=v<LinearLayout>(R.id.actions)
  button(parent,"Gestionar productos") { go("products") };button(parent,"Consultar pedidos recibidos") { go("orders") }
  if(u.role==Role.ADMIN) { button(parent,"Gestionar usuarios") { go("users") };button(parent,"Reporte de ventas") { go("report") } }
 }
 private fun products() {
  require(Rules.canManage(store.current!!.role))
  val parent=v<LinearLayout>(R.id.items);parent.removeAllViews()
  store.products.forEach { p -> card(parent,p.category+if(p.active) " · Activo" else " · Inactivo",p.name,"${Rules.money(p.price)} · ${p.stock} unidades",buildList {
   add("Editar producto" to { go("product_form",p.id) })
   if(p.active) add("Eliminar del menú" to { confirm("¿Dar de baja ${p.name}? El historial de pedidos se conserva.") { store.deactivateProduct(p.id);products() } })
  }) }
  if(parent.childCount==0) empty(parent)
  click(R.id.create) { go("product_form") }
 }
 private fun productForm() {
  val p=store.products.find { it.id==recordId }
  set(R.id.title,if(p==null) "Crear producto" else "Editar producto")
  v<EditText>(R.id.name).setText(p?.name.orEmpty());v<EditText>(R.id.price).setText(p?.price?.toString().orEmpty());v<EditText>(R.id.stock).setText(p?.stock?.toString() ?: "20");v<EditText>(R.id.description).setText(p?.description.orEmpty())
  spinner(R.id.category,Rules.categories,Rules.categories.indexOf(p?.category).coerceAtLeast(0));v<CheckBox>(R.id.active).isChecked=p?.active ?: true
  click(R.id.save) {
   store.saveProduct(p?.id,input(R.id.name),v<Spinner>(R.id.category).selectedItem.toString(),input(R.id.price).toLongOrNull() ?: 0,input(R.id.stock).toIntOrNull() ?: -1,input(R.id.description),v<CheckBox>(R.id.active).isChecked)
   host.navigate("products",root=true)
  }
  click(R.id.back) { host.back() }
 }
 private fun users() {
  require(store.current!!.role==Role.ADMIN)
  val parent=v<LinearLayout>(R.id.items);parent.removeAllViews()
  store.users.forEach { u -> card(parent,u.role.label+if(u.active) " · Activo" else " · Inactivo",u.name,u.email,buildList {
   add("Consultar y editar" to { go("user_form",u.id) })
   if(u.active && u.id!=store.current?.id) add("Eliminar acceso" to { confirm("¿Dar de baja a ${u.name}?") { store.saveUser(u.id,u.name,u.email,u.role,"",false);users() } })
  }) }
  click(R.id.create) { go("user_form") }
 }
 private fun userForm() {
  require(store.current!!.role==Role.ADMIN)
  val u=store.users.find { it.id==recordId };set(R.id.title,if(u==null) "Crear usuario" else "Editar usuario")
  v<EditText>(R.id.name).setText(u?.name.orEmpty());v<EditText>(R.id.email).setText(u?.email.orEmpty());spinner(R.id.role,Role.entries.map { it.label },u?.role?.ordinal ?: 0);v<CheckBox>(R.id.active).isChecked=u?.active ?: true
  click(R.id.save) {
   val name=input(R.id.name);val email=input(R.id.email);val role=Role.entries[v<Spinner>(R.id.role).selectedItemPosition];val password=input(R.id.password);val active=v<CheckBox>(R.id.active).isChecked
   confirm("¿Guardar los datos y permisos de esta cuenta?") {
    v<View>(R.id.save).isEnabled=false
    host.work({ store.saveUser(u?.id,name,email,role,password,active) }, { if(isAdded) host.navigate("users",root=true) }, { if(view!=null) v<View>(R.id.save).isEnabled=true })
   }
  }
  click(R.id.back) { host.back() }
 }
 private fun report() {
  require(store.current!!.role==Role.ADMIN)
  val paid=store.orders.filter { it.payment=="Aprobado" && it.status!="Cancelado" };val total=paid.sumOf { it.total }
  val summary="${paid.size} pedidos con pago aprobado\nVentas simuladas: ${Rules.money(total)}\nTicket promedio: ${Rules.money(if(paid.isEmpty()) 0 else total/paid.size)}\n${store.orders.count { it.payment=="Pendiente" }} intentos pendientes\n${store.orders.count { it.payment=="Rechazado" }} intentos rechazados\n${store.orders.count { it.status=="Cancelado" }} cancelaciones\nPeríodo: todos los registros de esta instalación."
  set(R.id.summary,summary)
  val parent=v<LinearLayout>(R.id.items)
  val byProduct=paid.flatMap { it.lines }.groupBy { it.productId }.values.sortedByDescending { lines -> lines.sumOf { it.price*it.quantity } }
  byProduct.forEach { lines -> card(parent,"${lines.sumOf { it.quantity }} UNIDADES",lines.last().name,Rules.money(lines.sumOf { it.price*it.quantity })) }
  if(byProduct.isEmpty()) empty(parent,"Completa una compra de prueba aprobada para generar el reporte.")
  click(R.id.share) { startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply { type="text/plain";putExtra(Intent.EXTRA_TEXT,"Esto Es Cachapas y Algo Más\nReporte académico\n\n$summary\n\n"+byProduct.joinToString("\n") { ls -> "${ls.last().name}: ${ls.sumOf { it.quantity }} unidades · ${Rules.money(ls.sumOf { it.price*it.quantity })}" }) },"Compartir reporte")) }
 }
}
