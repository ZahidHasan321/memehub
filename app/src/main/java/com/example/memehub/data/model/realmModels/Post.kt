package com.example.memehub.data.model.realmModels

import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.MutableRealmInt
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Post : RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var title: String = ""
    var caption: String = ""
    var image: String = ""
    var createdAt: RealmInstant = RealmInstant.now()
    var likes : MutableRealmInt = MutableRealmInt.create(0)
    var dislikes: MutableRealmInt = MutableRealmInt.create(0)
    var reactors: RealmList<Reactor> = realmListOf()
    var comments: RealmList<Comment> = realmListOf()
    val creator: RealmResults<User> by backlinks(User::posts)
}

class Reactor: EmbeddedRealmObject {
    var user: User? = null
    var score: Int = 0
}

