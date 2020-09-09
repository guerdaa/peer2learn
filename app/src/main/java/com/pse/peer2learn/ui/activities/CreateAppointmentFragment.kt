package com.pse.peer2learn.ui.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Appointment
import com.pse.peer2learn.models.Category
import com.pse.peer2learn.ui.activities.interfaces.ICreateAppointmentListener
import com.pse.peer2learn.ui.activities.interfaces.IDialogDismissListener
import com.pse.peer2learn.ui.viewmodels.CreateAppointementViewModel
import com.pse.peer2learn.utils.Utils
import kotlinx.android.synthetic.main.fragment_create_appointement.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * This Fragment is used to create/edit/delete appointments or categories inside a group
 */
class CreateAppointmentFragment : BottomSheetDialogFragment(), ICreateAppointmentListener {

    private lateinit var createAppointementViewModel: CreateAppointementViewModel
    /**
     * Used for the creation of the Fragment without any dataloss.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_appointement, container, false)
    }
    /**
     * This observes the required LiveData and sets up the listeners
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createAppointementViewModel = ViewModelProvider(this).get(CreateAppointementViewModel::class.java)
        if (currentCategory != null) {
            category_name_edit_text.setText(currentCategory?.name)
            appendAppointments()
        }
        //Starts to observe if there is any change in the createLiveData
        createAppointementViewModel.createLiveData.observe(viewLifecycleOwner, Observer {newValue ->
           createAppointementViewModel.observeLiveData(newValue, this)
        })
        //Starts to observe if there is any change in the updateLiveData
        createAppointementViewModel.updateLiveData.observe(viewLifecycleOwner, Observer {newValue ->
            createAppointementViewModel.observeLiveData(newValue, this)
        })
        add_appointment.setOnClickListener(addClickListener)
        submit_button.setOnClickListener(submitClickListener)
    }

    private val addClickListener = View.OnClickListener {
        add_appointment_layout.visibility = View.VISIBLE
        add_appointment.visibility = View.GONE
        selectDate()
        selectTime()
    }

    private val submitClickListener = View.OnClickListener {
        var appointment: Appointment? = null
        if (appointment_name_edit_text.text.trim().isNotEmpty()) {
            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
            formatter.isLenient = false
            val dateString = "${date_text_view.text} ${time_text_view.text}"
            val date = formatter.parse(dateString)
            val dateInMillis: Long = date!!.time
            appointment =
                Appointment(appointment_name_edit_text.text.trim().toString(), dateInMillis)
            if(dateInMillis < System.currentTimeMillis()) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.date_in_the_past), Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
        } else if(add_appointment_layout.visibility == View.VISIBLE && appointment_name_edit_text.text.trim().isEmpty()) {
            return@OnClickListener
        }
        submit(appointment)
    }

    /**
     * Method called when the user click on the submit button. Check whether it should update or created an new category
     */
    private fun submit(appointment: Appointment?) {
        if (currentCategory == null) {
            createCategory(appointment)
        } else {
            updateCategory(appointment)
        }

    }

    /**
     * help method used to create the category and an [appointment] to the category
     */
    private fun createCategory(appointment: Appointment?) {
        if (validate()) {
            currentCategory = Category(
                "",
                category_name_edit_text.text.trim().toString().toUpperCase(),
                currentGroupId,
                arrayListOf()
            )
            appointment?.let {
                currentCategory!!.appointments.add(it)
            }
            createAppointementViewModel.createCategory(currentCategory!!)
        }
    }

    /**
     * help method used to update the category and adding an [appointment] to the category
     */
    private fun updateCategory(appointment: Appointment?) {
        if (validate()) {
            currentCategory?.name = category_name_edit_text.text.trim().toString().toUpperCase()
            appointment?.let {
                currentCategory!!.appointments.add(it)
            }
            createAppointementViewModel.updateCategory(currentCategory!!)
        }
    }

    /**
     *Help method that shows the appointments in [currentCategory] and allows to delete an appointment on hold
     */
    private fun appendAppointments() {
        currentCategory?.let { category ->
            category.appointments.forEach { appointment ->
                val appointmentView = TextView(requireContext())
                //deletes an appointment on hold
                appointmentView.setOnLongClickListener {
                    deleteAppointment(appointment)
                }
                appointmentView.textSize = 20.0f
                appointmentView.text = "${appointment.title}  ${Utils.convertDate(appointment.date)}"
                appointments_holder.addView(appointmentView)
            }
        }
    }

    /**
     * Deletes an [appointment]
     */
    private fun deleteAppointment(appointment: Appointment): Boolean {
        currentCategory!!.appointments.remove(appointment)
        appointments_holder.removeAllViews()
        appendAppointments()
        return true
    }

    /**
     * Checks if a category name is set
     * @return true if the category name isn't empty and false otherwise
     */
    private fun validate(): Boolean {
        return if(category_name_edit_text.text.trim().isNotEmpty()) {
            submit_button.isEnabled = false
            submit_button.alpha = 0.3f
            true
        } else {
            false
        }
    }

    /**
     * Get the time in SimpleDateFormat that the user picked of a TimePicker, and opens the TimePickerDialog when the user clicks on the textView
     */
    @SuppressLint("SimpleDateFormat")
    private fun selectTime(){
        val cal = Calendar.getInstance()
        time_text_view.text = SimpleDateFormat("HH:mm").format(cal.time)
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            time_text_view.text = SimpleDateFormat("HH:mm").format(cal.time)
        }

        time_text_view.setOnClickListener {
            TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
    }
    /**
     * Get the date in SimpleDateFormat that the user picked of a DatePicker, and opens the DatePickerDialog when the user clicks on the textView
     */
    @SuppressLint("SimpleDateFormat")
    private fun selectDate(){
        val cal = Calendar.getInstance()
        date_text_view.text = SimpleDateFormat("dd.MM.YYYY").format(Date())
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, day)
            date_text_view.text = SimpleDateFormat("dd.MM.YYYY").format(cal.time)
        }
        date_text_view.setOnClickListener {
            DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    /**
     * Closes the dialog when the user clicks outside of it
     */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        currentDismissListener.dismissListener()
    }

    companion object {
        const val TAG = "CreateAppointementFragment"
        private var currentCategory: Category? = null
        private var currentGroupId = ""
        private lateinit var currentDismissListener: IDialogDismissListener

        @JvmStatic
        fun newInstance(category: Category?, groupId: String, dismissListener: IDialogDismissListener) =
            CreateAppointmentFragment().apply {
                currentCategory = category
                currentGroupId = groupId
                currentDismissListener = dismissListener
            }
    }

    override fun dismissDialog() {
        dismiss()
    }
}