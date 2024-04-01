package fr.isen.cailleaux.androiderestaurant

import android.content.Intent
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Surface
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import java.io.Serializable

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categoryName = intent.getStringExtra("categoryName") ?: "Catégorie"
        setContent {
            val menuItems = remember { mutableStateOf<List<MenuItem>>(listOf()) }
            AndroidERestaurantTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Scaffold(
                        topBar = {
                            TopBar(title = categoryName)
                        },
                        content = { paddingValues ->
                            MenuScreen(
                                categoryName = categoryName,
                                items = menuItems.value,
                                modifier = Modifier.padding(paddingValues)
                            )
                        }
                    )
                }
            }
            fetchMenuItems(categoryName) { items ->
                menuItems.value = items
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar(title: String) {
        TopAppBar(
            title = { Text(text = title, color = Color.White, fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        )
    }

private fun fetchMenuItems(categoryName: String, onResult: (List<MenuItem>) -> Unit) {
    val queue = Volley.newRequestQueue(this)
    val url = "http://test.api.catering.bluecodegames.com/menu"
    val params = JSONObject()
    params.put("id_shop", "1")

    val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, params,
        { response ->
            Log.d("CategoryActivity", "Réponse de l'API: $response")
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
fun MenuScreen(categoryName: String, items: List<MenuItem>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    LazyColumn(modifier = modifier) {
        items(items) { item ->
            MenuItemCard(item = item, onClick = {
                context.startActivity(Intent(context, DishDetailActivity::class.java).putExtra("DISH_DETAIL", item))
            })
        }
    }
}

@Composable
fun MenuItemCard(item: MenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            item.images.firstOrNull()?.let { imageUrl ->
                DishImage(imageUrl)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.name_fr,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun DishImage(imageUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth(),
        contentScale = ContentScale.Crop
    )
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
    urls.firstOrNull()?.let { url ->
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .error(android.R.drawable.ic_dialog_alert)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }
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
    val ingredients: List<Ingredient>,
    val prices: List<Price>
) : Serializable

data class Ingredient(
    val id: String,
    val id_shop: String,
    val name_fr: String,
    val create_date: String,
    val update_date: String,
    val id_pizza: String?
) : Serializable

data class Price(
    val id: String,
    val id_pizza: String,
    val id_size: String,
    val price: String,
    val create_date: String,
    val update_date: String,
    val size: String
) : Serializable