package vinh.le.bookappkotlin

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import vinh.le.bookappkotlin.MyApplication.Companion.incrementBookViewCount
import vinh.le.bookappkotlin.databinding.ActivityPdfDetailBinding
import java.io.FileOutputStream

class PdfDetailActivity : AppCompatActivity() {
	
	//view binding
	private  lateinit var binding: ActivityPdfDetailBinding
	
	private companion object{
		//TAG
		const val TAG = "BOOK_DETAILS_TAG"
	}
	
	//book id, from intent
	private var bookId = ""
	
	//get from firebase
	private var bookTitle = ""
	
	private var bookUrl = ""
	
	//holder for fav list
	private var isInMyFavourite = false
	
	private lateinit var firebaseAuth: FirebaseAuth
	
	private lateinit var progressDialog: ProgressDialog
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityPdfDetailBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		//get book id from intent
		bookId = intent.getStringExtra("bookId")!!
		
		//init firebase Auth
		firebaseAuth = FirebaseAuth.getInstance()
		if(firebaseAuth.currentUser !=null) {
			//user is logged in, check if favourite or not
			checkIsFavourite()
		}
		
		//progress Bar
		val progressDialog = ProgressDialog(this)
		progressDialog.setTitle("Please wait...")
		progressDialog.setCanceledOnTouchOutside(false)
		
		//increment views count when detail page starts
		incrementBookViewCount(bookId)
		
		loadBookDetails()
		
		//handle back button click, go back
		binding.backBtn.setOnClickListener {
			onBackPressed()
		}
		
		//handle click, open pdf view activity
		binding.readBookBtn.setOnClickListener {
			val intent = Intent(this, PdfViewActivity::class.java)
			intent.putExtra("bookId", bookId);
		  startActivity(intent)
		}
		
