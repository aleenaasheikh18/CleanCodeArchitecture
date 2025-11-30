package com.chat.myapplication.ui.fragments.profile

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.chat.myapplication.R
import com.chat.myapplication.base.BaseFragment
import com.chat.myapplication.core.data.auth.model.Allergy
import com.chat.myapplication.core.data.auth.model.Customer
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.databinding.FragmentProfileSettingBinding
import com.chat.myapplication.ui.auth.LauncherScreenActivity
import com.chat.myapplication.utility.AppConstants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileSettingFragment : BaseFragment<FragmentProfileSettingBinding>(
    FragmentProfileSettingBinding::inflate
), OnMapReadyCallback {

    private val viewModel: ProfileSettingViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private var pendingLocation: LatLng? = null
    private var mapView: MapView? = null

    override fun initUserInterface() {
        initMapView()
        initClickListeners()
        initApiObserver()
        viewModel.getCustomerProfile()
    }

    private fun initMapView() {
        mapView = bi.mapView1
        mapView?.onCreate(null)
        mapView?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.apply {
            isZoomControlsEnabled = false
            isScrollGesturesEnabled = false
            isZoomGesturesEnabled = false
        }
        pendingLocation?.let { showLocationOnMap(it) }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        mapView?.onDestroy()
        mapView = null
        googleMap = null
        super.onDestroyView()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    private fun setupUI(customer: Customer) {
        with(bi) {
            // Load Image
            loadProfileImage(customer.profileImage)

            // Eater Name
            tvCustomerName.text = buildString {
                append(customer.firstName.orEmpty())
                if (!customer.lastName.isNullOrEmpty()) {
                    append(" ")
                    append(customer.lastName)
                }
            }

            ivIsInfluencer.isVisible = customer.customerType == CUSTOMER_TYPE_INFLUENCER

            // Eater Phone & Email
            tvPhoneNumber.text = customer.phoneNo.takeIf { !it.isNullOrEmpty() } ?: "-"
            tvEmailAddress.text = customer.email.takeIf { !it.isNullOrEmpty() } ?: "-"

            // Allergies - update preferenceManager and display chips
            preferenceManager.allergies = customer.allergies.orEmpty()
            setupAllergyChips()

            // Address
            val address = customer.deliveryAddress?.address
            val hasAddress = !address.isNullOrEmpty()

            chipAddAddress.isVisible = !hasAddress
            groupAddressContent.isVisible = hasAddress

            if (hasAddress) {
                tvAddressTitle.text = getString(R.string.default_address)
                tvAddress.text = address

                // Delivery Instructions
                val note = customer.deliveryAddress?.note
                tvDeliveryAddress.text = note.takeIf { !it.isNullOrEmpty() } ?: "-"

                // Show location on map
                customer.location?.coordinates?.let { coords ->
                    if (coords.size >= 2) {
                        val latLng = LatLng(coords[1], coords[0]) // [longitude, latitude] -> LatLng(lat, lng)
                        if (googleMap != null) {
                            showLocationOnMap(latLng)
                        } else {
                            pendingLocation = latLng
                        }
                    }
                }
            }
        }
    }

    private fun showLocationOnMap(latLng: LatLng) {
        googleMap?.apply {
            clear()
            addMarker(MarkerOptions().position(latLng))
            moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
        }
    }

    private fun setupAllergyChips() {
        bi.chipGroupAllergies.removeAllViews()

        val userAllergies = preferenceManager.allergies

        // Add allergy chips (red)
        userAllergies.forEach { allergyName ->
            val chip = createAllergyChip(allergyName)
            bi.chipGroupAllergies.addView(chip)
        }

        // Add "Manage Allergen" chip (green) at the end
        val manageChip = createManageAllergenChip()
        bi.chipGroupAllergies.addView(manageChip)
    }

    private fun createAllergyChip(allergyName: String): Chip {
        return Chip(requireContext()).apply {
            text = allergyName
            isClickable = false
            isCheckable = false
            chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            )
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                .setAllCornerSizes(resources.getDimension(R.dimen.new_dimen_20_dp))
                .build()
        }
    }

    private fun createManageAllergenChip(): Chip {
        return Chip(requireContext()).apply {
            text = getString(R.string.manage_allergen)
            isClickable = true
            isCheckable = false
            chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.colorGreen)
            )
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                .setAllCornerSizes(resources.getDimension(R.dimen.new_dimen_20_dp))
                .build()
            setOnClickListener { openAllergySelectionBottomSheet() }
        }
    }

    private fun openAllergySelectionBottomSheet() {
        viewModel.loadAllergies(preferenceManager.allergies)
    }

    private fun showAllergyBottomSheet(allergies: List<Allergy>) {
        val bottomSheet = AllergySelectionBottomSheetFragment.newInstance(allergies)
        bottomSheet.onAllergiesUpdated = { updatedAllergies ->
            preferenceManager.allergies = updatedAllergies
            setupAllergyChips()
        }
        bottomSheet.show(childFragmentManager, AllergySelectionBottomSheetFragment.TAG)
    }

    private fun initClickListeners() {
        bi.tvLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        dialogManager.showDialog(
            context = requireContext(),
            titleStringResource = getString(R.string.logout),
            descriptionStringResource = getString(R.string.logout_text),
            positiveButtonStringResource = R.string.str_yes,
            extraButtonStringResource = R.string.no,
            onPositiveButtonClick = { viewModel.logout() }
        )
    }

    private fun initApiObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeProfileState() }
                launch { observeLogoutState() }
                launch { observeAllergiesState() }
            }
        }
    }

    private suspend fun observeProfileState() {
        viewModel.profileState.collectLatest { state ->
            when (state) {
                is State.Loading -> showProgressBar()
                is State.Success -> {
                    hideProgressBar()
                    setupUI(state.data)
                }
                is State.Error -> {
                    hideProgressBar()
                    showInfoDialog(description = state.message)
                }
                else -> Unit
            }
        }
    }

    private suspend fun observeLogoutState() {
        viewModel.logoutState.collectLatest { state ->
            when (state) {
                is State.Loading -> showProgressBar()
                is State.Success -> {
                    hideProgressBar()
                    navigateToLogin()
                }
                is State.Error -> {
                    hideProgressBar()
                    showInfoDialog(description = state.message)
                }
                else -> Unit
            }
        }
    }

    private suspend fun observeAllergiesState() {
        viewModel.allergiesState.collectLatest { state ->
            when (state) {
                is State.Loading -> showProgressBar()
                is State.Success -> {
                    hideProgressBar()
                    showAllergyBottomSheet(state.data)
                    viewModel.resetAllergiesState()
                }
                is State.Error -> {
                    hideProgressBar()
                    showInfoDialog(description = state.message)
                    viewModel.resetAllergiesState()
                }
                else -> Unit
            }
        }
    }

    private fun loadProfileImage(profileImage: String?) {
        if (!profileImage.isNullOrEmpty()) {
            val imageUrl = AppConstants.IMAGE_URL + profileImage
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .circleCrop()
                .into(bi.ivPreviewCustomer)
        }
    }

    private fun navigateToLogin() {
        preferenceManager.clearSession()
        val intent = Intent(requireContext(), LauncherScreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    companion object {
        private const val CUSTOMER_TYPE_INFLUENCER = "influencer"
        private const val DEFAULT_ZOOM = 15f
    }
}
