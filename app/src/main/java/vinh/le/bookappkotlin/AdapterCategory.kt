package vinh.le.bookappkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import vinh.le.bookappkotlin.databinding.RowCategoryBinding

class AdapterCategory : RecyclerView.Adapter<AdapterCategory.HolderCategory>, Filterable {
	private val context: Context
	public var categoryArrayList: ArrayList<ModelCategory>
	private var filterList: ArrayList<ModelCategory>
	
	private var filter: FilterCategory? = null
	
	// View binding
	private lateinit var binding: RowCategoryBinding
	
	//constructor
	constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>) {
		this.context = context
		this.categoryArrayList = categoryArrayList
		this.filterList = categoryArrayList
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
		// Inflate binding
		binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
		return HolderCategory(binding.root)
	}
	
	override fun onBindViewHolder(holder: HolderCategory, position: Int) {
		// Get data
		val model = categoryArrayList[position]
		val id = model.id
		val category = model.category
		val uid = model.uid
		val timestamp = model.timestamp
		
		// Set data
		holder.categoryTv.text = category
		
		// Handle on click, delete category
		holder.deleteBtn.setOnClickListener {
			// Create alert dialog
			val builder = AlertDialog.Builder(context)
			builder.setTitle("Delete")
				.setMessage("Are you sure you want to delete this item?")
				.setPositiveButton("Confirm") { dialog, _ ->
					// Handle deletion here
					// You can call a function to remove the item  from the list and notify the adapter
					categoryArrayList.removeAt(position)
					notifyItemRemoved(position)
					notifyItemRangeChanged(position, itemCount)
					dialog.dismiss()
					Toast.makeText(context, "Item deleted...", Toast.LENGTH_SHORT).show()
					deleteCategory(model, holder)
				}
				.setNegativeButton("Cancel") { dialog, _ ->
					dialog.dismiss()
				}
				.show()
		}
	}
	
	private fun deleteCategory(model: ModelCategory, holder: HolderCategory) {
		//get id of category to delete
		val id = model.id
		// Firebase DB > category > id
		val ref = FirebaseDatabase.getInstance().getReference("Categories")
		ref.child(id)
			.removeValue()
			.addOnSuccessListener {
				Toast.makeText(context, "Deleted...", Toast.LENGTH_SHORT).show()
				
			}
			.addOnFailureListener { e ->
				Toast.makeText(
					context,
					"Unable to delete due to ${e.message} ...",
					Toast.LENGTH_SHORT
				).show()
			}
	}
	
	override fun getItemCount(): Int {
		return categoryArrayList.size // Number of items in the list
	}
	
	inner class HolderCategory(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var categoryTv: TextView = binding.categoryTv
		var deleteBtn: ImageView = binding.deleteBtn
	}
	
	override fun getFilter(): Filter {
		if (filter == null) {
			filter = FilterCategory(filterList,this)
		}
		return filter as FilterCategory
	}
}
