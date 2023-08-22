package co.touchlab.droidcon.car.more

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import co.touchlab.droidcon.car.R
import co.touchlab.droidcon.car.bookmarks.BookmarksScreen
import co.touchlab.droidcon.car.schedule.ScheduleScreen
import co.touchlab.droidcon.car.search.SearchScreen
import co.touchlab.droidcon.car.speakers.SpeakersScreen
import co.touchlab.droidcon.car.sponsors.SponsorsScreen

class MoreScreen(
    carContext: CarContext
): Screen(carContext) {

    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()
        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.navigation_schedule))
                .setOnClickListener { screenManager.push(ScheduleScreen(carContext)) }
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.navigation_speakers))
                .setOnClickListener { screenManager.push(SpeakersScreen(carContext)) }
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.navigation_bookmarks))
                .setOnClickListener { screenManager.push(BookmarksScreen(carContext)) }
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.navigation_sponsors))
                .setOnClickListener { screenManager.push(SponsorsScreen(carContext)) }
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.navigation_search))
                .setOnClickListener { screenManager.push(SearchScreen(carContext)) }
                .build()
        )

        return ListTemplate.Builder().apply {
            setTitle(carContext.getString(R.string.navigation_more))
            setHeaderAction(Action.BACK)
            setLoading(false)
            setSingleList(listBuilder.build())
        }.build()
    }
}