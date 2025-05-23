package com.example.memehub.data.model.realmModels

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.realmSetOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmSet
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.util.Date

class User: RealmObject{
    @PrimaryKey var _id: ObjectId = ObjectId()
    var uid: String = ""
    var name: String = ""
    var email: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var gender: String? = null
    var birthDate: String? = null
    var avatar: Avatar? = null
    var favoritePosts: RealmSet<Post> = realmSetOf()
    var posts : RealmList<Post> = realmListOf()
}

class Avatar: EmbeddedRealmObject{
    var displayUrl: String = ""
    var thumbnailUrl: String = ""
}