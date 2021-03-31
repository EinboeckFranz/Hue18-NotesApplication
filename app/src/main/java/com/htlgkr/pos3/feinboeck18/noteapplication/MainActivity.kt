package com.htlgkr.pos3.feinboeck18.noteapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.htlgkr.pos3.feinboeck18.noteapplication.note.Note
import com.htlgkr.pos3.feinboeck18.noteapplication.note.NoteAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.note_layout.*
import java.io.*
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    private var list: ArrayList<Note> = ArrayList()
    private var file = File("savedNotes.csv")
    private lateinit var listAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.listAdapter = NoteAdapter(this, R.layout.note_layout, list)
        notesList.adapter = listAdapter
        loadNotes()
        listAdapter.notifyDataSetChanged()

        registerForContextMenu(notesList)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        if(v?.id == R.id.notesList)
            menuInflater.inflate(R.menu.contextmenu_items, menu)
        return super.onCreateContextMenu(menu, v, menuInfo)
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

    private fun saveNotes() {
        val writer = PrintWriter(openFileOutput(file.name, Context.MODE_PRIVATE))

        list.forEach { note -> writer.println("${note.noteContent};${note.dateTime}") }
        writer.close()
    }

    private fun loadNotes() {
        list.clear()

        try {
            val reader = BufferedReader(InputStreamReader(openFileInput(file.name)))
            reader.lines()
                    .forEach { line ->
                val args = line.split(";")
                list.add(Note(args[0], LocalDateTime.parse(args[1])))
            }
            Log.d("ListSize", list.size.toString())
        } catch (exception: FileNotFoundException) {
            Toast.makeText(this,"No Savesate found!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createANewNote() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        val alertLayout = setupAlertLayout("")
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
        saveNotes()
    }

    private fun editItem(position: Int) {
        val currentNote: Note = list[position]
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        val alertLayout = setupAlertLayout(list[position].noteContent)
        val newNoteText: EditText = alertLayout.findViewById(R.id.dialog_note_content)

        alertDialog.setTitle("Note Editor")
                .setView(alertLayout)
                .setPositiveButton("Confirm Changes") { _ , _ ->
                    currentNote.noteContent = newNoteText.text.toString()
                    currentNote.dateTime = LocalDateTime.now()
                    listAdapter.notifyDataSetChanged()
                    saveNotes()
                }
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun showDetailsOfItem(position: Int) {
        val alertLayout = setupAlertLayout(list[position].noteContent)
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)

        alertDialog.setTitle("Detailed Note")
                .setView(alertLayout)
                .setNeutralButton("Cancel", null)
        alertDialog.show()
    }

    private fun deleteItem(position: Int) {
        list.removeAt(position)
        listAdapter.notifyDataSetChanged()
        saveNotes()
    }

    private fun setupAlertLayout(currentNoteContent: String): LinearLayout {
        val layout = LinearLayout(applicationContext)
        val noteContent = EditText(applicationContext)

        layout.orientation = LinearLayout.VERTICAL

        noteContent.id = R.id.dialog_note_content
        if(currentNoteContent != "") noteContent.hint = currentNoteContent
        layout.addView(noteContent)

        return layout
    }
}