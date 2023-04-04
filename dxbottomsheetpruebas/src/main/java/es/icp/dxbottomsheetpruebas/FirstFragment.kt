package es.icp.dxbottomsheetpruebas

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.italic
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import es.icp.dxbottomsheet.BottomSheetDx
import es.icp.dxbottomsheetpruebas.databinding.FragmentFirstBinding
import es.icp.dxbottomsheetpruebas.databinding.PruebaDxBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private var fail = true


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnInfoDialogo.setOnClickListener {

            val s = buildSpannedString {
                append("Mensaje de prueba del dialogo de información")
                bold { append("ESTO ESTA EN NEGRETA") }
                italic { append("esto en cursiva") }

            }


            BottomSheetDx.Builder.Info()
                .setIcon(es.icp.dxbottomsheet.R.drawable.ic_delete)
                .setTitle("Título")
                .setMessage(s)
                .buildAndShow(parentFragmentManager)

        }

        binding.btnLottieDialogo.setOnClickListener {
            val s = "Mensaje de prueba del dialogo de información"
            BottomSheetDx.Builder.LottieOrImage()
                .setIcon(es.icp.dxbottomsheet.R.drawable.ic_delete)
                .setTitle("Título")
                .setMessage("Mensaje de prueba del dialogo de información")
                .setLottie(es.icp.dxbottomsheet.R.raw.no_data_lecturas_anim)
                .setPositiveButton("Aceptar") {
                    Toast.makeText(requireContext(), "Boton pulsado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancelar") {
                    Toast.makeText(requireContext(), "Boton pulsado", Toast.LENGTH_SHORT).show()
                }
                .buildAndShow(parentFragmentManager)

        }

        binding.btnActionDialogo.setOnClickListener {
            BottomSheetDx.Builder.Action()
                .setIcon(es.icp.dxbottomsheet.R.drawable.ic_wifi)
                .setTitle("Dialogo de accion")
                .setMessage("Mensaje de prueba del dialogo de acción")
                .setControlDismiss(true)
//                .setTheme(R.style.customThemeDx)
                .setPositiveButton("Salir y guardar") {
                    Toast.makeText(requireContext(), "Boton pulsado", Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        delay(4000)
                        Toast.makeText(requireContext(), "Despues del delay", Toast.LENGTH_SHORT)
                            .show()
                        it.resetUiState()
                    }
                }
                .setNegativeButton("Salir sin guardar") {
                    Toast.makeText(context, "Boton pulsado", Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO){  delay(4000) }
                        if (fail) {
                            Toast.makeText(context, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
                            it.resetUiState()
                        }
                        else {
                            Toast.makeText(context, "Todo ha ido perita", Toast.LENGTH_SHORT).show()
                            it.dissmis()
                        }
                        fail = !fail
                    }
                }
                .buildAndShow(childFragmentManager)
        }

        binding.btnInputDialogo.setOnClickListener {
            BottomSheetDx.Builder.Input()
                .setIcon(es.icp.dxbottomsheet.R.drawable.ic_wifi)
                .setTitle("Dialogo de entrada")
//                .setMessage("Mensaje de prueba del dialogo de entrada")
//                .setText("Texto de prueba bitch")
                .setControlDismiss(true)
                .setEndIconClearText(true)
                .setInputListener("Aceptar") { dx, text ->
                    Log.w("DIALOGO", "Texto introducido: $text")
                    lifecycleScope.launch {
                        delay(4000)
                        dx.setInputError("CODIGO NO VALIDO")
                        delay(2000)
                        dx.resetUiState()
                    }
                }
                .setTextHint("Introduce un texto")
                .setInputType( InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .setImeOptions(EditorInfo.IME_ACTION_NONE)
//                .setControlDismiss(true)
                .setOnCancelListener {
                    Log.w("DIALOGO", "Dialogo cancelado")
                }
                .setErrorMessage("Este campo no puede estar vacio")
                .buildAndShow(childFragmentManager)
        }

        binding.btnNumberPicker.setOnClickListener {
            BottomSheetDx.Builder.PickerNumber()
                .setIcon(es.icp.dxbottomsheet.R.drawable.ic_wifi)
                .setTitle("Dialogo de entrada")
                .setMessage("Mensaje de prueba del dialogo de entrada \nMensaje de prueba del dialogo de entrada")
                .setOnPickerNumberListener("Aceptar") { dx, int ->
                    Log.w("DIALOGO", "Texto introducido: $int")
                }
                .setInitialValue(10)
                .setNegativeButton("Cancelar"){ dx ->
                    Log.w("DIALOGO", "Dialogo cancelado")
                }
                .setControlDismiss(false)
                .buildAndShow(childFragmentManager)
        }

        binding.btnSelector.setOnClickListener {
            val items = arrayListOf("Opcion 1", "Opcion 2", "Opcion 3")
            BottomSheetDx.Builder.Selector()
                .setIcon(es.icp.dxbottomsheet.R.drawable.ic_wifi)
                .setTitle("Dialogo de entrada")
                .setMessage("Mensaje de prueba del dialogo de entrada")
                .setHint("Selecciona una opción")
                .setDropdownItems(items)
                .setDropdownListener("Aceptar") { dx, text ->
                    Log.w("DIALOGO", "Posicion Escogida: $text")
                    text.toString().toIntOrNull()?.let {
                        val seleccion = items[it]
                        Log.w("DIALOGO", "Opcion Escogida: $seleccion")
                    }
                }
                .setControlDismiss(false)
                .buildAndShow(childFragmentManager)
        }

        binding.btnCustom.setOnClickListener {
            var bindingStub: PruebaDxBinding? = null


            BottomSheetDx.Builder.Custom()
                .setIcon(es.icp.dxbottomsheet.R.drawable.ic_wifi)
                .setTitle("Dialogo de entrada")
                .setMessage("Mensaje de prueba del dialogo de entrada")
                .setCustomLayout(R.layout.prueba_dx) {viewStub ->
                    viewStub.setOnInflateListener { stub, inflated ->
                        bindingStub = PruebaDxBinding.bind(inflated)
                        bindingStub?.inputPrueba?.hint = "FEDE"
                        bindingStub?.txtPrueba?.setText("Hola")
                        bindingStub?.txtPrueba?.doAfterTextChanged {
                            Log.w("LISTENER VIEWSTUB", it.toString())
                        }
                    }

                    viewStub.inflate()

                }
                .setPositiveButton("Aceptar") {
                    Log.w("DIALOGO CUSTOM", "Texto introducido: ${bindingStub?.txtPrueba?.text}")
                }
                .setControlDismiss(false)
                .buildAndShow(childFragmentManager)


        }

        binding.btnBarcode.setOnClickListener {
            BottomSheetDx.Builder.Barcode()
                .setIcon(es.icp.dxbottomsheet.R.drawable.ic_wifi)
                .setTitle("Dialogo de entrada")
                .setMessage("Mensaje de prueba del dialogo de entrada")
                .setTextHint("Codigo de barras")
                .setEndIconClearText(true)
                .setErrorMessage("Este campo no puede estar vacio")
                .setImeOptions(EditorInfo.IME_ACTION_DONE)
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .setControlDismiss(true)
                .setPositiveButton("Aceptar") { dx, text ->
                    Log.w("DIALOGO", "Texto introducido: $text")
                    lifecycleScope.launch {
                        delay(4000)
                        dx.setInputError("CODIGO NO VALIDO")
                        delay(2000)
                        dx.resetUiState()
                    }
                }
                .buildAndShow(parentFragmentManager)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}