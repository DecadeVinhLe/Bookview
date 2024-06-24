package vinh.le.bookappkotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
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
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		firebaseAuth = FirebaseAuth.getInstance()
		
		//handle click, go back
		binding.profileEditBtn.setOnClickListener{
		   onBackPressed()
		}
		binding.profileEditBtn.setOnClickListener{
		
		}
		
	}
	
	private fun loadUserInfo(){
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
}