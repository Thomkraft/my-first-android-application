package com.example.my_first_application.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.my_first_application.data.product.Product
import com.example.my_first_application.view.destinations.FormViewDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.my_first_application.R
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Destination(start = true)
@Composable
fun Home(navigator: DestinationsNavigator, resultRecipient: ResultRecipient<FormViewDestination, Product>) {
    var products by rememberSaveable { mutableStateOf(listOf<Product>()) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    fun deleteProduct(product: Product) {
        products = products.filter { it != product }
    }

    resultRecipient.onNavResult { navResult ->
        when (navResult) {
            is NavResult.Value -> {
                val product = navResult.value
                products = if (products.any { it.id == product.id }) {
                    products.map { if (it.id == product.id) product else it }
                } else {
                    products + product
                }
            }
            is NavResult.Canceled -> { }
        }
    }


    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            Button(
                onClick = { navigator.navigate(FormViewDestination()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Create Product")
            }

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Rechercher un produit") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            val filteredProducts = products.filter { it.name.startsWith(searchQuery, ignoreCase = true) }
            ProductList(filteredProducts, onDelete = { product -> deleteProduct(product) }, navigator = navigator)
        }
    }
}

@Composable
fun ProductList(products: List<Product>, onDelete: (Product) -> Unit, navigator: DestinationsNavigator) {
    val context = LocalContext.current
    var alertDeleteProduct by rememberSaveable { mutableStateOf<Product?>(null) }
    var alertShowProduct by rememberSaveable { mutableStateOf<Product?>(null) }

    val favoriteProducts = products.filter { it.isFavorite }
    val otherProducts = products

    Column {
        if (favoriteProducts.isNotEmpty()) {
            Text(
                text = "Favoris",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
            )
            LazyRow {
                items(favoriteProducts) { product ->
                    ProductCard(product, onDelete = { alertDeleteProduct = it }, onShowDetails = { alertShowProduct = it },
                    )
                }
            }
        }

        Text(
            text = "Tous les produits",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.padding(8.dp)
        ) {
            items(otherProducts) { product ->
                ProductCard(product, onDelete = { alertDeleteProduct = it }, onShowDetails = { alertShowProduct = it },
                    )
            }
        }
    }

    alertDeleteProduct?.let { product ->
        DeleteProductDialog(product, onDelete = {
            onDelete(product)
            alertDeleteProduct = null
        }, onDismiss = { alertDeleteProduct = null })
    }

    alertShowProduct?.let { product ->
        ProductDialogSample(
            product,
            onDismiss = { alertShowProduct = null },
            onEdit = { navigator.navigate(FormViewDestination(product)) }
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onDelete: (Product) -> Unit,
    onShowDetails: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onShowDetails(product) },
                    onLongPress = { onDelete(product) },
                )
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (product.imageUri == null) {
                product_image(product.type)
            } else {
                AsyncImage(
                    model = product.imageUri,
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .size(100.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Name: ${product.name}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Type: ${product.type}", fontSize = 16.sp)

        }
    }
}



@Composable
fun ProductDialogSample(product: Product, onDismiss: () -> Unit, onEdit: (Product) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Product Details") },
        text = {
            Column {
                Text("Name: ${product.name}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Type: ${product.type}", fontSize = 16.sp)
                Text("Date: ${product.date}", fontSize = 16.sp)
                Text("Color: ${product.color}", fontSize = 16.sp)
                Text("Country: ${product.country}", fontSize = 16.sp)
                Text("Favorite: ${product.isFavorite}", fontSize = 16.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { onEdit(product) } ) {
                Text("Modifier")
            }
        }
    )
}

@Composable
fun DeleteProductDialog(product: Product, onDismiss: () -> Unit, onDelete: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Product") },
        text = {
            Text("Are you sure you want to delete ${product.name}?")
        },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
