package vinh.le.bookappkotlin

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import vinh.le.bookappkotlin.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
	
	//view binding
	private lateinit var binding:ActivityForgotPasswordBinding
	
	//firebase auth
	private lateinit var firebaseAuth: FirebaseAuth
	
	//progress dialog
	private lateinit var progressDialog: ProgressDialog
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		//init firebase auth
		firebaseAuth = FirebaseAuth.getInstance()
		
		//init/setup progress dialog
		progressDialog = ProgressDialog(this)
		progressDialog.setTitle("Please wait")
		progressDialog.setCanceledOnTouchOutside(false)
		
		//handle click, go back
		binding.backBtn.setOnClickListener {
			onBackPressed()
		}
		
		//handle click, begin recovery profile
		binding.submitBtn.setOnClickListener {
			validateData()
		}
	}
	private var email = ""
	private fun validateData() {
	 //get data
		email = binding.emailET.text.toString().trim()
	 //validate data
	  if(email.isEmpty()){
		Toast.makeText(this,"Enter email...",Toast.LENGTH_SHORT).show()
	}
		else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
		  Toast.makeText(this,"Invalid email pattern...",Toast.LENGTH_SHORT).show()
	  }
		else{
			recoverPassword()
	  }
	}
	
	private fun recoverPassword() {
	  //show progress
	  progressDialog.setMessage("Sending password reset instructions to $email")
	  progressDialog.show()
      firebaseAuth.sendPasswordResetEmail(email)
	      .addOnSuccessListener {
			  //sent
			  progressDialog.dismiss()
		      Toast.makeText(this,"Instructions sent to \n$email",Toast.LENGTH_SHORT).show()
		      
	      }
	      .addOnFailureListener(){ e->
			  //failed
			  progressDialog.dismiss()
		      Toast.makeText(this,"Failed to send due to ${e.message}",Toast.LENGTH_SHORT).show()
		      
	      }
	}
}