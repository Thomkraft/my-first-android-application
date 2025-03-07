package com.example.my_first_application.data.product

import android.net.Uri
import com.example.my_first_application.view.ProductType
import java.io.Serializable

data class Product(
    val id: Int? = null,
    val name: String,
    val date: String,
    val color: String,
    val country: String,
    val type: ProductType,
    val isFavorite: Boolean,
    val imageUri: Uri? = null
):Serializable