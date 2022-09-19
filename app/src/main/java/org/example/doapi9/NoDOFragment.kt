package org.example.doapi9

import android.app.admin.DevicePolicyManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import org.example.doapi9.databinding.NoDoFragmentBinding

class NoDOFragment : Fragment() {

    private lateinit var binding: NoDoFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = NoDoFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpButtonDO()
    }

    private fun setUpButtonDO() {
        binding.openDoFragment.setOnClickListener {
            val dpm = getSystemService(requireContext(), DevicePolicyManager::class.java) as DevicePolicyManager
            if (dpm.isDeviceOwnerApp(requireActivity().applicationContext.packageName)) {
                showFragment(DeviceOwnerFragment())
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
