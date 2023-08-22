package co.touchlab.droidcon.car.schedule

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.SectionedItemList
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import co.touchlab.droidcon.car.R
import co.touchlab.droidcon.car.schedule.sessions.SessionScreen
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.toConferenceDateTime
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScheduleScreen (
    carContext: CarContext
) : Screen(carContext), KoinComponent {

    private val sessionGateway by inject<SessionGateway>()
    private val dateTimeService by inject<DateTimeService>()

    private var days: Map<LocalDate, List<ScheduleItem>>? = null

    init {
        lifecycleScope.launch {
            sessionGateway.observeSchedule().collect { items ->
                days = items.groupBy {
                    it.session.startsAt.toConferenceDateTime(dateTimeService).date
                }

                invalidate()
            }
        }
    }
    override fun onGetTemplate(): Template {
        val listBuilder = createSessionsList(days)

        return listBuilder.apply {
            setTitle(carContext.getString(R.string.navigation_schedule))
            setHeaderAction(Action.BACK)
            setLoading(days == null)
        }.build()
    }

    private fun createSessionsList(days: Map<LocalDate, List<ScheduleItem>>?): ListTemplate.Builder {
        var listTemplate = ListTemplate.Builder()

        if (days == null) return listTemplate

        for (day in days.keys) {
            listTemplate = createDailyList(listTemplate, days[day])
        }

        return listTemplate
    }

    private fun createDailyList(
        listTemplate: ListTemplate.Builder,
        schedule: List<ScheduleItem>?
    ): ListTemplate.Builder {
        val times = schedule?.map { it.session.startsAt } ?: emptyList()

        val sessions = mutableMapOf<Instant, List<ScheduleItem>>()
        for (time in times) {
            sessions[time] = schedule?.filter { it.session.startsAt == time } ?: emptyList()
        }

        sessions.keys.forEach { time ->
            val listBuilder = ItemList.Builder()

            sessions[time]?.forEach { item ->
                val speakers = item.speakers.map { it.fullName }

                listBuilder.addItem(
                    Row.Builder().apply {
                        setTitle(item.session.title)

                        if (speakers.isNotEmpty()) {
                            addText(speakers.toString())
                        }

                        setOnClickListener {
                            screenManager.push(
                                SessionScreen(
                                carContext = carContext,
                                session = item.session
                            )
                            )
                        }
                    }.build()
                )
            }

            sessions[time]?.firstOrNull()?.let {
                listTemplate.addSectionedList(
                    SectionedItemList.create(
                        listBuilder.build(),
                        it.session.startsAt.toConferenceDateTime(dateTimeService).time.toString()
                    )
                )
            }
        }

        return listTemplate
    }
}