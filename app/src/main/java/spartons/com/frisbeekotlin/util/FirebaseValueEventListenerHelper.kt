package spartons.com.frisbeekotlin.util

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import spartons.com.frisbeekotlin.listeners.FirebaseObjectValueListener
import spartons.com.frisbeekotlin.models.Driver

class FirebaseValueEventListenerHelper constructor(private val firebaseObjectValueListener: FirebaseObjectValueListener) :
    ChildEventListener {

    override fun onCancelled(p0: DatabaseError) {

    }

    override fun onChildMoved(p0: DataSnapshot, p1: String?) {

    }

    override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        val driver = p0.getValue(Driver::class.java)
        driver?.let {
            firebaseObjectValueListener.onDriverChanged(it)
        }
    }

    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
        val driver = p0.getValue(Driver::class.java)
        driver?.let {
            firebaseObjectValueListener.onDriverOnline(it)
        }
    }

    override fun onChildRemoved(p0: DataSnapshot) {
        val driver = p0.getValue(Driver::class.java)
        driver?.let {
            firebaseObjectValueListener.onDriverOffline(it)
        }
    }
}