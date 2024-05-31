package vinh.le.bookappkotlin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import vinh.le.bookappkotlin.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
	// view binding
	private lateinit var binding: ActivityLoginBinding
	
	// firebase auth
	private lateinit var firebaseAuth: FirebaseAuth
	
	// progress Dialog
	private lateinit var progressDialog: ProgressDialog
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		binding = ActivityLoginBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		//init Firebase auth
		firebaseAuth = FirebaseAuth.getInstance()
		
		// ini Progress Dialog and wait for register
		progressDialog = ProgressDialog(this)
		progressDialog.setTitle("Please Wait")
		progressDialog.setCanceledOnTouchOutside(false)
		
		// handle back button
		binding.noAccountTV.setOnClickListener {
			// move user to register page
			startActivity(Intent(this, RegisterActivity::class.java))
			finish()
		}
		
		//handle click register Button
		binding.loginBtn.setOnClickListener {
//       Unit call
			validateData()
			
		}
	}
	
	private var email = ""
	private var password = ""
	private fun validateData() {
		email = binding.emailET.text.toString().trim()
		password = binding.passwordET.text.toString().trim()
		if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			Toast.makeText(this, "Invalid email...", Toast.LENGTH_SHORT).show()
		} else if (password.isEmpty()) {
			Toast.makeText(this, "Enter the password...", Toast.LENGTH_SHORT).show()
		} else {
			loginUser()
		}
		
	}
	
	private fun loginUser() {
		progressDialog.setMessage("Logging in...")
		progressDialog.show()
		
		firebaseAuth.createUserWithEmailAndPassword(email, password)
			.addOnSuccessListener {
				checkUser()
			}
			.addOnFailureListener { e ->
				//failed to login
				progressDialog.dismiss()
				Toast.makeText(this, "Logging in failed due to ${e.message}", Toast.LENGTH_SHORT)
					.show()
			}
		
	}
	
	private fun checkUser(){
		//checking user in db or not
		progressDialog.setMessage("Checking existed users...")
		
		val firebaseUser = firebaseAuth.currentUser!!
		// check userType admin or user
		val ref = FirebaseDatabase.getInstance().getReference("Users")
		ref.child(firebaseUser.uid)
			.addListenerForSingleValueEvent(object : ValueEventListener{
				override fun onDataChange(snapshot: DataSnapshot) {
					progressDialog.dismiss()
					val userType = snapshot.child("userType").value
					if(userType == "user"){
						//open user dash board
						startActivity(Intent(this@LoginActivity,DashboardUserActivity::class.java))
					} else if (userType == "admin"){
						//open admin dashboard
						startActivity(Intent(this@LoginActivity,DashboardAdminActivity::class.java))
						finish()
						
					}
				}
				
				override fun onCancelled(error: DatabaseError) {
				
				}
			})
			
		
	}
	
}
