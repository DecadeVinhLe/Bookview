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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import vinh.le.bookappkotlin.databinding.RowCommentBinding

class AdapterComment: RecyclerView.Adapter<AdapterComment.HolderComment> {
	//context
	val context: Context
	
	//arraylist to hold comment
	val commentArrayList:ArrayList<ModelComment>
	
	//firebase auth
	private lateinit var firebaseAuth: FirebaseAuth
	
	//viewbinding
	private lateinit var binding: RowCommentBinding
	
	//constructor
	constructor(context: Context, commentArrayList: ArrayList<ModelComment>) {
		this.context = context
		this.commentArrayList = commentArrayList
		
		//init firebase auth
		firebaseAuth = FirebaseAuth.getInstance()
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderComment {
	  //Bind/inflate view
		binding = RowCommentBinding.inflate(LayoutInflater.from(context),parent,false)
		return HolderComment(binding.root)
	}
	
	override fun getItemCount(): Int {
	 return commentArrayList.size //return size of arraylist || number of items in list
	}
	
	override fun onBindViewHolder(holder: HolderComment, position: Int) {
	 /*get data, set data, handle click*/
		//get data
		val model = commentArrayList[position]
		
		val id = model.id
		val bookId = model.bookId
		val timestamp = model.timestamp
		val comment = model.comment
		val uid = model.uid
		
		//format timestamp
		val date = MyApplication.formatTimeStamp(timestamp.toLong())
		
		//set data
		holder.commentTv.text = comment
		holder.dateTv.text = date
		
		//load user through uid
		loadUserDetails(model, holder)
		
		//handle click, delete comment show dialog
		holder.itemView.setOnClickListener{
			/*Requirements to delete a comment
			* User must logged in
			* uid in comment*/
			if(firebaseAuth.currentUser != null && firebaseAuth.uid == uid) {
			  deleteCommentDialog(model,holder)
			}
			
		}
		
	}
	
	private fun deleteCommentDialog(model: ModelComment, holder:HolderComment) {
		//alert dialog
		var builder = AlertDialog.Builder(context)
		builder.setTitle("Delete Comment")
			.setMessage("Are you sure you want to delete this comment?")
			.setPositiveButton("Delete") { d, e_ ->
				
				val bookId = model.bookId
				val commentId = model.id
				//delete comment
				val ref = FirebaseDatabase.getInstance().getReference("Books")
				ref.child(bookId).child("Comments").child(commentId)
					.removeValue()
					.addOnSuccessListener {
					  Toast.makeText(context,"Comment deleted...",Toast.LENGTH_SHORT).show()
					}
					.addOnFailureListener{e->
						//failed to delete
						Toast.makeText(context,"Failed to delete comment due to ${e.message}",Toast.LENGTH_SHORT).show()
						
					}

			
			}
			.setNegativeButton("Cancel") { d, e_ ->
				d.dismiss()
			}
			.show()

	}
	
	private fun loadUserDetails(model: ModelComment, holder: AdapterComment.HolderComment) {
		val uid = model.uid
		val ref = FirebaseDatabase.getInstance().getReference("Users")
		ref.child(uid)
			.addListenerForSingleValueEvent(object : ValueEventListener {
				override fun onDataChange(snapshot: DataSnapshot) {
					//get name, profile image
					val name = "${snapshot.child("name").value}"
					val profileImage = "${snapshot.child("profileImage").value}"
					
					//set data
					holder.nameTv.text = name
					try {
						Glide.with(context)
							.load(profileImage)
							.placeholder(R.drawable.ic_person_gray)
							.into(holder.profileTv)
					}
					catch (e:Exception){
					 // image not found, set default
						holder.profileTv.setImageResource(R.drawable.ic_person_gray)
					}
					
				}
				
				override fun onCancelled(error: DatabaseError) {
					TODO("Not yet implemented")
				}
			
			})
	}
	
	//ViewHolder for row comment
	inner class HolderComment(itemView: View) : RecyclerView.ViewHolder(itemView){
		val profileTv: ImageView = binding.profileIV
		val nameTv: TextView = binding.nameTv
		val dateTv: TextView = binding.dateTv
		val commentTv: TextView = binding.commentTv
	}
	

	
}