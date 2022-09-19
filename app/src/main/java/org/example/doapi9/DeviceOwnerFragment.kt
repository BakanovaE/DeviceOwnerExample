package org.example.doapi9

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.icu.util.TimeZone
import android.os.Bundle
import android.os.UserManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.example.doapi9.DeviceOwnerReceiver.Companion.getComponentName
import org.example.doapi9.databinding.DeviceOwnerFragmentBinding

private const val SECURE = "SECURE"
private const val SYSTEM = "SYSTEM"
private const val GLOBAL = "GLOBAL"

class DeviceOwnerFragment : Fragment(R.layout.device_owner_fragment) {

    private lateinit var binding: DeviceOwnerFragmentBinding
    private lateinit var mDevicePolicyManager: DevicePolicyManager
    private lateinit var mAdminComponentName: ComponentName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DeviceOwnerFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDevicePolicyManager = requireContext().getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mAdminComponentName = getComponentName(requireContext())
        setUpScreenOffTimeoutButton()
        setUpLocationModeButton()
        setUpADBButton()
        setUpInputMethodButton()
        setUpAutoDateTimeButton()
        setUpAutoTimeZoneButton()
        setUpDateTimeButton()
        setUpTimeZoneButton()
    }

    private fun setUpAutoDateTimeButton() {
        binding.buttonAutoDateTime.setOnClickListener {
            val oldAutoTime = getGlobalSettingInt(Settings.Global.AUTO_TIME)
            when (oldAutoTime) {
                1 -> setSettings(GLOBAL, Settings.Global.AUTO_TIME, "0")
                0 -> setSettings(GLOBAL, Settings.Global.AUTO_TIME, "1")
            }
            showToast("auto time was $oldAutoTime, changed to ${getGlobalSettingInt(Settings.Global.AUTO_TIME)}")
        }
    }

    private fun setUpDateTimeButton() {
        binding.buttonDateTime.setOnClickListener {
            val success = mDevicePolicyManager.setTime(mAdminComponentName, System.currentTimeMillis() - 2000000000L)
            showToast("time was changed = $success, newTime is ${System.currentTimeMillis()}")
        }
    }

    private fun setUpAutoTimeZoneButton() {
        binding.buttonAutoTimeZone.setOnClickListener {
            val oldAutoTimeZone = getGlobalSettingInt(Settings.Global.AUTO_TIME_ZONE)
            when (oldAutoTimeZone) {
                1 -> setSettings(GLOBAL, Settings.Global.AUTO_TIME_ZONE, "0")
                0 -> setSettings(GLOBAL, Settings.Global.AUTO_TIME_ZONE, "1")
            }
            showToast("auto time zone was $oldAutoTimeZone, changed to ${getGlobalSettingInt(Settings.Global.AUTO_TIME_ZONE)}")
        }
    }

    private fun setUpTimeZoneButton() {
        binding.buttonTimeZone.setOnClickListener {
            val oldTimeZone = TimeZone.getDefault()
            when (oldTimeZone.id) {
                "GMT" -> mDevicePolicyManager.setTimeZone(mAdminComponentName, "Australia/Sydney")
                else -> mDevicePolicyManager.setTimeZone(mAdminComponentName, "America/Chicago")
            }
            showToast("old time zone was ${oldTimeZone.displayName}, changed to ${TimeZone.getDefault().id}")
        }
    }

    private fun setUpADBButton() {
        binding.buttonAdb.setOnClickListener {
            when (getGlobalSettingInt(Settings.Global.ADB_ENABLED)) {
                1 -> setSettings(GLOBAL, Settings.Global.ADB_ENABLED, "0")
                0 -> setSettings(GLOBAL, Settings.Global.ADB_ENABLED, "1")
            }
            showToast("adb is ${getGlobalSettingInt(Settings.Global.ADB_ENABLED)}")
        }
    }

    private fun setUpScreenOffTimeoutButton() {
        binding.buttonScreenOffTimeout.setOnClickListener {
            val oldTimeout = getSystemSettingString(Settings.System.SCREEN_OFF_TIMEOUT)
            if (oldTimeout.toInt() != 0) {
                setSettings(SYSTEM, Settings.System.SCREEN_OFF_TIMEOUT, "0")
            } else {
                setSettings(SYSTEM, Settings.System.SCREEN_OFF_TIMEOUT, (oldTimeout.toInt() + 100).toString())
            }
            val newTimeout = getSystemSettingString(Settings.System.SCREEN_OFF_TIMEOUT)
            showToast("old timeout = $oldTimeout, new timeout = $newTimeout")
        }
    }

    // Starting from Android R, apps should no longer call this method with the setting
    // Settings.Secure.LOCATION_MODE, which is deprecated.
    // Instead, device owners should call setLocationEnabled(android.content.ComponentName, boolean).
    private fun setUpLocationModeButton() {
        binding.buttonLocationMode.setOnClickListener {
            val locationMode = getSecureSettingsInt(Settings.Secure.LOCATION_MODE)
            when (locationMode) {
                0 -> setSettings(SECURE, Settings.Secure.LOCATION_MODE, "1")
                1, 2, 3 -> setSettings(SECURE, Settings.Secure.LOCATION_MODE, "0")
            }
            val newLocationMode = getSecureSettingsInt(Settings.Secure.LOCATION_MODE)
            showToast("Location mode was changed from $locationMode to $newLocationMode")
        }
    }

    private fun setUpInputMethodButton() {
        binding.buttonInputMethod.setOnClickListener {
            val oldIM = getSecureSettingsString(Settings.Secure.DEFAULT_INPUT_METHOD)
            setSettings(SECURE, Settings.Secure.DEFAULT_INPUT_METHOD, "NewInputMethod")
            val newIM = getSecureSettingsString(Settings.Secure.DEFAULT_INPUT_METHOD)
            showToast("old im = $oldIM, new im = $newIM")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun getGlobalSettingInt(setting: String): Int {
        return Settings.Global.getInt(requireContext().contentResolver, setting)
    }

    private fun getSystemSettingString(setting: String): String {
        return Settings.System.getString(requireContext().contentResolver, setting)
    }

    private fun getSecureSettingsString(setting: String): String {
        return Settings.Secure.getString(requireContext().contentResolver, setting)
    }

    private fun getSecureSettingsInt(setting: String): Int {
        return Settings.Secure.getInt(requireContext().contentResolver, setting)
    }

    private fun setSettings(type: String, setting: String, value: String) {
        when (type) {
            SECURE -> mDevicePolicyManager.setSecureSetting(mAdminComponentName, setting, value)
            GLOBAL -> mDevicePolicyManager.setGlobalSetting(mAdminComponentName, setting, value)
            SYSTEM -> mDevicePolicyManager.setSystemSetting(mAdminComponentName, setting, value)
        }
    }
}
