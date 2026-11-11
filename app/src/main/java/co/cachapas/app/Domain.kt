package co.cachapas.app

import java.text.NumberFormat
import java.util.Locale

enum class Role(val label: String) { BUYER("Comprador"), SELLER("Vendedor"), ADMIN("Administrador") }
data class Product(val id: String, var name: String, var category: String, var price: Long, var stock: Int, var description: String, var active: Boolean = true)
data class User(val id: String, var name: String, var email: String, var role: Role, var salt: String, var hash: String, var active: Boolean = true, var phone: String = "", var address: String = "")
data class OrderLine(val productId: String, val name: String, val price: Long, val quantity: Int)
data class Order(val id: String, val userId: String, val created: Long, val lines: List<OrderLine>, val total: Long, val contact: String, val phone: String, val address: String, val note: String, var payment: String, var status: String)
object Rules {
 val categories = listOf("Cachapas", "Hamburguesas", "Pizzas", "Otros favoritos", "Combos y porciones")
 fun money(value: Long): String = "$" + NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CO")).format(value)
 fun email(s: String) = s.matches(Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
 fun product(name: String, price: Long, stock: Int) {
  require(name.trim().length >= 3) { "Escribe un nombre de al menos 3 caracteres." }
  require(price in 1..10_000_000) { "El precio debe estar entre $1 y $10.000.000." }
  require(stock in 0..100_000) { "El inventario debe estar entre 0 y 100.000." }
 }
 fun quantity(quantity: Int, stock: Int) { require(quantity in 1..99 && quantity <= stock) { "La cantidad debe estar entre 1 y 99 y no superar la disponibilidad." } }
 fun total(lines: List<OrderLine>): Long {
  require(lines.isNotEmpty()) { "El carrito está vacío." }
  return lines.sumOf { require(it.price > 0); require(it.quantity in 1..99); Math.multiplyExact(it.price, it.quantity.toLong()) }
 }
 fun transition(current: String, next: String): Boolean = when (current) {
  "Recibido" -> next == "En preparación" || next == "Cancelado"
  "En preparación" -> next == "Listo para recoger"
  "Listo para recoger" -> next == "Entregado"
  else -> false
 }
 fun canManage(role: Role) = role == Role.ADMIN || role == Role.SELLER
}
