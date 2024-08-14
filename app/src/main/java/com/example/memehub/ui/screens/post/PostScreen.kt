package com.example.memehub.ui.screens.post

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.memehub.ui.components.CommentCard
import com.example.memehub.ui.components.PostItem
import io.realm.kotlin.ext.isManaged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(navController1: SnackbarHostState, navController: NavHostController, string: String?) {
    val viewModel: PostViewModel = hiltViewModel()
    val post by viewModel.post.collectAsState()
    val user by viewModel.realmUser.collectAsState()
    val uiState by viewModel._uiState

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var isNavigationInProgress by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(
                    top = 0.dp,
                    bottom = 0.dp
                ),
                title = {
                    post?.title?.let { Text(text = it, maxLines = 1, fontSize = 14.sp) }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!isNavigationInProgress) {
                            isNavigationInProgress = true
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Go Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .imePadding(),
            ) {
                val focusManager = LocalFocusManager.current

                OutlinedTextField(
                    value = uiState.comment,
                    onValueChange = viewModel::setComment,
                    placeholder = { Text(text = "write a comment..") },
                    modifier = Modifier
                        .fillMaxWidth()
                        ,
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.handleSubmit()
                            focusManager.clearFocus()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Post comment",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
                    keyboardActions = KeyboardActions(onDone = {
                        viewModel.handleSubmit()
                        focusManager.clearFocus()
                    }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                )
            }

        }
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)


        ) {
            Column {
                if (post != null && user != null && post?.isManaged() == true) {
                    val comments = post!!.comments.toList()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            PostItem(
                                post = post!!,
                                user = user!!,
                                updateScore = viewModel::updateScore,
                                navController = navController,
                                clickDisabled = true
                            )
                        }
                        items(comments) { comment ->
                            CommentCard(comment = comment)
                        }
                    }
                }
            }
        }
    }
}
