package com.example.bitmaploadingandcaching.fragments

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
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.view.size
import com.example.bitmaploadingandcaching.R
import com.example.bitmaploadingandcaching.dataclass.Contact
import com.example.bitmaploadingandcaching.viewmodel.CacheData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random


class AddContactFragment : Fragment() {

    private lateinit var addPictureImg:ImageView
    private lateinit var addPictureText:MaterialButton
    private lateinit var firstName:TextInputEditText
    private lateinit var lastName:TextInputEditText
    private lateinit var companyName:TextInputEditText
    private lateinit var saveBtn:MaterialButton
    private lateinit var addNoteToolbar: MaterialToolbar
    private lateinit var emailContainer:LinearLayout
    private lateinit var phoneContainer:LinearLayout
    private lateinit var dateContainer:LinearLayout
    private var significantDate:MutableMap<String,String> = mutableMapOf()
    private var contactNumber:MutableMap<String,String> = mutableMapOf()
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

        saveBtn = view.findViewById(R.id.addContactSaveButton)
        addNoteToolbar = view.findViewById(R.id.addNoteToolbar)

        phoneContainer = view.findViewById(R.id.linearLayoutPhoneContainer)
        emailContainer = view.findViewById(R.id.linearLayoutEmailContainer)
        dateContainer = view.findViewById(R.id.linearLayoutDateContainer)
        addEmailLayout()
        addPhoneLayout()
        addDateLayout()
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select the Birthday Date")
            .setTextInputFormat(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .build()

        addNoteToolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        var left = 0
        var right = CacheData.list.size
        saveBtn.setOnClickListener {
            if(firstName.text.toString().isNotEmpty() || lastName.text.toString().isNotEmpty() ||
                companyName.text.toString().isNotEmpty()){
                var name = firstName.text.toString()
                if(firstName.text.toString().isEmpty()){
                    name += lastName.text.toString()
                }
                else{
                    name += " "+lastName.text.toString()
                }
                if(name.isEmpty()){
                    if(companyName.text.toString().isNotEmpty()){
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
                    if((contactNumber.size>0)&&(significantDate.size>0)){
                        CacheData.addList(Contact(name,
                            dataImage.toString(),
                            HomeFragment.COLORS_LIST[Random.nextInt(0,10)],contactNumber["1"]?:"", isHighlighted = false,isUri = true,significantDate["1"]?:""),left)
                    }
                    else{
                        CacheData.addList(Contact(name,
                            dataImage.toString(),
                            HomeFragment.COLORS_LIST[Random.nextInt(0,10)],"", isHighlighted = false,isUri = true,""),left)

                    }
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
        var oneTime = 0
        var phone = ""
        val phoneView=LayoutInflater.from(requireContext()).inflate(R.layout.phone_layout,phoneContainer,false)
        val layoutPhoneNumber = phoneView.findViewById<TextInputEditText>(R.id.phoneNumber)
        val layoutClearPhone = phoneView.findViewById<ImageButton>(R.id.clearPhone)
        val layoutPhoneLabel =phoneView.findViewById<AutoCompleteTextView>(R.id.phoneNumberLabel)
        phoneView.alpha = 0f
        phoneView.scaleX = 0.9f
        phoneView.scaleY = 0.9f
        layoutPhoneNumber.id = View.generateViewId()
        layoutClearPhone.id = View.generateViewId()
        layoutPhoneLabel.id = View.generateViewId()
        layoutPhoneNumber.setOnFocusChangeListener { v, hasFocus ->

            if(!hasFocus && v.isVisible){
                contactNumber[phoneContainer.size.toString()] = phone
                println("*** phone added: $phone")
            }
        }
        layoutPhoneNumber.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if(s?.isNotEmpty()==true){
                    println("*** after $s $phone")
                    layoutClearPhone.visibility = View.VISIBLE
                    phone = "$s"
                    if(oneTime==0){
                        addPhoneLayout()
                        oneTime++
                    }
                }
                else{
                    layoutClearPhone.visibility = View.INVISIBLE
                }
            }
        })

        layoutClearPhone.setOnClickListener{
            layoutPhoneLabel.text = null
            layoutPhoneLabel.clearFocus()
            layoutPhoneNumber.setText("")
            layoutClearPhone.visibility =View.INVISIBLE
            if(phoneContainer.size>1){
                removePhoneLayout(phoneView)
            }
            oneTimeGeneratePhone=0
        }
        phoneContainer.addView(phoneView)
        phoneView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .start()
        println("*** ${phoneContainer.size}")
    }

    private fun addEmailLayout() {
        var email = ""
        var oneTime = 0
        val emailView=LayoutInflater.from(requireContext()).inflate(R.layout.email_layout,emailContainer,false)
        val layoutEmail = emailView.findViewById<TextInputEditText>(R.id.email)
        val layoutClearEmail = emailView.findViewById<ImageButton>(R.id.clearEmail)
        val layoutEmailLabel = emailView.findViewById<AutoCompleteTextView>(R.id.emailLabel)
        layoutEmail.id = View.generateViewId()
        layoutClearEmail.id = View.generateViewId()
        layoutEmailLabel.id = View.generateViewId()
        emailView.alpha = 0f
        emailView.scaleX = 0.9f
        emailView.scaleY = 0.9f
        layoutEmail.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if(s?.isNotEmpty()==true){
                    layoutClearEmail.visibility = View.VISIBLE
                    if(oneTime==0){
                        addEmailLayout()
                        oneTime++
                    }
                }
                else{
                    layoutClearEmail.visibility = View.INVISIBLE
                }
            }
        })

        layoutClearEmail.setOnClickListener{
            layoutEmailLabel.text = null
            layoutEmailLabel.clearFocus()
            layoutEmail.setText("")
            layoutClearEmail.visibility =View.INVISIBLE
            if(emailContainer.size>1){
                removeEmailLayout(emailView)
            }
            oneTimeGenerateEmail=0
        }
        emailContainer.addView(emailView)
        emailView.animate()
            .alpha(1f)
            .scaleY(1f)
            .scaleX(1f)
            .setDuration(300)
            .start()
        println("*** ${emailContainer.size}")
    }

    private fun addDateLayout() {
        var tmpDate = ""
        val dateView=LayoutInflater.from(requireContext()).inflate(R.layout.date_layout,dateContainer,false)
        val layoutBirthday = dateView.findViewById<TextInputEditText>(R.id.birthdayDate)
        val layoutClearDate = dateView.findViewById<ImageButton>(R.id.clearBirthdayDate)
        val layoutDateLabel = dateView.findViewById<AutoCompleteTextView>(R.id.significantDateLabel)
        dateView.alpha = 0f
        dateView.scaleX = 0.9f
        dateView.scaleY = 0.9f
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
            tmpDate = formattedDate
            significantDate[dateContainer.size.toString()] = tmpDate
            println("*** date added $tmpDate")
            addDateLayout()
        }
        layoutClearDate.setOnClickListener{
            layoutDateLabel.text = null
            layoutDateLabel.clearFocus()
            layoutBirthday.setText("")
            layoutClearDate.visibility =View.INVISIBLE
            if(dateContainer.size>1){
                removeDateLayout(dateView)
            }
            oneTimeGenerateDate = 0
        }
        dateContainer.addView(dateView)
        dateView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .start()

        println("*** ${dateContainer.size}")
    }

    private fun removeEmailLayout(view: View){
        view.animate()
            .alpha(0f)
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(200)
            .withEndAction { emailContainer.removeView(view) }
            .start()
    }

    private fun removeDateLayout(view: View){
        view.animate()
            .alpha(0f)
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(200)
            .withEndAction { dateContainer.removeView(view) }
            .start()
    }

    private fun removePhoneLayout(view: View){
        view.animate()
            .alpha(0f)
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(200)
            .withEndAction { phoneContainer.removeView(view) }
            .start()
    }
}