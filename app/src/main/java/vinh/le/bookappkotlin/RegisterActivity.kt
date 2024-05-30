package vinh.le.bookappkotlin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
		// Create account firebase Auth
		
		// showing progress
		progressDialog.setMessage("Creating account...")
		progressDialog.show()
		
		// Create user account
		firebaseAth.createUserWithEmailAndPassword(email,password)
			.addOnSuccessListener {
				//successful create , add user info to db
				updateUserinfo()
			}
			.addOnFailureListener {e->
				//failed to create
				progressDialog.dismiss()
				Toast.makeText(this,"Failed creating account due to ${e.message}",Toast.LENGTH_SHORT).show()
			}
		
	}
	
	private fun updateUserinfo(){
     //save user info to firebase
		progressDialog.setMessage("Saving user information...")
		
		// timestamp
		val timestamp = System.currentTimeMillis()
//		get current user uid, when user register
	    val uid = firebaseAth.uid
//		setup data to add to db
	    val hashMap:HashMap<String,Any?> = HashMap()
		hashMap["uid"] = uid
		hashMap["email"] = email
		hashMap["name"] = name
		hashMap["profileImage"] = "" //add in profile edit
		hashMap["userType"] = "user" // Indicate user dashboard
		hashMap["timestamp"] = timestamp
		
		// Set data to firebase
		val ref = FirebaseDatabase.getInstance().getReference("Users")
		ref.child(uid!!)
			.setValue(hashMap)
			.addOnSuccessListener {
				// user created and pass to db
				progressDialog.dismiss()
				Toast.makeText(this,"Account created !",Toast.LENGTH_SHORT).show()
				startActivity(Intent(this,RegisterActivity::class.java))
				finish()
			
			}
			.addOnFailureListener {e->
				// Failed to send data
				progressDialog.dismiss()
				Toast.makeText(this,"Failed to send data due to ${e.message}...",Toast.LENGTH_SHORT).show()
			}
	}
}