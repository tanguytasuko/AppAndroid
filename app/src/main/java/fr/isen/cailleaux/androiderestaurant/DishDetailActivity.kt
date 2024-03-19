package fr.isen.cailleaux.androiderestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import fr.isen.cailleaux.androiderestaurant.ui.theme.AndroidERestaurantTheme

class DishDetailActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                val dishName = intent.getStringExtra("DISH_NAME") ?: "Dish Name"

                setContent {
                        AndroidERestaurantTheme {
                                // Votre UI pour afficher le détail du plat
                                DishDetailScreen(dishName)
                        }
                }
        }
}

@Composable
fun DishDetailScreen(dishName: String) {
        Column {
                Text(text = dishName, style = MaterialTheme.typography.headlineMedium)
                // Utilisez Image() avec Coil pour charger l'image.
                // Ajoutez plus de composables pour les autres informations du plat.
        }
}