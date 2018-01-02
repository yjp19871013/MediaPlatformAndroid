package com.yjp.mediaplatformandroid.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class DataBindingAdapter<out T>(protected val context: Context,
                                         protected val data: List<T>,
                                         protected val layoutId: Int) :
        RecyclerView.Adapter<DataBindingAdapter<T>.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemHolder {
        val inflater = LayoutInflater.from(context)
        val dataBinding: ViewDataBinding =
                DataBindingUtil.inflate(inflater, layoutId, parent, false)
        val holder = constructViewHolder(dataBinding.root)
        holder.dataBinding = dataBinding

        return holder
    }

    override fun onBindViewHolder(holder: ItemHolder?, position: Int) {
        holder?.let {
            val dataBinding = it.dataBinding!!
            doDataBinding(dataBinding, position)
            dataBinding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int = data.size

    abstract fun doDataBinding(dataBinding: ViewDataBinding, position: Int)
    abstract fun constructViewHolder(itemView: View): ItemHolder

    abstract inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var dataBinding: ViewDataBinding? = null
    }
}
