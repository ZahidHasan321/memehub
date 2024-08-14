package com.example.memehub.data.model.realmModels

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class Rating : RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var user : User? = null
    var rating: Float = 0.0f
}