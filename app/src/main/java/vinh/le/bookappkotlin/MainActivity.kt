package vinh.le.bookappkotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import vinh.le.bookappkotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
	
	private lateinit var binding : ActivityMainBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		//handler click, login
		binding.loginBtn.setOnClickListener{
			//access to login page
			startActivity(Intent(this, LoginActivity::class.java))
		}
		//hand click, skip and continue to main screen
		binding.continueBtn.setOnClickListener {
			//skip the login process
			startActivity(Intent(this,DashboardUserActivity::class.java))
		}
		// Firebase Inflate
	}
}