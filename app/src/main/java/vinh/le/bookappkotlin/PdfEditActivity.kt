package vinh.le.bookappkotlin

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import vinh.le.bookappkotlin.databinding.ActivityPdfEditBinding

class PdfEditActivity : AppCompatActivity() {
	
	//view Binding
    private lateinit var binding: ActivityPdfEditBinding
	
	private companion object{
		private const val TAG = "PDF_EDIT_TAG"
	}
	
	//book id from intent
	private var bookId = ""
	
	//progress Dialog
	private lateinit var progressDialog: ProgressDialog
	
	// arraylist to hold category Title
	private lateinit var categoryTitleArrayList:ArrayList<String>
	
	// arraylist to hold category id
	private lateinit var categoryIdArrayList:ArrayList<String>
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityPdfEditBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		//GET BOOK ID FROM INTENT
		bookId = intent.getStringExtra("bookId")!!
	   
	   //setup  progress dialog
	   progressDialog = ProgressDialog(this)
	   progressDialog.setTitle("Please Wait")
	   progressDialog.setCanceledOnTouchOutside(false)
		
	   loadCategories()
		loadBookInfo()
		
		//handle click, goback
		binding.backBtn.setOnClickListener {
			OnBackPressedDispatcher()
		}
		//handle click, pick category
		binding.categoryTv.setOnClickListener {
		   categoryDialog()
		}
		//handle click, begin update
		binding.submitBtn.setOnClickListener {
		 validateData()
		}
	}
	
	private fun loadBookInfo() {
		Log.d(TAG,"loadBookInfo: Loading Book Info")
		
		val ref = FirebaseDatabase.getInstance().getReference("Books")
		ref.child(bookId)
			.addListenerForSingleValueEvent(object: ValueEventListener{
				override fun onDataChange(snapshot: DataSnapshot) {
					//get book info
					selectedCategoryId = snapshot.child("categoryId").value.toString()
					val description = snapshot.child("description").value.toString()
					val title = snapshot.child("title").value.toString()
					
					//set to view
					binding.titleEt.setText(title)
					binding.descriptionEt.setText(description)
					
					//load book category info using category ID
					Log.d(TAG,"onDataChange: Loading book category info")
					val refBookCategory = FirebaseDatabase.getInstance().getReference("Categories")
						refBookCategory.child(selectedCategoryId)
							.addListenerForSingleValueEvent(object: ValueEventListener{
								override fun onDataChange(snapshot: DataSnapshot) {
								 //get category
									val category = snapshot.child("category").value
								 //set to Textview
									binding.categoryTv.text = category.toString()
								}
								
								override fun onCancelled(error: DatabaseError) {
								
								}
							})
				}
				
				override fun onCancelled(error: DatabaseError) {
				
				}
			})
	}
	
	private var title = ""
	private var description = ""
	private fun validateData() {
		//get dât
		title = binding.titleEt.text.toString().trim()
		description = binding.descriptionEt.text.toString().trim()
		
		//validate data
		if(title.isEmpty()){
			Toast.makeText(this,"Enter Title", LENGTH_SHORT).show()
		}
		else if(description.isEmpty())
		{
			Toast.makeText(this,"Enter Description", LENGTH_SHORT).show()
		}
		else if(selectedCategoryId.isEmpty())
		{
			Toast.makeText(this,"Pick Category", LENGTH_SHORT).show()
		}
		else{
			updatePdf()
		}
	
	}
	
	private fun updatePdf() {
	  Log.d(TAG, "updatePdf: Starting Update Pdf Info...")
		
		//show progress
		progressDialog.setMessage("Updating Pdf Info")
		progressDialog.show()
		
		//setup data to update to db, spellings of keys must be same as in firebase
		val hashMap = HashMap<String,Any>()
		hashMap["title"] = "$title"
		hashMap["description"] = "$description"
		hashMap["categoryId"] = "$selectedCategoryId"
	 
		//start updating
		val ref = FirebaseDatabase.getInstance().getReference("Books")
		ref.child(bookId)
			.updateChildren(hashMap)
			.addOnSuccessListener {
				Log.d(TAG,"updatePdf: Updated Successfully...")
				Toast.makeText(this,"Updated Successfully...", LENGTH_SHORT).show()
			
			}
			.addOnFailureListener {e ->
				Log.d(TAG,"updatePdf: Failed to update due to ${e.message}")
				progressDialog.dismiss()
				Toast.makeText(this,"Failed to update Pdf info due to ${e.message}", LENGTH_SHORT).show()
			}
	}
	
	private var selectedCategoryId = ""
	private var selectedCategoryTitle = ""
	private fun categoryDialog() {
		/*Show dialog to pick the category uf pdf/book */
		//get string array of categories from array
		//make string array from arrayList of string
		val categoriesArray = arrayOfNulls<String>(categoryIdArrayList.size)
		for (i in categoryIdArrayList.indices){
			categoriesArray[i] = categoryTitleArrayList[i]
		}
		//alert Dialog
		val builder = AlertDialog.Builder(this)
		builder.setTitle("Choose Category")
			.setItems(categoriesArray){dialog,position ->
				//handle click, save clicked category id and title
				selectedCategoryId = categoryIdArrayList[position]
				selectedCategoryTitle = categoryTitleArrayList[position]
				
				//set category to textview
				binding.categoryTv.text = selectedCategoryTitle
				
			}
			.show() // show dialog
	}
	
	private fun loadCategories() {
	  Log.d(TAG, "loadCategories: Loading Categories...")
		
		categoryTitleArrayList = ArrayList()
		categoryIdArrayList = ArrayList()
		
		val ref = FirebaseDatabase.getInstance().getReference("Categories")
		ref.addListenerForSingleValueEvent(object: ValueEventListener {
			override fun onDataChange(snapshot: DataSnapshot) {
				//clear list before adding data
				categoryIdArrayList.clear()
				categoryTitleArrayList.clear()
				
				for (ds in snapshot.children){
					val id = "${ds.child("id").value}"
					val category = "${ds.child("category").value}"
					
					categoryIdArrayList.add(id)
					categoryTitleArrayList.add(category)
					
					Log.d(TAG, "onDataChange: Category ID $id")
					Log.d(TAG, "onDataChange: Category $category")
					
				}
			}
			
			override fun onCancelled(error: DatabaseError) {
			
			}
		})
	}
}