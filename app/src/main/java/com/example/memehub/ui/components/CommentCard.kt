package com.example.memehub.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.memehub.R
import com.example.memehub.data.model.realmModels.Comment
import com.example.memehub.ui.screens.home.getTimeElapsed
import com.example.memehub.ui.screens.home.toCamelCase


@Composable
fun CommentCard(comment: Comment) {
    Card(
        onClick = {},
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
            Row {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(comment.commentor?.avatar?.thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile picture",
                    placeholder = painterResource(id = R.drawable.baseline_person_24),
                    fallback = painterResource(id = R.drawable.baseline_person_24),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(
                            border = BorderStroke(
                                2.dp,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ), shape = CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    Row (verticalAlignment = Alignment.CenterVertically){
                        comment.commentor?.firstName?.let { Text(text = toCamelCase(it), style = MaterialTheme.typography.titleMedium) }
                        Spacer(modifier = Modifier.width(4.dp))
                        comment.commentor?.lastName?.let { Text(text = toCamelCase(it), style = MaterialTheme.typography.titleMedium) }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = getTimeElapsed(comment.createdAt.epochSeconds),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    comment.commentor?.let {
                        Text(
                            text = "@" + it.name,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Text(modifier = Modifier.padding(vertical = 10.dp),text = comment.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}