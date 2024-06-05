package vinh.le.bookappkotlin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import vinh.le.bookappkotlin.databinding.ActivityDashboardAdminBinding

class DashboardAdminActivity : AppCompatActivity() {
	//view binding
	private lateinit var binding: ActivityDashboardAdminBinding
	//firebase auth
	private lateinit var firebaseAuth: FirebaseAuth
	
	//ArrayList to hold categories
	private lateinit var categoryArrayList:ArrayList<ModelCategory>
	//adapter
	private lateinit var adapterCategory:AdapterCategory
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
		setContentView(binding.root)
		//init firebase auth
		firebaseAuth = FirebaseAuth.getInstance()
		checkUser()
		loadCategories()
		
		//search
		binding.searchEt.addTextChangedListener(object: TextWatcher{
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
			}
			
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
				//called as and when user type anything
				try{
				  adapterCategory.filter.filter(s)
				} catch (e: Exception){
				
				}
			}
			override fun afterTextChanged(s: Editable?) {
			
			}
		})
		
		//handle log out which move admin to main page
		binding.logoutBtn.setOnClickListener {
			firebaseAuth.signOut()
			startActivity(Intent(this,MainActivity::class.java))
			finish()
		}
//		//handle add category
		binding.addCategoryBtn.setOnClickListener {
			startActivity(Intent(this,CategoryAddActivity::class.java))
		    checkUser()
		}
		//handle click, start add pdf page
		binding.addPdfFad.setOnClickListener {
			startActivity(Intent(this,PdfActivity::class.java))
		}
	}
	
	private fun loadCategories() {
	 //init arraylist
		categoryArrayList = ArrayList()
		
		// get all categories from firebase db ... Firebase DB-> Realtime Category
		val ref = FirebaseDatabase.getInstance().getReference("Categories")
		ref.addValueEventListener(object : ValueEventListener {
			override fun onDataChange(snapshot: DataSnapshot){
				//clear list before starting adding data into it
				categoryArrayList.clear()
				for (ds in snapshot.children){
					//get data as model
					val model = ds.getValue(ModelCategory::class.java)
					
					//add to arrayList
					categoryArrayList.add(model!!)
				}
				//setup adapter
				adapterCategory = AdapterCategory(this@DashboardAdminActivity,categoryArrayList)
				//set adapter to recyclerview
				binding.categoryRv.adapter = adapterCategory
			}
			override fun onCancelled(error: DatabaseError) {
			
			}
		})
	}
	
	private fun checkUser() {
		//		get current user
		val firebaseUser = firebaseAuth.currentUser
		if (firebaseUser == null){
//			not allow to login, admin can stay in user dashboard without login too
			binding.subTitleTV.text = getString(R.string.not_logged_in)
		} else {
			//Logged in, get and show user info
			val email = firebaseUser.email
			//set in textview of toolbar
			binding.subTitleTV.text = email
		}
	}
}