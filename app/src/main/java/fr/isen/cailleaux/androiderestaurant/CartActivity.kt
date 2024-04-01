package fr.isen.cailleaux.androiderestaurant

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(cartItems: List<CartItem>, onClearCart: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Votre Panier") },
                actions = {
                    IconButton(onClick = onClearCart) {
                        Icon(Icons.Filled.Delete, contentDescription = "Vider le panier")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            LazyColumn {
                items(cartItems) { item ->
                    CartItemCard(item = item)
                }
            }
            if (cartItems.isNotEmpty()) {
                Button(
                    onClick = onClearCart,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
                ) {
                    Text("Vider le panier")
                }
            }
        }
    }
}

@Composable
fun CartItemCard(item: CartItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            item.dish.images.firstOrNull()?.let { imageUrl ->
                Image(
                    painter = rememberImagePainter(imageUrl),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(item.dish.name_fr, fontWeight = FontWeight.Bold)
                Text("Quantité: ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                Text("Prix: ${item.dish.prices.firstOrNull()?.price}€", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun CartItemView(item: CartItem) {
    Text("Article: ${item.dish.name_fr}, Quantité: ${item.quantity}")
}
