package com.example.memehub.screens.profile

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.memehub.R
import com.example.memehub.data.model.UserUpdateModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(snackbarHostState: SnackbarHostState, openAndPopUp: (String, String) -> Unit) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState
    val context = LocalContext.current


    if (uiState.hasSignedOut) {
        openAndPopUp("auth", "protected")
    }

    val dateResult = remember {
        mutableStateOf(
            "Date Picker"
        )
    }

    val openDateDialog = remember { mutableStateOf(false) }


    LaunchedEffect(key1 = uiState.birthDate) {
        if (uiState.birthDate?.isNotEmpty() == true) {
            dateResult.value = uiState.birthDate!!
        }
    }

    LaunchedEffect(key1 = uiState.successMessage) {
        if (uiState.successMessage.isNotEmpty()) {
            Toast.makeText(context, uiState.successMessage, Toast.LENGTH_SHORT).show()
            viewModel.clearSuccessMessage()
        }

    }

    LaunchedEffect(key1 = uiState.errorMessage) {
        if (uiState.errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = uiState.errorMessage,
                actionLabel = "dismiss"
            )
            viewModel.clearErrorMessage()
        }
    }

    ProfileScreenContent(
        changeGender = viewModel::changeGender,
        openDateDialog = openDateDialog,
        dateResult = dateResult,
        setImage = viewModel::setImage,
        uiState = uiState,
        onSignOutClick = viewModel::signOutUser,
        updateUser = viewModel::updateUser,
        context = context
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    openDateDialog: MutableState<Boolean>,
    dateResult: MutableState<String>,
    context: Context,
    uiState: ProfileUiState,
    onSignOutClick: () -> Unit,
    setImage: (Context, Uri) -> Unit,
    updateUser: (UserUpdateModel) -> Unit,
    changeGender: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 10.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uiState.picture)
                .crossfade(true)
                .build(),
            contentDescription = "Profile picture",
            placeholder = painterResource(id = R.drawable.baseline_person_24),
            fallback = painterResource(id = R.drawable.baseline_person_24),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(
                    border = BorderStroke(
                        2.dp,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ), shape = CircleShape
                )
        )

        //Image picker 
        val pickMedia = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                if (uri != null) {
                    setImage(context, uri)
                }
            })

        TextButton(onClick = {
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }) {
            Text(text = "change picture")
        }

        Row(modifier = Modifier) {
            uiState.firstName?.let {
                Text(
                    text = it,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            uiState.lastName?.let {
                Text(
                    text = it,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
            }
        }

        uiState.username?.let { Text(text = it) }

        Row(modifier = Modifier.padding(10.dp, 10.dp)) {
            Icon(imageVector = Icons.Default.Email, contentDescription = "Email icon")
            Spacer(modifier = Modifier.width(4.dp))
            uiState.email?.let { Text(text = it) }
        }

        Divider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Text(text = "Birthdate: ", fontWeight = FontWeight.Bold)
                Text(text = dateResult.value)
            }

            IconButton(onClick = { openDateDialog.value = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Open date picker"
                )
            }

            if (openDateDialog.value) {
                MyDatePickerDialog(
                    onDateSelected = {
                        dateResult.value = it
                        updateUser(UserUpdateModel(birthDate = dateResult.value))
                    },
                    onDismiss = { openDateDialog.value = false }
                )
            }
        }

        Divider()

        var genderDialogState by remember {
            mutableStateOf(false)
        }

        Row(
            modifier = Modifier
                .padding(4.dp, 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(text = "Gender: ", fontWeight = FontWeight.Bold)
                Text(text = uiState.gender?:"None")
            }

            IconButton(onClick = { genderDialogState = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "open dialog"
                )
            }
        }

        if (genderDialogState) {
            Dialog(onDismissRequest = { genderDialogState = false }) {
                Card (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ){
                    DropDownMenu(
                        defaultValue = uiState.gender,
                        updateUser = updateUser,
                        changeGender = changeGender
                    )
                }
            }
        }


        Divider()

        TextButton(
            modifier = Modifier.padding(top = 20.dp),
            onClick = { onSignOutClick() },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout button"
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Log out", fontSize = 20.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(onDismissRequest = { onDismiss() }, confirmButton = {
        Button(onClick = {
            onDateSelected(selectedDate)
            onDismiss()
        }) {
            Text(text = "Ok")
        }
    },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DropDownMenu(
    defaultValue: String?,
    updateUser: (UserUpdateModel) -> Unit,
    changeGender: (String) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }


    var selectedText by remember {
        mutableStateOf(
            "none"
        )
    }

    LaunchedEffect(key1 = defaultValue) {
        if (defaultValue != null && defaultValue != "") {
            selectedText = defaultValue
        }
    }




    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp, 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.width(160.dp),
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )


            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Male, contentDescription = "male")
                    },
                    text = { Text(text = "male") },
                    onClick = {
                        changeGender("male")
                        expanded = false
                        updateUser(UserUpdateModel(gender = "male"))

                    })
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Female, contentDescription = "male")
                    },
                    text = { Text(text = "female") },
                    onClick = {
                        changeGender("female")
                        expanded = false
                        updateUser(UserUpdateModel(gender = "female"))
                    })
            }
        }


    }
}


fun convertMillisToDate(millis: Long): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val date = Date(millis)
    return dateFormat.format(date)
}

