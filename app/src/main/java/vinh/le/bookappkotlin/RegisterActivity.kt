package vinh.le.bookappkotlin

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import vinh.le.bookappkotlin.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
	// view Binding
	private lateinit var binding: ActivityRegisterBinding
	
	// firebase auth
	private lateinit var firebaseAth: FirebaseAuth
	
	// progress Dialog
	private lateinit var progressDialog: ProgressDialog
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		binding = ActivityRegisterBinding.inflate(layoutInflater)
		setContentView(binding.root)
		//init Firebase auth
		firebaseAth = FirebaseAuth.getInstance()
		
		// ini Progress Dialog and wait for register
		progressDialog = ProgressDialog(this)
		progressDialog.setTitle("Please Wait")
		progressDialog.setCanceledOnTouchOutside(false)
		
		// handle back button
		binding.backBtn.setOnClickListener {
			onBackPressed() // goto previous view
		}
		
		//handle click register Button
		binding.registerBtn.setOnClickListener {
//       Unit call
			validateData()
		}
	}
	private var name = ""
	private var email = ""
	private var password = ""
	
	// Validate call
	private fun validateData(){
		//Input data
		name = binding.nameET.text.toString().trim()
		email = binding.emailET.text.toString().trim()
		password = binding.passwordET.text.toString().trim()
		val cPassword = binding.cPasswordET.text.toString().trim()
		
		// validate
		if (name.isEmpty()){
			// tell user
			Toast.makeText(this,"Name is empty...",Toast.LENGTH_SHORT).show()
		}else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
			Toast.makeText(this,"Invalid email...",Toast.LENGTH_SHORT).show()
		}else if (password.isEmpty()){
			Toast.makeText(this,"Password is empty...",Toast.LENGTH_SHORT).show()
		}else if (cPassword.isEmpty()){
			Toast.makeText(this,"Confirm password is empty...",Toast.LENGTH_SHORT).show()
		}else if (cPassword != password) {
			Toast.makeText(this,"Confirm password does not match...",Toast.LENGTH_SHORT).show()
		}else{
			createUserAccount()
		}
	}
	// Create user account
	private fun createUserAccount() {
		TODO("Not yet implemented")
	}
}