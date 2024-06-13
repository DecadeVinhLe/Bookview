package vinh.le.bookappkotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import vinh.le.bookappkotlin.databinding.ActivityDashboardUserBinding

class DashboardUserActivity : AppCompatActivity() {
	//view binding
	private lateinit var binding: ActivityDashboardUserBinding
	//firebase auth
	private lateinit var firebaseAuth: FirebaseAuth
	
	private lateinit var categoriesAdapter: ArrayList<ModelCategory>
	private lateinit var viewPagerAdapter: ViewPagerAdapter
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		binding = ActivityDashboardUserBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		//init firebase Auth
		firebaseAuth = FirebaseAuth.getInstance()
		checkUser()
		
		//init
		setupWithViewPagerAdapter(binding.viewPager)
		binding.tabLayout.setupWithViewPager(binding.viewPager)
		
		//handling click, logout
		binding.logoutBtn.setOnClickListener {
			firebaseAuth.signOut()
			checkUser()
		}
		
	}
	
	private fun setupWithViewPagerAdapter(viewPager: ViewPager) {
		viewPagerAdapter = ViewPagerAdapter(
			supportFragmentManager,
		FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,this)
		
		//init list
		categoriesAdapter = ArrayList()
		
		//load categories from database
		val ref = FirebaseDatabase.getInstance().getReference("Categories")
		ref.addListenerForSingleValueEvent(object: ValueEventListener {
			override fun onDataChange(snapshot: DataSnapshot) {
				//clear list before adding data
				categoriesAdapter.clear()
				
				//add data to models
				val modelAll = ModelCategory("01","ALL",1,"")
				val modelMostViewed = ModelCategory("01","Most Viewed",1,"")
				val modelMostDownloaded = ModelCategory("01","Most Downloaded",1,"")
				
				//add to
				categoriesAdapter.add(modelAll)
				categoriesAdapter.add(modelMostViewed)
				categoriesAdapter.add(modelMostDownloaded)
				viewPagerAdapter.addFragment(
					BooksUserFragment.newInstance(
						"${modelAll.id}",
						"${modelAll.category}",
						"${modelAll.uid}"
					),modelAll.category
				)
				
				viewPagerAdapter.addFragment(
					BooksUserFragment.newInstance(
						"${modelMostViewed.id}",
						"${modelMostViewed.category}",
						"${modelMostViewed.uid}"
					),modelMostViewed.category
				)
				
				viewPagerAdapter.addFragment(
					BooksUserFragment.newInstance(
						"${modelMostDownloaded.id}",
						"${modelMostDownloaded.category}",
						"${modelMostDownloaded.uid}"
					),modelMostDownloaded.category
				)
				//refresh list
				viewPagerAdapter.notifyDataSetChanged()
				
				//load from firebase db
				for(ds in snapshot.children){
					//get data
					val model = ds.getValue(ModelCategory::class.java)
					
					//add to list
					categoriesAdapter.add(model!!)
					
					//add to Adapter
					viewPagerAdapter.addFragment(
						BooksUserFragment.newInstance(
							"${model.id}",
							"${model.category}",
							"${model.uid}"
						),model.category
					)
					//refresh List
					viewPagerAdapter.notifyDataSetChanged()
				}
			}
			
			override fun onCancelled(error: DatabaseError) {
			
			}
			
		})
		
		//set adapter to viewpager
		viewPager.adapter = viewPagerAdapter
		
		
	}
	
	class ViewPagerAdapter(fm: FragmentManager, behavior: Int, context: Context): FragmentPagerAdapter(fm,behavior) {
		//holds list of fragments
		private val fragmentsList: ArrayList<BooksUserFragment> = ArrayList()
		//list of titles of categories
		private val fragmentTitleList: ArrayList<String> = ArrayList()
		//Context
		private val context: Context
		
		init{
			this.context = context
		}
		
		override fun getCount(): Int {
			return fragmentsList.size
		}
		
		override fun getItem(position: Int): Fragment {
		 return  fragmentsList[position]
		}
		
		override fun getPageTitle(position: Int): CharSequence? {
			return fragmentTitleList[position]
		}
		
		public fun addFragment(fragment: BooksUserFragment,title: String){
			//add fragment that passed as parameter in fragmentList
			fragmentsList.add(fragment)
			//add title that passed as parameter in fragmentTitleList
			fragmentTitleList.add(title)
		
		}
	}
	private fun checkUser() {
//		get current user
     val firebaseUser = firebaseAuth.currentUser
		if (firebaseUser == null){
//			not allow to login, goto main screen
			startActivity(Intent(this,MainActivity::class.java))
			finish()
		} else {
			//Logged in, get and show user info
			val email = firebaseUser.email
			//set in textview of toolbar
			binding.subTitleTV.text = email
		}
	}
	
}