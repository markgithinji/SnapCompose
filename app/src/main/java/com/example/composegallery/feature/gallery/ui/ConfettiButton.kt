import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


@Composable
fun ConfettiButton( // An Experiment, still needs work. Could be replaced with a more simpler button
    modifier: Modifier = Modifier,
    onFollowChanged: ((Boolean) -> Unit)? = null
) {
    var isFollowing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var triggerConfetti by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val backgroundColor by animateColorAsState(
        targetValue = if (isFollowing) Color(0xFFB2DFDB) else Color(0xFFFF6E6E),
        label = "buttonColor"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (triggerConfetti) {
            ConfettiEffect(
                onEffectComplete = { triggerConfetti = false },
                particles = remember { mutableStateListOf<Offset>() }
            )
        }

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    delay(600) // simulate network delay
                    isFollowing = !isFollowing
                    isLoading = false
                    triggerConfetti = true
                    onFollowChanged?.invoke(isFollowing)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
            modifier = Modifier,
            shape = RoundedCornerShape(50)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 8.dp),
                    strokeWidth = 2.dp
                )
            }
            Text(
                text = if (isFollowing) "Following" else "Follow",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun ConfettiEffect(
    onEffectComplete: () -> Unit,
    particles: MutableList<Offset>
) {
    val particleCount = 100
    val animates = remember {
        List(particleCount) {
            Animatable(Offset(0f, 0f), Offset.VectorConverter)
        }
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        particles.clear() // Clear particles on new effect trigger
        repeat(particleCount) {
            val x = Random.nextFloat() * 300f - 150f
            val y = Random.nextFloat() * 300f - 300f
            val duration = Random.nextInt(600, 1200)
            val anim = animates[it]
            scope.launch {
                anim.snapTo(Offset(0f, 0f))
                anim.animateTo(
                    Offset(x, y),
                    animationSpec = tween(durationMillis = duration, easing = LinearOutSlowInEasing)
                )
            }
        }

        delay(1300) // Give particles time to animate
        onEffectComplete()
    }

    Canvas(
        modifier = Modifier
    ) {
        animates.forEach {
            drawCircle(
                color = Color(
                    Random.nextInt(256),
                    Random.nextInt(256),
                    Random.nextInt(256)
                ),
                radius = 6f,
                center = Offset(center.x + it.value.x, center.y + it.value.y)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewConfettiButton() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ConfettiButton(onFollowChanged = { /* Handle click */ })
        }
    }
}