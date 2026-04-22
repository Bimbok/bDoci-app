package com.example.bdoci

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bdoci.models.Doc

class DocAdapter(private var docList: List<Doc>) : RecyclerView.Adapter<DocAdapter.DocViewHolder>() {

    // ADD THIS NEW FUNCTION to handle search updates
    fun updateData(newList: List<Doc>) {
        docList = newList
        notifyDataSetChanged() // This tells the RecyclerView to refresh the screen
    }

    // This creates the visual view for a single item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doc, parent, false)
        return DocViewHolder(view)
    }

    // Tells the list how many items we have
    override fun getItemCount(): Int {
        return docList.size
    }

    // This binds the data to the specific views in the row
    override fun onBindViewHolder(holder: DocViewHolder, position: Int) {
        val currentDoc = docList[position]

        holder.titleText.text = currentDoc.title
        holder.descText.text = getShortDescription(currentDoc.document)

        holder.readMoreButton.setOnClickListener {
            // Create an Intent to open DocDetailActivity
            val context = holder.itemView.context
            val intent = android.content.Intent(context, DocDetailActivity::class.java)

            // Pack the data into the Intent payload
            intent.putExtra("EXTRA_TITLE", currentDoc.title)
            intent.putExtra("EXTRA_CATEGORY", currentDoc.category)
            intent.putExtra("EXTRA_DOCUMENT", currentDoc.document)
            intent.putExtra("EXTRA_CODE", currentDoc.code)

            // Launch the new screen!
            context.startActivity(intent)
        }
    }

    // Helper function to truncate the text
    private fun getShortDescription(fullText: String?): String {
        if (fullText == null) return "No description available."
        val maxLength = 100
        if (fullText.length <= maxLength) return fullText
        return fullText.substring(0, maxLength) + "..."
    }

    // This class finds and holds the views from item_doc.xml
    class DocViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.textTitle)
        val descText: TextView = itemView.findViewById(R.id.textDescription)
        val readMoreButton: Button = itemView.findViewById(R.id.btnReadMore)
    }
}