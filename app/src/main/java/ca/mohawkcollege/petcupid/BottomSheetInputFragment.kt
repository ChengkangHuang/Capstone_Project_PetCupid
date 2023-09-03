package ca.mohawkcollege.petcupid

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import ca.mohawkcollege.petcupid.chatmsg.ChatViewModel
import ca.mohawkcollege.petcupid.databinding.FragmentBottomSheetInputBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetInputFragment : BottomSheetDialogFragment() {

    private val TAG = "====BottomSheetInputFragment===="
    private var _binding: FragmentBottomSheetInputBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBottomSheetInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Here")

        val vm = activity?.let { ViewModelProvider(it) }?.get(ChatViewModel::class.java)
        Log.d(TAG, "onViewCreated: vm: $vm")

        binding.msgSendBtn.setOnClickListener {
            val msg = binding.editTextTextMultiLine.text.toString()
            vm?.sendMsg(msg)
        }
    }
}