package com.example.memehub.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.memehub.R
import com.example.memehub.data.model.realmModels.Post
import com.example.memehub.data.model.realmModels.User
import com.example.memehub.ui.screens.home.getTimeElapsed
import com.example.memehub.ui.screens.home.toCamelCase
import org.mongodb.kbson.ObjectId


@Composable
fun PostItem(
    post: Post,
    modifier: Modifier = Modifier,
    user: User,
    updateScore: (ObjectId, ObjectId, Int) -> Unit,
    navController: NavController,
    clickDisabled: Boolean = false
) {
    val creator = post.creator.first()
    val dateTime = getTimeElapsed(post.createdAt.epochSeconds)
    val isReacted = post.reactors.find { it.user?._id == user._id }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (!clickDisabled) navController.navigate("posts/${post._id.toHexString()}")
            }) {
        Column(
            modifier = modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        ) {

            Row(modifier) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(creator.avatar?.thumbnailUrl)
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

                Column{
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp) ){
                        Text(
                            text = toCamelCase(creator.firstName) + " " + toCamelCase(creator.lastName),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = dateTime,
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                    }

                    Text(
                        text = "@" + creator.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = post.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            if (post.caption != "")
                Text(
                    text = post.caption,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

            Spacer(modifier = Modifier.height(10.dp))

            if (post.image != "") {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile picture",
                    placeholder = painterResource(id = R.drawable.baseline_image_24),
                    fallback = painterResource(id = R.drawable.baseline_image_24),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedCounter(count = post.likes.toInt())
                        if (isReacted?.score == 1) {
                            IconButton(onClick = { updateScore(post._id, user._id, 0) }) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = "thumbs up clicked"
                                )
                            }
                        } else {
                            IconButton(onClick = { updateScore(post._id, user._id, 1) }) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUpOffAlt,
                                    contentDescription = "thumbs up clicked"
                                )
                            }
                        }
                    }


                    Row(verticalAlignment = Alignment.CenterVertically) {

                        if (isReacted?.score == -1) {
                            IconButton(onClick = { updateScore(post._id, user._id, 0) }) {
                                Icon(
                                    imageVector = Icons.Default.ThumbDown,
                                    contentDescription = "thumbs up clicked"
                                )
                            }
                        } else {
                            IconButton(onClick = { updateScore(post._id, user._id, -1) }) {
                                Icon(
                                    imageVector = Icons.Default.ThumbDownOffAlt,
                                    contentDescription = "thumbs up clicked"
                                )
                            }
                        }
                        AnimatedCounter(count = post.dislikes.toInt())
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (!clickDisabled) navController.navigate("posts/${post._id.toHexString()}") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = "comment"
                        )
                    }
                    AnimatedCounter(count = post.comments.size)
                }
            }
        }
    }
}