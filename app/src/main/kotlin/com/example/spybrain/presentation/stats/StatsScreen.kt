import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spybrain.domain.model.BreathingSession
import com.example.spybrain.domain.model.Session
import com.example.spybrain.presentation.base.UiEffect
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable // FIXME билд-фикс 09.05.2025
import com.example.spybrain.presentation.stats.StatsViewModel
import com.example.spybrain.presentation.stats.StatsContract

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val breathing by viewModel.breathingHistory.collectAsState()
    val meditations by viewModel.meditationHistory.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is StatsContract.Effect.ShowError -> Toast.makeText(context, when(val err = effect.error) {
                    is com.example.spybrain.util.UiError.Custom -> err.message
                    is com.example.spybrain.util.UiError.NetworkError -> stringResource(id = com.example.spybrain.R.string.error_network)
                    is com.example.spybrain.util.UiError.ValidationError -> stringResource(id = com.example.spybrain.R.string.error_validation)
                    is com.example.spybrain.util.UiError.UnknownError -> stringResource(id = com.example.spybrain.R.string.error_unknown)
                    else -> stringResource(id = com.example.spybrain.R.string.error_general)
                }, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            StatsContent(
                state = state,
                breathing = breathing,
                meditations = meditations
            )
        }
    }
}

@Composable
private fun StatsContent(state: StatsContract.State, breathing: List<BreathingSession>, meditations: List<Session>) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm") }
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = stringResource(id = com.example.spybrain.R.string.stats_title), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            state.stats?.let { stats ->
                StatsRow(label = stringResource(id = com.example.spybrain.R.string.stats_total_meditation_minutes), value = "${stats.totalMeditationTimeSeconds / 60}")
                StatsRow(label = stringResource(id = com.example.spybrain.R.string.stats_total_breathing_minutes), value = "${stats.totalBreathingTimeSeconds / 60}")
                StatsRow(label = stringResource(id = com.example.spybrain.R.string.stats_completed_meditation_sessions), value = "${stats.completedMeditationSessions}")
                StatsRow(label = stringResource(id = com.example.spybrain.R.string.stats_completed_breathing_sessions), value = "${stats.completedBreathingSessions}")
                StatsRow(label = stringResource(id = com.example.spybrain.R.string.stats_current_streak_days), value = "${stats.currentStreakDays}")
                StatsRow(label = stringResource(id = com.example.spybrain.R.string.stats_longest_streak_days), value = "${stats.longestStreakDays}")
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(id = com.example.spybrain.R.string.stats_breathing_history_title), style = MaterialTheme.typography.titleLarge)
        }
        items(breathing) { session: BreathingSession -> BreathingSessionCard(session, timeFormatter) }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(id = com.example.spybrain.R.string.stats_meditation_history_title), style = MaterialTheme.typography.titleLarge)
        }
        items(meditations) { session: Session -> MeditationSessionCard(session, dateFormatter) }
    }
}

@Composable
fun StatsRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun BreathingSessionCard(session: BreathingSession, formatter: DateTimeFormatter) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = session.patternName, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = stringResource(id = com.example.spybrain.R.string.duration_seconds_format, session.durationSeconds), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = session.timestamp.format(formatter), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun MeditationSessionCard(session: Session, formatter: SimpleDateFormat) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = session.id, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = stringResource(id = com.example.spybrain.R.string.duration_seconds_format, session.durationSeconds), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = formatter.format(session.startTime), style = MaterialTheme.typography.bodySmall)
        }
    }
} 