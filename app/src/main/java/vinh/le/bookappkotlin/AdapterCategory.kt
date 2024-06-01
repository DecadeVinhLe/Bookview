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

class AdapterCategory : RecyclerView.Adapter<AdapterCategory.ModelCategory>{
	
	//context
	private var context: Context
	
	//ArrayList
	private var categoryArrayList: ArrayList<ModelCategory>
	
	//view binding
	private lateinit var binding:RowCategoryBinding
	
	//constructor
	constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>){
		this.context = context
		this.categoryArrayList = categoryArrayList
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelCategory {
		//inflate binding rowBinding
		binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
		return ModelCategory(binding.root)
	}
	
	override fun onBindViewHolder(holder:ModelCategory, position: Int) {
//		Get Data, Set Data, Handle On Clicks

//		get data
		val model = categoryArrayList[position]
		val id = model.id
		val category = model.category
		val uid = model.uid
		val timestamp = model.timestamp
		//set data
		holder.categoryTv.text = category
		//handle on click
		holder.deleteBtn.setOnClickListener(){
			//delete
			val builder = AlertDialog.Builder(context)
			builder.setTitle("Delete")
					.setMessage("Are you sure delete this item?")
				.setPositiveButtonIcon("Confirm"){ a, d ->
				
				}
				.setNegativeButton("Cancel"){ a, d ->
					a.dismiss()}

		}
	}
	
	override fun getItemCount(): Int {
		return categoryArrayList.size //number items in list
	}
	
	inner class ModelCategory(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var categoryTv: TextView = binding.categoryTv
	 var deleteBtn : ImageView = binding.deleteBtn
	}
	
}

private fun AlertDialog.Builder.setPositiveButtonIcon(s: String, any: Any) {
	TODO("Not yet implemented")
}
