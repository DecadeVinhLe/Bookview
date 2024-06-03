package vinh.le.bookappkotlin

import android.widget.Filter

class FilterCategory: Filter {
	// Arraylist in which we want to search
	private var filterList: ArrayList<ModelCategory>
	
	// Adapter in which filter needs to be implemented
	private var adapterCategory: AdapterCategory
	
	// Constructor
	constructor(filterList: ArrayList<ModelCategory>, adapterCategory: AdapterCategory) {
		this.filterList = filterList
		this.adapterCategory = adapterCategory
	}
	
	override fun performFiltering(constraint: CharSequence?): FilterResults {
		var constraint = constraint
		val results = FilterResults()
		
		if (constraint != null && constraint.isNotEmpty()) {
			// Convert the constraint to uppercase to avoid case sensitivity
			 constraint = constraint.toString().uppercase()
			val filteredModels: ArrayList<ModelCategory> = ArrayList()
			
			// Loop through the original filter list
			for (i in 0 until filterList.size) {
				// Check if the item's category contains the filter pattern
				if (filterList[i].category.uppercase().contains(constraint)) {
					filteredModels.add(filterList[i])
				}
			}
			
			results.values = filteredModels
			results.count = filteredModels.size
		} else {
			results.values = filterList
			results.count = filterList.size
		}
		
		return results
	}
	
	override fun publishResults(constraint: CharSequence?, results: FilterResults) {
		// Update the adapter's data with the filtered list
		adapterCategory.categoryArrayList = results.values as ArrayList<ModelCategory>
		// Notify the adapter about the dataset change
		adapterCategory.notifyDataSetChanged()
	}
}
