package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.BuildConfig
import de.htwdd.htwdresden.ui.models.SettingsModel

class SettingsViewModel: ViewModel() {

    private var onImprintClosure: () -> Unit = {}
    private var onDeleteAllDataClosure: () -> Unit = {}
    private var onStudyGroupClosure: () -> Unit = {}
    private var onLoginClickClosure: () -> Unit = {}

    val model: SettingsModel by lazy { SettingsModel() }

    init {
        model.apply {
            version.set("${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            onImprintClick { onImprintClosure() }
            onDeleteAllDataClick { onDeleteAllDataClosure() }
            onStudyGroupClick { onStudyGroupClosure() }
            onLoginClick { onLoginClickClosure() }
        }
    }

    fun onImprintClick(callback: () -> Unit) {
        onImprintClosure = callback
    }

    fun onDeleteAllDataClick(callback: () -> Unit) {
        onDeleteAllDataClosure = callback
    }

    fun onStudyGroupClick(callback: () -> Unit) {
        onStudyGroupClosure = callback
    }

    fun onLoginClick(callback: () -> Unit) {
        onLoginClickClosure = callback
    }
}