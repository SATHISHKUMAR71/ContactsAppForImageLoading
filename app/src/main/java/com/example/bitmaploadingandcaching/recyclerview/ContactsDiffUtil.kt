package com.example.bitmaploadingandcaching.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.example.bitmaploadingandcaching.dataclass.Contact

class ContactsDiffUtil(private val oldList: List<Contact>, private val newList: List<Contact>):DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]==newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]==newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList[oldItemPosition].name
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}