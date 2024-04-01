package fr.isen.cailleaux.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.isen.cailleaux.androiderestaurant.ui.theme.AndroidERestaurantTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    HomeScreen()
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
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.snow_world_1),
                contentDescription = "Restaurant Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 16.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Choisissez à manger",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )


            val appetizers = "Entrées"
            val mainCourses = "Plats"
            val desserts = "Desserts"

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
}

@Composable
fun MenuItem(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        color = Color(0xFF001F3F),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
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

