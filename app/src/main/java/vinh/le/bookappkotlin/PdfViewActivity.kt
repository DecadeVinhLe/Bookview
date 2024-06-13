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
	
	// View Binding
	private lateinit var binding: ActivityPdfViewBinding
	
	// TAG
	private val TAG = "PDF_VIEW_TAG"
	
	// Book ID
	private var bookId: String = ""
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityPdfViewBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		// Get book id from intent
		bookId = intent.getStringExtra("bookId") ?: ""
		if (bookId.isEmpty()) {
			Log.e(TAG, "Book ID is missing")
			finish()
			return
		}
		
		loadBookDetails()
		
		// Handle click, go back
		binding.backBtn.setOnClickListener {
			onBackPressed()
		}
	}
	
	private fun loadBookDetails() {
		Log.d(TAG, "loadBookDetails: Get PDF URL from db")
		
		// Database Reference to get book details
		val ref = FirebaseDatabase.getInstance().getReference("Books")
		ref.child(bookId)
			.addListenerForSingleValueEvent(object : ValueEventListener {
				override fun onDataChange(snapshot: DataSnapshot) {
					// Get book URL
					val pdfUrl = snapshot.child("url").value as? String
					if (pdfUrl != null) {
						Log.d(TAG, "onDataChange: PDF_URL: $pdfUrl")
						// Load PDF using URL from Firebase Storage
						loadBookFromUrl(pdfUrl)
					} else {
						Log.e(TAG, "PDF URL is missing")
						binding.progressBar.visibility = View.GONE
					}
				}
				
				override fun onCancelled(error: DatabaseError) {
					Log.e(TAG, "loadBookDetails: Database error: ${error.message}")
					binding.progressBar.visibility = View.GONE
				}
			})
	}
	
	private fun loadBookFromUrl(pdfUrl: String) {
		Log.d(TAG, "loadBookFromUrl: Get PDF from Firebase Storage using URL")
		val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
		reference.getBytes(Constants.MAX_BYTES_PDF)
			.addOnSuccessListener { bytes ->
				Log.d(TAG, "loadBookFromUrl: PDF loaded from URL")
				
				// Load PDF
				binding.pdfView.fromBytes(bytes)
					.swipeHorizontal(false) // Set false to scroll vertically
					.onPageChange { page, pageCount ->
						// Set current and total page in toolbar subtitle
						val currentPage = page + 1 // Page starts from 0 so + 1
						binding.toolbarSubtitleTv.text = "$currentPage/$pageCount"
						Log.d(TAG, "loadBookFromUrl: Current Page: $currentPage/$pageCount")
					}
					.onError { t ->
						Log.e(TAG, "loadBookFromUrl: ${t.message}")
					}
					.onPageError { page, t ->
						Log.e(TAG, "loadBookFromUrl: ${t.message}")
					}
					.load()
				binding.progressBar.visibility = View.GONE
			}
			.addOnFailureListener { e ->
				Log.e(TAG, "loadBookFromUrl: Failed to load PDF due to ${e.message}")
				binding.progressBar.visibility = View.GONE
			}
	}
}
