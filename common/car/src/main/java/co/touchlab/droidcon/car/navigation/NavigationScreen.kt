package co.touchlab.droidcon.car.navigation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.text.SpannableString
import android.text.Spanned
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarLocation
import androidx.car.app.model.Distance
import androidx.car.app.model.DistanceSpan
import androidx.car.app.model.ForegroundCarColorSpan
import androidx.car.app.model.ItemList
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import co.touchlab.droidcon.car.R
import co.touchlab.droidcon.car.more.MoreScreen
import co.touchlab.droidcon.car.utils.METERS_TO_KMS
import co.touchlab.droidcon.car.utils.VENUE_ADDRESS
import co.touchlab.droidcon.car.utils.VENUE_LATITUDE
import co.touchlab.droidcon.car.utils.VENUE_LONGITUDE
import co.touchlab.droidcon.car.utils.VENUE_NAME
import co.touchlab.droidcon.car.utils.navigateTo
import kotlin.math.roundToInt

class NavigationScreen(
    carContext: CarContext,
) : Screen(carContext) {

    private var location: Location? = null

    override fun onGetTemplate(): Template {
        if (location == null) {
            location = setLocation()
        }

        val items = showPOIDetails()
        val anchor = getAnchorLocation()

        return PlaceListMapTemplate.Builder().apply {
            setTitle(carContext.getString(R.string.app_name))
            setHeaderAction(Action.APP_ICON)
            setAnchor(anchor)
            setCurrentLocationEnabled(true)
            setItemList(items.build())
        }.build()
    }

    private fun showPOIDetails() : ItemList.Builder {
        val listBuilder = ItemList.Builder()

        val venueLocation = Location(VENUE_NAME)
        venueLocation.latitude = VENUE_LATITUDE
        venueLocation.longitude = VENUE_LONGITUDE

        val distanceMeters = location?.distanceTo(venueLocation)?.roundToInt() ?: 0
        val distanceKm: Int = distanceMeters / METERS_TO_KMS

        val description = SpannableString("   \u00b7 $VENUE_NAME $VENUE_ADDRESS")
        description.setSpan(
            DistanceSpan.create(Distance.create(distanceKm.toDouble(), Distance.UNIT_KILOMETERS)),
            0,
            1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        description.setSpan(
            ForegroundCarColorSpan.create(CarColor.BLUE),
            0,
            1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        listBuilder.addItem(
            Row.Builder()
                .setOnClickListener {
                    screenManager.push(MoreScreen(carContext))
                }
                .setTitle(carContext.getString(R.string.navigation_more))
                .addText(description)
                .setBrowsable(false)
                .build())

        listBuilder.addItem(
            Row.Builder()
                .setOnClickListener {
                    navigateTo(carContext, venueLocation.latitude ?: 0.0, venueLocation.longitude ?: 0.0)
                }
                .setTitle(carContext.getString(R.string.auto_navigate_to))
                .setBrowsable(true)
                .setImage(
                    CarIcon.Builder(
                        IconCompat.createWithResource(carContext, R.drawable.ic_outlined_navigation),
                    ).build(),
                    Row.IMAGE_TYPE_ICON
                )
                .build()
        )

        return listBuilder
    }

    private fun getAnchorLocation(): Place {
        return Place.Builder(
            CarLocation.create(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
        )
            .setMarker(PlaceMarker.Builder().setColor(CarColor.BLUE).build())
            .build()
    }

    private fun setLocation(): Location? {
        if (ContextCompat.checkSelfPermission(carContext, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val locationManager = carContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 1f
            ) {
                if (location?.latitude == it.latitude && location?.longitude == it.longitude) {
                    return@requestLocationUpdates
                }

                location = it
                invalidate()
            }

            return lastKnownLocation
        }

        return null
    }
}