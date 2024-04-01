package fr.isen.cailleaux.androiderestaurant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fr.isen.cailleaux.androiderestaurant.ui.theme.AndroidERestaurantTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopBar(onCartClicked = {
                context.startActivity(Intent(context, CartActivity::class.java))
            })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CategoryItem(categoryName = "Entrées") {
                navigateToCategory("Entrées", context)
            }
            CategoryItem(categoryName = "Plats") {
                navigateToCategory("Plats", context)
            }
            CategoryItem(categoryName = "Desserts") {
                navigateToCategory("Desserts", context)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onCartClicked: () -> Unit) {
    TopAppBar(
        title = { Text("Bienvenue au Restaurant") },
        actions = {
            IconButton(onClick = { onCartClicked() }) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = "Panier")
            }
        }
    )
}

@Composable
fun CategoryItem(categoryName: String, onClick: () -> Unit) {
    Text(
        text = categoryName,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick),
        style = MaterialTheme.typography.headlineMedium
    )
}

fun navigateToCategory(categoryName: String, context: android.content.Context) {
    val intent = Intent(context, CategoryActivity::class.java)
    intent.putExtra("categoryName", categoryName)
    context.startActivity(intent)
}

