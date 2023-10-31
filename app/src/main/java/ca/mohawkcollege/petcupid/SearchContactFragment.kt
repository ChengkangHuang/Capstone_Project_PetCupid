package ca.mohawkcollege.petcupid

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.mohawkcollege.petcupid.databinding.FragmentSearchContactBinding
import ca.mohawkcollege.petcupid.searchcontact.ContactViewModel
import ca.mohawkcollege.petcupid.searchcontact.DataResult
import ca.mohawkcollege.petcupid.searchcontact.SearchContactResultAdapter

class SearchContactFragment : Fragment() {

    private val TAG = "====SearchContactFragment===="
    private var _binding: FragmentSearchContactBinding? = null
    private val binding get() = _binding!!
    private lateinit var contactViewModel: ContactViewModel
    private var isEmail: Boolean = false
    private lateinit var adapter: SearchContactResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactViewModel = ViewModelProvider(this)[ContactViewModel::class.java]

        setupTextInputHandler()
        setupSearchBtnHandler()
        setupSearchContactResultAdapter()
        setupSearchDataResultObserver(adapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * phoneNumberValidation
     * @param phoneNumber String phone number
     * @return Boolean
     */
    private fun phoneNumberValidation(phoneNumber: String): Boolean {
        return phoneNumber.matches(Regex("^[0-9]{10}$"))
    }

    /**
     * emailValidation
     * @param email String email address
     * @return Boolean
     */
    private fun emailValidation(email: String): Boolean {
        return email.matches(Regex("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"))
    }

    /**
     * searchContactFilter
     * @param inputText String input text
     */
    private fun searchContactFilter(inputText: String) {
        if (isEmail) {
            contactViewModel.getDataBySearchEmail(inputText)
        } else {
            contactViewModel.getDataBySearchPhoneNumber(inputText)
        }
    }

    /**
     * Setup text input handler
     * When user input text, check if it is email or phone number
     */
    private fun setupTextInputHandler() {
        binding.searchTextInputEditText.doOnTextChanged { text, _, _, _ ->
            if (text.toString().isNotEmpty()) {
                if (emailValidation(text.toString())) {
                    isEmail = true
                } else if (phoneNumberValidation(text.toString())) {
                    isEmail = false
                }
            }
        }
    }

    /**
     * Setup search button handler
     * When user click search button, search contact by email or phone number
     */
    private fun setupSearchBtnHandler() {
        binding.searchBtn.setOnClickListener {
            val inputText: String = binding.searchTextInputEditText.text.toString()
            searchContactFilter(inputText)
        }
    }

    /**
     * Setup search contact result adapter
     */
    private fun setupSearchContactResultAdapter() {
        adapter = context?.let {
            SearchContactResultAdapter(it, mutableListOf())
        }!!
        binding.searchResultListView.adapter = adapter
    }

    /**
     * Setup search data result observer
     * When data result is changed, update adapter
     * @param adapter SearchContactResultAdapter
     */
    private fun setupSearchDataResultObserver(adapter: SearchContactResultAdapter) {
        contactViewModel.dataResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is DataResult.Success -> {
                    adapter.clear()
                    for (item in result.searchContactResult!!) {
                        Log.d(TAG, "searchContactItem: $item")
                        adapter.add(item)
                    }
                    adapter.notifyDataSetChanged()
                }
                is DataResult.Error -> {
                    Log.d(TAG, "searchContact: ${result.exception.message}")
                }
                else -> {}
            }
        }
    }
}