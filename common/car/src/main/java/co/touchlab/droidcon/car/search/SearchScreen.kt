package co.touchlab.droidcon.car.search

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.Row
import androidx.car.app.model.SearchTemplate
import androidx.car.app.model.SearchTemplate.SearchCallback
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import co.touchlab.droidcon.car.R
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.toConferenceDateTime
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchScreen(
    carContext: CarContext,
) : Screen(carContext), KoinComponent {

    private val sessionGateway by inject<SessionGateway>()
    private val dateTimeService by inject<DateTimeService>()

    private var schedule: List<ScheduleItem>? = null

    init {
        lifecycleScope.launch {
            sessionGateway.observeSchedule().collect { items ->
                schedule = items
                invalidate()
            }
        }
    }

    override fun onGetTemplate(): Template {
        val listBuilder = createSessionsList(schedule)

        return SearchTemplate.Builder(
            object : SearchCallback {
                override fun onSearchSubmitted(searchText: String) {
                    super.onSearchSubmitted(searchText)
                    schedule = schedule?.filter { it.session.title.contains(searchText, true) }
                    invalidate()
                }
            }
        ).apply {
            setHeaderAction(Action.BACK)
            setShowKeyboardByDefault(false)

            val schedule = schedule
            if (schedule == null) {
                setLoading(true)
            } else {
                setItemList(listBuilder.build())
            }
        }.build()
    }

    private fun createSessionsList(schedule: List<ScheduleItem>?): ItemList.Builder {
        if (schedule == null) {
            return ItemList.Builder()
        }

        val listBuilder = ItemList.Builder()
        for (item in schedule) {
            listBuilder.addItem(
                Row.Builder()
                    .setTitle(item.session.title)
                    .addText(
                        carContext.getString(
                            R.string.auto_session_text,
                            item.room?.name ?: carContext.getString(R.string.auto_placeholder),
                            item.session.startsAt.toConferenceDateTime(dateTimeService).time
                        )
                    ).build()
            )
        }

        return listBuilder
    }
}