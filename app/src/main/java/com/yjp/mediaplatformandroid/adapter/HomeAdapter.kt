package com.yjp.mediaplatformandroid.adapter

import android.content.Context
import android.databinding.ViewDataBinding
import android.view.View
import android.widget.Toast
import com.yjp.mediaplatformandroid.R
import com.yjp.mediaplatformandroid.databinding.ItemHomeBinding
import kotlinx.android.synthetic.main.item_home.view.*


class HomeAdapter(context: Context, data: List<String>):
        DataBindingAdapter<String>(context, data, R.layout.item_home) {
    override fun doDataBinding(dataBinding: ViewDataBinding, position: Int) {
        val binding = dataBinding as ItemHomeBinding
        binding.title = data[position]
    }

    override fun constructViewHolder(itemView: View)
            : DataBindingAdapter<String>.ItemHolder = ItemHolder(itemView)

    inner class ItemHolder(itemView: View)
        : DataBindingAdapter<String>.ItemHolder(itemView) {

        init {
            itemView.setOnClickListener {
                Toast.makeText(context, itemView.titleTextView.text, Toast.LENGTH_SHORT).show()
            }

            itemView.titleTextView.text
        }
    }
}