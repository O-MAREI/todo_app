package com.example.project.ui.activities

import UploadImageBox
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.R
import com.example.project.Screen
import com.example.project.data.saveImage
import com.example.project.data.uriToBitmap
import com.example.project.ui.theme.Purple
import com.example.project.viewmodels.NewTripViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun NewTripPageContent(
    innerPadding: PaddingValues,
    pageNavigation: (Screen, Long?) -> Unit,
    newTripViewModel: NewTripViewModel
) {
    var tripName by rememberSaveable { mutableStateOf("") }
    var pictureUri: Uri? by remember { mutableStateOf(null) }

    var navigateToTrip by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var contentResolver = context.contentResolver

    LaunchedEffect(newTripViewModel.insertedTripID) {
        if (navigateToTrip && newTripViewModel.insertedTripID != null) {
            pageNavigation(Screen.Trip, newTripViewModel.insertedTripID!!)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 12.dp),
                        text = "Trip Name",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Left,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Default.Create,
                        contentDescription = "Localized description",
                        tint = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                OutlinedTextField(
                    value = tripName,
                    onValueChange = { tripName = it },
                    placeholder = { Text("Enter trip name here", style = TextStyle(fontSize = 18.sp)) },
                    textStyle = TextStyle(fontSize = 18.sp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Purple,
                    )
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 20.dp, top = 28.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Add Cover Photo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_image),
                    contentDescription = "Localized description",
                    tint = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            UploadImageBox(pictureUri) { uri ->
                pictureUri = uri
            }
        }

        Row (verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {
                    val uuid = UUID.randomUUID()
                    val uuidString = uuid.toString()
                    newTripViewModel.insertTrip(
                        tripName,
                        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        uuidString
                    )
                    var bitmap = pictureUri?.let { uriToBitmap(contentResolver, it) }
                    if (bitmap != null) {
                        saveImage(context, uuidString, bitmap)
                    }
                    navigateToTrip = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 42.dp, vertical = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                enabled = tripName.isNotBlank()
            ) {//TODO replace this trip entity with the new one that is created
                Text(
                    text = "Add Trip",
                    modifier = Modifier.padding(start = 2.dp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTripPageTopAppBar(pageNavigation: (Screen) -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = {pageNavigation(Screen.Home)}) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Localized description",)
            }
        },
        title = { Text(text = "New Trip") },
        actions = {}
    )
}


