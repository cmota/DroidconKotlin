package co.touchlab.droidcon

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.ui.uiModule
import co.touchlab.droidcon.util.ClasspathResourceReader
import org.koin.dsl.module

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(
            module {
                single<Context> { this@MainApp }
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences("DROIDCON_SETTINGS_2023", Context.MODE_PRIVATE)
                }
                single<ResourceReader> {
                    ClasspathResourceReader()
                }

            } + uiModule
        )
    }
}
