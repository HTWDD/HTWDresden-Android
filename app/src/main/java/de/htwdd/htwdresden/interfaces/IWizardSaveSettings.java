package de.htwdd.htwdresden.interfaces;

import android.support.annotation.NonNull;

import de.htwdd.htwdresden.types.dataBinding.WizardDataBindingObject;

/**
 * Interface zum Speicher der im Wizard vorgenommen Änderungen
 */
public interface IWizardSaveSettings {
    void setDataBindingObject(@NonNull final WizardDataBindingObject dataBindingObject);
}
