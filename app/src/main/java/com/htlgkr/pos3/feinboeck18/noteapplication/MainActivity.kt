package com.htlgkr.pos3.feinboeck18.noteapplication

import android.content.Context
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.htlgkr.pos3.feinboeck18.noteapplication.note.Note
import com.htlgkr.pos3.feinboeck18.noteapplication.note.NoteAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    private var list: ArrayList<Note> = ArrayList()
    private var file = File("savedNotes.txt")
    private lateinit var listAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadNotes()
        this.listAdapter = NoteAdapter(this, R.layout.note_layout, list)
        notesList.adapter = listAdapter
        listAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_items, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_save -> saveNotes()
            R.id.toolbar_newNote -> createANewNote()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.contextmenu_items, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val adapterView: AdapterView.AdapterContextMenuInfo = item.menuInfo as AdapterView.AdapterContextMenuInfo
        when(item.itemId) {
            R.id.contextMenu_Delete -> deleteItem(adapterView.position)
            R.id.contextMenu_Details -> showDetailsOfItem(adapterView.position)
            R.id.contextMenu_Edit -> editItem(adapterView.position)
        }
        return super.onContextItemSelected(item)
    }

    private fun saveNotes() {
        val writer = PrintWriter(openFileOutput(file.name, Context.MODE_PRIVATE))
        list.forEach { note -> writer.println("${note.noteContent};${note.dateTime}") }
        writer.close()
    }

    private fun loadNotes() {
        list.clear()

        if(!file.exists()) return

        val reader = BufferedReader(InputStreamReader(openFileInput(file.name)))
        var currentLine: String = reader.readLine()

        while (currentLine != null) {
            val args = currentLine.split(";")
            list.add(Note(args[0], LocalDateTime.parse(args[1])))
            currentLine = reader.readLine()
        }
    }

    private fun editItem(position: Int) {
        val currentNote: Note = list[position]
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        val alertLayout = setupAlertLayout()
        val newNoteText: EditText = alertLayout.findViewById(R.id.dialog_note_content)

        alertDialog.setTitle("Note Editor")
                .setView(alertLayout)
                .setPositiveButton("Confirm Changes") { _ , _ ->
                    currentNote.noteContent = newNoteText.text.toString()
                    currentNote.dateTime = LocalDateTime.now()
                    listAdapter.notifyDataSetChanged()
                }
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun createANewNote() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        val alertLayout = setupAlertLayout()
        val newNoteText: EditText = alertLayout.findViewById(R.id.dialog_note_content)

        alertDialog.setTitle("Create a new Note")
                .setView(alertLayout)
                .setPositiveButton("Create Note") { _, _ ->
                    val note = Note(newNoteText.text.toString(), LocalDateTime.now())
                    list.add(note)
                    listAdapter.notifyDataSetChanged()
                }
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun showDetailsOfItem(position: Int) {
        val alertLayout = LinearLayout(applicationContext)
        val text = TextView(applicationContext)
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)

        alertLayout.orientation = LinearLayout.VERTICAL
        text.text = list[position].noteContent
        alertLayout.addView(text)

        alertDialog.setTitle("Detailed Note")
                .setView(alertLayout)
                .setNeutralButton("Cancel", null)
        alertDialog.show()
    }

    private fun deleteItem(position: Int) {
        list.removeAt(position)
        listAdapter.notifyDataSetChanged()
    }

    private fun setupAlertLayout(): LinearLayout {
        val layout = LinearLayout(applicationContext)
        val noteContent = EditText(applicationContext)

        layout.orientation = LinearLayout.VERTICAL

        noteContent.id = R.id.dialog_note_content
        layout.addView(noteContent)

        return layout
    }
}