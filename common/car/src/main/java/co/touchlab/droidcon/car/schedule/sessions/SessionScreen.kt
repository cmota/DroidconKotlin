package co.touchlab.droidcon.car.schedule.sessions

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.LongMessageTemplate
import androidx.car.app.model.ParkedOnlyOnClickListener
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.lifecycleScope
import co.touchlab.droidcon.car.R
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SessionScreen(
    carContext: CarContext,
    val session: Session
): Screen(carContext), KoinComponent {

    private val sessionGateway by inject<SessionGateway>()

    private var schedule: List<ScheduleItem>? = null

    init {
        lifecycleScope.launch {
            sessionGateway.observeAgenda().collect { items ->
                schedule = items
                invalidate()
            }
        }
    }

    override fun onGetTemplate(): Template {
        return LongMessageTemplate.Builder(
            session.description ?: carContext.getString(R.string.auto_no_information)
        ).apply {
            setTitle(session.title)
            setHeaderAction(Action.BACK)

            if (schedule != null) {
                val isBookmarked = schedule?.firstOrNull { it.session == session } != null
                if (isBookmarked) {
                    addAction(getRemoveFromBookmarksAction().build())
                } else {
                    setActionStrip(
                        ActionStrip.Builder()
                            .addAction(getAddToBookmarksAction().build())
                            .build()
                    )
                }
            }
        }.build()
    }

    private fun getAddToBookmarksAction(): Action.Builder {
        return Action.Builder()
            .setOnClickListener(
                ParkedOnlyOnClickListener.create {
                    lifecycleScope.launch {
                        sessionGateway.setAttending(session, true)
                    }

                    CarToast.makeText(
                        carContext,
                        carContext.getString(R.string.auto_session_bookmark_success),
                        CarToast.LENGTH_SHORT
                    ).show()

                    invalidate()
                })
            .setTitle(carContext.getString(R.string.auto_session_bookmark))
            .setIcon(
                CarIcon.Builder(
                    IconCompat.createWithResource(carContext, R.drawable.ic_outlined_bookmarks)
                ).build()
            )
    }

    private fun getRemoveFromBookmarksAction(): Action.Builder {
        return Action.Builder()
            .setOnClickListener(
                ParkedOnlyOnClickListener.create {
                    lifecycleScope.launch {
                        sessionGateway.setAttending(session, false)
                    }

                    CarToast.makeText(
                        carContext,
                        carContext.getString(R.string.auto_session_remove_bookmark_success),
                        CarToast.LENGTH_SHORT
                    ).show()

                    invalidate()
                })
            .setTitle(carContext.getString(R.string.auto_session_remove_bookmark))
    }
}