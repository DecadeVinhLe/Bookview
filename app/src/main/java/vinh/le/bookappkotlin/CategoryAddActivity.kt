package vinh.le.bookappkotlin

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import vinh.le.bookappkotlin.databinding.ActivityCategoryAddBinding

class CategoryAddActivity : AppCompatActivity() {
	// view binding
	private lateinit var binding:ActivityCategoryAddBinding
	
	// firebase auth
	private lateinit var firebaseAuth: FirebaseAuth
	
	//progress dialog
	private lateinit var progressDialog: ProgressDialog
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		binding = ActivityCategoryAddBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		// init firebase auth
		firebaseAuth = FirebaseAuth.getInstance()
		
		//ini progress dialog
		progressDialog = ProgressDialog(this)
		progressDialog.setTitle("Please wait...")
		progressDialog.setCanceledOnTouchOutside(false)
		
		// handle back on click
		binding.backBtn.setOnClickListener{
			onBackPressed()
		}
		//handle click, begin upload category
		binding.submitBtn.setOnClickListener{
			validateDate()
		}
		
	}
	private var category = ""
	private fun validateDate() {
	 //validate data
		//get data
		category = binding.categoryET.text.toString().trim()
		
		//validate data
		if(category.isBlank()){
			Toast.makeText(this,"Enter Category...",Toast.LENGTH_SHORT).show()
		} else {
			addCategoryFirebase()
		}
	}
	
	private fun addCategoryFirebase() {
		//show progress
		progressDialog.show()
		
		//get timestamp
		val timestamp = System.currentTimeMillis()
		
		//set up data to add to database
		val hashMap = HashMap<String,Any>()
		hashMap["id"] = "$timestamp"
		hashMap["category"] = category
		hashMap["timestamp"] = timestamp
		hashMap["uid"] = "${firebaseAuth.uid}"
		
		// add firebase to db : Database Root > Category > CategoryId > Category info
		val ref = FirebaseDatabase.getInstance().getReference("Categories")
		ref.child("$timestamp")
			.setValue(hashMap)
			.addOnSuccessListener {
				progressDialog.dismiss()
				Toast.makeText(this,"Added Successful... ",Toast.LENGTH_SHORT).show()
			}
			.addOnFailureListener {e->
				progressDialog.dismiss()
				Toast.makeText(this,"Failed to add due to ${e.message}",Toast.LENGTH_SHORT).show()
			}
	}
}