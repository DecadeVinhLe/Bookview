package vinh.le.bookappkotlin

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import vinh.le.bookappkotlin.databinding.ActivityPdfViewBinding

class PdfViewActivity : AppCompatActivity() {
	
	//view Binding
	private lateinit var binding: ActivityPdfViewBinding
	
	//TAG
	private val TAG = "PDF_VIEW_TAG"
	
	//book id
	var bookId = ""
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityPdfViewBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		//get book id from intent
		bookId = intent.getStringExtra("bookId")!!
		loadBookDetails()
		
		//handle click, go back
		binding.backBtn.setOnClickListener {
			onBackPressed()
		}
		
	}
	
	private fun loadBookDetails() {
		Log.d(TAG,"loadBookDetails: Get PDF URL from db")
		
		//Database Reference to get book details
		val ref = FirebaseDatabase.getInstance().getReference("Books")
		ref.child(bookId)
			.addListenerForSingleValueEvent(object : ValueEventListener{
				override fun onDataChange(snapshot: DataSnapshot) {
					//get book url
					val pdfUrl = snapshot.child("url").value
					Log.d(TAG,"onDataChange: PDF_URL: $pdfUrl")
					
					//load pdf using url from firebase storage
					loadBookFromUrl("$pdfUrl")
				}
				
				override fun onCancelled(error: DatabaseError) {
					TODO("Not yet implemented")
				}
			})
	
	}
	
	private fun loadBookFromUrl(pdfUrl: String) {
		Log.d(TAG,"loadBookFromUrl: Get PDF from firebase storage using URL")
		val  reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
	    reference.getBytes(Constants.MAX_BYTES_PDF)
		    .addOnSuccessListener {bytes ->
				Log.d(TAG,"loadBookFromUrl: PDF loaded from URL")
			    
			    //load pdf
			    binding.pdfView.fromBytes(bytes)
				    .swipeHorizontal(false) //set false to scroll vertically
				    .onPageChange{page, pageCount ->
						//set current and total page in toolbar subtitle
						val currentPage = page + 1 //page starts from 0 so + 1
					    binding.toolbarSubtitleTv.text = "$currentPage/$pageCount"
					    Log.d(TAG,"loadBookFromUrl: Current Page: $currentPage/$pageCount")
				    }
				    .onError{t->
						Log.d(TAG,"loadBookFromUrl: ${t.message}")
				    }
				    .onPageError{  page, t ->
						Log.d(TAG,"loadBookFromUrl: ${t.message}")
				    }
				    .load()
			    binding.progressBar.visibility = View.GONE
				
		    }
		    .addOnFailureListener { e->
				Log.d(TAG,"loadBookFromUrl: Failed to pdf due to ${e.message}")
			    binding.progressBar.visibility = View.GONE
		    }
	}
}