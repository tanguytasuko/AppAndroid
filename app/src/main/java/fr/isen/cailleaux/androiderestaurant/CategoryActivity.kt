package fr.isen.cailleaux.androiderestaurant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.cailleaux.androiderestaurant.ui.theme.AndroidERestaurantTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier


class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val category = intent.getStringExtra("CATEGORY_NAME") ?: ""

        setContent {
            AndroidERestaurantTheme {
                CategoryScreen(categoryName = category) { dish ->
                    val intent = Intent(this, DishDetailActivity::class.java)
                    intent.putExtra("DISH_NAME", dish)
                    startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun CategoryScreen(categoryName: String, navigateToDishDetail: (String) -> Unit) {
    val dishes = when(categoryName) {
        "Entrée" -> listOf("Salade César", "Tomate Mozzarella")
        "Plats" -> listOf("Steak Frites", "Poulet Basquaise")
        "Dessert" -> listOf("Tarte Tatin", "Mousse au Chocolat")
        else -> emptyList()
    }

    Column {
        Text(text = categoryName, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))
        LazyColumn {
            items(dishes) { dish ->
                Text(text = dish, modifier = Modifier
                    .padding(16.dp)
                    .clickable { navigateToDishDetail(dish) })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryScreenPreview() {
    AndroidERestaurantTheme {
        CategoryScreen(categoryName = "Entrée") {}
    }
}