package com.lu.magic.module.main.vm

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lu.magic.module.main.Contracts
import com.lu.magic.util.AppUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    companion object {
        const val ID_APP = "app"
        const val KEY_AGREE_AGREEMENT = "agree_agreement"
    }

    fun checkShowAgreement(): MutableLiveData<String> {
        val live = MutableLiveData<String>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val sp = AppUtil.getContext().getSharedPreferences(ID_APP, AppCompatActivity.MODE_PRIVATE)
                val agreeAgreement = sp.getBoolean(KEY_AGREE_AGREEMENT, false)
                if (agreeAgreement) {
                    return@withContext
                }
                AppUtil.getContext().assets.open(Contracts.ASSET_PATH_AGREEMENT).use {
                    live.postValue(String(it.readBytes()))
                }
            }
        }
        return live
    }

    fun saveHasAgreeAgreement() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val sp = AppUtil.getContext().getSharedPreferences(ID_APP, AppCompatActivity.MODE_PRIVATE)
                sp.edit().putBoolean(KEY_AGREE_AGREEMENT, true).apply()
            }
        }
    }

}