		//handle click, download book
		binding.downloadsBookBtn.setOnClickListener {
			//first check storage permission
			if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
				Log.d(TAG, "onCreate: STORAGE PERMISSION is already granted")
			}
			else{
				Log.d(TAG, "onCreate: STORAGE PERMISSION is denied, request it")
			    requestStoragePermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
			}
		}
		
		//handle click, add/remove favourite
		binding.favouriteBtn.setOnClickListener {
			//check if favourite or not
			if (firebaseAuth.currentUser != null){
				//check if favourite or not and user is logged in
				Toast.makeText(this,"You're not logged in", Toast.LENGTH_SHORT).show()
			}
			else{
				//user is logged in, add to favourite
				if(isInMyFavourite){
					//already in fav
					removeFromFavourite()
				}
				else{
					//not in fav
					addToFavourite()
				}
			
			}
		}
	}
	
	private val requestStoragePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted:Boolean ->
		//check if granted or not
		if(isGranted){
			Log.d(TAG,"onCreate: STORAGE PERMISSION is granted")
			downloadBook()
		}
		else{
			Log.d(TAG,"onCreate: STORAGE PERMISSION is denied")
			Toast.makeText(this,"Permission denied...",Toast.LENGTH_SHORT).show()
		}
	}
	
	//Download book Function
	
	private fun downloadBook(){
		Log.d(TAG,"downloadBook: Downloading Book")
		progressDialog.setMessage("Downloading Book")
		progressDialog.show()
		
		//download book from firebase
		val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl)
		storageReference.getBytes(Constants.MAX_BYTES_PDF)
			.addOnSuccessListener { bytes ->
				Log.d(TAG,"downloadBook: Book downloaded...")
				saveToDownloadsFolder(bytes)
			}
			.addOnFailureListener { e ->
				progressDialog.dismiss()
				Log.d(TAG,"downloadBook: Failed to download book due to ${e.message}")
				Toast.makeText(this,"Failed to download book due to ${e.message}",Toast.LENGTH_SHORT).show()
			}
		
	}
	
	
	@SuppressLint("SuspiciousIndentation")
	private fun saveToDownloadsFolder(bytes: ByteArray?){
	  Log.d(TAG,"saveToDownloadsFolder: Saving downloaded book")
		
		val nameWithExtension = "${System.currentTimeMillis()}.pdf"
		
		try {
			val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
		    downloadsFolder.mkdirs()//create folder if not exist
			
			val filePath = downloadsFolder.path + "/" + nameWithExtension
			
			val out = FileOutputStream(filePath)
			out.write(bytes)
			out.close()
			
			Toast.makeText(this,"Saved to Downloads Folder",Toast.LENGTH_SHORT).show()
			Log.d(TAG,"saveToDownloadsFolder: Saved to Downloads Folder")
			progressDialog.dismiss()
			incrementDownloadCount()
		}
		catch(e:Exception){
			progressDialog.dismiss()
		  Log.d(TAG,"saveToDownloadsFolder: Failed to save book due to ${e.message}")
		  Toast.makeText(this,"Failed to save book due to ${e.message}",Toast.LENGTH_SHORT).show()
		
		}
	}
	
	private fun incrementDownloadCount() {
	  //increment downloads count to firebase db
		Log.d(TAG,"incrementDownloadCount: ")
		
		//get previous count
		val ref = FirebaseDatabase.getInstance().getReference("Books")
		ref.child(bookId)
			.addListenerForSingleValueEvent(object: ValueEventListener {
				override fun onDataChange(snapshot: DataSnapshot) {
					//get downloads count
					var downloadsCount = "${snapshot.child("downloadsCount").value}"
					Log.d(TAG,"onDataChange: Current Downloads Count: $downloadsCount")
				 
					if(downloadsCount == "" || downloadsCount == "null"){
						downloadsCount = "0"
					}
					
					//convert to long and increment
					val newDownloadsCount: Long = downloadsCount.toLong() + 1
					Log.d(TAG,"onDataChange: New Downloads Count: $newDownloadsCount")
					
					//setup data to update to db
					val hashMap = HashMap<String,Any>()
					hashMap["downloadsCount"] = newDownloadsCount
					
					//update new increment downloads count to db
					val dbRef = FirebaseDatabase.getInstance().getReference("Books")
					dbRef.child(bookId)
						.updateChildren(hashMap)
						.addOnSuccessListener {
							Log.d(TAG,"onDataChange: Downloads count incremented")
						
						}
						.addOnFailureListener { e->
							Log.d(TAG,"onDataChange: Failed to increment due to ${e.message}")
						}
				
				}
				
				override fun onCancelled(error: DatabaseError) {
					TODO("Not yet implemented")
				}
			})
		
	}
	
	
	private fun loadBookDetails(){
		    //Book > bookId > Details
			val ref = FirebaseDatabase.getInstance().getReference("Books")
			ref.child(bookId)
				.addListenerForSingleValueEvent(object: ValueEventListener {
					override fun onDataChange(snapshot: DataSnapshot) {
						//get data
						val categoryId = "${snapshot.child("categoryId").value}"
						val descriptor = "${snapshot.child("description").value}"
						val downloadsCount = "${snapshot.child("downloadsCount").value}"
						val timestamp= "${snapshot.child("timestamp").value}"
						bookTitle = "${snapshot.child("title").value}"
						val uid = "${snapshot.child("uid").value}"
						bookUrl = "${snapshot.child("url").value}"
						val viewsCount = "${snapshot.child("viewsCount").value}"

						//format date
						val date = MyApplication.formatTimeStamp(timestamp.toLong())
						
						//load pdf category
						MyApplication.loadCategory(categoryId, binding.categoryTv)
						
						//load pdf thumbnail, pages count
						MyApplication.loadPdfFromUrlSinglePage("$bookUrl", "$bookTitle", binding.pdfView, binding.progressBar, binding.pagesTv)
					 
						//load pdf size
						MyApplication.loadPdfSize("$bookUrl", "$bookTitle", binding.sizeTv)
						
						//set data
						binding.titleTv.text = bookTitle
						binding.descriptionTv.text = descriptor
						binding.viewsTv.text = viewsCount
						binding.downloadsTv.text = downloadsCount
						binding.dateTv.text = date
						
					}
					
					override fun onCancelled(error: DatabaseError) {
						TODO("Not yet implemented")
					}
				})
		}
	
	private fun checkIsFavourite(){
	  Log.d(TAG,"checkIsFavourite: Checking this book is in fav or not...")
		
		val ref = FirebaseDatabase.getInstance().getReference("Users")
		ref.child(firebaseAuth.uid!!).child("Favourites").child(bookId)
			.addListenerForSingleValueEvent(object: ValueEventListener {
				override fun onDataChange(snapshot: DataSnapshot) {
                   isInMyFavourite = snapshot.exists()
					if(isInMyFavourite){
						//available in fav
						Log.d(TAG, "onDataChange: Available in fav...")
						//set drawable
						binding.favouriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_favorite_filled_white,0,0)
					    binding.favouriteBtn.text = "Remove Favourites"
					}
					else{
						//not available in fav
						Log.d(TAG, "onDataChange: Not available in fav...")
						//set drawable
						binding.favouriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_favorite_white,0,0)
						binding.favouriteBtn.text = "Add Favourites"
					}
				}
				
				override fun onCancelled(error: DatabaseError) {
				
				}
			})
	}
	
	private fun addToFavourite(){
		val timestamp = System.currentTimeMillis()
		
		//setup data to db
		val hashMap = HashMap<String,Any>()
		hashMap["bookId"] = bookId
		hashMap["timestamp"] = timestamp
		
		//save to db
		val ref = FirebaseDatabase.getInstance().getReference("Users")
		ref.child(firebaseAuth.uid!!).child("Favourites").child(bookId)
			.setValue(hashMap)
			.addOnSuccessListener {
				//add to fav
				Log.d(TAG, "addToFavourite: Added to fav...")
			
			}
			.addOnFailureListener { e ->
				//failed to add to fav
				Log.d(TAG,"addToFavourite: Failed to add to fav due to ${e.message}")
				Toast.makeText(this,"Failed to add to fav due to ${e.message}",Toast.LENGTH_SHORT).show()
			
			}
	
	}
	
	private fun removeFromFavourite(){
		Log.d(TAG,"removeFromFavourite: Removing from fav")
		
		//database ref
		val ref = FirebaseDatabase.getInstance().getReference("Users")
		ref.child(firebaseAuth.uid!!).child("Favourites").child(bookId)
			.removeValue()
			.addOnSuccessListener {
				Log.d(TAG,"removeFromFavourite: Removed from fav")
				
			}
			.addOnFailureListener { e->
				Log.d(TAG,"removeFromFavourite: Failed to remove from fav due to ${e.message}")
				Toast.makeText(this,"Failed to remove from fav due to ${e.message}",Toast.LENGTH_SHORT).show()
			}
	}
	
	
}