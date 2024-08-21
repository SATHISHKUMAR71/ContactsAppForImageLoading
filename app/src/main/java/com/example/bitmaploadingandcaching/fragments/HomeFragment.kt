package com.example.bitmaploadingandcaching.fragments

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bitmaploadingandcaching.dataclass.Contact
import com.example.bitmaploadingandcaching.recyclerview.ImagesAdapter
import com.example.bitmaploadingandcaching.MainActivity
import com.example.bitmaploadingandcaching.R
import com.example.bitmaploadingandcaching.viewmodel.CacheData
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.search.SearchBar
import java.util.Locale
import kotlin.random.Random


class HomeFragment : Fragment() {

    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private lateinit var searchView:com.google.android.material.search.SearchView
    private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var recordAudioPermission = false
    private lateinit var addContact: ExtendedFloatingActionButton
    private var contactList:MutableList<Contact> = mutableListOf()
    private lateinit var addContactFragment:AddContactFragment
    companion object{
        var queryReceived = ""
        var COLORS_LIST = arrayOf(
            Color.argb(255,251,192,45),
            Color.argb(255,230,74,25),
            Color.argb(255,0,151,167),
            Color.argb(255,25,118,210),
            Color.argb(255,211,47,47),
            Color.argb(255,103,80,164),
            Color.argb(255,0,121,107),
            Color.argb(255,56,142,60),
            Color.argb(255,121,134,203),
            Color.argb(255,224,64,251))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        contactList = CacheData.i
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        val recyclerSearchView = view.findViewById<RecyclerView>(R.id.searchRecyclerView)
        val adapter = ImagesAdapter(requireContext())
        adapter.resetViews(contactList,null)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
        recyclerSearchView.adapter = adapter
        recyclerSearchView.layoutManager = LinearLayoutManager(requireContext())


        rv.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(dy<0){
                    addContact.extend()
                }
                else if(dy>0){
                    addContact.shrink()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        searchView = view.findViewById(R.id.searchView)
        val searchBar = view.findViewById<SearchBar>(R.id.searchBar)
        addContact = view.findViewById(R.id.addContacts)

        addContactFragment =  AddContactFragment()
        addContact.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in, //(Enter transition fragment enter the screen)
                    R.anim.fade_out, //(exit transition fragment exit the screen)
                    R.anim.fade_in, //(pop enter transition user navigates back to the fragment)
                    R.anim.slide_out //(pop exit transition fragment removed from the screen)
                )
                .replace(R.id.fragmentContainerView,addContactFragment)
                .addToBackStack("Add Contact")
                .commit()
        }

//        BackPressed Handler
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(),object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(searchView.isShowing && this@HomeFragment.isVisible){
                    searchView.hide()
                }
                else if(addContactFragment.isVisible){
                    parentFragmentManager.popBackStack()
                }
                else{
                    requireActivity().finish()
                }
            }

        })


//        Speech Activity Result
        val launchOnResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode== Activity.RESULT_OK){
                searchView.show()
                val activityResult = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val textOutput = activityResult?.get(0).toString()
                searchView.setText(textOutput)
                searchView.editText.setSelection(textOutput.length)
            }
        }



//        SearchBar Listener
        searchBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.mic ->{
                    if(!SpeechRecognizer.isRecognitionAvailable(requireContext())){
                        Toast.makeText(requireContext(),"Speech Recognition not available", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        if(MainActivity.recordAudioPermission){
                            val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                            recognizerIntent.putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Contact Name")
                            launchOnResult.launch(recognizerIntent)
                        }
                        else{
                            ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION)
                            if(MainActivity.recordAudioPermission){
                                val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                                recognizerIntent.putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Contact Name")
                                launchOnResult.launch(recognizerIntent)
                            }
                        }
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }



//        Search View Listener
        searchView.editText.addTextChangedListener{
            queryReceived = "${it?:""}"
            val list1 = getContactList((it?:"").toString(), contactList)
            adapter.resetViews(list1, queryReceived)
        }


//        Search View Transition Listener
        searchView.addTransitionListener { searchView, previousState, newState ->
            if(newState == com.google.android.material.search.SearchView.TransitionState.HIDING){
                searchView.editText.setText("")
                adapter.resetViews(contactList,null)
            }
        }
        return view
    }




//    Get Contact Based on Search Query
    private fun getContactList(query:String,list: List<Contact>):List<Contact>{
        val queriedList:MutableList<Contact> = mutableListOf()
        for(i in list){
            if((query.lowercase() in i.name.lowercase()) || (query.lowercase() in i.contactNumber.lowercase())){
                queriedList.add(i.copy(isHighlighted = true))
            }
        }
        return queriedList
    }
}