package com.example.final_project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class RecordAdapter(context: Context, private val records: List<String>) : ArrayAdapter<String>(context, 0, records) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_record, parent, false)


        val textView = view.findViewById<TextView>(R.id.tvRecord)
        val imageView = view.findViewById<ImageView>(R.id.imageView2)

        textView.text = records[position]
        imageView.setImageResource(R.drawable.ic_garbage)

        return view
    }
}