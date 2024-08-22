package com.example.bitmaploadingandcaching.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setPadding
import androidx.core.view.size
import com.example.bitmaploadingandcaching.R
import com.example.bitmaploadingandcaching.dataclass.Contact
import com.example.bitmaploadingandcaching.dataclass.LabelData
import com.example.bitmaploadingandcaching.viewmodel.CacheData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random


class AddContactFragment : Fragment() {

    private lateinit var launchImage: ActivityResultLauncher<Intent>
    private lateinit var launchCamera: ActivityResultLauncher<Intent>
    private lateinit var addPictureImg:ImageView
    private var phoneLabelArray = arrayOf("")
    private var phoneLabelLength = 0
    private var emailLabelArray = arrayOf("")
    private var emailLabelLength = 0
    private var left = 0
    private var right = CacheData.list.size
    private var dateLabelArray = arrayOf("")
    private var dateLabelLength = 0
    private var phoneListWithLabel:MutableMap<Int,LabelData> = mutableMapOf()
    private var emailListWithLabel:MutableMap<Int,LabelData> = mutableMapOf()
    private var dateListWithLabel:MutableMap<Int,LabelData> = mutableMapOf()
    private lateinit var addPictureText:MaterialButton
    private lateinit var firstName:TextInputEditText
    private lateinit var lastName:TextInputEditText
    private lateinit var companyName:TextInputEditText
    private lateinit var saveBtn:MaterialButton
    private lateinit var addNoteToolbar: MaterialToolbar
    private lateinit var emailContainer:LinearLayout
    private lateinit var phoneContainer:LinearLayout
    private lateinit var dateContainer:LinearLayout
    private var oneTimeGeneratePhone = 0
    private var oneTimeGenerateEmail = 0
    private var oneTimeGenerateDate = 0
    private var dataImage:Uri = Uri.parse("")
    private var bitmapData = ""
    private var isUri = false
    private var isBitmap = false
    private var phoneMapIndex = -1
    private var emailMapIndex = -1
    private var dateMapIndex = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        initResourceData()
        val view =  inflater.inflate(R.layout.fragment_add_note, container, false)
        initializeViews(view)
        addEmailLayout()
        addPhoneLayout()
        addDateLayout()
        setUpListeners(addNoteToolbar,saveBtn,addPictureText,addPictureImg)
        setActivityResultLaunchers()
        return view
    }

    private fun setActivityResultLaunchers() {
        launchImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                val image = result.data?.data
                isBitmap = false
                isUri = true
                println("ON DATA $image")
                dataImage = image?:Uri.parse("")
                println("ON DATA ${dataImage}")
                addPictureImg.setPadding(0)
                addPictureImg.setImageURI(image)
            }
        }

        launchCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                val image = result.data?.extras?.get("data") as Bitmap
                bitmapData = image.toString()
                isBitmap = true
                isUri = false
