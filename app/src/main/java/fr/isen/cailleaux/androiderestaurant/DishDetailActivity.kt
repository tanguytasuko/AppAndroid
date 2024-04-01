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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import com.google.gson.Gson
import androidx.compose.runtime.rememberCoroutineScope
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.FileNotFoundException

class DishDetailActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                val dish = intent.getSerializableExtra("DISH_DETAIL") as? MenuItem // Assurez-vous que c'est Serializable

                setContent {
                        AndroidERestaurantTheme {
                                // Ici, vérifiez si dish n'est pas null
                                dish?.let {
                                        DishDetailScreen(it)
                                }
                        }
                }
        }
}

@Composable
fun DishDetailScreen(dish: MenuItem) {
        // État pour suivre la quantité choisie
        var quantity by remember { mutableStateOf(1) }
        val pricePerItem = dish.prices.firstOrNull()?.price?.toFloatOrNull() ?: 0f
        val totalPrice = pricePerItem * quantity
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        Column(modifier = Modifier.padding(16.dp)) {
                Text(text = dish.name_fr, style = MaterialTheme.typography.headlineMedium)
                // Carousel d'images ou tout autre contenu que vous souhaitez montrer
                DishImagesPager(imageUrls = dish.images)

                // Sélecteur de quantité
                Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(onClick = { if (quantity > 1) quantity-- }) {
                                Text("-")
                        }
                        Text("$quantity", Modifier.padding(horizontal = 8.dp))
                        Button(onClick = { quantity++ }) {
                                Text("+")
                        }
                }

                // Affichage du prix total
                Text("Prix total : $totalPrice €", style = MaterialTheme.typography.bodyLarge)

                // Bouton d'ajout au panier
                Button(onClick = {
                        scope.launch {
                                addToCart(context, dish, quantity, snackbarHostState, scope)
                        }
                }) {
                        Text("Ajouter au panier")
                }
                SnackbarHost(hostState = snackbarHostState)
        }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DishImagesPager(imageUrls: List<String>) {
        val pagerState = rememberPagerState(pageCount = {imageUrls.size})

        HorizontalPager(
                state = pagerState,
                modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
        ) { page ->
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

        // Affichage du Snackbar avec une action "Voir"
        scope.launch {
                val result = snackbarHostState.showSnackbar(
                        message = "Article ajouté au panier !",
                        actionLabel = "Voir",
                        duration = SnackbarDuration.Long
                )
                if (result == SnackbarResult.ActionPerformed) {
                        // Naviguer vers CartActivity si "Voir" est cliqué
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
