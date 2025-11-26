package com.chat.myapplication.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.chat.myapplication.databinding.BottomSheetStepsBinding
import com.chat.myapplication.ui.fragments.settings.accountSecurity.StepActionListener
import com.chat.myapplication.ui.fragments.settings.accountSecurity.StepsPagerAdapter
import com.chat.myapplication.ui.fragments.settings.editEmail.EmailVerifiedSuccessFragment
import com.chat.myapplication.ui.fragments.settings.editEmail.EnterEmailFragment
import com.chat.myapplication.ui.fragments.settings.editEmail.VerifyEmailFragment
import com.chat.myapplication.utility.ZoomOutPageTransformer
import com.chat.myapplication.utility.hide
import com.chat.myapplication.utility.show
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StepsBottomSheet(
    private val onFinishClick: () -> Unit,
) : BottomSheetDialogFragment(), StepActionListener {

    private var _binding: BottomSheetStepsBinding? = null
    private val binding get() = _binding!!

    var fragments = listOf<Fragment>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetStepsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        fragments = listOf(
            EnterEmailFragment(this),
            VerifyEmailFragment(this),
        )

        binding.apply {

            val adapter = StepsPagerAdapter(this@StepsBottomSheet, fragments)
            viewPager.adapter = adapter
            viewPager.isUserInputEnabled = false

            viewPager.setPageTransformer(ZoomOutPageTransformer())

            iconBack.setOnClickListener {
                val current = viewPager.currentItem
                if (current > 0) {
                    viewPager.currentItem = current - 1
                } else {
                    dismiss()
                }
            }

            iconClose.setOnClickListener {
                dismiss()
            }

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    resizeViewPager(position)
                }
            })
        }
    }

    private fun resizeViewPager(position: Int) {
        val viewPager = binding.viewPager
        val recyclerView = viewPager.getChildAt(0) as? RecyclerView ?: return

        viewPager.post {
            val view = recyclerView.layoutManager?.findViewByPosition(position)
                ?: return@post

            view.measure(
                View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            val height = view.measuredHeight
            if (height > 0) {
                viewPager.layoutParams.height = height
                viewPager.requestLayout()
            }
        }
    }

    override fun onNextStep() {

        binding.apply {
            val current = viewPager.currentItem
            if (current < fragments.size - 1) {
                viewPager.currentItem = current + 1
            } else {
                onFinishClick()
                dismiss()
            }
        }
    }

    override fun onPreviousStep() {

        binding.apply {
            val current = viewPager.currentItem
            if (current > 0) {
                viewPager.currentItem = current - 1
            }
        }
    }

    override fun onFinishFlow() {
        onFinishClick()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
