package fr.isen.cailleaux.androiderestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import fr.isen.cailleaux.androiderestaurant.ui.theme.AndroidERestaurantTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject


class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categoryName = intent.getStringExtra("categoryName") ?: "Catégorie"

        setContent {
            val menuItems = remember { mutableStateOf<List<MenuItem>>(listOf()) }

            AndroidERestaurantTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Remplacez MenuScreen par le composant d'affichage de votre choix
                    MenuScreen(categoryName = categoryName, items = menuItems.value)
                }
            }

            fetchMenuItems(categoryName) { items ->
                menuItems.value = items
            }
        }
    }

private fun fetchMenuItems(categoryName: String, onResult: (List<MenuItem>) -> Unit) {
    val queue = Volley.newRequestQueue(this)
    val url = "http://test.api.catering.bluecodegames.com/menu"
    val params = JSONObject()
    params.put("id_shop", "1")

    val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, params,
        { response ->
            Log.d("CategoryActivity", "Réponse de l'API: $response") // Ajout du log ici
            try {
                val gson = Gson()
                val menuResponse = gson.fromJson(response.toString(), MenuResponse::class.java)
                val filteredItems =
                    menuResponse.data.firstOrNull { it.name_fr == categoryName }?.items
                        ?: emptyList()
                onResult(filteredItems)
            } catch (e: Exception) {
                Log.e("CategoryActivity", "Parsing error", e)
            }
        },
        { error ->
            error.printStackTrace()
            Log.e("CategoryActivity", "Volley error: ${error.message}")
            runOnUiThread {
                Toast.makeText(this, "Failed to load data: ${error.message}", Toast.LENGTH_LONG)
                    .show()
            }
        })

    queue.add(jsonObjectRequest)
}

}

@Composable
fun MenuScreen(categoryName: String, items: List<MenuItem>) {
    Column {
        Text(
            text = categoryName,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn {
            items(items) { item ->
                MenuItemComposable(item)
            }
        }
    }
}

@Composable
fun MenuItemComposable(item: MenuItem) {
    Text(
        text = item.name_fr,
        modifier = Modifier
            .padding(16.dp)

    )
}

data class MenuResponse(
    val data: List<Category>
)

data class Category(
    val name_fr: String,
    val items: List<MenuItem>
)

data class MenuItem(
    val id: String,
    val name_fr: String,
    val id_category: String,
    val categ_name_fr: String,
    val images: List<String>,
    val ingredients: List<Ingredient>, // Liste des ingrédients de l'item
    val prices: List<Price> // Liste des prix de l'item
)

data class Ingredient(
    val id: String, // Identifiant de l'ingrédient
    val id_shop: String, // Identifiant du magasin/shop
    val name_fr: String, // Nom français de l'ingrédient
    val create_date: String, // Date de création de l'ingrédient
    val update_date: String, // Date de mise à jour de l'ingrédient
    val id_pizza: String? // Identifiant de la pizza (si applicable, peut ne pas être présent pour tous les ingrédients, donc nullable)
)

data class Price(
    val id: String, // Identifiant du prix
    val id_pizza: String, // Identifiant de la pizza
    val id_size: String, // Identifiant de la taille
    val price: String, // Valeur du prix
    val create_date: String, // Date de création du prix
    val update_date: String, // Date de mise à jour du prix
    val size: String // Taille correspondante au prix
)