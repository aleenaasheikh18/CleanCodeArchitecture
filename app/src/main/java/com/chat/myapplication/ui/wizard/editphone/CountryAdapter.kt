package com.chat.myapplication.ui.wizard.editphone

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chat.myapplication.core.data.auth.model.CountryArea
import com.chat.myapplication.databinding.ItemCountryBinding
import com.chat.myapplication.utility.glide.loadSvg

class CountryAdapter(
    private val onCountrySelected: (CountryArea) -> Unit
) : ListAdapter<CountryArea, CountryAdapter.CountryViewHolder>(CountryDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding = ItemCountryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CountryViewHolder(
        private val binding: ItemCountryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCountrySelected(getItem(position))
                }
            }
        }

        fun bind(country: CountryArea) {
            with(binding) {
                ivFlag.loadSvg(country.flag)
                tvCountryName.text = country.name.orEmpty()
                tvCountryCode.text = country.countryCode.orEmpty()
            }
        }
    }

    companion object {
        private val CountryDiffCallback = object : DiffUtil.ItemCallback<CountryArea>() {
            override fun areItemsTheSame(oldItem: CountryArea, newItem: CountryArea): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CountryArea, newItem: CountryArea): Boolean {
                return oldItem == newItem
            }
        }
    }
}
