package com.ennote.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import java.util.*

private const val TAG = "NoteFragment"
private const val ARG_NOTE_ID = "note_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val REQUEST_IMAGE = 1

class NoteFragment : Fragment(), DatePickerFragment.Callbacks {

    interface Callbacks {
        fun onDeleteNoteSelected(note: Note)
    }

    private var callbacks: Callbacks? = null

    private lateinit var note: Note
    private lateinit var titleField: EditText
    private lateinit var endDateButton: Button
    private lateinit var encryptionCheckBox: CheckBox
    private lateinit var noteContent: EditText
    private lateinit var addImageButton: Button
    private lateinit var removeImageButton: Button
    private lateinit var imageView: ImageView

    private val noteDetailViewModel: NoteDetailViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(NoteDetailViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setHasOptionsMenu(true)
        note = Note()
        val noteId: UUID = arguments?.getSerializable(ARG_NOTE_ID) as UUID
        noteDetailViewModel.loadNote(noteId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note, container, false)

        titleField = view.findViewById(R.id.note_title) as EditText
        endDateButton = view.findViewById(R.id.end_date) as Button
        encryptionCheckBox = view.findViewById(R.id.encrypted) as CheckBox
        noteContent = view.findViewById(R.id.note_content) as EditText
        addImageButton = view.findViewById(R.id.add_image) as Button
        removeImageButton = view.findViewById(R.id.remove_image) as Button
        imageView = view.findViewById(R.id.imageView) as ImageView

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        noteDetailViewModel.noteLiveData.observe(viewLifecycleOwner, { note ->
            note?.let {
                this.note = note
                updateUI()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //intentionally blank
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                note.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                //intentionally blank
            }
        }
        titleField.addTextChangedListener(titleWatcher)

        encryptionCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                note.isEncrypted = isChecked
            }
        }

        val contentWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //intentionally blank
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                note.content = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                //intentionally blank
            }
        }
        noteContent.addTextChangedListener(contentWatcher)

        endDateButton.setOnClickListener {
            DatePickerFragment.newInstance(note.endDate).apply {
                setTargetFragment(this@NoteFragment, REQUEST_DATE)
                show(this@NoteFragment.parentFragmentManager, DIALOG_DATE)
            }
        }

        addImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE)
        }

        removeImageButton.setOnClickListener {
            note.imageUri = null
            noteDetailViewModel.saveNote(note)
            imageView.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
        noteDetailViewModel.saveNote(note)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_note, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_note -> {
                noteDetailViewModel.deleteNote(note)
                callbacks?.onDeleteNoteSelected(note)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        note.imageUri = uri
                    }
                    noteDetailViewModel.saveNote(note)
                    Log.d(TAG, "noteDetailViewModel.saveNote(note)")
                }
            }
        }
    }

    override fun onDateSelected(date: Date) {
        note.endDate = date
        updateUI()
    }

    private fun updateUI() {
        titleField.setText(note.title)
        endDateButton.text = dateToString(note.endDate)
        encryptionCheckBox.isChecked = note.isEncrypted
        noteContent.setText(note.content)
        if (note.imageUri != null) {
            context?.let { Glide.with(it).load(note.imageUri).into(imageView) }
        }
    }

    private fun dateToString(date: Date): String {
        Log.d("dateToString", "$date")
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${
            calendar.get(
                Calendar.DAY_OF_MONTH
            )
        }"
    }

    companion object {

        fun newInstance(noteId: UUID): NoteFragment {
            val args = Bundle().apply {
                putSerializable(ARG_NOTE_ID, noteId)
            }
            return NoteFragment().apply {
                arguments = args
            }
        }
    }
}