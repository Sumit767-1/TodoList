package pathak.sumit698.alarm.currenttodolist

class CustomDetails {

    var id:Int
    var title: String
    var category: String
    var datetime: String?=null

    constructor(title: String, category: String, datetime: String?=null, id:Int) {
        this.title = title
        this.category = category
        this.datetime = datetime
        this.id =id


    }
}
