package com.example.myapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RestauranteAdapter(private val restaurantes: List<Restaurante>, private val onClick: (Restaurante) -> Unit) :
    RecyclerView.Adapter<RestauranteAdapter.RestauranteViewHolder>() {

    class RestauranteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageButton: ImageButton = view.findViewById(R.id.card_button)
        val textView: TextView = view.findViewById(R.id.card_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestauranteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return RestauranteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestauranteViewHolder, position: Int) {
        val restaurante = restaurantes[position]
        holder.textView.text = restaurante.nombre

        // Cargar la imagen usando Glide o Picasso
        Glide.with(holder.itemView.context)
            .load(restaurante.imagenUrl)
            .into(holder.imageButton)

        holder.itemView.setOnClickListener {
            Log.d("RestauranteAdapter", "Seleccionado: ${restaurante.nombre}")
            onClick(restaurante) // Llama a la función al hacer clic
        }
    }

    override fun getItemCount(): Int = restaurantes.size
}