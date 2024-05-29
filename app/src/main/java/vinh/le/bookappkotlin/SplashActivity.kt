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

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
	
	private lateinit var cardFontLayout : View
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_splash)
		//Define the font and back of the card
		cardFontLayout = findViewById(R.id.card_font)
		
	//Define the flip animation
	val set : Animator? = AnimatorInflater.loadAnimator(this, R.animator.card_flip)
		.apply {
			setTarget(cardFontLayout)
			start()
		}
	Handler(Looper.getMainLooper()).postDelayed(Runnable{
		this.startActivity(Intent(this,MainActivity::class.java))
	},2000)
}
	}