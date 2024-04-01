package fr.isen.cailleaux.androiderestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import fr.isen.cailleaux.androiderestaurant.ui.theme.AndroidERestaurantTheme
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TopAppBar
import com.google.gson.Gson
import androidx.compose.runtime.rememberCoroutineScope
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.runtime.*


class DishDetailActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                val dish = intent.getSerializableExtra("DISH_DETAIL") as? MenuItem

                setContent {
                        AndroidERestaurantTheme {
                                dish?.let {
                                        DishDetailScreen(it)
                                }
                        }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishDetailScreen(dish: MenuItem) {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        var quantity by remember { mutableStateOf(1) }
        val context = LocalContext.current

        Scaffold(
                topBar = {
                        TopAppBar(title = { Text(dish.name_fr) })
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) {
                Column(
                        modifier = Modifier
                                .padding(it)
                                .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        DishImagesPager(imageUrls = dish.images)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = dish.name_fr,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = "Ingredients: ${dish.ingredients.joinToString { it.name_fr }}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        QuantitySelector(quantity) { newQuantity -> quantity = newQuantity }

                        Button(
                                onClick = {
                                        coroutineScope.launch {
                                                addToCart(context, dish, quantity, snackbarHostState, coroutineScope)
                                        }
                                },
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                        ) {
                                Text("Ajouter au panier")
                        }
                }
        }
}

@Composable
fun QuantitySelector(quantity: Int, onQuantityChanged: (Int) -> Unit) {
        Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
        ) {
                IconButton(onClick = { if (quantity > 1) onQuantityChanged(quantity - 1) }) {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Moins")
                }
                Text("$quantity", Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = { onQuantityChanged(quantity + 1) }) {
                        Icon(Icons.Filled.Add, contentDescription = "Plus")
                }
        }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DishImagesPager(imageUrls: List<String>) {
        val pagerState = rememberPagerState(pageCount = { imageUrls.size })
        HorizontalPager(state = pagerState, modifier = Modifier.height(200.dp)) { page ->
                AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrls[page])
                                .crossfade(true)
                                .build(),
                        contentDescription = "Image du plat",
                        modifier = Modifier.fillMaxWidth()
                )
        }
}

// Ajoutez la variable scope comme paramètre à addToCart
suspend fun addToCart(
        context: Context,
        newItem: MenuItem,
        quantity: Int,
        snackbarHostState: SnackbarHostState,
        scope: CoroutineScope
) {
        val cartItems: MutableList<CartItem> = loadCartItems(context)
        val existingItem = cartItems.find { it.dish.id == newItem.id }
        if (existingItem != null) {
                existingItem.quantity += quantity
        } else {
                cartItems.add(CartItem(newItem, quantity))
        }
        saveCartItems(context, cartItems)

        // Correction: Utilisation de `newItem` au lieu de `item`
        val message = "$quantity x ${newItem.name_fr} ajouté(s) au panier."
        scope.launch {
                val result = snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = "Voir le panier",
                        duration = SnackbarDuration.Indefinite
                )
                if (result == SnackbarResult.ActionPerformed) {
                        context.startActivity(Intent(context, CartActivity::class.java))
                }
        }
}


fun loadCartItems(context: Context): MutableList<CartItem> {
        val fileName = "cart.json"
        val gson = Gson()
        val file = context.getFileStreamPath(fileName)
        return if (file.exists()) {
                context.openFileInput(fileName).use { inputStream ->
                        val reader = inputStream.reader()
                        gson.fromJson(reader, object : TypeToken<List<CartItem>>() {}.type) ?: mutableListOf()
                }
        } else {
                mutableListOf()
        }
}

fun saveCartItems(context: Context, cartItems: List<CartItem>) {
        val fileName = "cart.json"
        val gson = Gson()
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
                val writer = outputStream.writer()
                gson.toJson(cartItems, writer)
                writer.close()
        }
}
