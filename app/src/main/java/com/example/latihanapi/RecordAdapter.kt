package com.example.latihanapi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class RecordAdapter(val arrayList: ArrayList<RecordModel>, val context: Context)
    : RecyclerView.Adapter<RecordAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val layoutAdapter  = itemView.findViewById<LinearLayout>(R.id.recordlayout_container)//recordlayout_container
        val posAdapter     = itemView.findViewById<TextView>(R.id.recordlayout_pos)//recordlayout_pos
        val idAdapter      = itemView.findViewById<TextView>(R.id.recordlayout_id)//recordlayout_id
        val nameAdapter    = itemView.findViewById<TextView>(R.id.recordlayout_name)//recordlayout_name
        val companyAdapter = itemView.findViewById<TextView>(R.id.recordlayout_company)//recordlayout_company
        val editAdapter    = itemView.findViewById<ImageView>(R.id.recordlayout_edit)
        val removeAdapter  = itemView.findViewById<ImageView>(R.id.recordlayout_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.record_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrayList[position]
        holder.posAdapter.text      = (position+1).toString() //position start from 0
        holder.idAdapter.text       = "(id = " + item.id + ")"
        holder.nameAdapter.text     = item.name
        holder.companyAdapter.text  = item.company_name

        if(position%2 == 0) {
            holder.layoutAdapter.setBackgroundColor(ContextCompat.getColor(context,R.color.purple_50))
        }
        else{
            holder.layoutAdapter.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }

        holder.editAdapter.setOnClickListener {
            if(context is MainActivity){context.updateRecord(item!!)}
        }

        holder.removeAdapter.setOnClickListener {
            if(context is MainActivity){context.deleteRecord(item!!)}
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}