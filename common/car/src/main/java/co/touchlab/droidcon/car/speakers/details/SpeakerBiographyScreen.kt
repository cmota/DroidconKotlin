package co.touchlab.droidcon.car.speakers.details

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.LongMessageTemplate
import androidx.car.app.model.Template
import co.touchlab.droidcon.car.R
import co.touchlab.droidcon.domain.entity.Profile

class SpeakerBiographyScreen(
    carContext: CarContext,
    private val speaker: Profile
): Screen(carContext) {

    override fun onGetTemplate(): Template {
        return LongMessageTemplate.Builder(
            if(speaker.bio.isNullOrEmpty()) {
                carContext.getString(R.string.auto_no_information)
            } else {
                speaker.bio.toString()
            }
        )
            .setTitle(
                carContext.getString(R.string.auto_speakers_biography_more, speaker.fullName)
            )
            .setHeaderAction(Action.BACK)
            .build()
    }
}