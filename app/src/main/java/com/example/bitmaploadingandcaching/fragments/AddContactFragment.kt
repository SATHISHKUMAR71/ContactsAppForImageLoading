package com.example.bitmaploadingandcaching.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setPadding
import com.example.bitmaploadingandcaching.R
import com.example.bitmaploadingandcaching.dataclass.Contact
import com.example.bitmaploadingandcaching.viewmodel.CacheData
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
    private var dataImage:Uri = Uri.parse("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
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

                while (left<right){
                    val mid = (left+right) / 2
                    if(CacheData.list[mid].name<name){
                        left = mid+1
                    }
                    else{
                        right = mid
                    }
                }
                println("$$$$ ${CacheData.list[left]}")
                CacheData.addList(Contact(name,
                    dataImage.toString(),
                    HomeFragment.COLORS_LIST[Random.nextInt(0,10)],phoneNumber.text.toString(), isHighlighted = false,isUri = true),left)
                parentFragmentManager.popBackStack()
                Toast.makeText(requireContext(),"Contact Saved Successfully",Toast.LENGTH_SHORT).show()
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
}