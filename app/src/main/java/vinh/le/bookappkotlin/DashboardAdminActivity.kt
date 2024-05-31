package vinh.le.bookappkotlin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import vinh.le.bookappkotlin.databinding.ActivityDashboardAdminBinding

class DashboardAdminActivity : AppCompatActivity() {
	//view binding
	private lateinit var binding: ActivityDashboardAdminBinding
	//firebase auth
	private lateinit var firebaseAuth: FirebaseAuth
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
		setContentView(binding.root)
		//init firebase auth
		firebaseAuth = FirebaseAuth.getInstance()
		checkUser()
		
		//handle log out which move admin to main page
		binding.logoutBtn.setOnClickListener {
			firebaseAuth.signOut()
			startActivity(Intent(this,MainActivity::class.java))
			checkUser()
		}
		//handle add category
		binding.addCategoryBtn.setOnClickListener {
			startActivity(Intent(this,CategoryAddActivity::class.java))
		}
	}
	
	private fun checkUser() {
		//		get current user
		val firebaseUser = firebaseAuth.currentUser
		if (firebaseUser == null){
//			not allow to login, admin can stay in user dashboard without login too
			binding.subTitleTV.text = getString(R.string.not_logged_in)
		} else {
			//Logged in, get and show user info
			val email = firebaseUser.email
			//set in textview of toolbar
			binding.subTitleTV.text = email
		}
	}
}