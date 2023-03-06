package es.icp.dxbottomsheetpruebas

import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import es.icp.dxbottomsheet.BottomSheetDx
import es.icp.dxbottomsheet.showInfoBottomSheet
import es.icp.dxbottomsheet.showLottieBottomSheet
import es.icp.dxbottomsheetpruebas.databinding.FragmentFirstBinding
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
            childFragmentManager.showInfoBottomSheet(
                icon = es.icp.dxbottomsheet.R.drawable.ic_delete,
                title = "Título",
                message = "Mensaje de prueba del dialogo de información",
            )
        }

        binding.btnLottieDialogo.setOnClickListener {
            childFragmentManager.showLottieBottomSheet(
                icon =  es.icp.dxbottomsheet.R.drawable.ic_delete,
                title = "Título",
                lottieFile =  es.icp.dxbottomsheet.R.raw.no_data_lecturas_anim,
                lottieLoop = true
            )
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
                        it.setInitialUiState()
                    }
                }
                .setNegativeButton("Salir sin guardar") {
                    Toast.makeText(context, "Boton pulsado", Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO){  delay(4000) }
                        if (fail) {
                            Toast.makeText(context, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
                            it.setInitialUiState()
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
                .setMessage("Mensaje de prueba del dialogo de entrada")
                .setPositiveButton("Aceptar") { dx, text ->
                    Log.w("DIALOGO", "Texto introducido: $text")
                }
                .setTextHint("Introduce un texto")
                .setInputType( InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .setImeOptions(EditorInfo.IME_ACTION_NONE)
                .setEndIconClearText(false)
                .setOnCancelListener { dx ->
                    Log.w("DIALOGO", "Dialogo cancelado")
                }
                .buildAndShow(childFragmentManager)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}