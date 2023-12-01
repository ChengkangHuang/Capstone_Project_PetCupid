package ca.mohawkcollege.petcupid

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import ca.mohawkcollege.petcupid.chatmsg.ChatViewModel
import ca.mohawkcollege.petcupid.databinding.FragmentBottomSheetInputBinding
import ca.mohawkcollege.petcupid.tools.AudioUtils
import ca.mohawkcollege.petcupid.tools.ContentUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.util.Calendar

class BottomSheetInputFragment : BottomSheetDialogFragment() {

    private val TAG = "====BottomSheetInputFragment===="
    private var _binding: FragmentBottomSheetInputBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChatViewModel
    private lateinit var audioUtils: AudioUtils
    private var audioOutputFile: File? = null
    private var isRecording = false

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

        audioUtils = AudioUtils()
        Log.d(TAG, "onViewCreated: audioUtils: $audioUtils")

        setupSendVoiceMessageHandler()
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
            if (msg.isEmpty()) {
                return@setOnClickListener
            }
            vm.sendTextMsg(msg)
            binding.editTextTextMultiLine.text.clear()
        }
    }

    /**
     * Setup the handler for sending voice message
     */
    private fun setupSendVoiceMessageHandler() {
        microphoneLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
    }

    /**
     * Register an activity result launcher for microphone permission request
     */
    @SuppressLint("ClickableViewAccessibility")
    private val microphoneLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "microphonePermissionRequestLauncher: Permission granted")
            binding.voiceRecordBtn.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startRecording()
                        Toast.makeText(requireContext(), "Recording started", Toast.LENGTH_SHORT).show()
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        view.performClick()
                        stopRecording()
                        Toast.makeText(requireContext(), "Recording stopped", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        } else {
            Log.d(TAG, "microphonePermissionRequestLauncher: Permission denied")
            binding.voiceRecordBtn.setOnClickListener {
                Snackbar.make(binding.root, "Please go to App Permissions, allow access Microphone", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Start recording and save the audio file to cache directory
     */
    private fun startRecording() {
        audioOutputFile = File.createTempFile("audio", ".3gp", requireContext().cacheDir)
        audioUtils.startRecording(audioOutputFile!!)
        isRecording = true
    }

    /**
     * Stop recording and upload the audio file to Firebase Storage
     */
    private fun stopRecording() {
        if (isRecording) {
            audioUtils.stopRecording()
            viewModel.uploadAudio(audioOutputFile!!)
            isRecording = false
        }
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
        datePickerLauncher.launch(
            arrayOf(
                android.Manifest.permission.READ_CALENDAR,
                android.Manifest.permission.WRITE_CALENDAR
            )
        )
    }

    /**
     * Register an activity result launcher for calendar permission request
     */
    private val datePickerLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted ->
        if (isGranted.containsValue(false)) {
            Log.d(TAG, "calendarPermissionRequestLauncher: Permission denied")
            binding.calendarBtn.setOnClickListener {
                Snackbar.make(binding.root, "Please go to App Permissions, allow access Calendar", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            Log.d(TAG, "calendarPermissionRequestLauncher: Permission granted")
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
}