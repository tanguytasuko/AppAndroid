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
        val scrollState = rememberScrollState()
        Column(modifier = Modifier.background(Color.White).verticalScroll(scrollState)) {
                Text(text = dish.name_fr, style = MaterialTheme.typography.headlineMedium)
                DishImagesPager(dish.images)
                Text(text = "Ingrédients: ${dish.ingredients.joinToString { it.name_fr }}",
                        style = MaterialTheme.typography.bodyMedium)

                // Supposons que dish.prices contient plusieurs éléments et que vous voulez afficher le premier
                Text(text = "Prix: ${dish.prices.firstOrNull()?.price ?: "Non disponible"}",
                        style = MaterialTheme.typography.bodyMedium)
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