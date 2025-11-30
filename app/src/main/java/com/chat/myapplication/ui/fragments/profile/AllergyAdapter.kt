package com.chat.myapplication.ui.fragments.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chat.myapplication.core.data.auth.model.Allergy
import com.chat.myapplication.databinding.ItemAllergyBinding

class AllergyAdapter(
    private val onAllergyToggled: (Allergy) -> Unit
) : ListAdapter<Allergy, AllergyAdapter.AllergyViewHolder>(AllergyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllergyViewHolder {
        val binding = ItemAllergyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AllergyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllergyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AllergyViewHolder(
        private val binding: ItemAllergyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(allergy: Allergy) {
            binding.tvAllergyName.text = allergy.name
            binding.cbAllergy.setOnCheckedChangeListener(null)
            binding.cbAllergy.isChecked = allergy.isSelected

            binding.cbAllergy.setOnCheckedChangeListener { _, _ ->
                onAllergyToggled(allergy)
            }

            binding.root.setOnClickListener {
                binding.cbAllergy.isChecked = !binding.cbAllergy.isChecked
            }
        }
    }

    private class AllergyDiffCallback : DiffUtil.ItemCallback<Allergy>() {
        override fun areItemsTheSame(oldItem: Allergy, newItem: Allergy): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Allergy, newItem: Allergy): Boolean {
            return oldItem == newItem
        }
    }
}
