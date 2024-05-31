package vinh.le.bookappkotlin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import vinh.le.bookappkotlin.databinding.ActivityDashboardUserBinding

class DashboardUserActivity : AppCompatActivity() {
	//view binding
	private lateinit var binding: ActivityDashboardUserBinding
	//firebase auth
	private lateinit var firebaseAuth: FirebaseAuth
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		binding = ActivityDashboardUserBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		//init firebase Auth
		firebaseAuth = FirebaseAuth.getInstance()
		checkUser()
		
		//handling click, logout
		binding.logoutBtn.setOnClickListener {
			firebaseAuth.signOut()
			checkUser()
		}
		
	}
	
	private fun checkUser() {
//		get current user
     val firebaseUser = firebaseAuth.currentUser
		if (firebaseUser == null){
//			not allow to login, goto main screen
			startActivity(Intent(this,MainActivity::class.java))
			finish()
		} else {
			//Logged in, get and show user info
			val email = firebaseUser.email
			//set in textview of toolbar
			binding.subTitleTV.text = email
		}
	}
	
}