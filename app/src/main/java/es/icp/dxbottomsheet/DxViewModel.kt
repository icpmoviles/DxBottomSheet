package es.icp.dxbottomsheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class DxViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState

    fun newUiState(uiState: UiState) = _uiState.update { uiState }

    private val _textoInput = MutableLiveData("")
    val textoInput: LiveData<String> get() = _textoInput
    fun setTextoInput(texto: String) = _textoInput.postValue(texto)

    private val _numPicker = MutableLiveData(0)
    val numPicker: LiveData<Int> get() = _numPicker
    fun plusNumPicker() = _numPicker.postValue(_numPicker.value?.plus(1))
    fun minusNumPicker() { if (_numPicker.value != 0) _numPicker.postValue(_numPicker.value?.minus(1)) }

    private val _dropSelecction = MutableLiveData<Int?>()
    val dropSelecction: LiveData<Int?> get() = _dropSelecction
    fun setDropSelecction(position: Int?) = _dropSelecction.postValue(position)

    private val _textoErrorInput = MutableLiveData("")
    val textoErrorInput: LiveData<String> get() = _textoErrorInput
    fun setTextoErrorInput(texto: String) = _textoErrorInput.postValue(texto)

    private val _valorScanner = MutableLiveData<String?>(null)
    val valorScanner: LiveData<String?> get() = _valorScanner
    fun setValorScanner(valor: String) = _valorScanner.postValue(valor)
    sealed class UiState {
        object Initial : UiState()

        object Reset : UiState()
        object OnClickPositiveButton : UiState()
        object OnClickNegativeButton : UiState()
        object OnInputClickListener : UiState()
        object OnNumPickerClickListener : UiState()
        object OnDropDownClickListener : UiState()
        object OnScannerClickListener : UiState()
        object Loading : UiState()
        object Hide: UiState()
    }
}