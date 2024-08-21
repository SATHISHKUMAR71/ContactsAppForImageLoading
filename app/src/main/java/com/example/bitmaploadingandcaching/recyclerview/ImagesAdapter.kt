package com.example.bitmaploadingandcaching.recyclerview

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bitmaploadingandcaching.dataclass.Contact
import com.example.bitmaploadingandcaching.dataclass.HolderWithPosition
import com.example.bitmaploadingandcaching.R
import com.example.bitmaploadingandcaching.fragments.HomeFragment
import com.example.bitmaploadingandcaching.viewmodel.CacheData
import java.net.MalformedURLException
import java.net.URL
import java.net.UnknownHostException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.Volatile

class ImagesAdapter(private var context: Context):RecyclerView.Adapter<ImagesAdapter.ImageHolder>(){

    private var searchQuery = ""
    private var contactList:MutableList<Contact> = mutableListOf()
    private var bitmapCache = CacheData.bitmapCache
    private val lock1 = Any()
    private val lock2 = Any()
    @Volatile
    private lateinit var currentHolder:ImageHolder
    @Volatile
    private var currentPosition = 0
    private var counter =0
    private var ongoingDownloads = ConcurrentHashMap<String,Boolean>()
    private var positionList:MutableList<Int> = mutableListOf()
    private var pendingRequests = ConcurrentHashMap<String,MutableList<HolderWithPosition>>()
    private var pauseDownload = ConcurrentHashMap<String, HolderWithPosition>()
    private val handler = Handler(Looper.getMainLooper())
    @Volatile
    private var networkLost = true

