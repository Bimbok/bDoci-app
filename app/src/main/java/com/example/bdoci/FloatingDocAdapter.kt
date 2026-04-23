package com.example.bdoci

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bdoci.models.Doc

class FloatingDocAdapter(private var docs: List<Doc>) :
    RecyclerView.Adapter<FloatingDocAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvDocTitle)
        val btnCopy: Button = view.findViewById(R.id.btnCopyCode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_floating_doc, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doc = docs[position]
        holder.tvTitle.text = doc.title
        holder.btnCopy.setOnClickListener {
            val clipboard = holder.itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Code Snippet", doc.code ?: "")
            clipboard.setPrimaryClip(clip)
            Toast.makeText(holder.itemView.context, "Code copied!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = docs.size

    fun updateData(newDocs: List<Doc>) {
        docs = newDocs
        notifyDataSetChanged()
    }
}