package com.example.memehub.ui.screens.addItem

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.memehub.R
import kotlinx.coroutines.delay



@Composable
fun AddItemScreen(visible: MutableState<Boolean>, snackbarHostState: SnackbarHostState) {
    val viewModel: AddItemViewModel = hiltViewModel()
    val uiState by viewModel._uiState
    val focusRequester = remember { FocusRequester() }

    val context = LocalContext.current

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.setUri(uri)
            }
        })


    if (visible.value) {
        Dialog(
            onDismissRequest = { visible.value = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Scaffold(
                modifier = Modifier.imePadding(),
                bottomBar = {
                    BottomAppBar(
                        containerColor = MaterialTheme.colorScheme.background,
                        tonalElevation = 0.dp,
                        actions = {
                            IconButton(onClick = {
                                pickMedia.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }) {
                                Icon(
                                    Icons.Filled.Image,
                                    contentDescription = "Localized description",
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->

                Column(
                    modifier = Modifier
                        .consumeWindowInsets(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { visible.value = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close dialog",
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Button(onClick = {
                            viewModel.handleSubmit(context)
                            visible.value = false
                        }) {
                            Text(text = "Post", fontWeight = FontWeight.Bold)
                        }
                    }

                    LaunchedEffect(focusRequester) {
                        delay(100)
                        focusRequester.requestFocus()
                    }


                    TextField(
                        value = uiState.title,
                        onValueChange = viewModel::setTitle,
                        placeholder = {
                            Text(text = "Title", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 22.sp, // Adjust text size as needed
                            fontWeight = FontWeight.Bold // Make text bold
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester = focusRequester),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        )
                    )

                    if (uiState.image != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uiState.image)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile picture",
                            placeholder = painterResource(id = R.drawable.baseline_person_24),
                            fallback = painterResource(id = R.drawable.baseline_person_24),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                        )
                    }

                    TextField(
                        value = uiState.caption,
                        onValueChange = viewModel::setCaption,
                        placeholder = {
                            Text(
                                text = "caption",
                                fontWeight = FontWeight.ExtraLight,
                                fontSize = 18.sp
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        textStyle = TextStyle(
                            fontSize = 18.sp, // Adjust text size as needed
                            fontWeight = FontWeight.Normal // Make text bold
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

        }
    }
}