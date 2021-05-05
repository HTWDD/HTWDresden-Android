package de.htwdd.htwdresden.ui.viewmodels.fragments

import androidx.lifecycle.ViewModel
import de.htwdd.htwdresden.ui.models.SettingsModel
import de.htwdd.htwdresden.utils.holders.ResourceHolder

class SettingsViewModel: ViewModel() {

    private var onImprintClosure: () -> Unit = {}
    private var onDataProtectionClosure: () -> Unit = {}
    private var onDeleteAllDataClosure: () -> Unit = {}
    private var onResetEventsClosure: () -> Unit = {}
    private var onStudyGroupClosure: () -> Unit = {}
    private var onLoginClickClosure: () -> Unit = {}

    val model: SettingsModel by lazy { SettingsModel() }
    private val rh by lazy { ResourceHolder.instance }

    init {
        model.apply {
            version.set("v ${rh.versionName} (${rh.versionCode})")
            onImprintClick { onImprintClosure() }
            onDataProtectionClick { onDataProtectionClosure() }
            onDeleteAllDataClick { onDeleteAllDataClosure() }
            onResetEventsClick { onResetEventsClosure() }
            onStudyGroupClick { onStudyGroupClosure() }
            onLoginClick { onLoginClickClosure() }
        }
    }

    fun onImprintClick(callback: () -> Unit) {
        onImprintClosure = callback
    }
    fun onDataProtectionClick(callback: () -> Unit) {
        onDataProtectionClosure = callback
    }

    fun onDeleteAllDataClick(callback: () -> Unit) {
        onDeleteAllDataClosure = callback
    }

    fun onResetEventsClick(callback: () -> Unit) {
        onResetEventsClosure = callback
    }

    fun onStudyGroupClick(callback: () -> Unit) {
        onStudyGroupClosure = callback
    }

    fun onLoginClick(callback: () -> Unit) {
        onLoginClickClosure = callback
    }
}