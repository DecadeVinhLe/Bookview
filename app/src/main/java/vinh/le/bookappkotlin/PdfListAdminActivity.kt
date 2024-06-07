package vinh.le.bookappkotlin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import vinh.le.bookappkotlin.databinding.ActivityPdfListAdminBinding

class PdfListAdminActivity : AppCompatActivity() {
	
	//viewBinding
	private lateinit var binding: ActivityPdfListAdminBinding
	
	private companion object{
		const val TAG = "PDF_LIST_ADMIN_TAG"
	}
 
	//categoryId, title
	private var categoryId = ""
	private var category = ""
	
	//arrayList to hold books
	private lateinit var pdfArrayList:ArrayList<ModelPdf>
	//adapter
	private lateinit var adapterPdfAdmin: AdapterPdfAdmin
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityPdfListAdminBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		//get from intent, that pass from adapter
		val intent = intent
		categoryId = intent.getStringExtra("categoryId")!!
		category = intent.getStringExtra("category")!!
		
		//set pdf category
		binding.subTitleTV.text = category
		
		//load pdf/book
		loadPdfList()
		
		//search
		binding.searchEt.addTextChangedListener(object: TextWatcher{
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
				TODO("Not yet implemented")
			}
			
			override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
				//filter data
				try{
					adapterPdfAdmin.filter!!.filter(s)
				}
				catch (e: Exception){
				 Log.d(TAG,"onTextChanged: ${e.message}")
				}
				
			}
			
			override fun afterTextChanged(s: Editable?) {
				TODO("Not yet implemented")
			}
		})
	}
	
	private fun loadPdfList() {
		//init arrayList
		pdfArrayList = ArrayList()
		
		val ref = FirebaseDatabase.getInstance().getReference("Books")
		ref.orderByChild("categoryId").equalTo(categoryId)
			.addValueEventListener(object: ValueEventListener{
				override fun onDataChange(snapshot: DataSnapshot) {
					//clear list before start adding data into it
					pdfArrayList.clear()
					for(ds in snapshot.children){
						//get data
						val model = ds.getValue(ModelPdf::class.java)
						//add to List
						if (model != null) {
							pdfArrayList.add(model)
							Log.d(TAG, "onDataChange: ${model.title} ${model.categoryId}")
						}
					}
					//setup adapter
					adapterPdfAdmin = AdapterPdfAdmin(this@PdfListAdminActivity, pdfArrayList)
					binding.booksRv.adapter = adapterPdfAdmin
					
				}
				
				override fun onCancelled(error: DatabaseError) {
				
				}
			})
	
	}
}