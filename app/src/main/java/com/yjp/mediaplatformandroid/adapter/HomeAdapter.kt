package com.yjp.mediaplatformandroid.adapter

import android.content.Context
import android.databinding.ViewDataBinding
import android.view.View
import com.yjp.mediaplatformandroid.R
import com.yjp.mediaplatformandroid.databinding.ItemHomeBinding
import kotlinx.android.synthetic.main.item_home.view.*
import org.greenrobot.eventbus.EventBus


class HomeAdapter(context: Context, data: List<String>):
        DataBindingAdapter<String>(context, data, R.layout.item_home) {

    data class ItemClicked(val title: String)

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
                val title = itemView.titleTextView.text.toString()
                EventBus.getDefault().post(ItemClicked(title))
            }

            itemView.titleTextView.text
        }
    }
}