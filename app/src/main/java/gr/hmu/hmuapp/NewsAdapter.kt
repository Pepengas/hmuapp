package gr.hmu.hmuapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gr.hmu.hmuapp.data.RssItem

class NewsAdapter(
    private val onItemClick: (RssItem) -> Unit
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    private val items = mutableListOf<RssItem>()

    fun submitList(newItems: List<RssItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val date: TextView = view.findViewById(R.id.date)

        fun bind(item: RssItem) {
            title.text = item.title
            date.text = item.pubDate
            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}
