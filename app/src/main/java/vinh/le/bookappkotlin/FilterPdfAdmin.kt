package vinh.le.bookappkotlin

import android.widget.Filter

/* Used to filter data */
class FilterPdfAdmin(
	private val filterList: ArrayList<ModelPdf>,
	private val adapterPdfAdmin: AdapterPdfAdmin
) : Filter() {
	
	override fun performFiltering(constraint: CharSequence?): FilterResults {
		val results = FilterResults()
		if (constraint != null && constraint.isNotEmpty()) {
			val searchString = constraint.toString().lowercase()
			val filteredModels = ArrayList<ModelPdf>()
			for (pdf in filterList) {
				if (pdf.title.lowercase().contains(searchString)) {
					filteredModels.add(pdf)
				}
			}
			results.count = filteredModels.size
			results.values = filteredModels
		} else {
			results.count = filterList.size
			results.values = filterList
		}
		return results
	}
	
	override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
		if (results != null && results.values is ArrayList<*>) {
			adapterPdfAdmin.pdfArrayList = results.values as ArrayList<ModelPdf>
			adapterPdfAdmin.notifyDataSetChanged()
		}
	}
}
