package es.icp.dxbottomsheet

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class DxViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState

    fun newUiState(uiState: UiState) = _uiState.update { uiState }


    sealed class UiState {
        object Initial : UiState()
        object OnClickPositiveButton : UiState()
        object OnClickNegativeButton : UiState()
        object OnInputClickListener : UiState()
        object Hide: UiState()
    }
}