package com.example.my_first_application.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.material3.AlertDialog
import android.icu.text.SimpleDateFormat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.example.my_first_application.R
import com.example.my_first_application.data.product.Product
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.ramcosta.composedestinations.result.ResultBackNavigator
import android.net.Uri
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.net.toUri
import coil.compose.AsyncImage


import kotlinx.coroutines.launch
import java.io.File
import java.util.Date


enum class ProductType {
    Consumable,
    Durable,
    Other
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun FormView(navigator: DestinationsNavigator, resultNavigator: ResultBackNavigator<Product>, existingProduct: Product? = null) {

    var selectedOption by remember { mutableStateOf(existingProduct?.type ?: ProductType.Consumable) }
    var productName by remember { mutableStateOf(existingProduct?.name ?: "") }
    var date by remember { mutableStateOf(existingProduct?.date ?: "") }
    var color by remember { mutableStateOf(existingProduct?.color ?: "None") }
    var country by remember { mutableStateOf(existingProduct?.country ?: "None") }
    var statement by remember { mutableStateOf(existingProduct?.isFavorite ?: false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    var imageUri by remember { mutableStateOf<Uri?>(existingProduct?.imageUri) }

    var showImagePickerDialog by remember { mutableStateOf(false) }

    val snack = remember { SnackbarHostState() };

    val context = LocalContext.current


    fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "captured_image.jpg")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        return file.toUri()
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val uri = saveBitmapToFile(context, it)
            imageUri = uri
        }
    }

    Scaffold (snackbarHost = { SnackbarHost(hostState = snack) }) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            if(imageUri == null) {
                product_image(selectedOption)
            } else {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Selected image",
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Choose a type:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Consommable", color = Color.Green)
                        RadioButton(
                            selected = selectedOption == ProductType.Consumable,
                            onClick = { selectedOption = ProductType.Consumable }
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Durable", color = Color.Green)
                        RadioButton(
                            selected = selectedOption == ProductType.Durable,
                            onClick = { selectedOption = ProductType.Durable }
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Autre", color = Color.Green)
                        RadioButton(
                            selected = selectedOption == ProductType.Other,
                            onClick = { selectedOption = ProductType.Other }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Product Date") },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showDatePicker = true
                    }
            )

            if (showDatePicker) {
                DatePickerDialogSample(
                    onDateSelected = { selectedDate -> date = selectedDate },
                    onDismiss = { showDatePicker = false }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            TextField(
                value = color,
                enabled = false,
                onValueChange = { color = it },
                label = { Text("Product Color") },
                modifier = Modifier.fillMaxWidth()
                    .clickable {
                        showColorPicker = true
                    }
            )

            if (showColorPicker) {
                ColorPickerDialogSample(
                    onColorSelected = { selectedColor -> color = selectedColor },
                    onDismiss = { showColorPicker = false }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Product Country") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Ajouter au favoris ?", color = Color.Green)
                Checkbox(
                    checked = statement,
                    onCheckedChange = { statement = !statement }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                showImagePickerDialog = true
            }) {
                Text("Choisir une image")
            }

            if (showImagePickerDialog) {
                AlertDialog(
                    onDismissRequest = { showImagePickerDialog = false },
                    title = { Text("Sélectionner une image") },
                    confirmButton = {},
                    text = {
                        Column {
                            Button(onClick = {
                                galleryLauncher.launch("image/*")
                                showImagePickerDialog = false
                            }) {
                                Text("Depuis la galerie")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(onClick = {
                                cameraLauncher.launch(null)
                                showImagePickerDialog = false
                            }) {
                                Text("Depuis la caméra")
                            }
                        }
                    }
                )
            }

            Validate(
                productName,
                date,
                color,
                country,
                selectedOption,
                snack,
                navigator,
                statement,
                resultNavigator,
                imageUri,
                existingProduct,
            )



        }
    }
}

object ProductIdManager {
    private var currentId: Int = 0

    fun getNextId(): Int {
        return currentId++
    }
}

@Composable
fun Validate(productName: String,
             date: String,
             color: String,
             country: String,
             selectedOption: ProductType,
             snack: SnackbarHostState,
             navigator: DestinationsNavigator,
             statement: Boolean = false,
             resultNavigator: ResultBackNavigator<Product>,
             imageUri:Uri?,
             existingProduct: Product?,
             ){
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }


    val coroutineScope = rememberCoroutineScope();
    Button(onClick = {
        if (productName.isNotBlank() && productName != "Product Name" && date.isNotBlank() && date != "Product Date") {

            showDialog.value = true

        } else {
            coroutineScope.launch {
                snack.showSnackbar("Please fill all fields !");
            }
        }
    }) {
        Text("Validate")
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Confirm Validation") },
            text = { Text("Are you sure you want to validate this product?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    val product = Product(
                        id = existingProduct?.id ?: ProductIdManager.getNextId(),
                        name = productName,
                        date = date,
                        color = color,
                        country = country,
                        type = selectedOption,
                        isFavorite = statement,
                        imageUri = imageUri
                    )

                    resultNavigator.navigateBack(product)
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun product_image(option: ProductType) {

    val imageModifier = Modifier
        .size(100.dp)
        .clip(RoundedCornerShape(16.dp))

    if(option == ProductType.Consumable){
        Image(
            painter = painterResource(id = R.drawable.oiseau),
            contentDescription = "Le contenue de zinzin",
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .size(100.dp)
        )
    }
    else if(option == ProductType.Durable){
        Image(
            painter = painterResource(id = R.drawable.spongebob),
            contentDescription = "Le contenue de zinzin",
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .size(100.dp)
        )
    }
    else if(option == ProductType.Other){
    Image(
        painter = painterResource(id = R.drawable.bomboclat),
        contentDescription = "Le contenue de zinzin",
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .size(100.dp)

    )
        }
}

@Composable
fun ColorPickerDialogSample(onColorSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val controller = rememberColorPickerController()
    var selectedColor by remember { mutableStateOf("#FFFFFF") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick a Color") },
        text = {
            Column {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        selectedColor = colorEnvelope.hexCode
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onColorSelected(selectedColor)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogSample(onDateSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }

        DatePickerDialog(
            onDismissRequest = {
                openDialog.value = false
                onDismiss()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        datePickerState.selectedDateMillis?.let {
                            val sdf = SimpleDateFormat("dd/MM/yyyy")
                            val formattedDate = sdf.format(Date(it))
                            onDateSelected(formattedDate)
                        }
                        onDismiss()
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    openDialog.value = false
                    onDismiss()
                }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


