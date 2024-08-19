package com.example.bitmaploadingandcaching.recyclerview

import android.content.Context
import android.graphics.Bitmap
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
import androidx.collection.LruCache
import androidx.lifecycle.MutableLiveData
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

class ImagesAdapter(private var context: Context):RecyclerView.Adapter<ImagesAdapter.ImageHolder>(){

    private var searchQuery = ""
    private var contactList:MutableList<Contact> = mutableListOf()
    private var bitmapCache = CacheData.bitmapCache
    private var ongoingDownloads = ConcurrentHashMap<String,Boolean>()
    private var positionList:MutableList<Int> = mutableListOf()
    private var pendingRequests = ConcurrentHashMap<String,MutableList<HolderWithPosition>>()
    private var pauseDownload = ConcurrentHashMap<String, HolderWithPosition>()
    private val handler = Handler(Looper.getMainLooper())
    private var networkLost = true
    private var updateUI = MutableLiveData(false)
    private var size = 0
    private var pos = 0
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.contactName.text = contactList[holder.absoluteAdapterPosition].name
        holder.profileName.text = contactList[holder.absoluteAdapterPosition].name[0].toString().uppercase()
        searchQuery = HomeFragment.queryReceived
//        println("ON Bind View Holder Called")
        updateSearchedView(holder)
        holder.callButton.setOnClickListener{
            Toast.makeText(context,"Can't Make a Call Please insert a SIM",Toast.LENGTH_SHORT).show()
        }
        holder.profileName.background.setTint(contactList[holder.absoluteAdapterPosition].contactColor)
        holder.mobileNumber.text = contactList[holder.absoluteAdapterPosition].contactNumber
        if(!contactList[holder.absoluteAdapterPosition].isUri){
            loadImage(holder,position,contactList[position].image)
        }
        else{
            println("IMAGE URI: ${contactList[holder.absoluteAdapterPosition].image}")
            if(holder.absoluteAdapterPosition == position){
                holder.imageView.setImageURI(Uri.parse(contactList[holder.absoluteAdapterPosition].image))
                holder.imageView.visibility = View.VISIBLE
            }
        }
    }



    fun resetViews(newList:List<Contact>, query:String?){
        println("On Reset View")
        val contactDiff = ContactsDiffUtil(contactList, newList)
        val diffResult = DiffUtil.calculateDiff(contactDiff)
        contactList.clear()
        contactList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
        if(query!=null){
            if(query.isNotEmpty()){
//                updateUI.value = true
                println("Search is Happened")
                notifyItemRangeChanged(0,newList.size)
            }
            else{
                println("Search is Happened IN Else")
                notifyItemRangeChanged(0,newList.size)
            }
        }
    }


    private fun updateSearchedView(holder: ImageHolder) {
        println("ON UPDATE SEARCH")
        val position = holder.absoluteAdapterPosition
        val length = searchQuery.length
        if(contactList[position].isHighlighted && searchQuery.isNotEmpty()){
//            For Name
            var startIndex = contactList[position].name.indexOf(searchQuery, ignoreCase = true)
            val highlightedName = SpannableString(contactList[position].name)
            while (startIndex>=0){
                val endIndex = startIndex+length
                highlightedName.setSpan(
                    ForegroundColorSpan(Color.argb(255,255,20,20)), // You can choose any color
                    startIndex,
                    endIndex,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                startIndex = contactList[position].name.indexOf(searchQuery,endIndex, ignoreCase = true)
            }

//            For Mobile Number
            var startIndexMobile = contactList[position].contactNumber.indexOf(searchQuery, ignoreCase = true)
            val highlightedMobile = SpannableString(contactList[position].contactNumber)
            while (startIndexMobile>=0){
                val endIndex = startIndex+length
                highlightedMobile.setSpan(
                    ForegroundColorSpan(Color.argb(255,255,20,20)), // You can choose any color
                    startIndexMobile,
                    endIndex,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                startIndexMobile = contactList[position].name.indexOf(searchQuery,endIndex, ignoreCase = true)
            }
            holder.contactName.text = highlightedName
            holder.mobileNumber.text = highlightedMobile
        }
    }



    private fun loadImage(holder: ImageHolder, position: Int, imageUrl: String){
        println("IN ELSE Load Image Called $position")
        if((bitmapCache.get(imageUrl)==null) && (ongoingDownloads[imageUrl]!=true)) {
            holder.imageView.visibility = View.INVISIBLE
            Thread {
                var name = contactList[holder.absoluteAdapterPosition].name
//                println("Thread Name: ${Thread.currentThread().id}  Task: ${holder.contactName.text}")
                try {
                    if (!networkLost) {
                        println("Download : $name ${Thread.currentThread().id} Started")
                        ongoingDownloads[imageUrl] = true
                        val url = URL(imageUrl).openConnection()
                        url.connect()
                        val inputStream = url.getInputStream()
                        val downloadedImage = BitmapFactory.decodeStream(inputStream)
                        inputStream.close()
                        println("Download : $name ${Thread.currentThread().id} Finished")
                        bitmapCache.put(imageUrl, downloadedImage)
                        handler.post {
                            var i = 0
                            println("updateding at Position: ${holder.absoluteAdapterPosition} actual pos: $position")
                            if (holder.absoluteAdapterPosition == position) {
                                i += 1
                                holder.imageView.setImageBitmap(downloadedImage)
                                holder.imageView.visibility = View.VISIBLE
                            }
                            pendingRequests[imageUrl]?.forEach {
                                i += 1
//                                println("adapter position: ${it.holder.adapterPosition} item position: ${it.position}")
                                if (it.holder.absoluteAdapterPosition == it.position) {
                                    it.holder.imageView.setImageBitmap(downloadedImage)
                                    it.holder.imageView.visibility = View.VISIBLE
                                }
                            }
                            pendingRequests[imageUrl] = mutableListOf()
                        }
                    } else {
                        pauseDownload[imageUrl] = HolderWithPosition(holder, position)
                        if (pendingRequests[imageUrl].isNullOrEmpty()) {
                            pendingRequests[imageUrl] = mutableListOf()
                        }
                        if (position !in positionList) {
                            positionList.add(position)
                            pendingRequests[imageUrl]?.add(HolderWithPosition(holder, position))
                        }
                    }

                } catch (e: UnknownHostException) {
                    println("In UnKnown Host Exception $name ${Thread.currentThread().id}")
//                    Adding the Pause Download if any Network Interrupt Happens
                    pauseDownload[imageUrl] = HolderWithPosition(holder, position)
                    if (pendingRequests[imageUrl].isNullOrEmpty()) {
                        pendingRequests[imageUrl] = mutableListOf()
                    }
                    if (position !in positionList) {
                        positionList.add(position)
                        pendingRequests[imageUrl]?.add(HolderWithPosition(holder, position))
                    }
                } catch (e: MalformedURLException) {
                    println("Download Error while Downloading : $name ${Thread.currentThread().id}")
                    ongoingDownloads[imageUrl] = true
                } catch (e: Exception) {
                    println("IN Catch $e $name ${Thread.currentThread().id}")
                    ongoingDownloads[imageUrl] = false
                }
            }.start()
        }

        else if((bitmapCache.get(imageUrl)==null) && (ongoingDownloads[imageUrl]==true)){
            if(pendingRequests[imageUrl].isNullOrEmpty()){
                pendingRequests[imageUrl] = mutableListOf()
            }
            if(position !in positionList){
                positionList.add(position)
                pendingRequests[imageUrl]?.add(HolderWithPosition(holder,position))
            }
//            pendingRequests[imageUrl]?.add(HolderWithPosition(holder,position))
            holder.imageView.visibility = View.INVISIBLE
        }
        else{
            holder.imageView.setImageBitmap(bitmapCache.get(imageUrl))
            if(holder.imageView.visibility != View.VISIBLE){
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
}