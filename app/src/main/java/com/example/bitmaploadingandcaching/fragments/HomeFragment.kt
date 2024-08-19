package com.example.bitmaploadingandcaching.fragments

import android.Manifest
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
        loadContactList()
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
                println("Transition state: ${searchView.currentTransitionState}")
                searchView.setText(activityResult?.get(0).toString())
            }
        }



//        SearchBar Listener
        searchBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.mic ->{
                    println("ITEM CLICKED")
                    if(!SpeechRecognizer.isRecognitionAvailable(requireContext())){
                        Toast.makeText(requireContext(),"Speech Recognition not available", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        println("ON Permission: $recordAudioPermission")
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


    //    Get Initial Contact List
    private fun loadContactList() {
        val list:MutableList<Contact> = mutableListOf()
        Thread{
            for (i in 1..1){
                list.add(
                    Contact("Dog","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcROf6UTZz7xI0CSxn0p8t8IP-tNdjz4HnV7fQ&s",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Penguin","https://t4.ftcdn.net/jpg/05/98/80/23/240_F_598802337_8tVqpC3zWLZrRxJwayYdUTM9E3d3xoNG.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Shark","https://thumbs.dreamstime.com/b/cute-cartoon-style-shark-ai-generated-301950258.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Kangaroo","https://t4.ftcdn.net/jpg/07/10/35/63/240_F_710356348_SHWeoISgEgyv0UaWmfut1rlwpmmAggvu.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Panda","https://i.natgeofe.com/k/75ac774d-e6c7-44fa-b787-d0e20742f797/giant-panda-eating_4x3.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Zebra","https://c02.purpledshub.com/uploads/sites/62/2014/09/GettyImages-520064858-79cc024.jpg?webp=1&w=1200",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Dragon","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRqEKYeZnBrX0Qf2ZdUZJEAka4FSQ1uYX6pXw&s",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Whale","https://easy-peasy.ai/cdn-cgi/image/quality=80,format=auto,width=700/https://fdczvxmwwjwpwbeeqcth.supabase.co/storage/v1/object/public/images/268c4b03-533f-4e81-aec1-0c22df466a90/eebf3caa-98db-42c0-983f-d6e4b85486e4.png",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Pegasus","https://t4.ftcdn.net/jpg/05/62/13/35/360_F_562133561_OIgHUI9hP11y6gWYxdmeNVGtjPHeHvYa.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Cat","https//images.pexels.com/photos/104827/cat-pet-animal-domestic-104827.jpeg?cs=srgb&dl=pexels-pixabay-104827.jpg&fm=jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Griffin","https//t3.ftcdn.net/jpg/05/84/03/80/360_F_584038065_bTAe8Ly8ZBejUYsJZVJFgYVYGCwbXRtN.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Egg","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8iPUqDUNZyP9rYqzg2JXonP7iuacq71NGmw&s",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Fish Nemo","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQLOfj5r_zUKHc9c6zEf00tww8nNwm7Fe1b_Q&s",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Octopus","https://as1.ftcdn.net/v2/jpg/05/38/90/32/1000_F_538903298_CDDXJNLeJKEamt5UsZOLViGIMBs2uhG6.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Boat","https://i.pinimg.com/originals/9e/d8/c2/9ed8c20db316bb5e9b000485f79d1169.png",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Ghost","https://png.pngtree.com/png-vector/20230831/ourmid/pngtree-sticker-halloween-cute-ghost-png-image_9236584.png",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Flower","https://artincontext.org/wp-content/uploads/2022/05/lily-sketch.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Giraffe","https://t4.ftcdn.net/jpg/07/55/79/77/360_F_755797761_syuApheTCciQYGWhH9BipcULAHb0PLGR.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Robot","https://cdn.pixabay.com/photo/2023/05/08/08/41/ai-7977960_1280.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Clown","https://static.vecteezy.com/system/resources/previews/026/793/431/original/3d-clown-ai-generated-png.png",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Robot Dog","https://m.media-amazon.com/images/I/51MsPhbA9rL._AC_UF1000,1000_QL80_.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Fox","https://as2.ftcdn.net/v2/jpg/05/46/87/85/1000_F_546878598_GrpyJuARW7ORzPTRKc2PmWHDwJXuKOaF.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("White Bat","https://i.redd.it/ai-generated-art-based-on-album-title-alone-white-bat-v0-ahrc1uudg4oa1.png?width=1024&format=png&auto=webp&s=2b7d61604812010e9830114303aedbb124ee944e",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Elephant","https://as2.ftcdn.net/v2/jpg/05/58/18/09/1000_F_558180984_bvzD7FmlT1OXnWJrFepZELt0xHEWCPk4.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Racoon","https://t3.ftcdn.net/jpg/01/73/37/16/240_F_173371622_02A2qGqjhsJ5SWVhUPu0t9O9ezlfvF8l.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Owl","https://t3.ftcdn.net/jpg/06/57/92/46/240_F_657924687_XCdCxvJWUTAsY9IFoONoCTtDVL7bXmmo.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Deer","https://t4.ftcdn.net/jpg/00/36/90/85/240_F_36908525_As0ghZR1cwgdeokBZGJirLJmaecyDVFD.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Parrot","https://t4.ftcdn.net/jpg/05/69/76/19/240_F_569761902_IJCFEmonuL6ZjRegG95ND6NZMNsYlCUF.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Crab","https://t4.ftcdn.net/jpg/02/21/29/01/240_F_221290156_nRmQC0NqaFcqqMG6XpAXxWsNXPivO392.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Turtle","https://t4.ftcdn.net/jpg/01/97/63/79/240_F_197637933_f6ImHqOfwvUQX0CNKxHjfNd21RDZoCbJ.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Dolphin","https://t4.ftcdn.net/jpg/03/69/16/01/240_F_369160188_IL1EEplxgVrn5Occa5uSZEWu2w1ON3kW.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Cola","https://t3.ftcdn.net/jpg/01/98/52/88/240_F_198528846_0nIrRofL2xrTUJqG9TgIn5hLA6GdFDr6.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Rhino","https://t4.ftcdn.net/jpg/03/00/24/41/240_F_300244130_YLQbwdSiZTEzdQFVYJ83EJoDVQZbddA2.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Polar Beer","https://t3.ftcdn.net/jpg/01/26/55/78/240_F_126557872_IrAoU2CN0yIZHntokz2yINUO7GsuE15N.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Snake","https://t3.ftcdn.net/jpg/05/03/96/58/240_F_503965831_FD7wnRsLumu7jHoIXqP7Xxd8mie8FEbv.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Eagle","https://t4.ftcdn.net/jpg/00/42/69/25/240_F_42692507_kYHdiXHXtx0wTSQY4XG1rZDRTy4QjAqK.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Husky","https://t4.ftcdn.net/jpg/02/83/11/49/240_F_283114913_4Le6zXwGlc68z9a75cd1lH3a280JIK7D.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Duck","https://t4.ftcdn.net/jpg/04/81/22/35/240_F_481223549_r03hJYLi6Q5Wgd9VcN7j1sQNHmVVDySQ.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Sea Lion","https://t3.ftcdn.net/jpg/06/17/49/84/240_F_617498451_BAzPPZQrg3WmfjUJORVx0cfgPkhsjfTv.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Cameleon","https://t3.ftcdn.net/jpg/05/98/73/20/240_F_598732082_xlL59GmevM6BeWOt3tY7Ea98ZDBiYewH.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Leopard","https://t3.ftcdn.net/jpg/05/67/73/32/240_F_567733294_3rTyBiZ72er4JhHVeINMN4SYVylcCxjv.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Monkey","https://t4.ftcdn.net/jpg/05/59/29/51/240_F_559295172_M8rFh1rzlRvUPUSvkW9LP5jttL1vLwp7.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Crocodile","https://t3.ftcdn.net/jpg/04/09/93/04/240_F_409930418_ZCsIGZVR3Fsx3vlwFXxtvLQcadRY21iR.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("frog","https://t3.ftcdn.net/jpg/00/06/74/82/240_F_6748285_nimv7590D4WEeKv86TrgVigSQSP8u3hY.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("White Tiger","https://t4.ftcdn.net/jpg/03/05/07/19/240_F_305071929_rpNHnojYD23nsb2MFIdTXIyxZA9MLmu2.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Sea Horse","https://t3.ftcdn.net/jpg/06/33/01/94/240_F_633019450_fJc0AePFnzhWyIb0gjszlm0hd2xRZIPq.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Star Fish","https://t3.ftcdn.net/jpg/05/02/61/06/240_F_502610663_0bDKK6BF4S67T7vBzh8fS4575nyZgHvP.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Tent","https://t3.ftcdn.net/jpg/06/19/36/18/240_F_619361881_kMhJyU5DXxzy5wVVgcesnfpp0B1zURnL.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Tap","https://t3.ftcdn.net/jpg/03/65/71/50/240_F_365715078_ArWaX2jhn87T0HmHhH6EACia4YO1EGLE.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Lizard","https://t4.ftcdn.net/jpg/03/27/84/79/240_F_327847948_FGf5gD2I1vzK31MzsRFPUXaYcm7pwzU2.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("flamingo","https://t4.ftcdn.net/jpg/06/20/98/53/240_F_620985342_7MLuFUGgSbmB02q1UvFhzIEQ3pUZjyWr.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Gorilla","https://t4.ftcdn.net/jpg/05/26/01/81/240_F_526018124_314zcONQdGH5avAhaZuLkPH2pxaaCl0s.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Tiger","https://t4.ftcdn.net/jpg/05/95/01/35/240_F_595013565_UaM1eVZq3oTBQQguFeO4ElXW5xKeUyIL.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Little Boy","https://t4.ftcdn.net/jpg/05/43/03/53/240_F_543035386_WymOyNgssDEL8B16wTM08rltZPB1uw7u.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Crow","https://t4.ftcdn.net/jpg/06/78/86/53/240_F_678865337_oJ2abC42oq8jhVoYXbQWl34RXYIfiPff.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Hyene","https://t4.ftcdn.net/jpg/03/12/51/11/240_F_312511168_bgj4i8daYsE2mttVxgKOJ09Km96Aq4jI.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Lion","https://t3.ftcdn.net/jpg/02/93/66/78/240_F_293667824_x1vsWXZMNv0PHb8BILh7pPtNvjPSM4aA.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Elephant On Tree","https://t3.ftcdn.net/jpg/02/69/87/04/240_F_269870413_IdnpSWcfCQO7kciAgPZ3y4g38rQLWpHf.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Butterfly","https://t3.ftcdn.net/jpg/03/51/40/20/240_F_351402022_Dx6yILBvfp8L7T2QOhsmKgYhHHR4GBuK.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Bee","https://t3.ftcdn.net/jpg/05/58/03/78/240_F_558037819_7hLgy7TsdZwCC7qJqmtjRytntOS2TAvc.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
                list.add(
                    Contact("Bison","https://t4.ftcdn.net/jpg/04/39/50/87/240_F_439508795_c1HbDXCEVfM1FeP7pVjYAZe2PqMa6DkZ.jpg",
                    COLORS_LIST[Random.nextInt(0,10)],"98272340292",false)
                )
            }
            println("Thread Finished")
        }.start()
        contactList = list
    }
}