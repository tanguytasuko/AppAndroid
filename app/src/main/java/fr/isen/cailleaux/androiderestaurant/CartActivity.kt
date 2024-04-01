package fr.isen.cailleaux.androiderestaurant

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val cartItems = remember { loadCartItems() }
            CartScreen(cartItems = cartItems, onClearCart = {
                clearCart()
                recreate() // Refresh the screen
            })
        }
    }

    private fun loadCartItems(): List<CartItem> {
        val fileName = "cart.json"
        return try {
            applicationContext.openFileInput(fileName).use { inputStream ->
                val text = inputStream.bufferedReader().readText()
                Log.d("CartActivity", "Reading cart JSON: $text") // Ajoutez cette ligne pour imprimer le contenu du JSON
                val gson = Gson()
                val cartDataType = object : TypeToken<List<CartItem>>() {}.type
                gson.fromJson<List<CartItem>>(text, cartDataType)
            }
        } catch (e: Exception) {
            Log.e("CartActivity", "Error loading cart items", e)
            emptyList()
        }
    }


    data class CartData(
        val items: MutableList<CartItem>
    )

    private fun clearCart() {
        val fileName = "cart.json"
        deleteFile(fileName) // Clears the cart by deleting the file
    }
}

@Composable
fun CartScreen(cartItems: List<CartItem>, onClearCart: () -> Unit) {
    Column {
        LazyColumn {
            items(cartItems) { item ->
                CartItemView(item)
            }
        }
        Button(onClick = onClearCart) {
            Text("Vider le panier")
        }
    }
}

@Composable
fun CartItemView(item: CartItem) {
    Text("Article: ${item.dish.name_fr}, Quantité: ${item.quantity}")
}
