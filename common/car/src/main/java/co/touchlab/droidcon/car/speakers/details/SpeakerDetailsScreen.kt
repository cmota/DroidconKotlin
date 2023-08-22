package co.touchlab.droidcon.car.speakers.details

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import co.touchlab.droidcon.car.R
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpeakerDetailsScreen(
    carContext: CarContext,
    val image: CarIcon,
    val speaker: Profile
): Screen(carContext), KoinComponent {

    private val sessionsGateway by inject<SessionGateway>()

    private var sessions: List<Session>? = null

    init {
        val data = sessionsGateway.observeAllSessionsFromSpeaker(speaker.id)
        lifecycleScope.launch {
            data.collect {
                sessions = it
                invalidate()
            }
        }
    }

    override fun onGetTemplate(): Template {
        val paneBuilder = Pane.Builder()

        val sessions = sessions
        if (sessions == null) {
            paneBuilder.setLoading(true)
        } else {
            paneBuilder.addRow(
                Row.Builder().apply {
                    setTitle(carContext.getString(R.string.auto_speakers_sessions))
                    for (session in sessions) {
                        addText(session.title)
                    }
                }.build()
            )
        }.setImage(image)

        paneBuilder.addAction(createAction(speaker).build())

        return PaneTemplate.Builder(paneBuilder.build())
            .setHeaderAction(Action.BACK)
            .setTitle(speaker.fullName)
            .build()
    }

    private fun createAction(speaker: Profile): Action.Builder {
        return Action.Builder()
            .setTitle(carContext.getString(R.string.auto_speakers_biography))
            .setOnClickListener {
                screenManager.push(SpeakerBiographyScreen(carContext, speaker))
            }
    }
}