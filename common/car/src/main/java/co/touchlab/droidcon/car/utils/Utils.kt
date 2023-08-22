package co.touchlab.droidcon.car.utils

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.HostException
import co.touchlab.droidcon.car.R
import co.touchlab.droidcon.composite.Url
import com.seiko.imageloader.imageLoader
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.model.ImageResult
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull

suspend fun fetchImage(carContext: CarContext, link: Url?): Bitmap? {

    val request = ImageRequest {
        data(link?.string)
    }

    val imageLoader = carContext.imageLoader
    return (imageLoader.async(request).filterIsInstance<ImageResult>().firstOrNull() as? ImageResult.Bitmap)?.bitmap
}

fun navigateTo(carContext: CarContext, latitude: Double, longitude: Double) {
    val uri = Uri.parse("geo:0,0?q=$latitude,$longitude")
    val intent = Intent(CarContext.ACTION_NAVIGATE, uri)

    try {
        carContext.startCarApp(intent)
    } catch (e: HostException) {
        CarToast.makeText(
            carContext,
            carContext.getString(R.string.auto_navigate_to_failed),
            CarToast.LENGTH_SHORT
        ).show()
    }
}