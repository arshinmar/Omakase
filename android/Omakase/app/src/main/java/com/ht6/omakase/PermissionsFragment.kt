package com.ht6.omakase

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

private const val PERMISSIONS_REQUEST_CODE = 10
private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

/**
 * This [Fragment] requests permissions and, once granted, it will navigate to the next fragment
 */
class PermissionsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasPermissions(requireContext())) {
            // If permissions have already been granted, proceed
            //Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
            Navigation.findNavController(requireActivity(), R.id.fragment_nav).navigate(
                //actionPermissionsToSelector
                //action_permissions_to_selector
                //action_permissions_fragment_to_blankFragment2
                //PermissionsFragmentDirections.actionPermissionsFragmentToBlankFragment2())
                PermissionsFragmentDirections.actionPermissionsFragmentToCameraFragment())
        } else {
            // Request camera-related permissions
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Takes the user to the success fragment when permission is granted
                //Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                Navigation.findNavController(requireActivity(), R.id.fragment_nav).navigate(
//                    PermissionsFragmentDirections.actionPermissionsFragmentToBlankFragment2())
                    PermissionsFragmentDirections.actionPermissionsFragmentToCameraFragment())
            } else {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {

        /** Convenience method used to check if all permissions required by this app are granted */
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}