//                DiskCache.saveBitMap(requireContext(),bitmapData,image)
                CacheData.bitmapCache.put(bitmapData,image)
                addPictureImg.setPadding(0)
                addPictureImg.setImageBitmap(image)
            }
        }

    }

    private fun setUpListeners(
        addNoteToolbar: MaterialToolbar,
        saveBtn: MaterialButton,
        addPictureText: MaterialButton,
        addPictureImg: ImageView
    ) {
        addPictureText.setOnClickListener {
            showAlertDialog()
        }
        addPictureImg.setOnClickListener{
            showAlertDialog()
        }
        addNoteToolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        saveBtn.setOnClickListener {
            if(firstName.text.toString().isNotEmpty() || lastName.text.toString().isNotEmpty() ||
                companyName.text.toString().isNotEmpty()){
                var name = firstName.text.toString()
                name += if(firstName.text.toString().isEmpty()){
                    lastName.text.toString()
                } else{
                    " "+lastName.text.toString()
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
                    var j =0
                    for(i in phoneListWithLabel){
                        if(i.value.value.isNotEmpty() && i.value.label.isNotEmpty()){
                            if(isUri){
                                val contact = Contact(name,
                                    dataImage.toString(),
                                    HomeFragment.COLORS_LIST[Random.nextInt(0,10)],i.value.value,i.value.label,
                                    isHighlighted = false,isUri = isUri,isBitmap,dateListWithLabel)
                                CacheData.addList(contact,left)
                            }
                            else{
                                val contact = Contact(name,
                                    bitmapData,
                                    HomeFragment.COLORS_LIST[Random.nextInt(0,10)],i.value.value,i.value.label,
                                    isHighlighted = false,isUri = isUri,isBitmap,dateListWithLabel)
                                CacheData.addList(contact,left)
                            }
                            j++
                        }
                    }
                    if(j==0){
                        if(isUri){
                            val contact = Contact(name,
                                dataImage.toString(),
                                HomeFragment.COLORS_LIST[Random.nextInt(0,10)],"","Mobile",
                                isHighlighted = false,isUri = isUri,isBitmap,dateListWithLabel)
                            CacheData.addList(contact,left)
                        }
                        else{
                            val contact = Contact(name,
                                bitmapData,
                                HomeFragment.COLORS_LIST[Random.nextInt(0,10)],"","Mobile",
                                isHighlighted = false,isUri = isUri,isBitmap,dateListWithLabel)
                            CacheData.addList(contact,left)
                        }

                    }
                    println("MAP DATA: $dateListWithLabel $emailListWithLabel $phoneListWithLabel")
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
    }

    private fun initResourceData() {
        phoneLabelArray =resources.getStringArray(R.array.phoneNumberLabel)
        phoneLabelLength = phoneLabelArray.size
        emailLabelArray = resources.getStringArray(R.array.emailLabel)
        emailLabelLength = emailLabelArray.size
        dateLabelArray = resources.getStringArray(R.array.dateLabel)
        dateLabelLength = dateLabelArray.size
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Choose Image Source")
            .setItems(arrayOf("Camera","Gallery")){_,which ->
                when(which){
                    0 -> {
                        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        launchCamera.launch(i)
                    }
                    1 -> {
                        val i = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        launchImage.launch(i)
                    }
                }
            }
            .show()
    }

    private fun initializeViews(view: View){
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
    }

    private fun addPhoneLayout() {
        val index = phoneMapIndex+1
        phoneListWithLabel[index] =  LabelData("","")
        phoneMapIndex++
        var oneTime = 0
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
        layoutPhoneNumber.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                phoneListWithLabel[index]?.value = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                if(s?.isNotEmpty()==true){
                    layoutClearPhone.visibility = View.VISIBLE
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
        if(phoneLabelLength<=index){
            val tmpLabel = phoneLabelArray[index%phoneLabelLength]
            layoutPhoneLabel.setText(tmpLabel,false)
            phoneListWithLabel[index]?.label = tmpLabel
        }
        else{
            val tmpLabel = phoneLabelArray[index]
            layoutPhoneLabel.setText(tmpLabel,false)
            phoneListWithLabel[index]?.label = tmpLabel
        }
        layoutPhoneLabel.setOnItemClickListener { parent, _, position, _ ->
            val option = parent.getItemAtPosition(position).toString()
            phoneListWithLabel[index]?.label = option
        }
        layoutClearPhone.setOnClickListener{
            layoutPhoneLabel.text = null
            layoutPhoneLabel.clearFocus()
            layoutPhoneNumber.setText("")
            layoutClearPhone.visibility =View.INVISIBLE
            if(phoneContainer.size>1){
                removePhoneLayout(phoneView)
                phoneListWithLabel.remove(index)
                phoneMapIndex--
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
    }

    private fun addEmailLayout() {
        val index = emailMapIndex+1
        emailListWithLabel[index] = LabelData("","")
        emailMapIndex++
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

        if(emailLabelLength<=index){
            val tmpLabel = emailLabelArray[index%emailLabelLength]
            layoutEmailLabel.setText(tmpLabel,false)
            emailListWithLabel[index]?.label = tmpLabel
        }
        else{
            val tmpLabel = emailLabelArray[index]
            layoutEmailLabel.setText(tmpLabel,false)
            emailListWithLabel[index]?.label = tmpLabel
        }
        layoutEmailLabel.setOnItemClickListener { parent, view, position, id ->
            val option = parent.getItemAtPosition(position).toString()
            emailListWithLabel[index]?.label = option
        }
        layoutEmail.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    emailListWithLabel[index]?.value = s.toString()
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
                emailListWithLabel.remove(index)
                emailMapIndex--
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
    }

    private fun addDateLayout() {
        val index = dateMapIndex+1
        dateMapIndex++
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
        dateListWithLabel[index] = LabelData("","")
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select the Birthday Date")
            .setTextInputFormat(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .build()
        layoutBirthday.setOnClickListener {
            datePicker.show(parentFragmentManager,"Date Picker")
        }
        if(dateLabelLength<=index){
            val tmpLabel = dateLabelArray[index%dateLabelLength]
            layoutDateLabel.setText(tmpLabel,false)
            dateListWithLabel[index]?.label = tmpLabel
        }
        else{
            val tmpLabel = dateLabelArray[index]
            layoutDateLabel.setText(tmpLabel,false)
            dateListWithLabel[index]?.label = tmpLabel
        }
        layoutDateLabel.setOnItemClickListener { parent, view, position, id ->
            val option = parent.getItemAtPosition(position).toString()
            dateListWithLabel[index]?.label = option
        }
        datePicker.addOnPositiveButtonClickListener {
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = date.format(it)
            layoutClearDate.visibility = View.VISIBLE
            layoutBirthday.setText(formattedDate)
            dateListWithLabel[index]?.value = formattedDate
            addDateLayout()
        }
        layoutClearDate.setOnClickListener{
            layoutDateLabel.text = null
            layoutDateLabel.clearFocus()
            layoutBirthday.setText("")
            layoutClearDate.visibility =View.INVISIBLE
            if(dateContainer.size>1){
                removeDateLayout(dateView)
                dateListWithLabel.remove(index)
                dateMapIndex--
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