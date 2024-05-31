package vinh.le.bookappkotlin

import android.animation.Animator
import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
	
	// firebase auth
	private lateinit var firebaseAuth: FirebaseAuth
	
	private lateinit var cardFontLayout : View
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_splash)
		//ini firebase
		firebaseAuth = FirebaseAuth.getInstance()
		//Define the font and back of the card
		cardFontLayout = findViewById(R.id.card_font)
		
	//Define the flip animation
	val set : Animator? = AnimatorInflater.loadAnimator(this, R.animator.card_flip)
		.apply {
			setTarget(cardFontLayout)
			start()
		}
	Handler(Looper.getMainLooper()).postDelayed(Runnable{
		checkUser()
	},1000)
}
	
	private fun checkUser() {
		//get current user, if logged in or not
		val firebaseUser = firebaseAuth.currentUser
		if(firebaseUser == null ) {
			//user not logged in, goto main screen
			startActivity(Intent(this,MainActivity::class.java))
		} else{
			//user logged in check user type
			val ref = FirebaseDatabase.getInstance().getReference("Users")
			ref.child(firebaseUser.uid)
				.addListenerForSingleValueEvent(object : ValueEventListener {
					override fun onDataChange(snapshot: DataSnapshot) {
						//get current user type
						val userType = snapshot.child("userType").value
						if(userType == "user"){
							//open user dashboard
							startActivity(Intent(this@SplashActivity,DashboardUserActivity::class.java))
						} else if (userType == "admin"){
							//open admin dashboard
							startActivity(Intent(this@SplashActivity,DashboardAdminActivity::class.java))
							finish()
							
						}
					}
					
					override fun onCancelled(error: DatabaseError) {
					
					}
					
					
				})
		}
	}
}