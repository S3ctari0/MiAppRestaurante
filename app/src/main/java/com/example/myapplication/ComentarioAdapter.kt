package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ComentariosAdapter(private val listaComentarios: List<Comentario>) : RecyclerView.Adapter<ComentariosAdapter.ComentariosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentariosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comentario, parent, false)
        return ComentariosViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComentariosViewHolder, position: Int) {
        val comentario = listaComentarios[position]
        holder.nombreTextView.text = comentario.autorNombre // Muestra el nombre del autor
        holder.comentarioTextView.text = comentario.texto
    }

    override fun getItemCount(): Int {
        return listaComentarios.size
    }

    inner class ComentariosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.userComment)
        val comentarioTextView: TextView = itemView.findViewById(R.id.textComment)
    }
}

