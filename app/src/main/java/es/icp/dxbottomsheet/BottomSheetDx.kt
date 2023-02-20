package es.icp.dxbottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StyleRes
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.icp.dxbottomsheet.databinding.BottomSheetDxBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class BottomSheetDx : BottomSheetDialogFragment() {

    private var _binding : BottomSheetDxBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DxViewModel by viewModels()

    // CAMPOS COMUNES
    private var icon: Int? = null
    private var title: String? = null
    private var message: String? = null
    private var cancelOnTouchOutSide: Boolean = true
    private var cancel: Boolean = true
    private var customTheme: Int? = null

    // CAMPOS PARA LOTTIE
    private var lottieFile: Int? = null
    private var lottieLoop: Boolean = true

    private var positiveTextButton: String? = null
    private var negativeTextButton: String? = null
    private var controlDismiss: Boolean = false
    private var onPositiveClickButton: ((BottomSheetDx) -> Unit)? = null
    private var onNegativeClickButton: ((BottomSheetDx) -> Unit)? = null

//    private var listener : OnClickListenerDx? = null


    companion object {
        const val TAG = "BottomSheetDx"
        private const val ARG_ICON = "argDxIcon"
        private const val ARG_TITLE = "argDxTitle"
        private const val ARG_MESSAGE = "argDxMessage"
        private const val ARG_CANCEL_ON_TOUCH_OUTSIDE = "argDxCancelOnTouchOutSide"
        private const val ARG_CANCELABLE = "argDxTcancelable"
        private const val ARG_THEME = "argDxTheme"

        private const val ARG_LOTTIE_FILE = "argDxLottieFile"
        private const val ARG_LOTTIE_LOOP = "argDxLottieLoop"


        private const val ARG_POSITIVE_TEXT_BUTTON = "argDxPositiveTextButton"
        private const val ARG_NEGATIVE_TEXT_BUTTON = "argDxNegativeTextButton"

        private const val ARG_CONTROL_DISMISS = "argDxControlDismiss"




        @JvmStatic
        private fun newInstance(
            @DrawableRes icon: Int? = null,
            title: String,
            message: String? = null,
            cancelOnTouchOutSide: Boolean = true,
            cancelable: Boolean = true,
            theme: Int? = null,

            @RawRes lottieFile: Int? = null,
            lottieLoop: Boolean = true,

            positiveTextButton: String? = null,
            negativeTextButton: String? = null,
            controlDismiss: Boolean = false,
            onPositiveClickButton: ((BottomSheetDx) -> Unit)? = null,
            onNegativeClickButton: ((BottomSheetDx) -> Unit)? = null
        ) =
            BottomSheetDx().apply {
                this.onPositiveClickButton = onPositiveClickButton
                this.onNegativeClickButton = onNegativeClickButton
                arguments = Bundle().apply {
                    icon?.let { putInt(ARG_ICON, it) }
                    putString(ARG_TITLE, title)
                    putString(ARG_MESSAGE, message)
                    putBoolean(ARG_CANCEL_ON_TOUCH_OUTSIDE, cancelOnTouchOutSide)
                    putBoolean(ARG_CANCELABLE, cancelable)
                    lottieFile?.let { putInt(ARG_LOTTIE_FILE, it) }
                    putBoolean(ARG_LOTTIE_LOOP, lottieLoop)
                    theme?.let { putInt(ARG_THEME, it) }
                    putString(ARG_POSITIVE_TEXT_BUTTON, positiveTextButton)
                    putString(ARG_NEGATIVE_TEXT_BUTTON, negativeTextButton)
                    putBoolean(ARG_CONTROL_DISMISS, controlDismiss)

                }
            }

        private fun newInstanceBuilder(builder: Builder) =
            when (builder) {
                is Builder.Info -> {
                    newInstance(
                        title = builder.title?:"No has puesto titulo",
                        message = builder.message,
                        icon = builder.icon,
                        cancelOnTouchOutSide = builder.cancelOnTouchOutSide,
                        cancelable = builder.cancelable,
                        theme = builder.theme
                    )
                }
                is Builder.Lottie -> {
                    newInstance(
                        title = builder.title?:"No has puesto titulo",
                        icon = builder.icon,
                        lottieFile = builder.lottieFile,
                        lottieLoop = builder.lottieLoop,
                        cancelOnTouchOutSide = builder.cancelOnTouchOutSide,
                        cancelable = builder.cancelable,
                        theme = builder.theme
                    )
                }
                is Builder.Action -> {
                    newInstance(
                        title = builder.title?:"No has puesto titulo",
                        message = builder.message,
                        icon = builder.icon,
                        positiveTextButton = builder.positiveTextButton,
                        cancelOnTouchOutSide = builder.cancelOnTouchOutSide,
                        cancelable = builder.cancelable,
                        theme = builder.theme,
                        controlDismiss = builder.controlDismiss,
                        onPositiveClickButton = builder.positiveListener,
                        onNegativeClickButton = builder.negativeListener,
                        negativeTextButton = builder.negativeTextButton
                    )
                }
            }
    }


    override fun getTheme(): Int = customTheme.takeIf { it != 0 } ?: R.style.BottomSheetDxBaseTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            icon = it.getInt(ARG_ICON)
            title = it.getString(ARG_TITLE)
            message = it.getString(ARG_MESSAGE)
            cancelOnTouchOutSide = it.getBoolean(ARG_CANCEL_ON_TOUCH_OUTSIDE)
            cancel = it.getBoolean(ARG_CANCELABLE)
            controlDismiss = it.getBoolean(ARG_CONTROL_DISMISS)

            lottieFile = it.getInt(ARG_LOTTIE_FILE)
            lottieLoop = it.getBoolean(ARG_LOTTIE_LOOP)

            positiveTextButton = it.getString(ARG_POSITIVE_TEXT_BUTTON)
            negativeTextButton = it.getString(ARG_NEGATIVE_TEXT_BUTTON)

            customTheme = it.getInt(ARG_THEME, R.style.BottomSheetDxBaseTheme)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val rootView = View.inflate(context, R.layout.bottom_sheet_dx, null)
            val dialog = BottomSheetDialog(requireContext(), theme).also { dx ->
                isCancelable = cancel
                dx.apply {
                    window?.attributes?.windowAnimations = R.style.BottomSheetDxBaseAnimation
                    dismissWithAnimation = true
                    setCanceledOnTouchOutside(cancelOnTouchOutSide)
                    setContentView(rootView)
                    setOnShowListener {
                        val d = it as BottomSheetDialog
                        val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                        bottomSheet?.let { view ->
                            val behavior = BottomSheetBehavior.from(view)
                            behavior.state = BottomSheetBehavior.STATE_EXPANDED
                            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                                override fun onStateChanged(bottomSheet: View, newState: Int) {
                                    if (newState == BottomSheetBehavior.STATE_HIDDEN)
                                        this@BottomSheetDx.dismiss()
                                }
                                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                            })
                        }
                    }
                }

            }
            dialog
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            icon?.let { if (it != 0) binding.iconBottomSheet.setImageResource(it) }
            txtTituloBottomSheet.text = title
            message?.let { txtMessageBottomSheet.text = it }
            txtMessageBottomSheet.show(message != null)
            lottieBottomSheet.show(lottieFile != 0)
            lottieBottomSheet.apply {
                lottieFile?.let { if (it != 0) this.setAnimation(it) }
                this.repeatCount = if (lottieLoop) LottieDrawable.INFINITE else 0
                // delay 600ms to play animation
                postDelayed({ this.playAnimation() }, 600)
            }

            containerButtonsBottomSheet.show(onPositiveClickButton != null || onNegativeClickButton != null)
            btnAceptarBottomSheet.apply {
                show(onPositiveClickButton != null)
                positiveTextButton.takeIf { it != null }?.let { text = it }
                setOnClickListener {
                    viewModel.newUiState(DxViewModel.UiState.OnClickPositiveButton)
                }
            }

            btnCancelarBottomSheet.apply {
                show(onNegativeClickButton != null)
                negativeTextButton.takeIf { it != null }?.let { text = it }
                setOnClickListener {
                    viewModel.newUiState(DxViewModel.UiState.OnClickNegativeButton)
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(
                lifecycle = viewLifecycleOwner.lifecycle,
                minActiveState = Lifecycle.State.STARTED,
            ).collectLatest { state ->
                when (state) {
                    is DxViewModel.UiState.Initial -> {
                        binding.btnAceptarBottomSheet.isEnabled = true
                        binding.btnCancelarBottomSheet.isEnabled = true
                    }
                    is DxViewModel.UiState.OnClickPositiveButton -> {
                        binding.btnAceptarBottomSheet.isEnabled = false
                        binding.btnCancelarBottomSheet.isEnabled = false
                        onPositiveClickButton?.invoke(this@BottomSheetDx)
                        if (!controlDismiss) viewModel.newUiState(DxViewModel.UiState.Hide)
                    }
                    is DxViewModel.UiState.OnClickNegativeButton -> {
                        binding.btnAceptarBottomSheet.isEnabled = false
                        binding.btnCancelarBottomSheet.isEnabled = false
                        onNegativeClickButton?.invoke(this@BottomSheetDx)
                        if (!controlDismiss) viewModel.newUiState(DxViewModel.UiState.Hide)
                    }
                    is DxViewModel.UiState.Hide -> {
                        this@BottomSheetDx.dismiss()
                    }
                }
            }
        }
    }


    fun show(fragmentManager: FragmentManager) = show(fragmentManager, TAG)

    fun dissmis() = viewModel.newUiState(DxViewModel.UiState.Hide)
    fun setInitialUiState() = viewModel.newUiState(DxViewModel.UiState.Initial)

    sealed class Builder {

        fun build() = newInstanceBuilder(this)
        fun buildAndShow(fragmentManager: FragmentManager) = build().show(fragmentManager)

        class Info : Builder() {
            internal var icon: Int? = null
            internal var title: String? = null
            internal var message: String? = null
            internal var cancelOnTouchOutSide: Boolean = true
            internal var cancelable: Boolean = true
            internal var theme: Int? = null

            fun setIcon(@DrawableRes icon: Int) = apply { this.icon = icon }
            fun setTitle(title: String) = apply { this.title = title }
            fun setMessage(message: String) = apply { this.message = message }
            fun setCancelOnTouchOutSide(cancelOnTouchOutSide: Boolean) = apply { this.cancelOnTouchOutSide = cancelOnTouchOutSide }
            fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
            fun setTheme(@StyleRes theme: Int) = apply { this.theme = theme }
        }
        class Lottie : Builder() {
            internal var icon: Int? = null
            internal var title: String? = null
            internal var cancelOnTouchOutSide: Boolean = true
            internal var cancelable: Boolean = true
            internal var lottieFile: Int? = null
            internal var lottieLoop: Boolean = true
            internal var theme: Int? = null

            fun setIcon(@DrawableRes icon: Int) = apply { this.icon = icon }
            fun setTitle(title: String) = apply { this.title = title }
            fun setCancelOnTouchOutSide(cancelOnTouchOutSide: Boolean) = apply { this.cancelOnTouchOutSide = cancelOnTouchOutSide }
            fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
            fun setTheme(@StyleRes theme: Int) = apply { this.theme = theme }

            fun setLottie(@RawRes lottieRaw: Int) = apply { this.lottieFile = lottieRaw }
            fun setLottieLoop(lottieLoop: Boolean) = apply { this.lottieLoop = lottieLoop }
        }
        class Action : Builder() {
            internal var icon: Int? = null
            internal var title: String? = null
            internal var message: String? = null
            internal var cancelOnTouchOutSide: Boolean = true
            internal var cancelable: Boolean = true
            internal var controlDismiss: Boolean = false
            internal var positiveTextButton: String? = null
            internal var negativeTextButton: String? = null
            internal var theme: Int? = null

            internal var positiveListener: ((BottomSheetDx) -> Unit)? = null
            internal var negativeListener: ((BottomSheetDx) -> Unit)? = null

            fun setIcon(@DrawableRes icon: Int) = apply { this.icon = icon }
            fun setTitle(title: String) = apply { this.title = title }
            fun setMessage(message: String) = apply { this.message = message }
            fun setCancelOnTouchOutSide(cancelOnTouchOutSide: Boolean) = apply { this.cancelOnTouchOutSide = cancelOnTouchOutSide }
            fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
            fun setControlDismiss(controlDismiss: Boolean) = apply { this.controlDismiss = controlDismiss }
            fun setTextButton(textButton: String) = apply { this.positiveTextButton = textButton }
            fun setTheme(@StyleRes theme: Int) = apply { this.theme = theme }
            fun setPositiveButton(textButton: String, onClickListener: ((BottomSheetDx) -> Unit)?) = apply {
                this.positiveTextButton = textButton
                this.positiveListener = onClickListener
            }
            fun setNegativeButton(textButton: String, onClickListener: ((BottomSheetDx) -> Unit)?) = apply {
                this.negativeTextButton = textButton
                this.negativeListener = onClickListener
            }
        }

    }

}
