package com.example.bdoci

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private var categories: List<String>,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    fun updateCategories(newCategories: List<String>) {
        val list = mutableListOf("All")
        list.addAll(newCategories)
        categories = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        val textView = holder.itemView.findViewById<TextView>(R.id.categoryName)
        textView.text = category

        val isSelected = position == selectedPosition
        val isFavorites = category == "Favorites"
        
        // Apply styling
        if (isSelected) {
            if (isFavorites) {
                textView.setBackgroundResource(R.drawable.category_favorites_bg)
                textView.setTextColor(holder.itemView.context.getColor(R.color.gruv_fg))
            } else {
                textView.setBackgroundResource(R.drawable.category_item_bg)
                textView.setTextColor(holder.itemView.context.getColor(R.color.primary))
            }
            textView.isActivated = true
            textView.setTypeface(null, android.graphics.Typeface.BOLD)
        } else {
            if (isFavorites) {
               textView.setBackgroundResource(0)
               textView.setTextColor(holder.itemView.context.getColor(R.color.gruv_yellow))
               textView.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
               textView.setBackgroundResource(0)
               textView.setTextColor(holder.itemView.context.getColor(R.color.on_surface))
               textView.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
            textView.isActivated = false
        }
        
        textView.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION && currentPos != selectedPosition) {
                val oldPosition = selectedPosition
                selectedPosition = currentPos
                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)
                onCategoryClick(categories[currentPos])
            } else if (currentPos == selectedPosition) {
                onCategoryClick(categories[currentPos])
            }
        }
    }

    override fun getItemCount(): Int = categories.size

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
