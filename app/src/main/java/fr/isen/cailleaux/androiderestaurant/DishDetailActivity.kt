package fr.isen.cailleaux.androiderestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import fr.isen.cailleaux.androiderestaurant.ui.theme.AndroidERestaurantTheme
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
                        // Ici, ajoutez la logique pour ajouter au panier
                }) {
                        Text("Ajouter au panier")
                }
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