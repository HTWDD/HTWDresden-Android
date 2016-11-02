package de.htwdd.htwdresden.interfaces;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Interface zum Speicher der im Wizard vorgenommen Änderungen
 */
public interface IWizardSaveSettings {
    View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState);

    void saveSettings(@NonNull final Bundle bundle);
}
