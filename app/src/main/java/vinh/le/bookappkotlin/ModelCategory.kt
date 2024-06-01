package vinh.le.bookappkotlin

class ModelCategory {
	//variables match firbase database
	var id: String = ""
	var category: String = ""
	var timestamp: Long = 0
	var uid: String = ""
	// empty constructor required in firebase
	constructor()
	// parameterized constructor
	constructor(id:String, category:String, timestamp:Long, uid:String){
		this.id = id
		this.category = category
		this.timestamp = timestamp
		this.uid = uid

	}
	
}