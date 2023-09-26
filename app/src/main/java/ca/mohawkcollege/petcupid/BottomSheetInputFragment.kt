package ca.mohawkcollege.petcupid

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import ca.mohawkcollege.petcupid.chatmsg.ChatViewModel
import ca.mohawkcollege.petcupid.databinding.FragmentBottomSheetInputBinding
import ca.mohawkcollege.petcupid.tools.ContentUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetInputFragment : BottomSheetDialogFragment() {

    private val TAG = "====BottomSheetInputFragment===="
    private var _binding: FragmentBottomSheetInputBinding? = null
    private val binding get() = _binding!!
//    private lateinit var _viewModel: ChatViewModel
    private lateinit var viewModel: ChatViewModel
//    private val viewModel by lazy { _viewModel }

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

        viewModel = activity?.let { ViewModelProvider(it) }?.get(ChatViewModel::class.java) as ChatViewModel
        Log.d(TAG, "onViewCreated: viewModel: $viewModel")

        setupSendTextMessageHandler(viewModel)
        setupSendImageMessageHandler()
    }

    private fun setupSendTextMessageHandler(vm: ChatViewModel) {
        binding.msgSendBtn.setOnClickListener {
            val msg = binding.editTextTextMultiLine.text.toString()
            vm.sendMsg(msg)
            binding.editTextTextMultiLine.text.clear()
        }
    }

    private fun setupVoiceAndTextSwitcherHandler() {
        TODO()
    }

    private fun setupSendVoiceMessageHandler() {
        TODO()
    }

    private fun setupSendImageMessageHandler() {
        binding.galleryBtn.setOnClickListener {
            openGalleryActivityResultLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
            )
        }
    }

    private val openGalleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(2)
    ) { uris ->
        if (uris != null) {
            Log.d(TAG, "onActivityResult: uri: $uris")
            for (uri in uris) {
                val filePath = ContentUtils.getFilePathFromUri(requireContext(), uri)
                if (filePath != null) {
                    viewModel.uploadFile(filePath)
                } else {
                    Log.d(TAG, "onActivityResult: filePath is null")
                }
            }
        } else {
            Log.d(TAG, "onActivityResult: uri is null")
        }
    }

    private fun setupAppointmentsHandler() {
        TODO()
    }
}