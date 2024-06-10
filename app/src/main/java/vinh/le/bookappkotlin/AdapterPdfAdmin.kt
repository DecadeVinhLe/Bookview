package vinh.le.bookappkotlin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import vinh.le.bookappkotlin.databinding.RowPdfAdminBinding

class AdapterPdfAdmin(context: Context, pdfArrayList: ArrayList<ModelPdf>) :
	RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>(), Filterable {
	
	private var context: Context = context
	
	// ArrayList
	public var pdfArrayList: ArrayList<ModelPdf> = pdfArrayList
	private val filterList: ArrayList<ModelPdf> = ArrayList(pdfArrayList)
	
	// Filter object
	private var filter: FilterPdfAdmin? = null
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdmin {
		// Bind/inflate layout row_pdf_admin.xml
		val binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false)
		return HolderPdfAdmin(binding)
	}
	
	override fun getItemCount(): Int {
		return pdfArrayList.size // Items count
	}
	
	override fun onBindViewHolder(holder: HolderPdfAdmin, position: Int) {
		/* Get Data, Handle Click etc */
		// Get Data
		val model = pdfArrayList[position]
		val pdfId = model.id
		val categoryId = model.categoryId
		val title = model.title
		val description = model.description
		val pdfUrl = model.url
		val timestamp = model.timestamp
		
		// Convert timestamp to dd/mm/yyyy
		val formattedDate = MyApplication.formatTimeStamp(timestamp)
		
		// Set data
		holder.titleTv.text = title
		holder.descriptionTv.text = description
		holder.dateTv.text = formattedDate
		
		// Load further details like category, pdf from url
		// Category ID
		MyApplication.loadCategory(categoryId, holder.categoryTv)
		
		// Pass null for page number
		MyApplication.loadPdfFromUrlSinglePage(pdfUrl, title, holder.pdfView, holder.progressBar, null)
		
		// Load pdf size
		MyApplication.loadPdfSize(pdfUrl, title, holder.sizeTv)
		
		//handle click, show dialog with option
		holder.moreBtn.setOnClickListener{
		   moreOptionsDialog(model, holder)
		}
	}
	
	private fun moreOptionsDialog(model: ModelPdf, holder: AdapterPdfAdmin.HolderPdfAdmin) {
		//get id, url, title
		val bookId = model.id
		val bookUrl = model.url
		val bookTitle = model.title
		
		// options to show in dialog
		val options = arrayOf("Edit", "Delete")
		
		//alert dialog
		val builder = AlertDialog.Builder(context)
		builder.setTitle("Choose Option")
			.setItems(options) { dialog, position ->
			  //handle item click
				if (position==0){
				 //edit is click
					val intent = Intent(context,PdfEditActivity::class.java)
					 intent.putExtra("bookId", bookId)
					context.startActivity(intent)
				}
				else if (position==1){
				  //delete is click, lets create function in MyApplication class
				}
			}
			.show()
	}
	
	override fun getFilter(): Filter {
		if (filter == null) {
			filter = FilterPdfAdmin(filterList, this)
		}
		return filter as FilterPdfAdmin
	}
	
	/* View Holder class for row_pdf_admin */
	inner class HolderPdfAdmin(binding: RowPdfAdminBinding) : RecyclerView.ViewHolder(binding.root) {
		// UI Views of row_pdf_admin.xml
		val pdfView = binding.pdfView
		val progressBar = binding.progressBar
		val titleTv = binding.titleTV
		val descriptionTv = binding.descriptionTv
		val categoryTv = binding.categoryTv
		val sizeTv = binding.sizeTv
		val dateTv = binding.dateTv
		val moreBtn = binding.moreBtn
	}
}
