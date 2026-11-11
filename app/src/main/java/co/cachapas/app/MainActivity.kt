package co.cachapas.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity: BaseActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  if(store.current == null) { logout(false);return }
  layout(R.layout.activity_main)
  val role = store.current!!.role
  findViewById<MaterialToolbar>(R.id.toolbar).apply {
   subtitle = role.label
   menu.add("Cerrar sesión").setOnMenuItemClickListener { logout(true); true }
   setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
  }
  val bottom = findViewById<BottomNavigationView>(R.id.bottom)
  bottom.inflateMenu(when(role) { Role.BUYER -> R.menu.nav_buyer; Role.SELLER -> R.menu.nav_seller; Role.ADMIN -> R.menu.nav_admin })
  bottom.setOnItemSelectedListener { item ->
   val route = when(item.itemId) { R.id.nav_home -> "home";R.id.nav_catalog -> "catalog";R.id.nav_cart -> "cart";R.id.nav_profile -> "profile";R.id.nav_users -> "users";R.id.nav_products -> "products";R.id.nav_orders -> "orders";R.id.nav_report -> "report";else -> "panel" }
   navigate(route, root=true);true
  }
  supportFragmentManager.addOnBackStackChangedListener { updateBackIcon() }
  if(savedInstanceState == null) navigate(if(role == Role.BUYER) "home" else "panel",root=true)
  updateBackIcon()
  onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) { override fun handleOnBackPressed() {
   if(supportFragmentManager.backStackEntryCount>0) supportFragmentManager.popBackStack()
   else { isEnabled=false;onBackPressedDispatcher.onBackPressed();isEnabled=true }
  } })
 }
 private fun updateBackIcon() {
  val toolbar = findViewById<MaterialToolbar>(R.id.toolbar) ?: return
  if(supportFragmentManager.backStackEntryCount>0) toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material) else toolbar.navigationIcon = null
  toolbar.navigationContentDescription = "Volver"
 }
 fun navigate(route: String, id: String? = null, root: Boolean = false) {
  attempt {
   val role = store.current?.role ?: error("Inicia sesión.")
   require(route !in listOf("users","user_form","report") || role == Role.ADMIN) { "Acceso reservado al administrador." }
   require(route !in listOf("panel","products","product_form") || Rules.canManage(role)) { "Acceso reservado al negocio." }
   require(route !in listOf("home","catalog","detail","cart","checkout") || role == Role.BUYER) { "Acceso reservado al comprador." }
   if(root) supportFragmentManager.popBackStackImmediate(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
   val tx = supportFragmentManager.beginTransaction().replace(R.id.container,ScreenFragment.newInstance(route,id))
   if(!root) tx.addToBackStack(route)
   tx.commit()
  }
 }
 fun back() { onBackPressedDispatcher.onBackPressed() }
 fun logout(clear: Boolean) {
  if(clear) { store.sessionId=null;BiometricGate(this).clear() }
  startActivity(Intent(this,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK));finish()
 }
}
