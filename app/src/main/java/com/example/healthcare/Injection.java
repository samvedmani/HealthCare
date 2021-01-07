package com.example.healthcare;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.healthcare.data.source.MedicineRepository;
import com.example.healthcare.data.source.local.MedicinesLocalDataSource;

/**
 * Created by gautam on 13/07/17.
 */

public class Injection {

    public static MedicineRepository provideMedicineRepository(@NonNull Context context) {
        return MedicineRepository.getInstance(MedicinesLocalDataSource.getInstance(context));
    }
}