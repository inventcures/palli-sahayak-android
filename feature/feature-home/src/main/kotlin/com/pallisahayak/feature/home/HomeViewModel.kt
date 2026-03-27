package com.pallisahayak.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pallisahayak.core.data.database.dao.MedicationReminderDao
import com.pallisahayak.core.data.database.dao.PatientDao
import com.pallisahayak.core.data.database.dao.UserDao
import com.pallisahayak.core.data.database.entity.PatientEntity
import com.pallisahayak.core.data.network.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userDao: UserDao,
    private val patientDao: PatientDao,
    private val reminderDao: MedicationReminderDao,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {

    val currentUser = userDao.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val patients = patientDao.getAllPatients()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val pendingReminders = reminderDao.getAllActiveReminders()
        .map { reminders -> reminders.filter { it.callStatus == "scheduled" || it.callStatus == "pending" } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val isOnline = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
}
