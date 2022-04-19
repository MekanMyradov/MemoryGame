package com.mekan_myradov.memorygame.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mekan_myradov.memorygame.ModelClass
import com.mekan_myradov.memorygame.R
import kotlinx.android.synthetic.main.item_custom_row.view.*

class ItemAdapter(val context: Context, val items: ArrayList<ModelClass>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_custom_row,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items.get(position)


        holder.tvDateItem.text = item.date
        holder.tvTimeItem.text = item.time
        holder.tvScoreItem.text = item.score.toString()
        holder.tvLvlItem.text = item.level.toString()

        // Updating the background color according to the odd/even positions in list.
        if (position % 2 == 0) {
            holder.cardViewItem.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.lightGray
                )
            )
        } else {
            holder.cardViewItem.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each item to
        val tvDateItem = view.tvDateItem
        val tvTimeItem = view.tvTimeItem
        val tvScoreItem = view.tvScoreItem
        val tvLvlItem = view.tvLvlItem
        val cardViewItem = view.card_view_item
    }
}