package co.touchlab.droidcon.car.bookmarks

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import co.touchlab.droidcon.car.R
import co.touchlab.droidcon.car.schedule.sessions.SessionScreen
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.gateway.SessionGateway
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BookmarksScreen(
    carContext: CarContext,
) : Screen(carContext), KoinComponent {

    private val sessionGateway by inject<SessionGateway>()

    private var bookmarks: List<ScheduleItem>? = null

    init {
        lifecycleScope.launch {
            sessionGateway.observeAgenda().collect { items ->
                bookmarks = items
                invalidate()
            }
        }
    }

    override fun onGetTemplate(): Template {

        val listBuilder = createBookmarksList(bookmarks)
        return ListTemplate.Builder().apply {
            setTitle(carContext.getString(R.string.navigation_bookmarks))
            setHeaderAction(Action.BACK)

            if (bookmarks == null) {
                setLoading(true)
            } else {
                setSingleList(listBuilder.build())
            }
        }.build()
    }

    private fun createBookmarksList(bookmarks: List<ScheduleItem>?): ItemList.Builder {
        val listBuilder = ItemList.Builder()
        bookmarks?.forEach { item ->
            listBuilder.addItem(
                Row.Builder()
                    .setTitle(item.session.title)
                    .addText(item.speakers.map { it.fullName }.toString())
                    .setOnClickListener {
                        screenManager.push(SessionScreen(carContext, item.session))
                    }
                    .build()
            )
        }

        return listBuilder
    }
}