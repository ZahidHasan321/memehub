package com.example.memehub.ui.screens.profile

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.memehub.R
import com.example.memehub.data.model.realmModels.User
import com.example.memehub.ui.components.DropDownMenu
import com.example.memehub.ui.components.FullScreenPhoto
import com.example.memehub.ui.components.MyDatePickerDialog
import com.example.memehub.ui.components.YoutubePlayer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import java.io.IOException
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ProfileScreen(snackbarHostState: SnackbarHostState, openAndPopUp: (String, String) -> Unit) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState
    val context = LocalContext.current

    val avgRating by viewModel.avgRating.collectAsState()
    val totalRatingCount by viewModel.totalRatingCount.collectAsState()
    val user by viewModel.realmUser.collectAsState()


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
        updateBirthdate = viewModel::updateBirthday,
        updateGender = viewModel::updateGender,
        context = context,
        user = user,
        avgRating = avgRating,
        totalRatingCount = totalRatingCount,
        rateApp = viewModel::rateApp
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    openDateDialog: MutableState<Boolean>,
    dateResult: MutableState<String>,
    context: Context,
    uiState: ProfileUiState,
    onSignOutClick: () -> Unit,
    setImage: (Context, Uri) -> Unit,
    updateBirthdate: (String) -> Unit,
    updateGender: (String) -> Unit,
    changeGender: (String) -> Unit,
    user: User?,
    avgRating: Float,
    totalRatingCount: Long,
    rateApp: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 10.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        //Avatar handler
        val fullScreenAvatar = remember {
            mutableStateOf(false)
        }


        //Image viewer
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user?.avatar?.thumbnailUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Profile picture",
            placeholder = painterResource(id = R.drawable.baseline_person_24),
            fallback = painterResource(id = R.drawable.baseline_person_24),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clickable(
                    onClick = {
                        fullScreenAvatar.value = true
                    }
                )
                .size(120.dp)
                .clip(CircleShape)
                .border(
                    border = BorderStroke(
                        2.dp,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ), shape = CircleShape
                )
        )

        user?.avatar?.displayUrl?.let {
            FullScreenPhoto(
                isVisible = fullScreenAvatar,
                imageUrl = it
            )
        }

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

            user?.firstName?.let {
                Text(
                    text = it,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))
            user?.lastName?.let {
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
            user?.email?.let { Text(text = it) }
        }

        Divider()

        //Birthdate picker
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Text(text = "Birthdate: ", fontWeight = FontWeight.Bold)
                Text(text = user?.birthDate ?: dateResult.value)
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
                        updateBirthdate(dateResult.value)
                    },
                    onDismiss = { openDateDialog.value = false }
                )
            }
        }

        Divider()

        //Gender selection
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
                Text(text = user?.gender ?: "None")
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    DropDownMenu(
                        defaultValue = uiState.gender,
                        updateGender = updateGender,
                        changeGender = changeGender
                    )
                }
            }
        }


        Divider()

        //Location picker
        var locationDialog by remember {
            mutableStateOf(false)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Location", fontWeight = FontWeight.Bold)
            IconButton(onClick = { locationDialog = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Change location"
                )
            }
        }

        val singapore = LatLng(1.35, 103.87)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(singapore, 10f)
        }
        val uiSettings = remember {
            MapUiSettings(myLocationButtonEnabled = true)
        }
        val properties by remember {
            mutableStateOf(MapProperties(isMyLocationEnabled = true))
        }


        val geocoder = Geocoder(LocalContext.current, Locale.getDefault())

        try {
            geocoder.getFromLocation(1.35, 103.87, 1) {
                Log.d("CURRENT_LOCATION", it.toString())
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }


        if (locationDialog) {
            Dialog(
                onDismissRequest = { locationDialog = false }, properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false
                )
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        properties = properties,
                        uiSettings = uiSettings,
                        cameraPositionState = cameraPositionState
                    )
                }
            }
        }


        Divider()

        var additionalInfo by remember {
            mutableStateOf(false)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "AdditionalInfo", fontWeight = FontWeight.Bold)
            IconButton(onClick = { additionalInfo = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Change location"
                )
            }
        }

        if (additionalInfo) {
            Dialog(
                onDismissRequest = { additionalInfo = false }, properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false
                )
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column (verticalArrangement = Arrangement.Top){
                        YoutubePlayer(youtubeVideoId = "dWP2vP7eZ9I", lifecycleOwner = LocalLifecycleOwner.current )
                    }
                }
            }
        }


        Divider()

        var ratingBTN by remember {
            mutableStateOf(false)
        }

        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "App Rating")
            Row(verticalAlignment = Alignment.CenterVertically){
                if(totalRatingCount < 1)
                    Text(text = "No rating yet")
                else
                    Text(text = (avgRating/totalRatingCount).toString())

                IconButton(onClick = { ratingBTN = true }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Change location"
                    )
                }
            }

        }



        var rating by remember { mutableFloatStateOf(1f) } //default rating will be 1



        if (ratingBTN) {
            Dialog(
                onDismissRequest = { ratingBTN = false }
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        StarRatingBar(
                            maxStars = 5,
                            rating = rating,
                            onRatingChanged = {
                                rating = it
                                rateApp(it)
                            }
                        )
                    }

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

@Composable
fun StarRatingBar(
    maxStars: Int = 5,
    rating: Float,
    onRatingChanged: (Float) -> Unit
) {
    val density = LocalDensity.current.density
    val starSize = (12f * density).dp
    val starSpacing = (0.5f * density).dp

    Row(
        modifier = Modifier.selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            var icon = if (isSelected)
                Icons.Filled.Star
            else
                Icons.Outlined.StarBorder

            val iconTintColor = if (isSelected) Color(0xFFFDD835) else Color(0xFF000000)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            onRatingChanged(i.toFloat())
                        }
                    )
                    .width(starSize)
                    .height(starSize)

            )

            if (i < maxStars) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}
