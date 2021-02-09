package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView

class PokemonFiltersViewModel @ViewModelInject constructor(
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val isFiltersLayoutExpanded: MutableLiveData<Boolean> = getFiltersLayoutExpanded()

    val onCloseFiltersLayout: SingleLiveEvent<Void> = SingleLiveEvent()
    val onAddScrollAwareFilerFab: SingleLiveEvent<RecyclerView> = SingleLiveEvent()

    fun setFiltersLayoutExpanded(isFiltersLayoutExpanded: Boolean) {
        this.isFiltersLayoutExpanded.value = isFiltersLayoutExpanded
        savedStateHandle.set(isFiltersLayoutExpandedKey, isFiltersLayoutExpanded)
    }

    private fun getFiltersLayoutExpanded(): MutableLiveData<Boolean> {
        val isExpanded = savedStateHandle.get<Boolean>(isFiltersLayoutExpandedKey) ?: false
        return MutableLiveData(isExpanded)
    }

    fun addScrollAwareFilerFab(view: RecyclerView) {
        this.onAddScrollAwareFilerFab.value = view
    }

    fun closeFiltersLayout() {
        onCloseFiltersLayout.call()
    }

    companion object {
        private const val isFiltersLayoutExpandedKey: String = "filtersExpanded"
    }
}