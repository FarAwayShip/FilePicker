package com.farawayship.library.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.farawayship.library.R
import com.farawayship.library.enum.FileType

class SearchResultAdapter(
    private val results: MutableList<SearchResult>,
    private val onItemChoose: (SearchResult) -> Unit
) :
    RecyclerView.Adapter<SearchResultAdapter.SearchResultHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun update(newData: List<SearchResult>) {
        results.clear()
        results.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item_layout, parent, false)
        return SearchResultHolder(view)
    }

    override fun onBindViewHolder(holder: SearchResultHolder, position: Int) {
        val res = results[position]
        holder.itemView.setOnClickListener {
            onItemChoose.invoke(res)
        }
        holder.bind(res)
    }

    override fun getItemCount(): Int {
        return results.size
    }

    inner class SearchResultHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: AppCompatImageView = itemView.findViewById(R.id.icon)
        private val name: AppCompatTextView = itemView.findViewById(R.id.name)
        private val capacity: AppCompatTextView = itemView.findViewById(R.id.capacity)

        fun bind(result: SearchResult) {
            val iconId = when (result.extension) {
                FileType.PDF -> R.drawable.ic_pdf
                else -> null
            }
            val iconDrawable = iconId?.let { ContextCompat.getDrawable(itemView.context, it) }
            icon.setImageDrawable(iconDrawable)
            name.text = result.name
            capacity.text = "${result.capacity}"
        }
    }
}