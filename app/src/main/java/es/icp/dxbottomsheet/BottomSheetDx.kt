package es.icp.dxbottomsheet

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.RawRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeView
import es.icp.dxbottomsheet.databinding.BottomSheetDxBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

open class BottomSheetDx : BottomSheetDialogFragment() {

    private var _binding : BottomSheetDxBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DxViewModel by viewModels()

    // CAMPOS COMUNES
    private var typeLayout: TypeLayout? = null
    private var icon: Int? = null
    private var title: String? = null
    private var message: String? = null
    private var cancelOnTouchOutSide: Boolean = true
    private var cancel: Boolean = true
    private var controlDismiss: Boolean = false
    private var customTheme: Int? = null

    // CAMPOS PARA LOTTIE / IMAGEN
    private var lottieFile: Int? = null
    private var lottieLoop: Boolean = true
    private var imagenFile: Int? = null
    private var bitmap: Bitmap? = null


    //CAMPOS PARA INPUT
    private var inputType: Int? = null
    private var textHint: String? = null
    private var endIconClearText: Boolean = false
    private var imeOptions: Int? = null
    private var inputLayout: TextInputLayout? = null
    private var textoInput: String? = null
    private var errorMessage: String? = null

    private var dropdownItems: List<String>? = null
    private var initialValuePicker: Int? = null

    //LISTENERS
    private var onPositiveClickButton: ((BottomSheetDx) -> Unit)? = null
    private var onNegativeClickButton: ((BottomSheetDx) -> Unit)? = null
    private var inputListener : ((BottomSheetDx, String) -> Unit)? = null
    private var onCancelListener : (() -> Unit)? = null
    private var onPickerNumberListener : ((BottomSheetDx, Int) -> Unit)? = null
    private var onSelectorListener: ((BottomSheetDx, Int?) -> Unit)? = null
    private var barcodeListener: ((BottomSheetDx, String) -> Unit)? = null

    private var positiveTextButton: String? = null
    private var negativeTextButton: String? = null

    private var customLayout: Int? = null

    private var viewStubCallBack : ((ViewStub) -> Unit)? = null

    private var barcodeView: BarcodeView? = null
    private var barcodeContainer : MaterialCardView? = null


