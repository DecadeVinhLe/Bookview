package vinh.le.bookappkotlin

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.github.barteksc.pdfviewer.PDFView
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
		
		// create a static method to convert timestamp to proper date
		fun formatTimeStamp(timestamp: Long): String {
			val cal = Calendar.getInstance(Locale.ENGLISH)
			cal.timeInMillis = timestamp
			// format dd/MM/yyyy
			val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
			return dateFormat.format(cal.time)
		}
		
		// function to get pdf size
		fun loadPdfSize(pdfUrl: String, pdfTitle: String, sizeTv: TextView) {
			val TAG = "PDF_SIZE_TAG"
			// using url to get file size from storage
			val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
			ref.metadata
				.addOnSuccessListener { storageMetaData ->
					Log.d(TAG, "loadPdfSize: got metadata")
					val bytes = storageMetaData.sizeBytes.toDouble()
					Log.d(TAG, "loadPdfSize: Size Bytes $bytes")
					
					// convert bytes to KB/MB
					val kb = bytes / 1024
					val mb = kb / 1024
					if (mb >= 1) {
						sizeTv.text = "${String.format("%.2f", mb)} MB"
					} else if (kb >= 1) {
						sizeTv.text = "${String.format("%.2f", kb)} KB"
					} else {
						sizeTv.text = "${String.format("%.2f", bytes)} bytes"
					}
				}
				.addOnFailureListener { e ->
					// failed to get metadata
					Log.d(TAG, "loadPdfSize: Failed to get metadata due to ${e.message}")
				}
		}
		
		fun loadPdfFromUrlSinglePage(
			pdfUrl: String, pdfTitle: String, pdfView: PDFView,
			progressBar: ProgressBar, pagesTv: TextView?
		) {
			// using url to get file from storage
			val TAG = "PDF_THUMBNAIL_TAG"
			val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
			ref.getBytes(Constants.MAX_BYTES_PDF)
				.addOnSuccessListener { bytes ->
					Log.d(TAG, "loadPdfFromUrlSinglePage: got the file")
					
					// Set to PDFView
					pdfView.fromBytes(bytes)
						.pages(0) // show first page only
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
							// pdf loaded from url then setup thumbnail
							progressBar.visibility = View.INVISIBLE
							
							// if pagesTv is not null then set page count
							if (pagesTv != null) {
								pagesTv.text = "$nbPages"
							}
						}
						.load()
				}
				.addOnFailureListener { e ->
					// failed to get the file
					Log.d(TAG, "loadPdfFromUrlSinglePage: Failed to get file due to ${e.message}")
				}
		}
		
		fun loadCategory(categoryId: String, categoryTv: TextView) {
			// load category using id from firebase
			val ref = FirebaseDatabase.getInstance().getReference("Categories")
			ref.child(categoryId)
				.addListenerForSingleValueEvent(object : ValueEventListener {
					override fun onDataChange(snapshot: DataSnapshot) {
						// get category
						val category = "${snapshot.child("category").value}"
						
						// set category
						categoryTv.text = category
					}
					
					override fun onCancelled(error: DatabaseError) {
						// failed to get category
						Log.d("CATEGORY_LOAD_ERROR", "Failed to load category: ${error.message}")
					}
				})
		}
	}
}
