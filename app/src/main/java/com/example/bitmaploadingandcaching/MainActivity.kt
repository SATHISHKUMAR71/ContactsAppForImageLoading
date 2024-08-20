package com.example.bitmaploadingandcaching


import android.Manifest
import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.ActionMenuView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import androidx.transition.Visibility
import com.example.bitmaploadingandcaching.fragments.HomeFragment
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.search.SearchBar
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class MainActivity : AppCompatActivity() {


    companion object{
        private val REQUEST_RECORD_AUDIO_PERMISSION = 200
        private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
        var recordAudioPermission = false
        private var permissionToRecordAccepted = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

//    Permission Handler
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            recordAudioPermission = permissionToRecordAccepted
        }
    }

}