    //    private var threadPoolExecutor = ThreadPoolExecutor(20,40,2,TimeUnit.SECONDS,LinkedBlockingQueue())
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java).apply {
        registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                networkLost = false
                downloadPendingRequests()
                println("Network Available")
            }

            override fun onUnavailable() {
                super.onUnavailable()
                println("Network Unavailable")
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                networkLost = true
                println("Network Lost")
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                println("Network Losing")
            }
        })
    }

    inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.contactImage)
        val contactName:TextView = itemView.findViewById(R.id.contactName)
        val profileName:TextView = itemView.findViewById(R.id.textName)
        val callButton:ImageButton = itemView.findViewById(R.id.callButton)
        val mobileNumber:TextView = itemView.findViewById(R.id.mobileNumber)
        val mobileType:TextView = itemView.findViewById(R.id.mobileType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        println("&&& on Bind View Holder $searchQuery ${HomeFragment.queryReceived}")
        holder.contactName.text = contactList[holder.absoluteAdapterPosition].name
        holder.profileName.text = contactList[holder.absoluteAdapterPosition].name[0].toString().uppercase()
//        println("ON Bind View Holder Called")
        holder.callButton.setOnClickListener{
            Toast.makeText(context,"Can't Make a Call Please insert a SIM",Toast.LENGTH_SHORT).show()
        }
        holder.profileName.background.setTint(contactList[holder.absoluteAdapterPosition].contactColor)
        holder.mobileNumber.text = contactList[holder.absoluteAdapterPosition].contactNumber
        if(contactList[holder.absoluteAdapterPosition].contactNumber.isEmpty()){
            println("ON If")
            holder.mobileType.visibility = View.GONE
            holder.mobileNumber.visibility = View.GONE
        }
        else{
            println("ON else")
            holder.mobileType.text = contactList[holder.absoluteAdapterPosition].contactType
            holder.mobileNumber.text = contactList[holder.absoluteAdapterPosition].contactNumber
            holder.mobileType.visibility = View.VISIBLE
            holder.mobileNumber.visibility = View.VISIBLE
        }
        searchQuery = HomeFragment.queryReceived
        println("**** $searchQuery ${HomeFragment.queryReceived}")
        updateSearchedView(holder)
        if(!contactList[holder.absoluteAdapterPosition].isUri){
//            println("1234 in download : ${contactList[position].name}")
            loadImage(holder,position,contactList[position].image)
        }
        else{
            holder.imageView.visibility = View.INVISIBLE
            val i = context.contentResolver.query(Uri.parse(contactList[position].image),null,null,null,null)
            if(i?.moveToNext()==true){
//                println("1234 IMAGE URI: $i")
                if(holder.absoluteAdapterPosition == position){
                    holder.imageView.setImageURI(Uri.parse(contactList[position].image))
                    holder.imageView.visibility = View.VISIBLE
                    i.close()
                }
            }
        }

    }



    fun resetViews(newList:List<Contact>, query:String?){
<<<<<<< HEAD
//        println("On Reset View")
=======
        println("^^^^ On Reset View $query")
>>>>>>> change-dataclass
        val contactDiff = ContactsDiffUtil(contactList, newList)
        val diffResult = DiffUtil.calculateDiff(contactDiff)
        contactList.clear()
        contactList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
        if(query!=null){
            if(query.isNotEmpty()){
                notifyItemRangeChanged(0,newList.size)
            }
            else{
                notifyItemRangeChanged(0,newList.size)
            }
        }
    }


    private fun updateSearchedView(holder: ImageHolder) {
        println("ON Update Search ${searchQuery}")
        val position = holder.absoluteAdapterPosition
        val length = searchQuery.length
        if(contactList[position].isHighlighted && searchQuery.isNotEmpty()){
//            For Name
            var startIndex = contactList[position].name.indexOf(searchQuery, ignoreCase = true)
            val highlightedName = SpannableString(contactList[position].name)
            while (startIndex>=0){
                val endIndex = startIndex+length
                if(endIndex>startIndex){
                    highlightedName.setSpan(
                        ForegroundColorSpan(Color.argb(255,255,20,20)), // You can choose any color
                        startIndex,
                        endIndex,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }
                startIndex = contactList[position].name.indexOf(searchQuery,endIndex, ignoreCase = true)
            }

//            For Mobile Number
            var startIndexMobile = contactList[position].contactNumber.indexOf(searchQuery, ignoreCase = true)
            val highlightedMobile = SpannableString(contactList[position].contactNumber)
            println("$searchQuery ${contactList[position].contactNumber} $startIndexMobile")
            while (startIndexMobile>=0){
                println("$searchQuery ${contactList[position].contactNumber} $startIndexMobile")
                val endIndex = startIndexMobile+length
                if(endIndex>startIndexMobile){
                    highlightedMobile.setSpan(
                        ForegroundColorSpan(Color.argb(255,255,20,20)), // You can choose any color
                        startIndexMobile,
                        endIndex,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }
                startIndexMobile = contactList[position].contactNumber.indexOf(searchQuery,endIndex, ignoreCase = true)
            }
            holder.contactName.text = highlightedName
            holder.mobileNumber.text = highlightedMobile
        }
    }



    private fun loadImage(holder: ImageHolder, position: Int, imageUrl: String){
            currentHolder = holder
            counter++
//            println("RUNNING holder ${holder.absoluteAdapterPosition} ${position}")
            if ((bitmapCache.get(imageUrl) == null) && (ongoingDownloads[imageUrl] != true)) {
                holder.imageView.visibility = View.INVISIBLE
                Thread {
                    var name = contactList[holder.absoluteAdapterPosition].name
//                    println("RUNNING holder in thread ${holder.absoluteAdapterPosition} ${position}")
                    try {
                        if (!networkLost) {
                            println("Download : $name ${Thread.currentThread().id} Started")
                            ongoingDownloads[imageUrl] = true
                            val url = URL(imageUrl).openConnection()
                            url.connect()
                            val inputStream = url.getInputStream()
                            val downloadedImage = BitmapFactory.decodeStream(inputStream)
                            inputStream.close()
                            println("holder Download : $name ${Thread.currentThread().id} Finished")
                            bitmapCache.put(imageUrl, downloadedImage)
                            synchronized(lock2){
//                                println("RUNNING holder in handler ${holder.absoluteAdapterPosition} ${position} count : $counter")
                                handler.post {
//                                    println("******* Holder: on handler ${currentHolder.absoluteAdapterPosition} actual position: ${currentPosition}")
//                                println("RUNNING ")
                                var i = 0
//                                println("updating at Position: ${holder.absoluteAdapterPosition} actual pos: $position")
                                if (holder.absoluteAdapterPosition == position) {
                                    i += 1
                                    holder.imageView.setImageBitmap(downloadedImage)
                                    holder.imageView.visibility = View.VISIBLE
                                }
                                else if (currentHolder.absoluteAdapterPosition == currentPosition) {
//                                    println("******* Holder: on else if ${currentHolder.absoluteAdapterPosition} actual position: $currentPosition")
                                    i += 1
                                    currentHolder.imageView.setImageBitmap(downloadedImage)
                                    currentHolder.imageView.visibility = View.INVISIBLE
                                    notifyItemChanged(currentPosition)
                                }
                                pendingRequests[imageUrl]?.forEach {
                                    i += 1
                                    if (it.holder.absoluteAdapterPosition == it.position) {
                                        it.holder.imageView.setImageBitmap(downloadedImage)
                                        it.holder.imageView.visibility = View.VISIBLE
                                    }
                                }
                                pendingRequests[imageUrl] = mutableListOf()
                            }
                            }
                        } else {
                            pauseDownload[imageUrl] = HolderWithPosition(holder, position)
                            if (pendingRequests[imageUrl].isNullOrEmpty()) {
                                pendingRequests[imageUrl] = mutableListOf()
                            }
                            check(position, holder, imageUrl)
                        }

                    } catch (e: UnknownHostException) {
                        println("In UnKnown Host Exception $name ${Thread.currentThread().id}")
//                    Adding the Pause Download if any Network Interrupt Happens
                        pauseDownload[imageUrl] = HolderWithPosition(holder, position)
                        if (pendingRequests[imageUrl].isNullOrEmpty()) {
                            pendingRequests[imageUrl] = mutableListOf()
                        }
                        check(position, holder, imageUrl)
                    } catch (e: MalformedURLException) {
                        println("Download Error while Downloading : $name ${Thread.currentThread().id}")
                        ongoingDownloads[imageUrl] = true
                    } catch (e: Exception) {
                        println("IN Catch $e $name ${Thread.currentThread().id}")
                        ongoingDownloads[imageUrl] = false
                    }
                }.start()
            } else if ((bitmapCache.get(imageUrl) == null) && (ongoingDownloads[imageUrl] == true)) {
                currentPosition = position
                currentHolder = holder
//                println("******* Holder: ${currentHolder.absoluteAdapterPosition} actual position: ${currentPosition}")
//                println("RUNNING holder in thread else if ${holder.absoluteAdapterPosition} ${position}")
                if (pendingRequests[imageUrl].isNullOrEmpty()) {
                    pendingRequests[imageUrl] = mutableListOf()
                }
                check(position, holder, imageUrl)
                holder.imageView.visibility = View.INVISIBLE
            } else {
                currentPosition = position
                currentHolder = holder
//                println("******* Holder: ${currentHolder.absoluteAdapterPosition} actual position: ${currentPosition}")
//                println("RUNNING holder in thread else ${holder.absoluteAdapterPosition} ${position}")
                holder.imageView.setImageBitmap(bitmapCache.get(imageUrl))
                if (holder.imageView.visibility != View.VISIBLE) {
                    holder.imageView.visibility = View.VISIBLE
                }
            }
    }


    fun downloadPendingRequests() {

        pauseDownload.forEach {
            Thread{
//            threadPoolExecutor.execute {
                try {
//                println("Pause Download Started: ${contactList[it.value.position].name} ${Thread.currentThread().id}")
                    ongoingDownloads[it.key] = true
                    val url = URL(it.key).openConnection()
                    url.connect()
                    val inputStream = url.getInputStream()
                    val downloadedImage = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
//                println("Pause Download Finished: ${contactList[it.value.position].name} ${Thread.currentThread().id}")
                    bitmapCache.put(it.key, downloadedImage)
                    handler.post {
                        var i = 0
                        if (it.value.holder.adapterPosition == it.value.position) {
                            i += 1
                            it.value.holder.imageView.setImageBitmap(downloadedImage)
                            it.value.holder.imageView.visibility = View.VISIBLE
                        }
                        var j = 0
                        pendingRequests[it.key]?.forEach { applyChange ->
                            i += 1
//                        println("adapter position: ${applyChange.holder.adapterPosition} item position: ${applyChange.position}")
                            if (applyChange.holder.adapterPosition == applyChange.position) {
                                j += 1
                                applyChange.holder.imageView.setImageBitmap(downloadedImage)
                                applyChange.holder.imageView.visibility = View.VISIBLE
                            }
                        }

//                    println("Total Updates for ${contactList[it.value.position]} is $i actual updates: $j")
                        pendingRequests[it.key] = mutableListOf()
                    }
                } catch (e: Exception) {
                    ongoingDownloads[it.key] = false
                }
            }.start()
        }
    }

    private fun check(position: Int,holder: ImageHolder,imageUrl: String){
        synchronized(positionList){
            if(position !in positionList){
                positionList.add(position)
                pendingRequests[imageUrl]?.add(HolderWithPosition(holder,position))
            }
        }
    }
}