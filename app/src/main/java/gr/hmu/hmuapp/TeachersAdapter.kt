package gr.hmu.hmuapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gr.hmu.hmuapp.data.Teacher

class TeachersAdapter(
    private val onItemClick: (Teacher) -> Unit
) : RecyclerView.Adapter<TeachersAdapter.ViewHolder>() {

    private val items = mutableListOf<Teacher>()

    fun submitList(newItems: List<Teacher>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_teacher, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.name)
        private val title: TextView = view.findViewById(R.id.title)
        private val department: TextView = view.findViewById(R.id.department)

        fun bind(item: Teacher) {
            name.text = item.name
            title.text = item.title
            department.text = item.department
            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}
