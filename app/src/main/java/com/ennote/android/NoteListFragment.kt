package com.ennote.android

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class NoteListFragment : Fragment() {

    interface Callbacks {
        fun onNoteSelected(noteId: UUID, isEncrypted: Boolean)
    }

    private var callbacks: Callbacks? = null

    private lateinit var noteRecyclerView: RecyclerView
    private lateinit var floatingActionButton: FloatingActionButton
    private var adapter: NoteAdapter? = NoteAdapter(emptyList())

    private val noteListViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(NoteListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_list, container, false)

        noteRecyclerView = view.findViewById(R.id.note_recycler_view) as RecyclerView
        noteRecyclerView.layoutManager = LinearLayoutManager(context)
        noteRecyclerView.adapter = adapter

        floatingActionButton =
            view.findViewById(R.id.floating_action_button) as FloatingActionButton

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteListViewModel.noteListLiveData.observe(viewLifecycleOwner, Observer { notes ->
            notes?.let {
                updateUI(notes)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        floatingActionButton.setOnClickListener {
            //Create new note
            val note = Note()
            noteListViewModel.addNote(note)
            callbacks?.onNoteSelected(note.id, false)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    companion object {
        fun newInstance(): NoteListFragment {
            return NoteListFragment()
        }
    }

    private inner class NoteHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var note: Note

        init {
            itemView.setOnClickListener(this)
        }

        private val titleTextView: TextView = itemView.findViewById(R.id.note_title)
        private val encryptedImageView: ImageView = itemView.findViewById(R.id.note_encrypted)

        fun bind(note: Note) {
            this.note = note
            titleTextView.text = this.note.title
            encryptedImageView.visibility = if (note.isEncrypted) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View) {
            callbacks?.onNoteSelected(note.id, note.isEncrypted)
        }
    }

    private inner class NoteAdapter(var notes: List<Note>) : RecyclerView.Adapter<NoteHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
            val view = layoutInflater.inflate(R.layout.list_item_note, parent, false)
            return NoteHolder(view)
        }

        override fun onBindViewHolder(holder: NoteHolder, position: Int) {
            val note = notes[position]
            holder.bind(note)
        }

        override fun getItemCount() = notes.size
    }

    private fun updateUI(notes: List<Note>) {
        adapter = NoteAdapter(notes)
        noteRecyclerView.adapter = adapter
    }
}