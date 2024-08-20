package com.example.bitmaploadingandcaching.fragments

import android.animation.AnimatorSet
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setPadding
import com.example.bitmaploadingandcaching.R
import com.example.bitmaploadingandcaching.dataclass.Contact
import com.example.bitmaploadingandcaching.viewmodel.CacheData
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random


class AddContactFragment : Fragment() {

    private lateinit var addPictureImg:ImageView
    private lateinit var addPictureText:MaterialButton
    private lateinit var firstName:TextInputEditText
    private lateinit var lastName:TextInputEditText
    private lateinit var companyName:TextInputEditText
    private lateinit var phoneNumber:TextInputEditText
    private lateinit var email:TextInputEditText
    private lateinit var saveBtn:MaterialButton
    private lateinit var addNoteToolbar: MaterialToolbar
    private lateinit var birthday:TextInputEditText
    private lateinit var clearBirthdayDate:ImageButton
    private lateinit var clearEmail:ImageButton
    private lateinit var clearPhone:ImageButton
    private lateinit var phoneLabel:AutoCompleteTextView
    private lateinit var emailLabel:AutoCompleteTextView
    private lateinit var dateLabel:AutoCompleteTextView
    private lateinit var emailContainer:LinearLayout
    private lateinit var phoneContainer:LinearLayout
    private lateinit var dateContainer:LinearLayout
    private var oneTimeGeneratePhone = 0
    private var oneTimeGenerateEmail = 0
    private var oneTimeGenerateDate = 0
    private var dataImage:Uri = Uri.parse("")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_add_note, container, false)
        addPictureText = view.findViewById(R.id.addPictureButton)
        addPictureImg = view.findViewById(R.id.addPictureImage)
        firstName = view.findViewById(R.id.firstName)
        lastName = view.findViewById(R.id.lastName)
        companyName = view.findViewById(R.id.companyName)
        phoneNumber = view.findViewById(R.id.phoneNumber)
        email = view.findViewById(R.id.email)
        saveBtn = view.findViewById(R.id.addContactSaveButton)
        addNoteToolbar = view.findViewById(R.id.addNoteToolbar)
        birthday = view.findViewById(R.id.birthdayDate)
        clearBirthdayDate = view.findViewById(R.id.clearBirthdayDate)
        clearEmail =view.findViewById(R.id.clearEmail)
        clearPhone = view.findViewById(R.id.clearPhone)
        phoneLabel = view.findViewById(R.id.phoneNumberLabel)
        emailLabel = view.findViewById(R.id.emailLabel)
        dateLabel = view.findViewById(R.id.significantDateLabel)
        phoneContainer = view.findViewById(R.id.linearLayoutPhoneContainer)
        emailContainer = view.findViewById(R.id.linearLayoutEmailContainer)
        dateContainer = view.findViewById(R.id.linearLayoutDateContainer)
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select the Birthday Date")
            .setTextInputFormat(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .build()
        birthday.setOnClickListener {
            datePicker.show(parentFragmentManager,"Date Picker")
        }

        clearPhone.setOnClickListener {
            phoneLabel.text = null
            phoneLabel.clearFocus()
            phoneNumber.setText("")
            clearPhone.visibility =View.INVISIBLE
        }

        clearEmail.setOnClickListener {
            emailLabel.text = null
            emailLabel.clearFocus()
            email.setText("")
            clearEmail.visibility =View.INVISIBLE
        }

        email.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if(s?.isNotEmpty()!=true){
                    clearEmail.visibility = View.VISIBLE
                    if(oneTimeGenerateEmail==0){
                        addEmailLayout()
                        oneTimeGenerateEmail++
                    }
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.isNotEmpty()!=true){
                    clearEmail.visibility = View.VISIBLE
                    if(oneTimeGenerateEmail==0){
                        addEmailLayout()
                        oneTimeGenerateEmail++
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if(s?.isNotEmpty()!=true){
                    clearEmail.visibility = View.VISIBLE
                    if(oneTimeGenerateEmail==0){
                        addEmailLayout()
                        oneTimeGenerateEmail++
                    }
                }
            }
        })

        phoneNumber.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if(s?.isNotEmpty()!=true){
                    clearPhone.visibility = View.VISIBLE
                    if(oneTimeGeneratePhone==0){
                        addPhoneLayout()
                        oneTimeGeneratePhone++
                    }
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.isNotEmpty()!=true){
                    clearPhone.visibility = View.VISIBLE
                    if(oneTimeGeneratePhone==0){
                        addPhoneLayout()
                        oneTimeGeneratePhone++
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if(s?.isNotEmpty()!=true){
                    clearPhone.visibility = View.VISIBLE
                    if(oneTimeGeneratePhone==0){
                        addPhoneLayout()
                        oneTimeGeneratePhone++
                    }
                }

            }
        })

        datePicker.addOnPositiveButtonClickListener {
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = date.format(it)
            println("On Add Positive Button: ${it}")
            println("On Add Positive Button: ${formattedDate}")
            clearBirthdayDate.visibility = View.VISIBLE
            birthday.setText(formattedDate)
            addDateLayout()
        }
        clearBirthdayDate.setOnClickListener {
            dateLabel.text = null
            dateLabel.clearFocus()
            birthday.setText("")
            clearBirthdayDate.visibility = View.INVISIBLE
        }
        addNoteToolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        var left = 0
        var right = CacheData.list.size
        saveBtn.setOnClickListener {
            if(firstName.text.toString().isNotEmpty() || lastName.text.toString().isNotEmpty() ||
                companyName.text.toString().isNotEmpty() || phoneNumber.text.toString().isNotEmpty() || email.text.toString().isNotEmpty()){
                var name = firstName.text.toString()
                if(firstName.text.toString().isEmpty()){
                    name += lastName.text.toString()
                }
                else{
                    name += " "+lastName.text.toString()
                }
                if(name.isEmpty()){
                    if(email.text.toString().isNotEmpty()){
                        name = email.text.toString()
                    }
                    else if(phoneNumber.text.toString().isNotEmpty()){
                        name = phoneNumber.text.toString()
                    }
                    else if(companyName.text.toString().isNotEmpty()){
                        name = companyName.text.toString()
                    }
                }
                if(name.isNotEmpty()){
                    while (left<right){
                        val mid = (left+right) / 2
                        if(CacheData.list[mid].name.lowercase()<name.lowercase()){
                            left = mid+1
                        }
                        else{
                            right = mid
                        }
                    }
                    CacheData.addList(Contact(name,
                        dataImage.toString(),
                        HomeFragment.COLORS_LIST[Random.nextInt(0,10)],phoneNumber.text.toString(), isHighlighted = false,isUri = true,birthday.text.toString()),left)
                    parentFragmentManager.popBackStack()
                    Toast.makeText(requireContext(),"Contact Saved Successfully",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(requireContext(),"Add Info To Save as a Contact",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(requireContext(),"Add Info To Save as a Contact",Toast.LENGTH_SHORT).show()
            }
        }

        var launchImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                var image = result.data?.data
                println("Image: $image")
                dataImage = image?:Uri.parse("")
                addPictureImg.setPadding(0)
                addPictureImg.setImageURI(image)
            }
        }

        addPictureText.setOnClickListener{
            val i = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            launchImage.launch(i)
        }
        addPictureImg.setOnClickListener{
            val i = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            launchImage.launch(i)
        }
        return view
    }

    private fun addPhoneLayout() {
        val anotherPhoneView=LayoutInflater.from(requireContext()).inflate(R.layout.phone_layout,phoneContainer,false)
        val layoutPhoneNumber = anotherPhoneView.findViewById<TextInputEditText>(R.id.phoneNumber)
        val layoutClearPhone = anotherPhoneView.findViewById<ImageButton>(R.id.clearPhone)
        val layoutPhoneLabel =anotherPhoneView.findViewById<AutoCompleteTextView>(R.id.phoneNumberLabel)
        layoutPhoneNumber.id = View.generateViewId()
        layoutClearPhone.id = View.generateViewId()
        layoutPhoneLabel.id = View.generateViewId()

        layoutPhoneNumber.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if(s?.isNotEmpty()!=true){
                    layoutClearPhone.visibility = View.VISIBLE
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.isNotEmpty()!=true){
                    layoutClearPhone.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if(s?.isNotEmpty()!=true){
                    layoutClearPhone.visibility = View.VISIBLE
                }
            }
        })

        layoutClearPhone.setOnClickListener{
            layoutPhoneLabel.text = null
            layoutPhoneLabel.clearFocus()
            layoutPhoneNumber.setText("")
            layoutClearPhone.visibility =View.INVISIBLE
            phoneContainer.removeView(anotherPhoneView)
            oneTimeGeneratePhone=0
        }
        phoneContainer.addView(anotherPhoneView)
    }

    private fun addEmailLayout() {
        val anotherPhoneView=LayoutInflater.from(requireContext()).inflate(R.layout.email_layout,emailContainer,false)
        val layoutEmail = anotherPhoneView.findViewById<TextInputEditText>(R.id.email)
        val layoutClearEmail = anotherPhoneView.findViewById<ImageButton>(R.id.clearEmail)
        val layoutEmailLabel =anotherPhoneView.findViewById<AutoCompleteTextView>(R.id.emailLabel)
        layoutEmail.id = View.generateViewId()
        layoutClearEmail.id = View.generateViewId()
        layoutEmailLabel.id = View.generateViewId()

        layoutEmail.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if(s?.isNotEmpty()!=true){
                    layoutClearEmail.visibility = View.VISIBLE
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.isNotEmpty()!=true){
                    layoutClearEmail.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if(s?.isNotEmpty()!=true){
                    layoutClearEmail.visibility = View.VISIBLE
                }
            }
        })

        layoutClearEmail.setOnClickListener{
            layoutEmailLabel.text = null
            layoutEmailLabel.clearFocus()
            layoutEmail.setText("")
            layoutClearEmail.visibility =View.INVISIBLE
            emailContainer.removeView(anotherPhoneView)
            oneTimeGenerateEmail=0
        }
        emailContainer.addView(anotherPhoneView)
    }

    private fun addDateLayout() {
        val anotherPhoneView=LayoutInflater.from(requireContext()).inflate(R.layout.date_layout,dateContainer,false)
        val layoutBirthday = anotherPhoneView.findViewById<TextInputEditText>(R.id.birthdayDate)
        val layoutClearDate = anotherPhoneView.findViewById<ImageButton>(R.id.clearBirthdayDate)
        val layoutDateLabel =anotherPhoneView.findViewById<AutoCompleteTextView>(R.id.significantDateLabel)
        layoutBirthday.id = View.generateViewId()
        layoutClearDate.id = View.generateViewId()
        layoutDateLabel.id = View.generateViewId()
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select the Birthday Date")
            .setTextInputFormat(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .build()
        layoutBirthday.setOnClickListener {
            datePicker.show(parentFragmentManager,"Date Picker")
        }
        datePicker.addOnPositiveButtonClickListener {
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = date.format(it)
            layoutClearDate.visibility = View.VISIBLE
            layoutBirthday.setText(formattedDate)
        }
        layoutClearDate.setOnClickListener{
            layoutDateLabel.text = null
            layoutDateLabel.clearFocus()
            layoutBirthday.setText("")
            layoutClearDate.visibility =View.INVISIBLE
            dateContainer.removeView(anotherPhoneView)
            oneTimeGenerateDate = 0
        }
        dateContainer.addView(anotherPhoneView)
    }
}