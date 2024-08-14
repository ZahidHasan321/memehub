package com.example.memehub.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.memehub.util.Digit
import com.example.memehub.util.compareTo


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedCounter(count: Int) {
    Row(
        modifier = Modifier
            .animateContentSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        count.toString()
            .mapIndexed { index, c -> Digit(c, count, index) }
            .forEach { digit ->
                AnimatedContent(
                    targetState = digit,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInVertically { -it } togetherWith slideOutVertically { it }
                        } else {
                            slideInVertically { it } togetherWith slideOutVertically { -it }
                        }
                    }, label = ""
                ) { digit ->
                    Text(
                        "${digit.digitChar}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
    }
}
