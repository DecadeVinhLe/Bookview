package vinh.le.bookappkotlin

class ModelComment {
	//variable
	var id = ""
	var bookId = ""
	var timestamp = ""
	var comment = ""
	var uid = ""
	
	//empty constructor
	constructor()
	
	//param constructor
	constructor(id: String, bookId: String, timestamp: String, comment: String, uid: String) {
		this.id = id
		this.bookId = bookId
		this.timestamp = timestamp
		this.comment = comment
		this.uid = uid
	}
	
}