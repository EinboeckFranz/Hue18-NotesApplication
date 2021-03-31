package com.htlgkr.pos3.feinboeck18.noteapplication.note

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.htlgkr.pos3.feinboeck18.noteapplication.R

class NoteAdapter(applicationContext: Context, private val layoutID: Int, private val notesList: List<Note>): BaseAdapter() {
    private val layoutInflater: LayoutInflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return notesList.size
    }

    override fun getItem(position: Int): Note {
        return notesList[position]
    }

    override fun getItemId(itemPosition: Int): Long {
        return 0
    }

    override fun getView(pos: Int, currentView: View?, p2: ViewGroup?): View {
        val view: View = currentView ?: layoutInflater.inflate(this.layoutID, null)
        view.findViewById<TextView>(R.id.noteListItem).text = notesList[pos].toString()
        return view
    }
}