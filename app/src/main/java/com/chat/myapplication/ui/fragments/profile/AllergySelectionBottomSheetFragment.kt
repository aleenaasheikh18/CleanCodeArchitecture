package com.chat.myapplication.ui.fragments.profile

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.chat.myapplication.base.BaseBottomSheetDialogFragment
import com.chat.myapplication.core.data.auth.model.Allergy
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.databinding.BottomSheetAllergySelectionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllergySelectionBottomSheetFragment : BaseBottomSheetDialogFragment<BottomSheetAllergySelectionBinding>(
    BottomSheetAllergySelectionBinding::inflate
) {

    private val viewModel: AllergySelectionViewModel by viewModels()

    private var preloadedAllergies: List<Allergy> = emptyList()
    var onAllergiesUpdated: ((List<String>) -> Unit)? = null

    private val allergyAdapter by lazy {
        AllergyAdapter { allergy ->
            allergy.name?.let { viewModel.toggleAllergy(it) }
        }
    }

    override fun initUserInterface() {
        setupRecyclerView()
        setupClickListeners()
        initObservers()
        viewModel.setPreloadedAllergies(preloadedAllergies)
    }

    private fun setupRecyclerView() {
        bi.rvAllergies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = allergyAdapter
        }
    }

    private fun setupClickListeners() {
        bi.ivClose.setOnClickListener { dismiss() }
        bi.btnSave.setOnClickListener { viewModel.saveAllergies() }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeAllergies() }
                launch { observeUpdateState() }
            }
        }
    }

    private suspend fun observeAllergies() {
        viewModel.allergiesState.collectLatest { state ->
            when (state) {
                is State.Loading -> {
                    bi.progressBar.isVisible = true
                    bi.rvAllergies.isVisible = false
                }
                is State.Success -> {
                    bi.progressBar.isVisible = false
                    bi.rvAllergies.isVisible = true
                    allergyAdapter.submitList(state.data)
                }
                is State.Error -> {
                    bi.progressBar.isVisible = false
                    bi.rvAllergies.isVisible = true
                }
                else -> Unit
            }
        }
    }

    private suspend fun observeUpdateState() {
        viewModel.updateState.collectLatest { state ->
            when (state) {
                is State.Loading -> showProgressBar()
                is State.Success -> {
                    hideProgressBar()
                    onAllergiesUpdated?.invoke(viewModel.getSelectedAllergies())
                    dismiss()
                }
                is State.Error -> {
                    hideProgressBar()
                }
                else -> Unit
            }
        }
    }

    fun setPreloadedAllergies(allergies: List<Allergy>) {
        this.preloadedAllergies = allergies
    }

    companion object {
        const val TAG = "AllergySelectionBottomSheetFragment"

        fun newInstance(allergies: List<Allergy>): AllergySelectionBottomSheetFragment {
            return AllergySelectionBottomSheetFragment().apply {
                setPreloadedAllergies(allergies)
            }
        }
    }
}
