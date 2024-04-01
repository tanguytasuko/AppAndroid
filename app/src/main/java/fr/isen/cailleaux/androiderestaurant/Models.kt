package fr.isen.cailleaux.androiderestaurant

import java.io.Serializable

data class CartItem(
    val dish: MenuItem,
    var quantity: Int
) : Serializable
