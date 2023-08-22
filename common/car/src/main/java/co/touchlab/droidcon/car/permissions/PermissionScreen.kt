package co.touchlab.droidcon.car.permissions

import android.Manifest.permission
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.OnClickListener
import androidx.car.app.model.ParkedOnlyOnClickListener
import androidx.car.app.model.Template
import co.touchlab.droidcon.car.R

class PermissionScreen(
    carContext: CarContext,
) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val permissions: MutableList<String> = ArrayList()
        permissions.add(permission.ACCESS_FINE_LOCATION)

        val listener: OnClickListener = ParkedOnlyOnClickListener.create {
            carContext.requestPermissions(
                permissions
            ) { approved: List<String?>, _: List<String?>? ->
                if (approved.isNotEmpty()) {
                    CarToast.makeText(carContext, R.string.auto_permission_granted, CarToast.LENGTH_SHORT).show()
                    screenManager.pop()
                } else {
                    CarToast.makeText(carContext, R.string.auto_permission_not_granted, CarToast.LENGTH_SHORT).show()
                }
            }
        }

        return MessageTemplate.Builder(carContext.getString(R.string.auto_permission_message))
            .setTitle(carContext.getString(R.string.app_name))
            .setHeaderAction(Action.APP_ICON)
            .setIcon(CarIcon.ALERT)
            .addAction(Action.Builder()
                .setTitle(carContext.getString(R.string.auto_permission_title))
                .setOnClickListener(listener)
                .build()
            )
            .build()
    }
}