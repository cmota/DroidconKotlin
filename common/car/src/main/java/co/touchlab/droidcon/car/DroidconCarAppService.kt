package co.touchlab.droidcon.car

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.Session
import androidx.car.app.SessionInfo
import androidx.car.app.validation.HostValidator
import androidx.core.content.ContextCompat
import co.touchlab.droidcon.car.navigation.NavigationScreen
import co.touchlab.droidcon.car.permissions.PermissionScreen

class DroidconCarAppService  : CarAppService() {

    override fun createHostValidator(): HostValidator {
        return HostValidator.Builder(applicationContext)
            .addAllowedHosts(R.array.hosts_allowlist)
            .build()
    }

    override fun onCreateSession(sessionInfo: SessionInfo): Session {
        return object : Session() {

            override fun onCreateScreen(intent: Intent): Screen {
                return if (ContextCompat.checkSelfPermission(carContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    NavigationScreen(carContext)
                } else {

                    val screenManager = carContext.getCarService(ScreenManager::class.java)
                    screenManager.push(NavigationScreen(carContext))
                    PermissionScreen(carContext)
                }
            }
        }
    }
}