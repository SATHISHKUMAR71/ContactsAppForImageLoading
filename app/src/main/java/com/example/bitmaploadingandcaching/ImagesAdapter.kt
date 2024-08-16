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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.collection.LruCache
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.delay
import java.net.MalformedURLException
import java.net.URL
import java.net.UnknownHostException
import java.sql.Time
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class ImagesAdapter(private var imageList:MutableList<Contact>,private var context: Context):RecyclerView.Adapter<ImagesAdapter.ImageHolder>(){

    private var i =0
    private var count =1
    private var bitmapCache = LruCache<String,Bitmap>((Runtime.getRuntime().maxMemory()/4).toInt())
    private var ongoingDownloads = ConcurrentHashMap<String,Boolean>()
    private var positionList:MutableList<Int> = mutableListOf()
    private var pendingRequests = ConcurrentHashMap<String,MutableList<HolderWithPosition>>()
    private var pauseDownload = ConcurrentHashMap<String,HolderWithPosition>()
    private val handler = Handler(Looper.getMainLooper())
    private var networkLost = true
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


    companion object{
        var COLORS_LIST = arrayOf(Color.argb(255,251,192,45),
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
    inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.contactImage)
        val contactName:TextView = itemView.findViewById(R.id.contactName)
        val profileName:TextView = itemView.findViewById(R.id.textName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.contactName.text = imageList[holder.adapterPosition].name
        holder.profileName.text = imageList[holder.adapterPosition].name[0].toString()
        holder.profileName.background.setTint(COLORS_LIST[Random.nextInt(0,10)])
        loadImage(holder,position,imageList[position].image)
    }



    private fun loadImage(holder: ImageHolder,position: Int,imageUrl: String){
        if((bitmapCache.get(imageUrl)==null) && (ongoingDownloads[imageUrl]!=true)){
            holder.imageView.visibility = View.INVISIBLE
            Thread{
                try{
                    if(!networkLost){
                        println("Download Started: ${imageList[position].name} ${Thread.currentThread().id}")
                        ongoingDownloads[imageUrl] = true
                        val url = URL(imageUrl).openConnection()
                        url.connect()
                        val inputStream = url.getInputStream()
                        val downloadedImage = BitmapFactory.decodeStream(inputStream)
                        inputStream.close()
                        println("Download Finished: ${imageList[position].name} ${Thread.currentThread().id}")
                        bitmapCache.put(imageUrl,downloadedImage)
                        handler.post {
                            var i = 0
                            if (holder.adapterPosition == position) {
                                i += 1
                                holder.imageView.setImageBitmap(downloadedImage)
                                holder.imageView.visibility = View.VISIBLE
                            }
                            pendingRequests[imageUrl]?.forEach {
                                i += 1
//                                println("adapter position: ${it.holder.adapterPosition} item position: ${it.position}")
                                if (it.holder.adapterPosition == it.position) {
                                    it.holder.imageView.setImageBitmap(downloadedImage)
                                    it.holder.imageView.visibility = View.VISIBLE
                                }
                            }
                            pendingRequests[imageUrl] = mutableListOf()
                        }
                    }

                    else{
                        pauseDownload[imageUrl] = HolderWithPosition(holder,position)
                        if(pendingRequests[imageUrl].isNullOrEmpty()){
                            pendingRequests[imageUrl] = mutableListOf()
                        }
                        if(position !in positionList){
                            positionList.add(position)
                            pendingRequests[imageUrl]?.add(HolderWithPosition(holder,position))
                        }
                    }

                }

                catch (e:UnknownHostException){
                    println("In UnKnown Host Exception")
//                    Adding the Pause Download if any Network Interrupt Happens
                    pauseDownload[imageUrl] = HolderWithPosition(holder,position)
                    if(pendingRequests[imageUrl].isNullOrEmpty()){
                        pendingRequests[imageUrl] = mutableListOf()
                    }
                    if(position !in positionList){
                        positionList.add(position)
                        pendingRequests[imageUrl]?.add(HolderWithPosition(holder,position))
                    }
                }

                catch (e:MalformedURLException){
                    println("Download Error while Downloading : ${imageList[position].name}")
                    ongoingDownloads[imageUrl] = true
                }

                catch (e:Exception){
                    println("IN Catch $e")
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
            holder.imageView.visibility = View.VISIBLE
        }
    }

    fun downloadPendingRequests() {

        println("Pause Download Value: $pauseDownload")
        println("Pause Download Size: ${pauseDownload.size}")
        println("Pending Request Size: ${pendingRequests.size}")
        pendingRequests.forEach{
            println("${it.key} size: ${it.value.size}")
        }
        pauseDownload.forEach {
            Thread{
            try{
//                println("Pause Download Started: ${imageList[it.value.position].name} ${Thread.currentThread().id}")
                ongoingDownloads[it.key] = true
                val url = URL(it.key).openConnection()
                url.connect()
                val inputStream = url.getInputStream()
                val downloadedImage = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
//                println("Pause Download Finished: ${imageList[it.value.position].name} ${Thread.currentThread().id}")
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
                        i+=1
//                        println("adapter position: ${applyChange.holder.adapterPosition} item position: ${applyChange.position}")
                        if (applyChange.holder.adapterPosition == applyChange.position) {
                            j+=1
                            applyChange.holder.imageView.setImageBitmap(downloadedImage)
                            applyChange.holder.imageView.visibility = View.VISIBLE
                        }
                    }

//                    println("Total Updates for ${imageList[it.value.position]} is $i actual updates: $j")
                    pendingRequests[it.key] = mutableListOf()
                }
            }
            catch (e:Exception){
                ongoingDownloads[it.key] = false
            }
        }.start()
        }
    }
}