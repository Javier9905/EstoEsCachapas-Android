package co.cachapas.app

import org.junit.Assert.*
import org.junit.Test

class RulesTest {
 @Test fun correctTotalWithMenuPrices() { assertEquals(39000L,Rules.total(listOf(OrderLine("p01","Cachapa Sencilla",8000,3),OrderLine("p05","Cachapa Con Carne Mechada",15000,1)))) }
 @Test fun moneyUsesColombianThousands() { assertEquals("$25.000",Rules.money(25000)) }
 @Test fun rejectsStockOverrun() { assertThrows(IllegalArgumentException::class.java) { Rules.quantity(4,3) } }
 @Test fun rejectsZeroAndNegativeQuantity() { listOf(0,-1,100).forEach { n -> assertThrows(IllegalArgumentException::class.java) { Rules.quantity(n,200) } } }
 @Test fun rejectsEmptyCart() { assertThrows(IllegalArgumentException::class.java) { Rules.total(emptyList()) } }
 @Test fun rejectsInvalidPriceOrInventory() { assertThrows(IllegalArgumentException::class.java) { Rules.product("Cachapa",0,20) };assertThrows(IllegalArgumentException::class.java) { Rules.product("Cachapa",8000,-1) } }
 @Test fun preparationCannotSkipSteps() { assertFalse(Rules.transition("Recibido","Entregado"));assertTrue(Rules.transition("Recibido","En preparación"));assertTrue(Rules.transition("En preparación","Listo para recoger"));assertTrue(Rules.transition("Listo para recoger","Entregado"));assertFalse(Rules.transition("Entregado","Cancelado")) }
 @Test fun buyerCannotManageCatalog() { assertFalse(Rules.canManage(Role.BUYER));assertTrue(Rules.canManage(Role.SELLER));assertTrue(Rules.canManage(Role.ADMIN)) }
 @Test fun rejectsMalformedEmail() { assertTrue(Rules.email("javier@example.com"));assertFalse(Rules.email("javier@"));assertFalse(Rules.email("javier example.com")) }
}
