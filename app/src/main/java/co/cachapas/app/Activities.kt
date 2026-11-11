package co.cachapas.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.security.SecureRandom
import java.util.concurrent.Executors

open class BaseActivity: AppCompatActivity() {
 protected val store by lazy { Store.get(this) }
 private val io = Executors.newSingleThreadExecutor()
 fun <T> work(task: () -> T, success: (T) -> Unit, complete: () -> Unit = {}) {
  io.execute {
   val result = runCatching(task)
   runOnUiThread { if (!isDestroyed && !isFinishing) { complete();result.fold(success) { message(it.message ?: "No fue posible completar la operación.") } } }
  }
 }
 override fun onDestroy() { io.shutdown();super.onDestroy() }
 fun layout(id: Int) {
  setContentView(id)
  val root = findViewById<android.view.ViewGroup>(android.R.id.content).getChildAt(0)
  ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
   val b = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
   v.setPadding(b.left,b.top,b.right,b.bottom); insets
  }
 }
 fun message(s: String) { MaterialAlertDialogBuilder(this).setMessage(s).setPositiveButton("Aceptar",null).show() }
 fun attempt(block: () -> Unit) { runCatching(block).onFailure { message(it.message ?: "No fue posible completar la operación.") } }
 fun text(id: Int) = findViewById<EditText>(id).text.toString()
 fun click(id: Int, block: () -> Unit) { findViewById<View>(id).setOnClickListener { attempt(block) } }
 protected fun openMain() { startActivity(Intent(this,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish() }
}
class SplashActivity: BaseActivity() {
 private val handler = Handler(Looper.getMainLooper())
 private val go = Runnable { startActivity(Intent(this,LoginActivity::class.java)); finish() }
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); layout(R.layout.activity_splash); handler.postDelayed(go,900) }
 override fun onDestroy() { handler.removeCallbacks(go); super.onDestroy() }
}
class LoginActivity: BaseActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState); layout(R.layout.activity_login)
  busy(true)
  work({ Store.get(applicationContext) }, { busy(false) })
  click(R.id.login) {
   val email=text(R.id.email);val password=text(R.id.password);busy(true)
   work({ store.login(email,password) }, { openMain() }, { busy(false) })
  }
  click(R.id.register) { startActivity(Intent(this,RegisterActivity::class.java)) }
  click(R.id.recover) { startActivity(Intent(this,RecoveryActivity::class.java)) }
  click(R.id.biometric) {
   val uid = store.current?.id ?: error("Primero ingresa con correo y contraseña y activa la biometría en Perfil.")
   BiometricGate(this).unlock(this,uid) { e -> if(e == null && store.current?.id == uid) openMain() else message(e ?: "Cuenta no disponible.") }
  }
  val choices = findViewById<LinearLayout>(R.id.demoAccounts)
  listOf("Comprador" to "comprador@cachapas.demo", "Vendedor" to "vendedor@cachapas.demo", "Administrador" to "admin@cachapas.demo").forEach { (label,email) ->
   choices.addView(MaterialButton(this,null,com.google.android.material.R.attr.borderlessButtonStyle).apply { this.text = "Explorar como $label"; setOnClickListener { findViewById<EditText>(R.id.email).setText(email); findViewById<EditText>(R.id.password).setText(Store.DEMO_PASSWORD) } })
  }
 }
 private fun busy(value: Boolean) {
  listOf(R.id.login,R.id.biometric,R.id.register,R.id.recover).forEach { findViewById<View>(it).isEnabled=!value }
  findViewById<TextView>(R.id.demoTitle).text=if(value) "Preparando el acceso, espera un momento…" else "Cuentas para explorar la actividad"
  findViewById<MaterialButton>(R.id.login).text=if(value) "Espera un momento…" else getString(R.string.login)
 }
}
class RegisterActivity: BaseActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState); layout(R.layout.activity_register)
  click(R.id.back) { finish() }
  click(R.id.submit) {
   require(findViewById<CheckBox>(R.id.terms).isChecked) { "Acepta las condiciones de la demostración." }
   require(text(R.id.password) == text(R.id.confirm)) { "Las contraseñas no coinciden." }
   val name=text(R.id.name);val email=text(R.id.email);val password=text(R.id.password)
   findViewById<View>(R.id.submit).isEnabled=false
   work({ store.register(name,email,password);store.login(email,password) }, { openMain() }, { findViewById<View>(R.id.submit).isEnabled=true })
  }
 }
}
class RecoveryActivity: BaseActivity() {
 private var code = ""; private var email = ""; private var expires = 0L
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState); layout(R.layout.activity_recovery)
  code = savedInstanceState?.getString("code").orEmpty(); email = savedInstanceState?.getString("email").orEmpty(); expires = savedInstanceState?.getLong("expires") ?: 0
  if(code.isNotEmpty()) showCode()
  click(R.id.back) { finish() }
  click(R.id.request) {
   email = text(R.id.email).trim()
   require(store.users.any { it.active && it.email.equals(email,true) }) { "Escribe el correo de una cuenta de prueba activa." }
   code = (100000 + SecureRandom().nextInt(900000)).toString(); expires = System.currentTimeMillis()+600000; showCode()
  }
  click(R.id.submit) {
   require(code.isNotEmpty() && text(R.id.code) == code && System.currentTimeMillis() <= expires && text(R.id.email).trim().equals(email,true)) { "Revisa el código o genera uno nuevo. Caduca en 10 minutos." }
   require(text(R.id.password) == text(R.id.confirm)) { "Las contraseñas no coinciden." }
   val newPassword=text(R.id.password);val targetEmail=email
   findViewById<View>(R.id.submit).isEnabled=false
   work({ store.resetPassword(targetEmail,newPassword);BiometricGate(this).clear() }, {
    code = ""
    MaterialAlertDialogBuilder(this).setMessage("Contraseña actualizada en este dispositivo.").setPositiveButton("Ingresar") { _,_ -> finish() }.setCancelable(false).show()
   }, { findViewById<View>(R.id.submit).isEnabled=true })
  }
 }
 private fun showCode() { findViewById<TextView>(R.id.codeNotice).text = "Código de demostración: $code. Válido por 10 minutos." }
 override fun onSaveInstanceState(outState: Bundle) { outState.putString("code",code);outState.putString("email",email);outState.putLong("expires",expires);super.onSaveInstanceState(outState) }
}
