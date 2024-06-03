package vinh.le.bookappkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import vinh.le.bookappkotlin.databinding.RowCategoryBinding

class AdapterCategory(
	private val context: Context,
	private val categoryArrayList: ArrayList<ModelCategory>
) : RecyclerView.Adapter<AdapterCategory.ModelCategoryViewHolder>() {
	
	// View binding
	private lateinit var binding: RowCategoryBinding
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelCategoryViewHolder {
		// Inflate binding
		binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
		return ModelCategoryViewHolder(binding.root)
	}
	
	override fun onBindViewHolder(holder: ModelCategoryViewHolder, position: Int) {
		// Get data
		val model = categoryArrayList[position]
		val id = model.id
		val category = model.category
		val uid = model.uid
		val timestamp = model.timestamp
		
		// Set data
		holder.categoryTv.text = category
		
		// Handle on click
		holder.deleteBtn.setOnClickListener {
			// Create alert dialog
			val builder = AlertDialog.Builder(context)
			builder.setTitle("Delete")
				.setMessage("Are you sure you want to delete this item?")
				.setPositiveButton("Confirm") { dialog, _ ->
					// Handle deletion here
					// You can call a function to remove the item from the list and notify the adapter
					categoryArrayList.removeAt(position)
					notifyItemRemoved(position)
					notifyItemRangeChanged(position, itemCount)
					dialog.dismiss()
					Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
				}
				.setNegativeButton("Cancel") { dialog, _ ->
					dialog.dismiss()
				}
				.show()
		}
	}
	
	override fun getItemCount(): Int {
		return categoryArrayList.size // Number of items in the list
	}
	
	inner class ModelCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var categoryTv: TextView = binding.categoryTv
		var deleteBtn: ImageView = binding.deleteBtn
	}
}
