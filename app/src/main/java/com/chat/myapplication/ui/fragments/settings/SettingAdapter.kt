package com.chat.myapplication.ui.fragments.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chat.myapplication.core.data.settings.SettingItem
import com.chat.myapplication.databinding.ItemSettingsBinding
import com.chat.myapplication.utility.hide
import com.chat.myapplication.utility.show

class SettingsAdapter(
    private val items: List<SettingItem>,
    private val onItemClick: (SettingItem) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {

    inner class SettingsViewHolder(val binding: ItemSettingsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SettingItem) = with(binding) {
            imgIcon.setImageResource(item.icon)
            tvTitle.text = root.context.getString(item.title)
            tvDescription.apply {
                text = item.description
                if (item.description.isNotEmpty()) show() else hide()
            }
            imgVerified.apply{ if (item.isVerified) show() else hide() }
            root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val binding = ItemSettingsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SettingsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
