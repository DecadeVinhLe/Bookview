package vinh.le.bookappkotlin

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyApplication : Application() {
	override fun onCreate() {
		super.onCreate()
	}
	
	companion object {
		
		// Create a static method to convert timestamp to a formatted date
		fun formatTimeStamp(timestamp: Long): String {
			val cal = Calendar.getInstance(Locale.ENGLISH)
			cal.timeInMillis = timestamp
			val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
			return dateFormat.format(cal.time)
		}
		
		// Function to get PDF size from URL
		fun loadPdfSize(pdfUrl: String, pdfTitle: String, sizeTv: TextView) {
			val TAG = "PDF_SIZE_TAG"
			val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
			ref.metadata
				.addOnSuccessListener { storageMetaData ->
					Log.d(TAG, "loadPdfSize: got metadata")
					val bytes = storageMetaData.sizeBytes.toDouble()
					Log.d(TAG, "loadPdfSize: Size Bytes $bytes")
					
					// Convert bytes to KB/MB
					val kb = bytes / 1024
					val mb = kb / 1024
					sizeTv.text = when {
						mb >= 1 -> "${String.format("%.2f", mb)} MB"
						kb >= 1 -> "${String.format("%.2f", kb)} KB"
						else -> "${String.format("%.2f", bytes)} bytes"
					}
				}
				.addOnFailureListener { e ->
					Log.d(TAG, "loadPdfSize: Failed to get metadata due to ${e.message}")
					sizeTv.text = "Size not available"
				}
		}
		
		// Function to load a single page of a PDF from URL
		fun loadPdfFromUrlSinglePage(
			pdfUrl: String, pdfTitle: String, pdfView: PDFView,
			progressBar: ProgressBar, pagesTv: TextView?
		) {
			val TAG = "PDF_THUMBNAIL_TAG"
			val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
			ref.getBytes(Constants.MAX_BYTES_PDF)
				.addOnSuccessListener { bytes ->
					Log.d(TAG, "loadPdfFromUrlSinglePage: got the file")
					
					pdfView.fromBytes(bytes)
						.pages(0) // Show the first page only
						.spacing(0)
						.swipeHorizontal(false)
						.enableSwipe(false)
						.onError { t ->
							progressBar.visibility = View.INVISIBLE
							Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")
						}
						.onPageError { page, t ->
							progressBar.visibility = View.INVISIBLE
							Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")
						}
						.onLoad { nbPages ->
							Log.d(TAG, "loadPdfFromUrlSinglePage: Pages: $nbPages")
							progressBar.visibility = View.INVISIBLE
							pagesTv?.text = "$nbPages"
						}
						.load()
				}
				.addOnFailureListener { e ->
					progressBar.visibility = View.INVISIBLE
					Log.d(TAG, "loadPdfFromUrlSinglePage: Failed to get file due to ${e.message}")
				}
		}
		
		// Function to load category from Firebase by ID
		fun loadCategory(categoryId: String, categoryTv: TextView) {
			val ref = FirebaseDatabase.getInstance().getReference("Categories")
			ref.child(categoryId)
				.addListenerForSingleValueEvent(object : ValueEventListener {
					override fun onDataChange(snapshot: DataSnapshot) {
						val category = snapshot.child("category").value.toString()
						categoryTv.text = category
					}
					
					override fun onCancelled(error: DatabaseError) {
						Log.d("CATEGORY_LOAD_ERROR", "Failed to load category: ${error.message}")
						categoryTv.text = "Category not available"
					}
				})
		}
		
		// Function to delete a book from Firebase
		fun deleteBook(context: Context, bookId: String, bookUrl: String, bookTitle: String) {
			val TAG = "DELETE_BOOK_TAG"
			Log.d(TAG, "deleteBook: deleting...")
			
			// Progress Bar
			val progressBar = ProgressBar(context).apply {
				visibility = View.VISIBLE
			}
			
			// Delete book from storage
			Log.d(TAG, "deleteBook: Deleting from storage...")
			val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl)
			storageReference.delete()
				.addOnSuccessListener {
					Log.d(TAG, "Deleted from storage")
					Log.d(TAG, "Deleting from db now...")
					
					val ref = FirebaseDatabase.getInstance().getReference("Books")
					ref.child(bookId)
						.removeValue()
						.addOnSuccessListener {
							progressBar.visibility = View.GONE
							Toast.makeText(context, "Successfully deleted...", Toast.LENGTH_SHORT).show()
							Log.d(TAG, "deleteBook: Deleted from db too...")
						}
						.addOnFailureListener { e ->
							progressBar.visibility = View.GONE
							Log.d(TAG, "deleteBook: Failed to delete from db due to ${e.message}")
							Toast.makeText(context, "Failed to delete from db due to ${e.message}", Toast.LENGTH_SHORT).show()
						}
				}
				.addOnFailureListener { e ->
					progressBar.visibility = View.GONE
					Log.d(TAG, "deleteBook: Failed to delete from storage due to ${e.message}")
					Toast.makeText(context, "Failed to delete from storage due to ${e.message}", Toast.LENGTH_SHORT).show()
				}
		}
		
		// Function to increment book view count
		fun incrementBookViewCount(bookId: String) {
			val ref = FirebaseDatabase.getInstance().getReference("Books")
			ref.child(bookId)
				.addListenerForSingleValueEvent(object : ValueEventListener {
					override fun onDataChange(snapshot: DataSnapshot) {
						// Get views count
						var viewsCount = snapshot.child("viewsCount").value.toString()
						if (viewsCount == "" || viewsCount == "null") {
							viewsCount = "0"
						}
						// Increment views count
						val newViewsCount = viewsCount.toLong() + 1
						
						// Setup data to update to db
						val hashMap = HashMap<String, Any>()
						hashMap["viewsCount"] = newViewsCount
						
						// Set to db
						val dbRef = FirebaseDatabase.getInstance().getReference("Books")
						dbRef.child(bookId)
							.updateChildren(hashMap)
					}
					
					override fun onCancelled(error: DatabaseError) {
						Log.d("VIEW_COUNT_ERROR", "Failed to increment view count: ${error.message}")
					}
				})
			
		}
		
		public fun removeFromFavourite(context: Context,bookId: String){
			val TAG = "REMOVE_FAV_TAG"
			Log.d(TAG,"removeFromFavourite: Removing from fav")
			
			val firebaseAuth = FirebaseAuth.getInstance()
			
			//database ref
			val ref = FirebaseDatabase.getInstance().getReference("Users")
			ref.child(firebaseAuth.uid!!).child("Favourites").child(bookId)
				.removeValue()
				.addOnSuccessListener {
					Log.d(TAG,"removeFromFavourite: Removed from fav")
					Toast.makeText(context,"Removed from fav",Toast.LENGTH_SHORT).show()
					
					
				}
				.addOnFailureListener { e->
					Log.d(TAG,"removeFromFavourite: Failed to remove from fav due to ${e.message}")
					Toast.makeText(context,"Failed to remove from fav due to ${e.message}",Toast.LENGTH_SHORT).show()
				}
		}
		
	}
}
