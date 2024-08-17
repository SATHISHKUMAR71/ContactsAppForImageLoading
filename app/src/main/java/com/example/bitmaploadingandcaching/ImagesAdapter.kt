package com.example.bitmaploadingandcaching

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.collection.LruCache
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay
import java.net.MalformedURLException
import java.net.URL
import java.net.UnknownHostException
import java.sql.Time
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ImagesAdapter(private var context: Context):RecyclerView.Adapter<ImagesAdapter.ImageHolder>(){

    private var searchQuery = ""
    private var contactList:MutableList<Contact> = mutableListOf()
    private var bitmapCache = LruCache<String,Bitmap>((Runtime.getRuntime().maxMemory()/4).toInt())
    private var ongoingDownloads = ConcurrentHashMap<String,Boolean>()
    private var positionList:MutableList<Int> = mutableListOf()
    private var pendingRequests = ConcurrentHashMap<String,MutableList<HolderWithPosition>>()
    private var pauseDownload = ConcurrentHashMap<String,HolderWithPosition>()
    private val handler = Handler(Looper.getMainLooper())
    private var networkLost = true
    private var threadPoolExecutor = ThreadPoolExecutor(10,20,2,TimeUnit.SECONDS,LinkedBlockingQueue())
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
        holder.profileName.text = contactList[holder.absoluteAdapterPosition].name[0].toString()
        searchQuery = MainActivity.queryReceived

//        holder.itemView.setOnClickListener{
//            val list = contactList.toMutableList()
//            list.removeAt(holder.absoluteAdapterPosition)
//            resetViews(list)
//        }
        holder.callButton.setOnClickListener{
            Toast.makeText(context,"Can't Make a Call Please insert a SIM",Toast.LENGTH_SHORT).show()
        }
        holder.profileName.background.setTint(contactList[holder.absoluteAdapterPosition].contactColor)
        holder.mobileNumber.text = contactList[holder.absoluteAdapterPosition].contactNumber
        loadImage(holder,position,contactList[position].image)
    }



    fun resetViews(newList:List<Contact>,query:String?){
        println("On Reset View")
        val contactDiff = ContactsDiffUtil(contactList, newList)
        val diffResult = DiffUtil.calculateDiff(contactDiff)
        contactList.clear()
        contactList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
        if(query!=null){
            if(query.isNotEmpty()){
                println("Search is Happened")
            }
            return
        }
    }



    private fun loadImage(holder: ImageHolder,position: Int,imageUrl: String){
        if((bitmapCache.get(imageUrl)==null) && (ongoingDownloads[imageUrl]!=true)){
            holder.imageView.visibility = View.INVISIBLE
//            Thread{
            threadPoolExecutor.execute {
//                println("Thread Name: ${Thread.currentThread().id}  Task: ${holder.contactName.text}")
                try {
                    if (!networkLost) {
                        println("Download Started: ${contactList[position].name} ${Thread.currentThread().id}")
                        ongoingDownloads[imageUrl] = true
                        val url = URL(imageUrl).openConnection()
                        url.connect()
                        val inputStream = url.getInputStream()
                        val downloadedImage = BitmapFactory.decodeStream(inputStream)
                        inputStream.close()
                        println("Download Finished: ${contactList[position].name} ${Thread.currentThread().id}")
                        bitmapCache.put(imageUrl, downloadedImage)
                        handler.post {
                            var i = 0
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
                    println("In UnKnown Host Exception")
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
                    println("Download Error while Downloading : ${contactList[position].name}")
                    ongoingDownloads[imageUrl] = true
                } catch (e: Exception) {
                    println("IN Catch $e")
                    ongoingDownloads[imageUrl] = false
                }
            }
//            }.start()
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
            holder.imageView.visibility = View.VISIBLE
        }
    }


    fun downloadPendingRequests() {

//        println("Pause Download Value: $pauseDownload")
//        println("Pause Download Size: ${pauseDownload.size}")
//        println("Pending Request Size: ${pendingRequests.size}")
//        pendingRequests.forEach{
//            println("${it.key} size: ${it.value.size}")
//        }
        pauseDownload.forEach {
//            Thread{
            threadPoolExecutor.execute {
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
            }
//        }.start()
        }
    }
}