import com.example.happyplaces.models.HappyPlaceModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirebasePlaceRepository {
    private val db = Firebase.firestore.collection("places")

    suspend fun addPlace(place: HappyPlaceModel) {
        try {
            db.add(place.toHashMap()).await()
        } catch (e: Exception) {
            throw PlaceSyncException("Failed to sync place: ${e.message}")
        }
    }

    suspend fun getPlaces(): List<HappyPlaceModel> {
        return try {
            db.get().await().documents.map { it.toPlace() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

// Extension functions
private fun HappyPlaceModel.toHashMap() = hashMapOf(
    "title" to title,
    "imagePath" to imagePath,
    "lat" to location.latitude,
    "lng" to location.longitude
)

private fun DocumentSnapshot.toPlace() = HappyPlaceModel(
    title = getString("title")!!,
    imagePath = getString("imagePath")!!,
    location = LatLng(getDouble("lat")!!, getDouble("lng")!!)
)