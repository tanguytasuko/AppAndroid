package fr.isen.cailleaux.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.cailleaux.androiderestaurant.ui.theme.AndroidERestaurantTheme
import androidx.compose.foundation.layout.*


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    HomeActivityScreen()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("HomeActivity", "Activity destroyed")
    }
}



@Composable
fun HomeActivityScreen() {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Header()
        Image(
            painter = painterResource(id = R.drawable.snow_world_1),
            contentDescription = "Restaurant Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(top = 16.dp)
                .padding(bottom = 16.dp)
        )

        // Texte d'en-tête
        Text(
            text = "Bienvenue au Restaurant",
            style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier
                    .padding(bottom = 16.dp)
        )


        // Utilisation de vos variables existantes pour les catégories
        val appetizers = "Entrées"
        val mainCourses = "Plats"
        val desserts = "Desserts"

        // Affichage des catégories avec un design attrayant
        MenuItem(text = appetizers) {
            navigateToCategory(appetizers, context)
        }
        MenuItem(text = mainCourses) {
            navigateToCategory(mainCourses, context)
        }
        MenuItem(text = desserts) {
            navigateToCategory(desserts, context)
        }
    }
}

@Composable
fun Header() {
    Surface(
        color = Color(0xFF001F3F), // Couleur de fond bleu navy
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Hauteur de l'en-tête
    ) {
        Text(
            text = "TanguyRestaurant",
            color = Color.White, // Couleur du texte blanc
            style = MaterialTheme.typography.headlineMedium, // Style du texte
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center) // Alignement au centre
        )
    }
}

@Composable
fun MenuItem(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // Ajout d'un espace autour du MenuItem
            .clickable(onClick = onClick),
        color = Color(0xFF001F3F), // Couleur de fond bleu navy
        shape = RoundedCornerShape(8.dp) // Forme arrondie des coins
    ) {
        Text(
            text = text,
            color = Color.White, // Couleur du texte blanc
            style = MaterialTheme.typography.bodyLarge, // Style du texte
            modifier = Modifier.padding(16.dp)
        )
    }
}

fun navigateToCategory(category: String, context: android.content.Context) {
    val intent = Intent(context, CategoryActivity::class.java)
    intent.putExtra("categoryName", category) // Utilisez "categoryName" comme clé
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidERestaurantTheme {
        HomeActivityScreen()
    }
}
