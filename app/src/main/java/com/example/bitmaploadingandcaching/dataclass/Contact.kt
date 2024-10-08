package com.example.bitmaploadingandcaching.dataclass

data class Contact (val name:String,val image:String,val contactColor:Int,val contactNumber:String,val contactType:String,val isHighlighted:Boolean,val isUri:Boolean,val isBitmap:Boolean,val date:MutableMap<Int,LabelData>)