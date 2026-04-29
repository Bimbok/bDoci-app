package com.example.bdoci

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bdoci.models.Doc

class DocAdapter(
    private var docList: List<Doc>,
    private val onFavoriteClick: (Doc) -> Unit
) : RecyclerView.Adapter<DocAdapter.DocViewHolder>() {

    fun updateData(newList: List<Doc>) {
        docList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doc, parent, false)
        return DocViewHolder(view)
    }

    override fun getItemCount(): Int {
        return docList.size
    }

    override fun onBindViewHolder(holder: DocViewHolder, position: Int) {
        val currentDoc = docList[position]

        holder.titleText.text = currentDoc.title
        holder.descText.text = getShortDescription(currentDoc.document)
        
        // Update favorite star icon
        if (currentDoc.isFavorite) {
            holder.favoriteButton.setImageResource(R.drawable.ic_star_filled)
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_star_outline)
        }

        holder.favoriteButton.setOnClickListener {
            onFavoriteClick(currentDoc)
        }

        holder.readMoreButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = android.content.Intent(context, DocDetailActivity::class.java)

            intent.putExtra("EXTRA_ID", currentDoc.id)
            intent.putExtra("EXTRA_TITLE", currentDoc.title)
            intent.putExtra("EXTRA_CATEGORY", currentDoc.category)
            intent.putExtra("EXTRA_DOCUMENT", currentDoc.document)
            intent.putExtra("EXTRA_CODE", currentDoc.code)

            context.startActivity(intent)
        }
    }

    private fun getShortDescription(fullText: String?): String {
        if (fullText == null) return "No description available."
        val maxLength = 100
        if (fullText.length <= maxLength) return fullText
        return fullText.substring(0, maxLength) + "..."
    }

    class DocViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.textTitle)
        val descText: TextView = itemView.findViewById(R.id.textDescription)
        val readMoreButton: Button = itemView.findViewById(R.id.btnReadMore)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.btnFavorite)
    }
}
