package co.cachapas.app

import android.content.Context
import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import java.security.SecureRandom
import java.security.MessageDigest
import java.util.UUID
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Repositorio local de la actividad. Sustituir por API autenticada antes de producción. */
class Store private constructor(context: Context) {
 companion object {
  @Volatile private var instance: Store? = null
  fun get(context: Context): Store = instance ?: synchronized(this) { instance ?: Store(context.applicationContext).also { instance = it } }
  const val DEMO_PASSWORD = "Cachapas2026!"
 }
 private val prefs = context.getSharedPreferences("cachapas_data", Context.MODE_PRIVATE)
 val products = mutableListOf<Product>()
 val users = mutableListOf<User>()
 val orders = mutableListOf<Order>()
 private val carts = mutableMapOf<String, MutableMap<String, Int>>()
 var sessionId: String?
  get() = prefs.getString("session", null)
  set(value) { prefs.edit().putString("session", value).apply() }
 val current: User? get() = users.find { it.id == sessionId && it.active }
 init {
  val raw = prefs.getString("data", null)
  if (raw == null) {
   val a = JSONArray(context.assets.open("menu.json").bufferedReader().use { it.readText() })
   for (i in 0 until a.length()) products.add(readProduct(a.getJSONObject(i)))
   seed("Javier Barrera", "comprador@cachapas.demo", Role.BUYER)
   seed("Vendedor de prueba", "vendedor@cachapas.demo", Role.SELLER)
   seed("Administrador de prueba", "admin@cachapas.demo", Role.ADMIN)
   persist()
  } else load(JSONObject(raw))
 }
 private fun seed(name: String, email: String, role: Role) {
  val (salt, hash) = credentials(DEMO_PASSWORD)
  users.add(User(UUID.randomUUID().toString(), name, email, role, salt, hash))
 }
 private fun encoded(b: ByteArray) = Base64.encodeToString(b, Base64.NO_WRAP)
 private fun hash(password: String, salt: String): String {
  val spec = PBEKeySpec(password.toCharArray(), Base64.decode(salt, Base64.NO_WRAP), 120_000, 256)
  return try { encoded(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded) } finally { spec.clearPassword() }
 }
 private fun credentials(password: String): Pair<String, String> {
  require(password.length >= 8) { "La contraseña debe tener al menos 8 caracteres." }
  val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }.let(::encoded)
  return salt to hash(password, salt)
 }
 fun login(email: String, password: String): User {
  val u = users.find { it.active && it.email.equals(email.trim(), true) }
  require(u != null && MessageDigest.isEqual(hash(password, u.salt).toByteArray(), u.hash.toByteArray())) { "Correo o contraseña incorrectos." }
  sessionId = u.id
  return u
 }
 fun register(name: String, email: String, password: String): User = createUser(name, email, Role.BUYER, password)
 private fun createUser(name: String, email: String, role: Role, password: String): User {
  require(name.trim().length >= 3) { "Escribe tu nombre completo." }
  require(Rules.email(email.trim())) { "Escribe un correo válido." }
  require(users.none { it.email.equals(email.trim(), true) }) { "Ya existe una cuenta con ese correo." }
  val (salt, hash) = credentials(password)
  val user = User(UUID.randomUUID().toString(), name.trim(), email.trim().lowercase(), role, salt, hash)
  users.add(user); persist(); return user
 }
 fun resetPassword(email: String, password: String) {
  val u = users.find { it.active && it.email.equals(email.trim(), true) } ?: error("Cuenta no disponible.")
  val (salt, hash) = credentials(password); u.salt = salt; u.hash = hash
  if (sessionId == u.id) sessionId = null
  persist()
 }
 fun saveUser(id: String?, name: String, email: String, role: Role, password: String, active: Boolean) {
  require(current?.role == Role.ADMIN) { "Acceso reservado al administrador." }
  if (id == null) { createUser(name, email, role, password).also { it.active = active }; persist(); return }
  val u = users.first { it.id == id }
  require(name.trim().length >= 3 && Rules.email(email.trim())) { "Revisa nombre y correo." }
  require(users.none { it.id != id && it.email.equals(email.trim(), true) }) { "Ese correo ya está en uso." }
  require(id != current?.id || (role == Role.ADMIN && active)) { "No puedes quitar tu propio acceso de administrador." }
  require(u.role != Role.ADMIN || (role == Role.ADMIN && active) || users.any { it.id != id && it.role == Role.ADMIN && it.active }) { "Debe quedar un administrador activo." }
  val newCredentials = if (password.isNotEmpty()) credentials(password) else null
  u.name = name.trim(); u.email = email.trim().lowercase(); u.role = role; u.active = active
  newCredentials?.let { u.salt = it.first; u.hash = it.second }
  persist()
 }
 fun profile(name: String, email: String, phone: String, address: String) {
  val u = current ?: error("Inicia sesión.")
  require(name.trim().length >= 3 && Rules.email(email.trim())) { "Revisa nombre y correo." }
  require(users.none { it.id != u.id && it.email.equals(email.trim(), true) }) { "El correo ya está registrado." }
  u.name = name.trim(); u.email = email.trim().lowercase(); u.phone = phone.trim(); u.address = address.trim(); persist()
 }
 fun saveProduct(id: String?, name: String, category: String, price: Long, stock: Int, description: String, active: Boolean) {
  require(current?.role?.let(Rules::canManage) == true) { "Acceso reservado al negocio." }
  Rules.product(name, price, stock); require(category in Rules.categories); require(description.trim().isNotEmpty()) { "Escribe una descripción." }
  val p = products.find { it.id == id }
  if (p == null) products.add(Product(UUID.randomUUID().toString(), name.trim(), category, price, stock, description.trim(), active))
  else { p.name = name.trim(); p.category = category; p.price = price; p.stock = stock; p.description = description.trim(); p.active = active }
  persist()
 }
 fun deactivateProduct(id: String) {
  require(current?.role?.let(Rules::canManage) == true) { "Acceso reservado al negocio." }
  products.first { it.id == id }.active = false; persist()
 }
 fun cart(): MutableMap<String, Int> {
  val u = current ?: error("Inicia sesión.")
  require(u.role == Role.BUYER) { "El carrito pertenece al comprador." }
  return carts.getOrPut(u.id) { mutableMapOf() }
 }
 fun add(id: String, quantity: Int) {
  val p = products.first { it.id == id }; require(p.active) { "El producto ya no está disponible." }
  Rules.quantity(quantity, p.stock); val c = cart(); val next = (c[id] ?: 0) + quantity; Rules.quantity(next, p.stock); c[id] = next; persist()
 }
 fun updateCart(id: String, quantity: Int) {
  if (quantity <= 0) cart().remove(id) else {
   val p = products.first { it.id == id }; require(p.active) { "Retira el producto que ya no está disponible." }
   Rules.quantity(quantity, p.stock); cart()[id] = quantity
  }
  persist()
 }
 fun clearCart() { cart().clear(); persist() }
 fun cartLines() = cart().map { (id, q) -> val p = products.first { it.id == id }; OrderLine(id, p.name, p.price, q) }
 @Synchronized fun checkout(contact: String, phone: String, note: String, payment: String): Order {
  val u = current ?: error("Inicia sesión.")
  require(contact.trim().length >= 3) { "Escribe el nombre de quien recoge." }
  require(phone.count { it.isDigit() } in 7..15) { "Escribe un teléfono válido." }
  require(payment in listOf("Aprobado", "Pendiente", "Rechazado"))
  val lines = cartLines(); val total = Rules.total(lines)
  lines.forEach { l -> val p = products.first { it.id == l.productId }; require(p.active) { "${p.name} no está disponible." }; Rules.quantity(l.quantity, p.stock) }
  val order = Order("CA-" + UUID.randomUUID().toString().take(8).uppercase(), u.id, System.currentTimeMillis(), lines, total, contact.trim(), phone.trim(), "Recogida en el local", note.trim(), payment, if (payment == "Aprobado") "Recibido" else "Sin confirmar")
  if (payment == "Aprobado") { lines.forEach { l -> products.first { it.id == l.productId }.stock -= l.quantity }; cart().clear() }
  orders.add(order); persist(); return order
 }
 fun visibleOrders(): List<Order> {
  val u = current ?: return emptyList()
  return orders.filter { u.role != Role.BUYER || it.userId == u.id }.sortedByDescending { it.created }
 }
 fun changeOrder(id: String, next: String) {
  val u = current ?: error("Inicia sesión.")
  val o = visibleOrders().first { it.id == id }
  if (u.role == Role.BUYER) require(next == "Cancelado" && o.userId == u.id && o.status == "Sin confirmar") { "El pedido confirmado requiere atención del negocio." }
  else require(Rules.transition(o.status, next) && o.payment == "Aprobado") { "El cambio de estado no está permitido." }
  if (next == "Cancelado" && o.payment == "Aprobado") {
   o.lines.forEach { l -> products.find { it.id == l.productId }?.let { it.stock += l.quantity } }
   o.payment = "Reintegrado (simulado)"
  }
  o.status = next; persist()
 }
 private fun readProduct(j: JSONObject) = Product(j.getString("id"), j.getString("name"), j.getString("category"), j.getLong("price"), j.getInt("stock"), j.getString("description"), j.optBoolean("active", true))
 private fun load(j: JSONObject) {
  j.getJSONArray("products").each { products.add(readProduct(it)) }
  j.getJSONArray("users").each { users.add(User(it.getString("id"), it.getString("name"), it.getString("email"), Role.valueOf(it.getString("role")), it.getString("salt"), it.getString("hash"), it.getBoolean("active"), it.optString("phone"), it.optString("address"))) }
  j.getJSONArray("orders").each { o ->
   val lines = mutableListOf<OrderLine>(); o.getJSONArray("lines").each { lines.add(OrderLine(it.getString("productId"), it.getString("name"), it.getLong("price"), it.getInt("quantity"))) }
   orders.add(Order(o.getString("id"), o.getString("userId"), o.getLong("created"), lines, o.getLong("total"), o.getString("contact"), o.getString("phone"), o.getString("address"), o.getString("note"), o.getString("payment"), o.getString("status")))
  }
  val cs = j.getJSONObject("carts"); cs.keys().forEach { uid -> val map = mutableMapOf<String, Int>(); val c = cs.getJSONObject(uid); c.keys().forEach { map[it] = c.getInt(it) }; carts[uid] = map }
 }
 private fun JSONArray.each(block: (JSONObject) -> Unit) { for (i in 0 until length()) block(getJSONObject(i)) }
 private fun persist() {
  val j = JSONObject()
  j.put("products", JSONArray(products.map { p -> JSONObject().put("id",p.id).put("name",p.name).put("category",p.category).put("price",p.price).put("stock",p.stock).put("description",p.description).put("active",p.active) }))
  j.put("users", JSONArray(users.map { u -> JSONObject().put("id",u.id).put("name",u.name).put("email",u.email).put("role",u.role.name).put("salt",u.salt).put("hash",u.hash).put("active",u.active).put("phone",u.phone).put("address",u.address) }))
  j.put("orders", JSONArray(orders.map { o -> JSONObject().put("id",o.id).put("userId",o.userId).put("created",o.created).put("lines",JSONArray(o.lines.map { l -> JSONObject().put("productId",l.productId).put("name",l.name).put("price",l.price).put("quantity",l.quantity) })).put("total",o.total).put("contact",o.contact).put("phone",o.phone).put("address",o.address).put("note",o.note).put("payment",o.payment).put("status",o.status) }))
  val cs = JSONObject(); carts.forEach { (uid,c) -> cs.put(uid,JSONObject(c as Map<*,*>)) }; j.put("carts",cs)
  check(prefs.edit().putString("data",j.toString()).commit()) { "No fue posible guardar los cambios locales." }
 }
}
