package co.touchlab.droidcon.car.sponsors

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
import co.touchlab.droidcon.domain.composite.SponsorGroupWithSponsors
import co.touchlab.droidcon.domain.gateway.SponsorGateway
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SponsorsScreen(
    carContext: CarContext
): Screen(carContext), KoinComponent {

    private val sponsorsGateway by inject<SponsorGateway>()

    private var sponsors: List<SponsorGroupWithSponsors>? = null

    init {
        lifecycleScope.launch {
            sponsorsGateway.observeSponsors().collect { items ->
                sponsors = items.sortedBy { it.group.displayPriority }
                invalidate()
            }
        }
    }

    override fun onGetTemplate(): Template {
        val listBuilder = createSponsorsList(sponsors)

        return listBuilder.apply {
            setTitle(carContext.getString(R.string.navigation_sponsors))
            setHeaderAction(Action.BACK)
            setLoading(sponsors == null)
        }.build()
    }

    private fun createSponsorsList(
        sponsors: List<SponsorGroupWithSponsors>?
    ): ListTemplate.Builder {
        val listTemplate = ListTemplate.Builder()
        sponsors?.forEach { group ->
            val listBuilder = ItemList.Builder()

            group.sponsors.forEach { sponsor ->
                listBuilder.addItem(
                    Row.Builder().apply {
                        setTitle(sponsor.name)

                        sponsor.description?.let {
                            addText(it)
                        }
                    }.build()
                )
            }

            listTemplate.addSectionedList(
                SectionedItemList.create(
                    listBuilder.build(),
                    group.group.name
                )
            )
        }

        return listTemplate
    }
}