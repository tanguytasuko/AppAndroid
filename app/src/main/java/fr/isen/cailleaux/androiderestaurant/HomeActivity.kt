package fr.isen.cailleaux.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.cailleaux.androiderestaurant.ui.theme.AndroidERestaurantTheme

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
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Entrées", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.clickable {
            navigateToCategory("Entrées", context)
        })
        Text(text = "Plats", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.clickable {
            navigateToCategory("Plats", context)
        })
        Text(text = "Desserts", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.clickable {
            navigateToCategory("Desserts", context)
        })
    }
}

fun navigateToCategory(category: String, context: android.content.Context) {
    val intent = Intent(context, CategoryActivity::class.java)
    intent.putExtra("CATEGORY_NAME", category)
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidERestaurantTheme {
        HomeActivityScreen()
    }
}
