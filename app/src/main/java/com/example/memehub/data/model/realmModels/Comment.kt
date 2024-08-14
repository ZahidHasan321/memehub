package com.example.memehub.data.model.realmModels

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmInstant

class Comment: EmbeddedRealmObject  {
    var content : String = ""
    var createdAt : RealmInstant = RealmInstant.now()
    var commentor: User? = null
}