package vinh.le.bookappkotlin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import vinh.le.bookappkotlin.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
	
	//view binding
	private lateinit var binding: ActivityProfileBinding
	
	//firebase auth
	private lateinit var firebaseAuth: FirebaseAuth
	
	//firebase current user
	private  lateinit var firebaseUser: FirebaseUser
	
	//progress Dialog
	private lateinit var progressDialog: ProgressDialog
	
	//ArrayList to Hold Books
	private lateinit var booksArrayList: ArrayList<ModelPdf>
	private lateinit var adapterPdfFavourite: AdapterPdfFavourite
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		//reset to default values
		binding.accountStatusTv.text = "N/A"
		binding.memberDateTv.text = "N/A"
		binding.favouriteBookCountTv.text = "N/A"
		binding.accountStatusTv.text = "N/A"
		
		//firebase init
		firebaseAuth = FirebaseAuth.getInstance()
		firebaseUser = firebaseAuth.currentUser!!
		
		//init/setup progress Dialog
		progressDialog = ProgressDialog(this)
		progressDialog.setTitle("Please wait...")
		progressDialog.setCanceledOnTouchOutside(false)
		
		loadUserInfo()
		loadFavoriteBooks()
		
		//handle click, go back
		binding.profileEditBtn.setOnClickListener{
		   onBackPressed()
		}
		binding.profileEditBtn.setOnClickListener{
		 startActivity(Intent(this,ProfileActivity::class.java))
		}
		binding.accountStatusTv.setOnClickListener {
			if (firebaseUser.isEmailVerified) {
				//User is verified
				Toast.makeText(this, "Already verified...!", Toast.LENGTH_SHORT).show()
			} else {
				//User's not found
				emailVerificationDialog()
			}
		}
	}
	
	private fun emailVerificationDialog() {
		val builder = AlertDialog.Builder(this)
		builder.setTitle("Verify Email")
			.setMessage("Verification had been sent to your email ${firebaseUser.email}")
			.setPositiveButton("SEND"){d,e ->
				sendEmailVerification()
			}
			.setNegativeButton("CANCEL"){d,e->
				d.dismiss()
			}
			.show()
	}
	
	private fun sendEmailVerification() {
	 //show progressDialog
		progressDialog.setMessage("Sending email verification to email ${firebaseUser.email}")
	    progressDialog.show()
		
		//send instruction
		firebaseUser.sendEmailVerification()
			.addOnSuccessListener {
				//successfully sent
				progressDialog.dismiss()
				Toast.makeText(this, "Instruction sent! Please check your email !", Toast.LENGTH_SHORT).show()
				
			}
			.addOnFailureListener{e->
				progressDialog.dismiss()
				Toast.makeText(this, "Failed to send due to ${e.message}", Toast.LENGTH_SHORT).show()
			}
	}
	
	private fun loadUserInfo(){
		//check if user is verified or not
		if(firebaseUser.isEmailVerified){
		   binding.accountStatusTv.text = "Verified"
		}
		else{
			binding.accountStatusTv.text = "Not Verified"
		}
		
		//db reference to load user info
		val ref = FirebaseDatabase.getInstance().getReference("Users")
		ref.child(firebaseAuth.uid!!)
			.addValueEventListener(object: ValueEventListener{
				override fun onDataChange(snapshot: DataSnapshot) {
					//get users info
					val email = "${snapshot.child("email").value}"
					val name = "${snapshot.child("name").value}"
					val profileImage = "${snapshot.child("profileImage").value}"
					val timestamp = "${snapshot.child("timestamp").value}"
					val uid= "${snapshot.child("uid").value}"
					val userType = "${snapshot.child("userType").value}"
					
					//convert timestamp to proper date format
					val formattedDate = MyApplication.formatTimeStamp(timestamp.toLong())
					
					//set data
					binding.nameTV.text = name
					binding.emailTV.text = email
					binding.memberDateTv.text = formattedDate
					binding.accountTypeTv.text = userType
					//set Image
					//add glide library and show it from firebase
					try{
					  Glide.with(this@ProfileActivity).load(profileImage).placeholder(R.drawable.ic_person_gray).into(binding.profileIV)

					}
					catch(e: Exception){


					}
				}
				
				override fun onCancelled(error: DatabaseError) {
					TODO("Not yet implemented")
				}
			})
		
	}
	
	private fun loadFavoriteBooks(){
		//init array list
		booksArrayList = ArrayList()
		
	   val ref = FirebaseDatabase.getInstance().getReference("Users")
		 ref.child(firebaseAuth.uid!!).child("Favourites")
			 .addValueEventListener(object: ValueEventListener{
				 override fun onDataChange(snapshot: DataSnapshot) {
					//clear array, before add data
					 booksArrayList.clear()
					 for(ds in snapshot.children){
						 //get only id of books
						 val bookId = "${ds.child("bookId").value}"
						 //set to model
						 val modelPdf = ModelPdf()
						 modelPdf.id = bookId
						 //add model to list
						 booksArrayList.add(modelPdf)
					 }
					 
					 //set number of favorite books
					 binding.favouriteBookCountTv.text = "${booksArrayList.size}"
					 
					 //set adapter
					 adapterPdfFavourite = AdapterPdfFavourite(this@ProfileActivity,booksArrayList)
					 
					 //set adapter to recyclerview
					 binding.favouriteRv.adapter = adapterPdfFavourite
				 }
				 
				 override fun onCancelled(error: DatabaseError) {
					 TODO("Not yet implemented")
				 }
			 } )
	}
}