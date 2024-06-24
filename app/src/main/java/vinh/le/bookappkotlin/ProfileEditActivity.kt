package vinh.le.bookappkotlin

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import vinh.le.bookappkotlin.databinding.ActivityProfileEditBinding

class ProfileEditActivity : AppCompatActivity() {
	//viewBinding
	private lateinit var binding: ActivityProfileEditBinding
	
	//firebase auth
	private lateinit var firebaseAuth: FirebaseAuth
	
	//image uri(which one is picked)
	private var imageUri: Uri? = null
	
	//progress dialog
	private  lateinit var progressDialog: ProgressDialog
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityProfileEditBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		//setup progress dialog
		progressDialog = ProgressDialog(this)
		progressDialog.setTitle("Please Wait")
		progressDialog.setCanceledOnTouchOutside(false)
		
		//init firebase auth
		firebaseAuth = FirebaseAuth.getInstance()
		loadUserInfo()
		
		//handle click, go back
		binding.backBtn.setOnClickListener{
			onBackPressed()
		}
		
		//handle click, pick image from gallery
		binding.profileIV.setOnClickListener{
		
		}
		
		//handle click, begin update profile
		binding.updateBtn.setOnClickListener{
		  validateData()
		}
	}
	
	private var name = ""
	private fun validateData() {
		//get data
		name = binding.nameEt.text.toString().trim()
		
		//validate data
		if (name.isEmpty()) {
			//name not entered
			Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show()
		} else {
			//name is entered
			if (imageUri == null) {
				//update without image
				updateProfile("")
			} else {
				//update with image
				uploadImage()
			}
		}
	}
	
	private fun uploadImage() {
		progressDialog.setMessage("Uploading profile image")
		progressDialog.show()
		
		//image path and name, use uid to replace
		val filePathAndName = "ProfileImages/"+ firebaseAuth.uid
		
		//storage reference
		val reference = FirebaseStorage.getInstance().getReference(filePathAndName)
		reference.putFile(imageUri!!)
			.addOnSuccessListener { taskSnapshot ->
				//image uploaded, get url of uploaded image
				
				val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
				while (!uriTask.isSuccessful);
				val uploadedImageUrl = "${uriTask.result}"
				
				updateProfile(uploadedImageUrl)
			}
			.addOnFailureListener{e->
				//failed to upload image
				progressDialog.dismiss()
				Toast.makeText(this,"Failed to upload image due to ${e.message}",Toast.LENGTH_SHORT).show()
			}
	}
	
	private fun updateProfile(uploadedImageUrl: String) {
	  progressDialog.setMessage("Updating profile...")
		 
	  //setup info to update to db
	val hashMap: HashMap<String, Any> = HashMap()
		hashMap["name"] = "$name"
		if(imageUri != null){
			hashMap["profileImage"] = uploadedImageUrl
		}
		
		//update to db
		val reference = FirebaseDatabase.getInstance().getReference("Users")
		reference.child(firebaseAuth.uid!!)
			.updateChildren(hashMap)
			.addOnSuccessListener {
				//profile updated
				progressDialog.dismiss()
				Toast.makeText(this,"Profile Updated",Toast.LENGTH_SHORT).show()
			}
			.addOnFailureListener{e->
				//failed to upload image
				progressDialog.dismiss()
				Toast.makeText(this,"Failed to upload profile due to ${e.message}",Toast.LENGTH_SHORT).show()
				
			}
	}
	
	private fun loadUserInfo() {
		//db reference to load user info
		val ref = FirebaseDatabase.getInstance().getReference("Users")
		ref.child(firebaseAuth.uid!!)
			.addValueEventListener(object: ValueEventListener {
				override fun onDataChange(snapshot: DataSnapshot) {
					//get users info
					val name = "${snapshot.child("name").value}"
					val profileImage = "${snapshot.child("profileImage").value}"
					val timestamp = "${snapshot.child("timestamp").value}"
					
					//set data
					binding.nameEt.setText(name)
					//set Image
					//add glide library and show it from firebase
					try{
						Glide.with(this@ProfileEditActivity).load(profileImage).placeholder(R.drawable.ic_person_gray).into(binding.profileIV)
						
					}
					catch(e: Exception){
					
					
					}
				}
				
				override fun onCancelled(error: DatabaseError) {
					TODO("Not yet implemented")
				}
			})
		
	}
	
	private fun showImageAttachMenu(){
		//Show Image menu options
		
		//setup popup menu
		val popupMenu = PopupMenu(this,binding.profileIV)
		popupMenu.menu.add(Menu.NONE,0,0,"Camera")
		popupMenu.menu.add(Menu.NONE,1,1,"Gallery")
		popupMenu.show()
		
		//handle popup menu item clicks
		
		popupMenu.setOnMenuItemClickListener { item ->
		   //return id of clicked item
			val id = item.itemId
			if(id == 0){
				//camera click
				pickImageCamera()
			}
			else if (id == 1){
				//gallery click
				pickImageGallery()
			}
			true
		}
	}
	
	private fun pickImageCamera() {
	 //intent to pick image from camera
		val values = ContentValues()
		values.put(MediaStore.Images.Media.TITLE,"Temp_Title")
		values.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Description")
		
		//launch uri of image
		imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
		
		val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
		intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
		cameraActivityResultLauncher.launch(intent)
	}
	
	private fun pickImageGallery() {
		//intent to pick image from gallery
		val intent = Intent(Intent.ACTION_PICK)
		intent.type = "image/*"
		galleryActivityResultLauncher.launch(intent)
	
	}
	
	//handle result of camera intent
	private val cameraActivityResultLauncher = registerForActivityResult(
		ActivityResultContracts.StartActivityForResult(),
		ActivityResultCallback<ActivityResult>{ result->
	        //get uri of image
			if(result.resultCode == Activity.RESULT_OK){
				val  data = result.data
				
				//set to imageView
				binding.profileIV.setImageURI(imageUri)
			}
			else{
				//cancelled
				Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show()
			}
			
		}
		
	)
	
	//handle result of gallery intent
	private val galleryActivityResultLauncher = registerForActivityResult(
		ActivityResultContracts.StartActivityForResult(),
		ActivityResultCallback<ActivityResult>{result ->
			//get uri of image
			if(result.resultCode == Activity.RESULT_OK){
				val  data = result.data
				imageUri = data!!.data
				
				//set to imageView
				binding.profileIV.setImageURI(imageUri)
			}
			else{
				//cancelled
				Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show()
			}
			
		}
	)
	
}