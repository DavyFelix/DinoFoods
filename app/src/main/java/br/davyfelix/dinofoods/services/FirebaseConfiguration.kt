package br.davyfelix.dinofoods.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.content.Context

object FirebaseConfig {

    private var databaseReference: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    @JvmStatic
    fun getFirebaseDatabase(): DatabaseReference {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().reference
        }
        return databaseReference!!
    }

    @JvmStatic
    fun getFirebaseAuth(): FirebaseAuth {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance()
        }
        return firebaseAuth!!
    }
}