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
import androidx.compose.foundation.background
import androidx.compose.material3.Surface
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categoryName = intent.getStringExtra("categoryName") ?: "Catégorie"

        setContent {
            val menuItems = remember { mutableStateOf<List<MenuItem>>(listOf()) }

            AndroidERestaurantTheme {
                Surface(color = MaterialTheme.colorScheme.background)  {
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
                    Card(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                        Column {
                            ImageFromUrls(urls = item.images) // Utilisez votre fonction composable pour afficher les images
                            Text(
                                text = item.name_fr,
                                modifier = Modifier.padding(16.dp)
                            ) // Affichez le nom du plat
                            // Ici, vous pouvez ajouter d'autres détails comme les ingrédients ou le prix
                        }
                    }
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


@Composable
fun ImageFromUrls(urls: List<String>) {
    var currentUrlIndex by rememberSaveable { mutableStateOf(0) }

    // Utilisez LocalContext.current seulement à l'intérieur d'une fonction Composable
    val context = LocalContext.current

    // Vous pouvez omettre les paramètres de builder si vous n'avez pas besoin de les personnaliser
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(urls.getOrNull(currentUrlIndex)) // Utilisez l'URL à l'indice courant
            .error(android.R.drawable.ic_dialog_alert) // Fallback en cas d'erreur
            .build(),
        contentDescription = null, // Fournissez une description appropriée pour l'accessibilité.
        modifier = Modifier
            .size(150.dp) // Définissez la taille de l'image. Ajustez selon vos besoins.
            .aspectRatio(1f),
        contentScale = ContentScale.Crop, // Gère comment l'image doit être redimensionnée ou déplacée pour remplir les dimensions données.
        onLoading = {
            // Affichez un indicateur de chargement si nécessaire
        },
        onError = {
            // Passez à l'URL suivante si une image ne se charge pas
            if (currentUrlIndex < urls.size - 1) {
                currentUrlIndex++
            }
        }
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