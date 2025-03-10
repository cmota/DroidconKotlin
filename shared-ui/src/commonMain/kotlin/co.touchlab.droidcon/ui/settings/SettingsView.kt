package co.touchlab.droidcon.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.ui.icons.MailOutline
import co.touchlab.droidcon.ui.icons.Notifications
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.viewmodel.settings.SettingsViewModel
import org.brightify.hyperdrive.multiplatformx.property.MutableObservableProperty

@Composable
internal fun SettingsView(viewModel: SettingsViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                elevation = 0.dp,
                modifier = Modifier.shadow(AppBarDefaults.TopAppBarElevation),
                backgroundColor = MaterialTheme.colors.primary,
            )
        },
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(it)
                .verticalScroll(scrollState)
        ) {
            IconTextSwitchRow(
                text = "Enable feedback",
                image = Icons.Default.MailOutline,
                checked = viewModel.observeIsFeedbackEnabled,
            )

            Divider()

            IconTextSwitchRow(
                text = "Enable reminders",
                image = Icons.Default.Notifications,
                checked = viewModel.observeIsRemindersEnabled,
            )

            Divider()

            PlatformSpecificSettingsView(viewModel = viewModel)

            AboutView(viewModel.about)
        }
    }
}

@Composable
internal fun IconTextSwitchRow(text: String, image: ImageVector, checked: MutableObservableProperty<Boolean>) {
    val isChecked by checked.observeAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { checked.value = !checked.value },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.padding(Dimensions.Padding.default),
            imageVector = image,
            contentDescription = text,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = text,
        )
        Switch(
            modifier = Modifier.padding(vertical = Dimensions.Padding.half, horizontal = 24.dp),
            checked = isChecked,
            onCheckedChange = { checked.value = it },
        )
    }
}
