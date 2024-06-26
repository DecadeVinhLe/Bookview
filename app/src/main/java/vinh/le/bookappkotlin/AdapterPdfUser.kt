package vinh.le.bookappkotlin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import vinh.le.bookappkotlin.databinding.RowPdfUserBinding

class AdapterPdfUser : RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser>, Filterable {
	
	//context, get using constructor
	private var context:Context
	
	//arraylist to hold pdfs, get using constructor
	public var pdfArrayList:ArrayList<ModelPdf> // access in filter class
	
    public var filterList: ArrayList<ModelPdf>
	//viewBinding
	private lateinit var binding: RowPdfUserBinding
	
	//filter class to enable searching
	private var filter:FilterPdfUser? = null
	
	constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) {
		this.context = context
		this.pdfArrayList = pdfArrayList
		this.filterList = pdfArrayList
		
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfUser {
		
		//inflate layout
		binding = RowPdfUserBinding.inflate(LayoutInflater.from(context),parent,false)
		return HolderPdfUser(binding.root)
	}
	
	override fun getItemCount(): Int {
		return pdfArrayList.size //return list size number of pdf
	}
	
	override fun onBindViewHolder(holder: HolderPdfUser, position: Int) {
//		Get data, set data, handle click
		
		//get data
		val model = pdfArrayList[position]
		val bookId = model.id
		val categoryId = model.categoryId
		val title = model.title
		val description = model.description
		val uid = model.uid
		val url = model.url
		val timestamp = model.timestamp
		
//		convert time
		val date = MyApplication.formatTimeStamp(timestamp)
		
		//set data
		holder.titleTV.text = title
		holder.descriptionTv.text = description
		holder.dateTv.text = date
		
		MyApplication.loadPdfFromUrlSinglePage(url,title,holder.pdfView,holder.progressBar,null)
	
		MyApplication.loadCategory(categoryId, holder.categoryTv)
		
		MyApplication.loadPdfSize(url, title,holder.sizeTv)
		
		//handle click, open pdf detail page
		holder.itemView.setOnClickListener{
			//pass book id in intent , that will be used to get info
			val intent = Intent(context,PdfViewActivity::class.java)
			intent.putExtra("bookId",bookId)
			context.startActivity(intent)
		}
	}
	
	/*ViewHolder class*/
	inner class HolderPdfUser(itemView: View): RecyclerView.ViewHolder(itemView){
	  //init UI component of row_pdf_user.xml
		var pdfView = binding.pdfView
		var progressBar = binding.progressBar
		var titleTV = binding.titleTV
		var descriptionTv = binding.descriptionTv
		var categoryTv = binding.categoryTv
		var sizeTv = binding.sizeTv
		var dateTv = binding.dateTv
	}
	
	override fun getFilter(): Filter {
		if(filter == null){
			filter = FilterPdfUser(filterList,this)
		}
		return filter as FilterPdfUser
	}
	
	
}