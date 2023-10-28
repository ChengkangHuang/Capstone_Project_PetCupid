package ca.mohawkcollege.petcupid

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import java.util.Calendar

class BottomSheetInputFragment : BottomSheetDialogFragment() {

    private val TAG = "====BottomSheetInputFragment===="
    private var _binding: FragmentBottomSheetInputBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChatViewModel

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
        setupAppointmentsHandler()
    }

    /**
     * Setup the handler for sending text message
     */
    private fun setupSendTextMessageHandler(vm: ChatViewModel) {
        binding.msgSendBtn.setOnClickListener {
            val msg = binding.editTextTextMultiLine.text.toString()
            vm.sendTextMsg(msg)
            binding.editTextTextMultiLine.text.clear()
        }
    }

    private fun setupVoiceAndTextSwitcherHandler() {
        TODO()
    }

    private fun setupSendVoiceMessageHandler() {
        TODO()
    }

    /**
     * Setup the handler for sending media message
     * Include image and video
     */
    private fun setupSendImageMessageHandler() {
        binding.galleryBtn.setOnClickListener {
            openGalleryActivityResultLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
            )
        }
    }

    /**
     * Register an activity result launcher for gallery activity
     * Upload the selected media to Firebase Storage
     */
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

    /**
     * Setup the handler for setting appointments
     */
    private fun setupAppointmentsHandler() {
        binding.calendarBtn.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                Log.d(TAG, "DatePickerDialog: year: $year, month: $month, dayOfMonth: $dayOfMonth")
                TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                    Log.d(TAG, "TimePickerDialog: hourOfDay: $hourOfDay, minute: $minute")
                    viewModel.setAppointment(
                        year.toLong(),
                        month.toLong(),
                        dayOfMonth.toLong(),
                        hourOfDay.toLong(),
                        minute.toLong())
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
    }
}