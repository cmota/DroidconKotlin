package co.touchlab.droidcon.car.speakers

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.lifecycleScope
import co.touchlab.droidcon.car.R
import co.touchlab.droidcon.car.speakers.details.SpeakerDetailsScreen
import co.touchlab.droidcon.car.utils.fetchImage
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.gateway.SpeakerGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpeakersScreen(
    carContext: CarContext,
): Screen(carContext), KoinComponent {

    private val speakersGateway by inject<SpeakerGateway>()

    private var images: Map<String, CarIcon> = emptyMap()

    private val fallbackImage = CarIcon.Builder(
        IconCompat.createWithResource(carContext, R.drawable.ic_filled_person)
    ).build()

    init {
        lifecycleScope.launch {
            fetchImages().collect { newImages ->
                images = newImages
                invalidate()
            }
        }
    }

    private fun fetchImages(): Flow<Map<String, CarIcon>> {
        val speakers = speakersGateway.getAllSpeakers()
        return if (speakers.isNotEmpty()) {
            combine(speakers.map { imageFlow(it) }) {
                it.toMap()
            }
        } else {
            flowOf(mapOf())
        }
    }

    private fun imageFlow(speaker: Profile): Flow<Pair<String, CarIcon>> = flow {
        emit(speaker.id.value to fallbackImage)

        val photoUrl = speaker.profilePicture
        if (photoUrl != null) {
            val bitmap = fetchImage(carContext, photoUrl)

            if (bitmap != null) {
                val icon = IconCompat.createWithBitmap(bitmap)
                emit(speaker.id.value to CarIcon.Builder(icon).build())
            }
        }
    }

    override fun onGetTemplate(): Template {
        val speakers = speakersGateway.getAllSpeakers()

        return GridTemplate.Builder().apply {
            setTitle(carContext.getString(R.string.navigation_speakers))
            setHeaderAction(Action.BACK)
            setSingleList(createSpeakersList(speakers, images))
        }.build()
    }

    private fun createSpeakersList(
        speakers: List<Profile>,
        images: Map<String, CarIcon>
    ): ItemList {
        return ItemList.Builder().apply {
            for (speaker in speakers) {
                val image = images.getOrDefault(speaker.id.value, fallbackImage)

                addItem(
                    GridItem.Builder()
                        .setTitle(speaker.fullName)
                        .setText(speaker.tagLine ?: "")
                        .setOnClickListener {
                            screenManager.push(
                                SpeakerDetailsScreen(
                                    carContext = carContext,
                                    image = image,
                                    speaker = speaker
                                )
                            )
                        }
                        .setImage(image)
                        .build()
                )
            }
        }.build()
    }
}