    companion object {
        const val TAG = "BottomSheetDx"
        private const val ARG_ICON = "argDxIcon"
        private const val ARG_TITLE = "argDxTitle"
        private const val ARG_MESSAGE = "argDxMessage"
        private const val ARG_CANCEL_ON_TOUCH_OUTSIDE = "argDxCancelOnTouchOutSide"
        private const val ARG_CONTROL_DISMISS = "argDxControlDismiss"
        private const val ARG_CANCELABLE = "argDxTcancelable"
        private const val ARG_THEME = "argDxTheme"
        private const val ARG_LOTTIE_FILE = "argDxLottieFile"
        private const val ARG_LOTTIE_LOOP = "argDxLottieLoop"
        private const val ARG_IMAGEN_FILE = "argDxImagenFile"
        private const val ARG_POSITIVE_TEXT_BUTTON = "argDxPositiveTextButton"
        private const val ARG_NEGATIVE_TEXT_BUTTON = "argDxNegativeTextButton"
        private const val ARG_INPUT_TYPE = "argDxInputType"
        private const val ARG_INPUT_HINT = "argDxInputHint"
        private const val ARG_END_ICON_CLEAR_TEXT = "argDxEndIconClearText"
        private const val ARG_IME_OPTIONS = "argDxImeOptions"
        private const val ARG_TEXTO_INPUT = "argDxTextInput"
        private const val ARG_TYPE_LAYOUT = "argDxTypeLayout"
        private const val ARG_CUSTOM_LAYOUT = "argDxCustomLayout"
        private const val ARG_ERROR_MESSAGE = "argDxErrorMessage"
        private const val ARG_BITMAP = "argDxBitmap"
        private const val ARG_INITIAL_VALUE_PICKER = "argDxInitialValuePicker"
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
            @DrawableRes imagenFile: Int? = null,
            bitmap: Bitmap? = null,

            positiveTextButton: String? = null,
            negativeTextButton: String? = null,
            controlDismiss: Boolean = false,
            inputType: Int? = null,

            textInput: String? = null,
            textHint : String? = null,
            endIconClearText : Boolean = true,
            imeOptions: Int? = null,
            typeLayout: TypeLayout? = null,
            errorMessage: String? = null,
            initialValuePicker: Int? = null,

            onCancelListener: (() -> Unit)? = null,
            onPositiveClickButton: ((BottomSheetDx) -> Unit)? = null,
            onNegativeClickButton: ((BottomSheetDx) -> Unit)? = null,
            inputListener: ((BottomSheetDx, String) -> Unit)? = null,
            onPickerNumberListener : ((BottomSheetDx, Int) -> Unit)? = null,
            onSelectorListener: ((BottomSheetDx, Int?) -> Unit)? = null,
            dropdownItems: List<String>? = null,
            barcodeListener: ((BottomSheetDx, String) -> Unit)? = null,

            @LayoutRes customLayout: Int? = null,
            viewStubCallBack: ((ViewStub) -> Unit)? = null

        ) =
            BottomSheetDx().apply {
                this.onPositiveClickButton = onPositiveClickButton
                this.onNegativeClickButton = onNegativeClickButton
                this.inputListener = inputListener
                this.onCancelListener = onCancelListener
                this.onPickerNumberListener = onPickerNumberListener
                this.dropdownItems = dropdownItems
                this.onSelectorListener = onSelectorListener
                this.viewStubCallBack = viewStubCallBack
                this.barcodeListener = barcodeListener
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
                    inputType?.let { putInt(ARG_INPUT_TYPE, it) }
                    putString(ARG_INPUT_HINT, textHint)
                    putBoolean(ARG_END_ICON_CLEAR_TEXT, endIconClearText)
                    imeOptions?.let { putInt(ARG_IME_OPTIONS, it) }
                    imagenFile?.let { putInt(ARG_IMAGEN_FILE, it) }
                    typeLayout?.let { putSerializable(ARG_TYPE_LAYOUT, it) }
                    customLayout?.let { putInt(ARG_CUSTOM_LAYOUT, it) }
                    textInput?.let { putString(ARG_TEXTO_INPUT, it) }
                    errorMessage?.let { putString(ARG_ERROR_MESSAGE, it) }
                    bitmap?.let { putParcelable(ARG_BITMAP, it) }
                    initialValuePicker?.let { putInt(ARG_INITIAL_VALUE_PICKER, it) }
                }
            }

        fun newInstanceBuilder(builder: Builder) =
            when (builder) {
                is Builder.Info -> {
                    newInstance(
                        icon = builder.icon,
                        title = builder.title?:"No has puesto titulo",
                        message = builder.message,
                        cancelOnTouchOutSide = builder.cancelOnTouchOutSide,
                        cancelable = builder.cancelable,
                        theme = builder.theme,
                        typeLayout = TypeLayout.INFO
                    )
                }
                is Builder.LottieOrImage -> {
                    newInstance(
                        icon = builder.icon,
                        title = builder.title?:"No has puesto titulo",
                        message = builder.message,
                        lottieFile = builder.lottieFile,
                        lottieLoop = builder.lottieLoop,
                        cancelOnTouchOutSide = builder.cancelOnTouchOutSide,
                        cancelable = builder.cancelable,
                        positiveTextButton = builder.positiveTextButton,
                        onPositiveClickButton = builder.positiveListener,
                        theme = builder.theme,
                        imagenFile = builder.imageFile,
                        bitmap = builder.bitmap,
                        typeLayout = if (builder.lottieFile != null) TypeLayout.LOTTIE else TypeLayout.IMAGE
                    )
                }
                is Builder.Action -> {
                    newInstance(
                        icon = builder.icon,
                        title = builder.title?:"No has puesto titulo",
                        message = builder.message,
                        positiveTextButton = builder.positiveTextButton,
                        cancelOnTouchOutSide = builder.cancelOnTouchOutSide,
                        cancelable = builder.cancelable,
                        theme = builder.theme,
                        controlDismiss = builder.controlDismiss,
                        onPositiveClickButton = builder.positiveListener,
                        onNegativeClickButton = builder.negativeListener,
                        negativeTextButton = builder.negativeTextButton,
                        onCancelListener = builder.onCancelListener,
                        typeLayout = TypeLayout.ACTION
                    )
                }
                is Builder.Input -> {
                    newInstance(
                        icon = builder.icon,
                        title = builder.title ?: "No has puesto titulo",
                        message = builder.message,
                        positiveTextButton = builder.positiveTextButton,
                        negativeTextButton = builder.negativeTextButton,
                        cancelOnTouchOutSide = builder.cancelOnTouchOutSide,
                        cancelable = builder.cancelable,
                        theme = builder.theme,
                        controlDismiss = builder.controlDismiss,
                        inputListener = builder.inputListener,
                        inputType = builder.inputType,
                        textHint = builder.textHint,
                        endIconClearText = builder.endIconClearText,
                        imeOptions = builder.imeOptions,
                        onCancelListener = builder.onCancelListener,
                        onNegativeClickButton = builder.negativeListener,
                        typeLayout = TypeLayout.INPUT,
                        textInput = builder.texto,
                        errorMessage = builder.errorMessage

                    )
                }
                is Builder.Selector -> {
                    newInstance(
                        icon = builder.icon,
                        title = builder.title ?: "No has puesto titulo",
                        message = builder.message,
                        positiveTextButton = builder.positiveTextButton,
                        negativeTextButton = builder.negativeTextButton,
                        cancelOnTouchOutSide = builder.cancelOnTouchOutSide,
                        cancelable = builder.cancelable,
                        theme = builder.theme,
                        controlDismiss = builder.controlDismiss,
                        textHint = builder.textHint,
                        onCancelListener = builder.onCancelListener,
                        onNegativeClickButton = builder.negativeListener,
                        typeLayout = TypeLayout.SELECTOR,
                        dropdownItems = builder.dropdownItems,
                        onSelectorListener = builder.dropdownListener
                    )
                }
                is Builder.PickerNumber -> {
                    newInstance(
                        icon = builder.icon,
                        title = builder.title ?: "No has puesto titulo",
                        message = builder.message,
                        positiveTextButton = builder.positiveTextButton,
                        cancelOnTouchOutSide = builder.cancelOnTouchOutSide,
                        negativeTextButton = builder.negativeTextButton,
                        onNegativeClickButton = builder.negativeListener,
                        cancelable = builder.cancelable,
                        theme = builder.theme,
                        controlDismiss = builder.controlDismiss,
                        onPickerNumberListener = builder.onPickerNumberListener,
                        onCancelListener = builder.onCancelListener,
                        initialValuePicker = builder.initialValue,
                        typeLayout = TypeLayout.PICKER_NUMBER
                    )
                }
                is Builder.Custom -> {
                    newInstance(
                        icon = builder.icon,
                        title = builder.title ?: "No has puesto titulo",
                        message = builder.message,
                        positiveTextButton = builder.positiveTextButton,
                        cancelOnTouchOutSide = builder.cancelOnTouchOutSide,
                        negativeTextButton = builder.negativeTextButton,
                        onNegativeClickButton = builder.negativeListener,
                        cancelable = builder.cancelable,
                        theme = builder.theme,
                        controlDismiss = builder.controlDismiss,
                        onCancelListener = builder.onCancelListener,
                        onPositiveClickButton = builder.positiveListener,
                        typeLayout = TypeLayout.CUSTOM,
                        customLayout = builder.customLayout,
                        viewStubCallBack = builder.viewStubCallBack

                    )
                }
                is Builder.Barcode->{
                    newInstance(
                        icon = builder.icon,
                        title = builder.title ?: "No has puesto titulo",
                        message = builder.message,
                        positiveTextButton = builder.positiveTextButton,
                        cancelOnTouchOutSide = builder.cancelOnTouchOutSide,
                        cancelable = builder.cancelable,
                        theme = builder.theme,
                        controlDismiss = builder.controlDismiss,
                        onCancelListener = builder.onCancelListener,
                        typeLayout = TypeLayout.BARCODE,
                        barcodeListener = builder.onBarcodeListener,
                        inputType = builder.inputType,
                        textHint = builder.textHint,
                        endIconClearText = builder.endIconClearText,
                        imeOptions = builder.imeOptions,
                        errorMessage = builder.errorMessage,
                        textInput = builder.texto,

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
            inputType = it.getInt(ARG_INPUT_TYPE)
            textHint = it.getString(ARG_INPUT_HINT)
            endIconClearText = it.getBoolean(ARG_END_ICON_CLEAR_TEXT)
            imeOptions = it.getInt(ARG_IME_OPTIONS)
            imagenFile = it.getInt(ARG_IMAGEN_FILE)
            typeLayout =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    it.getSerializable(ARG_TYPE_LAYOUT, TypeLayout::class.java)
                else   it.getSerializable(ARG_TYPE_LAYOUT) as TypeLayout
            customLayout = it.getInt(ARG_CUSTOM_LAYOUT)
            textoInput = it.getString(ARG_TEXTO_INPUT)
            errorMessage = it.getString(ARG_ERROR_MESSAGE)
            bitmap= it.getParcelable(ARG_BITMAP)
            initialValuePicker = it.getInt(ARG_INITIAL_VALUE_PICKER)
        }
    }
    override fun onCancel(dialog: DialogInterface) {
        onCancelListener?.invoke()
        super.onCancel(dialog)
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
        setupCommonViews()
        setupButtons()
        initUiState()
        when (typeLayout) {
            TypeLayout.LOTTIE -> setupViewLottie()
            TypeLayout.IMAGE -> setupViewImage()
            TypeLayout.INPUT -> setupViewInput()
            TypeLayout.PICKER_NUMBER -> setupViewPickerNumber()
            TypeLayout.SELECTOR -> setupViewSelector()
            TypeLayout.CUSTOM -> setupViewCustom()
            TypeLayout.BARCODE -> setupViewBarcode()
            else -> {}
        }
    }

    private fun initUiState() =
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(
                lifecycle = viewLifecycleOwner.lifecycle,
                minActiveState = Lifecycle.State.STARTED,
            ).collectLatest { state ->
                when (state) {
                    is DxViewModel.UiState.Initial -> {
                        binding.btnCancelarBottomSheet.isEnabled = true
                        binding.dxProgressBar.hide()
                    }
                    is DxViewModel.UiState.Reset -> {
                        binding.btnAceptarBottomSheet.isEnabled = true
                        binding.btnCancelarBottomSheet.isEnabled = true
//                        inputLayout?.error = null
                        binding.dxProgressBar.hide()
                    }
                    is DxViewModel.UiState.Loading -> {
                        binding.btnAceptarBottomSheet.isEnabled = false
                        binding.btnCancelarBottomSheet.isEnabled = false
                        binding.dxProgressBar.visible()
                    }
                    is DxViewModel.UiState.OnScannerClickListener -> {
                        viewModel.newUiState(DxViewModel.UiState.Loading)
                        barcodeListener?.invoke(this@BottomSheetDx, viewModel.textoInput.value.orEmpty())
                        if (!controlDismiss) viewModel.newUiState(DxViewModel.UiState.Hide)
                    }
                    is DxViewModel.UiState.OnClickPositiveButton -> {
                        viewModel.newUiState(DxViewModel.UiState.Loading)
                        onPositiveClickButton?.invoke(this@BottomSheetDx)
                        if (!controlDismiss) viewModel.newUiState(DxViewModel.UiState.Hide)
                    }
                    is DxViewModel.UiState.OnInputClickListener -> {
                        viewModel.newUiState(DxViewModel.UiState.Loading)
                        inputListener?.invoke(this@BottomSheetDx, viewModel.textoInput.value.orEmpty())
                        if (!controlDismiss) viewModel.newUiState(DxViewModel.UiState.Hide)
                    }
                    is DxViewModel.UiState.OnClickNegativeButton -> {
                        viewModel.newUiState(DxViewModel.UiState.Loading)
                        onNegativeClickButton?.invoke(this@BottomSheetDx)
                        if (!controlDismiss) viewModel.newUiState(DxViewModel.UiState.Hide)
                    }
                    is DxViewModel.UiState.OnNumPickerClickListener -> {
                        viewModel.newUiState(DxViewModel.UiState.Loading)
                        onPickerNumberListener?.invoke(this@BottomSheetDx, viewModel.numPicker.value ?: 0)
                        if (!controlDismiss) viewModel.newUiState(DxViewModel.UiState.Hide)
                    }
                    is DxViewModel.UiState.OnDropDownClickListener -> {
                        viewModel.newUiState(DxViewModel.UiState.Loading)
                        onSelectorListener?.invoke(this@BottomSheetDx, viewModel.dropSelecction.value)
                        if (!controlDismiss) viewModel.newUiState(DxViewModel.UiState.Hide)
                    }
                    is DxViewModel.UiState.Hide -> {
                        this@BottomSheetDx.dismiss()
                    }
                }
            }
        }

    /*
     * METODOS PUBLICOS
     */
    fun show(fragmentManager: FragmentManager) = show(fragmentManager, TAG)
    fun dissmis() = viewModel.newUiState(DxViewModel.UiState.Hide)
    fun resetUiState() = viewModel.newUiState(DxViewModel.UiState.Reset)
    fun setInputError(msg:String) = viewModel.setTextoErrorInput(msg)

    private fun ocultarLector() {
        barcodeContainer?.goneAlpha()
        inputLayout?.visibleAlpha()
        binding.btnCancelarBottomSheet.text = "Escanear"
        barcodeView?.pause()
    }

    private fun mostrarLector() {
        binding.btnCancelarBottomSheet.text = "Cerrar"
        inputLayout?.goneAlpha()
        barcodeContainer?.visibleAlpha()
        IntentIntegrator.forSupportFragment(this@BottomSheetDx)
            .also {
                it.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                it.setCameraId(0)
                it.setBeepEnabled(true)
                it.setOrientationLocked(true)
                it.setBarcodeImageEnabled(true)
            }

        barcodeView?.resume()
        leerCodigo()
    }

    private fun leerCodigo(){
        barcodeView?.decodeContinuous { result ->
            barcodeView?.pause()
            viewModel.setValorScanner(result.text)
            Log.w("BARCODE", "leerCodigo: ${result.text}")
            ocultarLector()
        }
    }
    private fun setupViewBarcode() = with(binding) {
        btnAceptarBottomSheet.isEnabled = false
        viewStub.layoutResource = R.layout.barcode_layout
        var txtInput: TextInputEditText? = null
        viewStub.inflate().also { vs ->

            barcodeView = vs.findViewById<BarcodeView>(R.id.barcode_view)
            barcodeContainer = vs.findViewById<MaterialCardView>(R.id.barcode_container)

            inputLayout = vs.findViewById<TextInputLayout>(R.id.input_layout_dx)
                .apply {
                    this.hint = textHint.orEmpty()
                    this.endIconMode = if (endIconClearText) TextInputLayout.END_ICON_CLEAR_TEXT else TextInputLayout.END_ICON_NONE

                }
            txtInput = vs.findViewById<TextInputEditText>(R.id.txt_input_dx)
                .apply {
                    this@BottomSheetDx.inputType?.let { this.inputType = it }
                    this@BottomSheetDx.imeOptions?.let { this.imeOptions = it }
                    this.setText(textoInput.orEmpty())
                    this.doOnTextChanged { text, _, _, _ ->
                        viewModel.setTextoInput(text.toString())
                        errorMessage?.let { msg->
                            inputLayout?.error = if (text.isNullOrBlank()) msg else null
                        }
                        btnAceptarBottomSheet.isEnabled = text?.toString()?.isNotEmpty() ?: false
                    }
                }
        }
        viewModel.textoErrorInput.observe(viewLifecycleOwner) { error ->
            inputLayout?.error = error
        }
        viewModel.valorScanner.observe(viewLifecycleOwner) { value ->
            value?.let { txtInput?.setText(it) }
        }
        btnCancelarBottomSheet.apply {
            text = "Escanear"
            icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_barcode, null)
            iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            setIconTintResource(R.color.btn_text_selector)
            setOnClickListener {
                if (barcodeContainer?.isVisible == true) ocultarLector()
                else mostrarLector()
            }
        }

    }

    private fun setupViewCustom() = binding.apply {
        customLayout.takeIf { it != 0 }?.let { viewStub.layoutResource = it }
        viewStubCallBack?.invoke(viewStub)
    }

    private fun setupCommonViews() = binding.apply {
        icon.takeIf { it != 0 }?.let { iconBottomSheet.setImageResource(it) }
        txtTituloBottomSheet.text = title
        txtMessageBottomSheet.apply {
            show(message != null)
            text = message.orEmpty()
        }
    }
    private fun setupButtons() = binding.apply {
        containerButtonsBottomSheet.show(
            onPositiveClickButton != null || onNegativeClickButton != null || inputListener != null ||
                    onPickerNumberListener != null || onSelectorListener != null || barcodeListener != null)
        btnAceptarBottomSheet.apply {
            show(onPositiveClickButton != null || inputListener != null || onPickerNumberListener != null || onSelectorListener != null || barcodeListener != null)
            text = positiveTextButton.orEmpty()
            setOnClickListener {
                when {
                    inputListener != null -> viewModel.newUiState(DxViewModel.UiState.OnInputClickListener)
                    onPickerNumberListener != null ->viewModel.newUiState(DxViewModel.UiState.OnNumPickerClickListener)
                    onSelectorListener != null -> viewModel.newUiState(DxViewModel.UiState.OnDropDownClickListener)
                    barcodeListener != null -> viewModel.newUiState(DxViewModel.UiState.OnScannerClickListener)
                    else -> viewModel.newUiState(DxViewModel.UiState.OnClickPositiveButton)
                }
            }
        }

        btnCancelarBottomSheet.apply {
            show(onNegativeClickButton != null || barcodeListener != null)
            text = negativeTextButton.orEmpty()
            setOnClickListener {
                viewModel.newUiState(DxViewModel.UiState.OnClickNegativeButton)
            }
        }

    }
    private fun setupViewPickerNumber() = binding.apply {
        viewStub.layoutResource = R.layout.picker_number_layout
        viewModel.setNumPicker(initialValuePicker?:0)
        viewStub.inflate().also {
            it.findViewById<ImageView>(R.id.picker_minus).apply {
                setOnClickListener { viewModel.minusNumPicker() }
            }
            it.findViewById<ImageView>(R.id.picker_mas).apply {
                setOnClickListener { viewModel.plusNumPicker() }
            }
            it.findViewById<TextView>(R.id.txt_number).apply {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.numPicker.observe(viewLifecycleOwner) {txt ->
                        text = txt.toString()
                    }
                }
            }
        }
    }
    private fun setupViewImage() = binding.apply {
        viewStub.layoutResource = R.layout.imagen_layout
        viewStub.inflate().also {
            it.findViewById<ImageView>(R.id.img_dx).apply {
                imagenFile.takeIf { it != 0 }?.let { img ->
                    setImageResource(img)
                }
                bitmap?.let { bmp -> setImageBitmap(bmp) }
            }
        }


    }
    private fun setupViewLottie() = binding.apply {
        lottieFile.takeIf { it != 0 }?.let { lottie ->
            viewStub.layoutResource = R.layout.lottie_layout
            viewStub.inflate().also {
                it.findViewById<LottieAnimationView>(R.id.lottie_bottom_sheet)
                    .apply{
                        this.setAnimation(lottie)
                        this.repeatCount = if (lottieLoop) LottieDrawable.INFINITE else 0
                        postDelayed({ this.playAnimation() }, 600)
                    }

            }
        }
    }
    private fun setupViewInput() = binding.apply {
        btnAceptarBottomSheet.isEnabled = false
        viewStub.layoutResource = R.layout.input_layout
        viewStub.inflate().also { vs ->
            inputLayout = vs.findViewById<TextInputLayout>(R.id.input_layout_dx)
                .apply {
                    this.hint = textHint.orEmpty()
                    this.endIconMode = if (endIconClearText) TextInputLayout.END_ICON_CLEAR_TEXT else TextInputLayout.END_ICON_NONE

                }
            vs.findViewById<TextInputEditText>(R.id.txt_input_dx)
                .apply {
                    this@BottomSheetDx.inputType?.let { this.inputType = it }
                    this@BottomSheetDx.imeOptions?.let { this.imeOptions = it }
                    this.setText(textoInput.orEmpty())
                    this.doOnTextChanged { text, _, _, _ ->
                        viewModel.setTextoInput(text.toString())
                        errorMessage?.let { msg->
                            inputLayout?.error = if (text.isNullOrEmpty()) msg else null
                        }
                        btnAceptarBottomSheet.isEnabled = text?.toString()?.isNotEmpty() ?: false
                    }
                }
        }
        viewModel.textoErrorInput.observe(viewLifecycleOwner) { error ->
            inputLayout?.error = error
        }

    }

    private fun setupViewSelector() = binding.apply {
        viewStub.layoutResource = R.layout.dropdown_layout
        viewStub.inflate().also { vs ->
            vs.findViewById<TextInputLayout>(R.id.dropdown_layout_dx)
                .apply {this.hint = textHint.orEmpty() }

            vs.findViewById<AutoCompleteTextView>(R.id.dropdown_dx)
                .apply {
                    setAdapter(ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, dropdownItems?.toMutableList().orEmpty()))
                    setOnClickListener { showDropDown() }
                    setOnItemClickListener { parent, view, position, id ->
                        viewModel.setDropSelecction(position)
                    }
                }
        }
    }




    sealed class Builder {

        internal var icon: Int? = null
        internal var title: String? = null
        internal var message: String? = null
        internal var cancelOnTouchOutSide: Boolean = true
        internal var cancelable: Boolean = true
        internal var controlDismiss: Boolean = false
        internal var theme: Int? = null

        internal var positiveTextButton: String? = null
        internal var positiveListener: ((BottomSheetDx) -> Unit)? = null

        internal var negativeTextButton: String? = null
        internal var negativeListener: ((BottomSheetDx) -> Unit)? = null

        internal var onCancelListener: (() -> Unit)? = null

        fun build() = newInstanceBuilder(this)

        fun buildAndShow(fragmentManager: FragmentManager) = build().show(fragmentManager)
        @Override protected abstract fun setIcon(@DrawableRes icon: Int): Builder
        @Override protected abstract fun setTitle(title: String): Builder
        @Override protected abstract fun setMessage(message: String): Builder
        @Override protected abstract fun setCancelOnTouchOutSide(cancelOnTouchOutSide: Boolean): Builder
        @Override protected abstract fun setCancelable(cancelable: Boolean): Builder
        @Override protected abstract fun setTheme(@StyleRes theme: Int): Builder
        @Override protected abstract fun setControlDismiss(controlDismiss: Boolean) : Builder
        @Override protected abstract fun setOnCancelListener(onCancelListener: (() -> Unit)?): Builder


        class Info : Builder() {
            public override fun setIcon(@DrawableRes icon: Int) = apply { this.icon = icon }
            public override fun setTitle(title: String) = apply { this.title = title }
            public override fun setMessage(message: String) = apply { this.message = message }
            public override fun setCancelOnTouchOutSide(cancelOnTouchOutSide: Boolean) = apply { this.cancelOnTouchOutSide = cancelOnTouchOutSide }
            public override fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
            public override fun setTheme(@StyleRes theme: Int) = apply { this.theme = theme }
            public override fun setControlDismiss(controlDismiss: Boolean) = apply { this.controlDismiss = controlDismiss }
            public override fun setOnCancelListener(onCancelListener: (() -> Unit)?) = apply { this.onCancelListener = onCancelListener }

            fun setPositiveButton(textButton: String, onPositiveClickListener: ((BottomSheetDx) -> Unit)?) = apply {
                this.positiveTextButton = textButton
                this.positiveListener = onPositiveClickListener
            }
            fun setNegativeButton(textButton: String, onNegativeClickListener: ((BottomSheetDx) -> Unit)?) = apply {
                this.negativeTextButton = textButton
                this.negativeListener = onNegativeClickListener
            }

        }
        class LottieOrImage : Builder() {

            internal var lottieFile: Int? = null
            internal var lottieLoop: Boolean = true
            internal var imageFile: Int? = null
            internal var bitmap: Bitmap? = null

            public override fun setIcon(@DrawableRes icon: Int) = apply { this.icon = icon }
            public override fun setTitle(title: String) = apply { this.title = title }
            public override fun setMessage(message: String) = apply { this.message = message }
            public override fun setCancelOnTouchOutSide(cancelOnTouchOutSide: Boolean) = apply { this.cancelOnTouchOutSide = cancelOnTouchOutSide }
            public override fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
            public override fun setControlDismiss(controlDismiss: Boolean) = apply { this.controlDismiss = controlDismiss }

            public override fun setTheme(@StyleRes theme: Int) = apply { this.theme = theme }
            fun setLottie(@RawRes lottieRaw: Int) = apply { this.lottieFile = lottieRaw }
            fun setLottieLoop(lottieLoop: Boolean) = apply { this.lottieLoop = lottieLoop }
            fun setImageResource(@DrawableRes image: Int) = apply { this.imageFile = image }

            fun setImageBitmap(bitmap: Bitmap) = apply { this.bitmap = bitmap }

            public override fun setOnCancelListener(onCancelListener: (() -> Unit)?) = apply { this.onCancelListener = onCancelListener }

            fun setPositiveButton(textButton: String, onPositiveClickListener: ((BottomSheetDx) -> Unit)?) = apply {
                this.positiveTextButton = textButton
                this.positiveListener = onPositiveClickListener
            }
            fun setNegativeButton(textButton: String, onNegativeClickListener: ((BottomSheetDx) -> Unit)?) = apply {
                this.negativeTextButton = textButton
                this.negativeListener = onNegativeClickListener
            }


        }
        class Action : Builder() {

            public override fun setIcon(@DrawableRes icon: Int) = apply { this.icon = icon }
            public override fun setTitle(title: String) = apply { this.title = title }
            public override fun setMessage(message: String) = apply { this.message = message }
            public override fun setCancelOnTouchOutSide(cancelOnTouchOutSide: Boolean) = apply { this.cancelOnTouchOutSide = cancelOnTouchOutSide }
            public override fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
            public override fun setControlDismiss(controlDismiss: Boolean) = apply { this.controlDismiss = controlDismiss }
            public override fun setTheme(@StyleRes theme: Int) = apply { this.theme = theme }

            public override fun setOnCancelListener(onCancelListener: (() -> Unit)?) = apply { this.onCancelListener = onCancelListener }

            fun setPositiveButton(textButton: String, onPositiveClickListener: ((BottomSheetDx) -> Unit)?) = apply {
                this.positiveTextButton = textButton
                this.positiveListener = onPositiveClickListener
            }
            fun setNegativeButton(textButton: String, onNegativeClickListener: ((BottomSheetDx) -> Unit)?) = apply {
                this.negativeTextButton = textButton
                this.negativeListener = onNegativeClickListener
            }

        }
        class Input : Builder() {

            internal var inputType : Int? = null
            internal var textHint : String? = null
            internal var texto: String? = null
            internal var endIconClearText : Boolean = true
            internal var imeOptions : Int? = null
            internal var inputListener: ((BottomSheetDx, String) -> Unit)? = null
            internal var errorMessage : String? = null

            public override fun setIcon(@DrawableRes icon: Int) = apply { this.icon = icon }
            public override fun setTitle(title: String) = apply { this.title = title }
            public override fun setMessage(message: String) = apply { this.message = message }
            public override fun setCancelOnTouchOutSide(cancelOnTouchOutSide: Boolean) = apply { this.cancelOnTouchOutSide = cancelOnTouchOutSide }
            public override fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
            public override fun setTheme(@StyleRes theme: Int) = apply { this.theme = theme }
            public override fun setControlDismiss(controlDismiss: Boolean) = apply { this.controlDismiss = controlDismiss }
            public override fun setOnCancelListener(onCancelListener: (() -> Unit)?) = apply { this.onCancelListener = onCancelListener }

            fun setInputListener(textButton: String, onInputListener: ((BottomSheetDx, String) -> Unit)?) = apply {
                this.positiveTextButton = textButton
                this.inputListener = onInputListener
            }

            fun setNegativeButton(textButton: String, onNegativeClickListener: ((BottomSheetDx) -> Unit)?) = apply {
                this.negativeTextButton = textButton
                this.negativeListener = onNegativeClickListener
            }


            fun setText(texto: String) = apply { this.texto = texto }
            fun setInputType (inputType: Int) = apply { this.inputType = inputType }
            fun setTextHint (texto : String) = apply { this.textHint = texto }
            fun setEndIconClearText (value: Boolean) = apply { this.endIconClearText = value }
            fun setImeOptions (editorInfo: Int) = apply { this.imeOptions = editorInfo }

            fun setErrorMessage (message: String) = apply { this.errorMessage = message }
//            fun settingsInput( inputLayout: ((TextInputLayout)-> Unit)? = null) {  }

        }
        class PickerNumber: Builder() {

            internal var onPickerNumberListener: ((BottomSheetDx, Int) -> Unit)? = null
            internal var initialValue : Int? = null
            public override fun setIcon(@DrawableRes icon: Int) = apply { this.icon = icon }
            public override fun setTitle(title: String) = apply { this.title = title }
            public override fun setMessage(message: String) = apply { this.message = message }
            public override fun setCancelOnTouchOutSide(cancelOnTouchOutSide: Boolean) = apply { this.cancelOnTouchOutSide = cancelOnTouchOutSide }
            public override fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
            public override fun setTheme(@StyleRes theme: Int) = apply { this.theme = theme }
            public override fun setControlDismiss(controlDismiss: Boolean) = apply { this.controlDismiss = controlDismiss }
            public override fun setOnCancelListener(onCancelListener: (() -> Unit)?) = apply { this.onCancelListener = onCancelListener }

            fun setOnPickerNumberListener(textButton: String, onPickerNumberListener: ((BottomSheetDx, Int) -> Unit)?) = apply {
                this.positiveTextButton = textButton
                this.onPickerNumberListener = onPickerNumberListener
            }

            fun setNegativeButton(textButton: String, onNegativeClickListener: ((BottomSheetDx) -> Unit)?) = apply {
                this.negativeTextButton = textButton
                this.negativeListener = onNegativeClickListener
            }

            fun setInitialValue (value: Int) = apply { this.initialValue = value }
        }
        class Selector: Builder() {

            internal var dropdownItems: List<String>? = null
            internal var textHint: String? = null
            internal var dropdownListener : ((BottomSheetDx, Int?) -> Unit)? = null
            public override fun setIcon(@DrawableRes icon: Int) = apply { this.icon = icon }
            public override fun setTitle(title: String) = apply { this.title = title }
            public override fun setMessage(message: String) = apply { this.message = message }
            public override fun setCancelOnTouchOutSide(cancelOnTouchOutSide: Boolean) = apply { this.cancelOnTouchOutSide = cancelOnTouchOutSide }
            public override fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
            public override fun setTheme(@StyleRes theme: Int) = apply { this.theme = theme }
            public override fun setControlDismiss(controlDismiss: Boolean) = apply { this.controlDismiss = controlDismiss }
            public override fun setOnCancelListener(onCancelListener: (() -> Unit)?) = apply { this.onCancelListener = onCancelListener }

            fun setHint(textHint: String) = apply { this.textHint = textHint }
            fun setNegativeButton(textButton: String, onNegativeClickListener: ((BottomSheetDx) -> Unit)?) = apply {
                this.negativeTextButton = textButton
                this.negativeListener = onNegativeClickListener
            }

            fun setDropdownListener(textButton: String, onDropdownListener: ((BottomSheetDx, Int?) -> Unit)?) = apply {
                this.positiveTextButton = textButton
                this.dropdownListener = onDropdownListener
            }
            fun setDropdownItems (items: List<String>) = apply { this.dropdownItems = items }
        }

        class Custom : Builder() {

            internal var customLayout: Int? = null
            internal var viewStubCallBack :( (ViewStub) -> Unit)? = null
            public override fun setIcon(@DrawableRes icon: Int) = apply { this.icon = icon }
            public override fun setTitle(title: String) = apply { this.title = title }
            public override fun setMessage(message: String) = apply { this.message = message }
            public override fun setCancelOnTouchOutSide(cancelOnTouchOutSide: Boolean) = apply { this.cancelOnTouchOutSide = cancelOnTouchOutSide }
            public override fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
            public override fun setTheme(@StyleRes theme: Int) = apply { this.theme = theme }
            public override fun setControlDismiss(controlDismiss: Boolean) = apply { this.controlDismiss = controlDismiss }
            public override fun setOnCancelListener(onCancelListener: (() -> Unit)?) = apply { this.onCancelListener = onCancelListener }

            fun setPositiveButton(textButton: String, onPositiveClickListener: ((BottomSheetDx) -> Unit)?) = apply {
                this.positiveTextButton = textButton
                this.positiveListener = onPositiveClickListener
            }
            fun setNegativeButton(textButton: String, onNegativeClickListener: ((BottomSheetDx) -> Unit)?) = apply {
                this.negativeTextButton = textButton
                this.negativeListener = onNegativeClickListener
            }

            fun setCustomLayout(@LayoutRes customLayout: Int, viewStubCallBack: (ViewStub) -> Unit ) = apply {
                this.customLayout = customLayout
                this.viewStubCallBack = viewStubCallBack
            }
        }


        class Barcode: Builder() {

            internal var onBarcodeListener: ((BottomSheetDx, String) -> Unit)? = null
            internal var inputType : Int? = null
            internal var textHint : String? = null
            internal var texto: String? = null
            internal var endIconClearText : Boolean = true
            internal var imeOptions : Int? = null
            internal var errorMessage : String? = null
            public override fun setIcon(@DrawableRes icon: Int) = apply { this.icon = icon }
            public override fun setTitle(title: String) = apply { this.title = title }
            public override fun setMessage(message: String) = apply { this.message = message }
            public override fun setCancelOnTouchOutSide(cancelOnTouchOutSide: Boolean) = apply { this.cancelOnTouchOutSide = cancelOnTouchOutSide }
            public override fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
            public override fun setTheme(@StyleRes theme: Int) = apply { this.theme = theme }
            public override fun setControlDismiss(controlDismiss: Boolean) = apply { this.controlDismiss = controlDismiss }
            public override fun setOnCancelListener(onCancelListener: (() -> Unit)?) = apply { this.onCancelListener = onCancelListener }

            fun setPositiveButton(textButton: String, onBarcodeListener: ((BottomSheetDx, String) -> Unit)?) = apply {
                this.positiveTextButton = textButton
                this.onBarcodeListener = onBarcodeListener
            }

            fun setText(texto: String) = apply { this.texto = texto }
            fun setInputType (inputType: Int) = apply { this.inputType = inputType }
            fun setTextHint (texto : String) = apply { this.textHint = texto }
            fun setEndIconClearText (value: Boolean) = apply { this.endIconClearText = value }
            fun setImeOptions (editorInfo: Int) = apply { this.imeOptions = editorInfo }

            fun setErrorMessage (message: String) = apply { this.errorMessage = message }

        }

    }


}
