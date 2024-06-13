package vinh.le.bookappkotlin

import android.widget.Filter

class FilterPdfUser: Filter {
	//arrayList in which user want to search
	var filterList: ArrayList<ModelPdf>
	
	//adapter in which filter
	var adapterPdfUser: AdapterPdfUser
	
	//constructor
	constructor(filterList: ArrayList<ModelPdf>, adapterPdfUser: AdapterPdfUser):super() {
		this.filterList = filterList
		this.adapterPdfUser = adapterPdfUser
	}
	
	override fun performFiltering(constraint: CharSequence): FilterResults {
		var constraint: CharSequence? = constraint
		
		val results = FilterResults()
		//value to be searched should not be null and not empty
		if(constraint != null && constraint.isNotEmpty()) {
			//not null nor empty
			
			//change to upper case, or lowercase to avoid case sensitivity
			constraint = constraint.toString().uppercase()
			val filteredModels = ArrayList<ModelPdf>()
			for(i in filterList.indices){
				//validate if match
				if(filterList[i].title.uppercase().contains(constraint)){
					//searched value matched with title, add to list
					filteredModels.add(filterList[i])
				}
			}
			//return filtered list and value
			results.count = filterList.size
			results.values = filteredModels
		}
		else{
			// neither null or empty
			//return original state list or size
			results.count = filterList.size
			results.values = filterList
		}
		return results
	}
	
	override fun publishResults(constraint: CharSequence, results: FilterResults) {
		//apply filter change
		adapterPdfUser.pdfArrayList = results.values as ArrayList<ModelPdf>
		
		//notify change
		adapterPdfUser.notifyDataSetChanged()
	}
}