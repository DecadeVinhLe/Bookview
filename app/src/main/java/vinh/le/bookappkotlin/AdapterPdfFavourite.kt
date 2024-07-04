package vinh.le.bookappkotlin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import vinh.le.bookappkotlin.databinding.RowPdfFavouriteBinding

class AdapterPdfFavourite: RecyclerView.Adapter<AdapterPdfFavourite.HolderPdfFavourite>{
	
	//context
	private val context: Context
	// ArrayList to hold books
	private var booksArrayList : ArrayList<ModelPdf>
	
	//view binding
	private lateinit var binding: RowPdfFavouriteBinding
	
	//constructor
	constructor(context: Context, booksArrayList: ArrayList<ModelPdf>) {
		this.context = context
		this.booksArrayList = booksArrayList
	}
	
	//View holder class to manage UI views of row_pdf_fav
	inner class HolderPdfFavourite(itemView: View) : RecyclerView.ViewHolder(itemView) {
	 
		//init UI Views
		var pdfView = binding.pdfView
		var progressBar = binding.progressBar
		var titleTv = binding.titleTv
		var removeFavBtn = binding.removeFavBtn
		var descriptionTv = binding.descriptionTv
		var categoryTv = binding.categoryTv
		var sizeTv = binding.sizeTv
		var dateTv = binding.dateTv
	}
	
	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int,
	): HolderPdfFavourite {
		//binding/inflate row_pdf_favourite.xml
		binding = RowPdfFavouriteBinding.inflate(LayoutInflater.from(context),parent,false)
		
		return HolderPdfFavourite(binding.root)
	}
	
	override fun onBindViewHolder(holder: HolderPdfFavourite, position: Int) {
		//Get data, set data, handle click
		
		//get data
		val model = booksArrayList[position]
		
		loadBookDetails(model,holder)
		
		//hande click, open pdf details then pass bookId
		holder.itemView.setOnClickListener{
			val intent = Intent(context,PdfDetailActivity::class.java)
			intent.putExtra("bookId",model.id)
			context.startActivity(intent)
		}
		
		//handle click remove from fav
		holder.removeFavBtn.setOnClickListener{
		  MyApplication.removeFromFavourite(context,model.id)
		}
		
	}
	
	private fun loadBookDetails(model: ModelPdf, holder: HolderPdfFavourite) {
	 val bookId = model.id
		
	 val ref = FirebaseDatabase.getInstance().getReference("Books")
		ref.child(bookId)
			.addListenerForSingleValueEvent(object: ValueEventListener{
				override fun onDataChange(snapshot: DataSnapshot) {
					//get book info
					val categoryId = "${snapshot.child("categoryId").value}"
					val description ="${snapshot.child("description").value}"
					val downloadsCount = "${snapshot.child("downloadCount").value}"
				    val timestamp = "${snapshot.child("timestamp").value}"
					val title = "${snapshot.child("title").value}"
					val uid = "${snapshot.child("uid").value}"
					val url = "${snapshot.child("url").value}"
					val viewsCount = "${snapshot.child("viewsCount").value}"
					
					//set data to model
					model.isFavorite = true
					model.title = title
					model.description = description
					model.categoryId = categoryId
					model.timestamp=timestamp.toLong()
					model.uid = uid
					model.url = url
					model.downloadsCount = downloadsCount.toLong()
					
					
					//format date
					val date = MyApplication.formatTimeStamp(timestamp.toLong())
					
					MyApplication.loadCategory(categoryId,holder.categoryTv)
					MyApplication.loadPdfFromUrlSinglePage(url, title,holder.pdfView,holder.progressBar,null);
				    MyApplication.loadPdfSize(url,title,holder.sizeTv)
					
					holder.titleTv.text = title
					holder.descriptionTv.text = description
					holder.dateTv.text = date
				}
				override fun onCancelled(error: DatabaseError) {
				}
			})
	}
	
	override fun getItemCount(): Int {
		return booksArrayList.size
	}
	
	
}