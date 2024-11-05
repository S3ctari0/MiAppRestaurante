package com.example.myapplication

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RestauranteAdapter(
    private val restaurantes: List<Restaurante>,
    private val onClick: (Restaurante) -> Unit
) : RecyclerView.Adapter<RestauranteAdapter.RestauranteViewHolder>() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val favoriteIcons = hashMapOf<String, Boolean>() // Aquí guardamos el estado de los favoritos

    var onFavoriteClickListener: ((Restaurante) -> Unit)? = null

    init {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        cargarFavoritos() // Cargar los favoritos al iniciar
    }

    private fun cargarFavoritos() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val usuarioDocRef = db.collection("usuarios").document(userId)
            usuarioDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val favoritos = document.get("favoritos") as? List<String> ?: emptyList()
                    favoritos.forEach { id -> favoriteIcons[id] = true } // Marcamos cada favorito en el mapa
                    notifyDataSetChanged() // Notificar cambios para que se actualicen los íconos
                }
            }.addOnFailureListener { e ->
                Log.e("RestauranteAdapter", "Error al cargar los favoritos: ${e.message}", e)
            }
        }
    }

    class RestauranteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageButton: ImageButton = view.findViewById(R.id.card_button)
        val textView: TextView = view.findViewById(R.id.card_text_view)
        val ratingTextView: TextView = view.findViewById(R.id.card_rating_text_view)
        val favoriteIcon: ImageView = view.findViewById(R.id.favorite_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestauranteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return RestauranteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestauranteViewHolder, position: Int) {
        val restaurante = restaurantes[position]
        holder.textView.text = restaurante.nombre
        holder.ratingTextView.text = "${restaurante.calificacionPromedio}"

        // Cargar la imagen usando Glide
        Glide.with(holder.itemView.context)
            .load(restaurante.imagenUrl)
            .into(holder.imageButton)

        // Obtener el estado de favorito del mapa
        val isFavorite = favoriteIcons[restaurante.id] ?: false
        holder.favoriteIcon.setImageResource(if (isFavorite) R.drawable.heart_filled else R.drawable.heart_empty)

        holder.favoriteIcon.setOnClickListener {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                // Cambiar el estado del favorito en el mapa local
                val newFavoriteState = !isFavorite
                favoriteIcons[restaurante.id] = newFavoriteState

                // Actualizar el icono
                holder.favoriteIcon.setImageResource(
                    if (newFavoriteState) R.drawable.heart_filled else R.drawable.heart_empty
                )

                // Acceder al documento del usuario en la colección "usuarios"
                val usuarioDocRef = db.collection("usuarios").document(userId)

                usuarioDocRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Obtener los favoritos actuales
                        val favoritos = document.get("favoritos") as? MutableList<String> ?: mutableListOf()

                        if (newFavoriteState) {
                            // Agregar el ID del restaurante a la lista de favoritos
                            if (!favoritos.contains(restaurante.id)) {
                                favoritos.add(restaurante.id)
                            }
                        } else {
                            // Eliminar el ID del restaurante de la lista de favoritos
                            favoritos.remove(restaurante.id)
                        }

                        // Actualizar el campo "favoritos" en Firestore
                        usuarioDocRef.update("favoritos", favoritos)
                            .addOnSuccessListener {
                                Log.d("RestauranteAdapter", "Favorito actualizado en el usuario.")
                                notifyItemChanged(position) // Refresca la vista para reflejar el cambio
                            }
                            .addOnFailureListener { e ->
                                Log.e("RestauranteAdapter", "Error al actualizar los favoritos del usuario: ${e.message}", e)
                            }
                    } else {
                        Log.e("RestauranteAdapter", "El documento del usuario no existe.")
                    }
                }
            } else {
                Log.e("RestauranteAdapter", "Usuario no autenticado.")
            }
        }

        holder.itemView.setOnClickListener {
            // Crear un Intent para ir a ActivityPlace
            val context = holder.itemView.context
            val intent = Intent(context, ActivityPlace::class.java).apply {
                putExtra("restaurante_id", restaurante.id)
                putExtra("restaurante_nombre", restaurante.nombre)
                putExtra("restaurante_descripcion", restaurante.descripcion)
                putExtra("restaurante_imagen_url", restaurante.imagenUrl)
            }
            context.startActivity(intent) // Iniciar la nueva actividad
        }
    }

    // Implementación del método getItemCount
    override fun getItemCount(): Int {
        return restaurantes.size // Devuelve el tamaño de la lista de restaurantes
    }
}
