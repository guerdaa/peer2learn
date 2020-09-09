package com.pse.peer2learn.utils.adapters

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Appointment
import com.pse.peer2learn.models.Category
import com.pse.peer2learn.ui.broadcasts.AppointmentBroadcastReceiver
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.Utils
import kotlinx.android.synthetic.main.category_item_layout.view.*
import kotlin.collections.ArrayList


abstract class CategoriesRecyclerViewAdapter(
    private val categoriesList: ArrayList<Category>,
    private val context: Context,
    private val isUserAdmin: Boolean
) : RecyclerView.Adapter<CategoriesRecyclerViewAdapter.CategoriesViewHolder>() {

    var clickedCategory: Category? = null

    /**
     * A method that is called when the RecycleView new ViewHolder of typ [Category] needs, to create this holder
     * Returns the new Categories ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item_layout, parent, false)
        return CategoriesViewHolder(view, context)
    }

    /**
     * Returns the number of elements in [categoriesList]
     */
    override fun getItemCount() = categoriesList.size

    /**
     * This method is called from RecyclerView to update the [holder] at the given [position]
     *
     */
    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.setCategory(categoriesList[position])
        holder.itemView.delete_image_view.setOnClickListener {
            clickedCategory = categoriesList[position]
            delete()
        }
        holder.itemView.edit_image_view.setOnClickListener {
            clickedCategory = categoriesList[position]
            edit()
        }

        holder.adjustUI(isUserAdmin)
    }

    abstract fun delete()
    abstract fun edit()

    class CategoriesViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

        private val currentRequestCode = context.getSharedPreferences(
            context.getString(R.string.request_preference),
            Context.MODE_PRIVATE
        )
            .getInt(Constants.REQUEST_CODE, 0)

        fun setCategory(category: Category) {
            itemView.category_name_text_view.text = category.name
            appendAppointments(category)
        }

        fun adjustUI(isUserAdmin: Boolean) {
            if(isUserAdmin) {
                itemView.edit_layout.visibility = View.VISIBLE
            } else {
                itemView.edit_layout.visibility = View.GONE
            }
        }

        /**
         * Sets an alarm on the phone as an reminder for an [appointment] that is inside the [category]
         */
        private fun setAlarm(appointment: Appointment, category: Category): Boolean {
            val alarmMgr: AlarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent =
                Intent(context, AppointmentBroadcastReceiver::class.java).let { intent ->
                    intent.putExtra(Constants.CATEGORY_NAME_EXTRA, category.name)
                    intent.putExtra(
                        Constants.APPOINTMENT_EXTRA,
                        "${appointment.title}: ${Utils.convertDate(appointment.date)}"
                    )
                    setNextRequestCode()
                    PendingIntent.getBroadcast(
                        context,
                        currentRequestCode + 1,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

            alarmMgr.set(
                AlarmManager.RTC_WAKEUP,
                appointment.date,
                alarmIntent
            )
            Toast.makeText(
                context,
                "${context.getText(R.string.alarm_set)}: ${Utils.convertDate(appointment.date)}",
                Toast.LENGTH_SHORT
            ).show()
            return true
        }

        private fun setNextRequestCode() {
            context.getSharedPreferences(
                context.getString(R.string.request_preference),
                Context.MODE_PRIVATE
            ).edit().putInt(Constants.REQUEST_CODE, currentRequestCode + 1).apply()
        }

        /**
         * Shows the appointments inside a [category]
         * Allows the user to set an alarm for a specific Appointment by holding his finger on the appointment
         */
        @SuppressLint("SetTextI18n")
        private fun appendAppointments(category: Category) {
            category.appointments.forEach { appointment ->
                val appointmentView = TextView(context)
                appointmentView.setOnLongClickListener {
                    Log.d("∞∞∞∞∞∞∞∞∞", appointment.title)
                    setAlarm(appointment, category)
                }
                appointmentView.text =
                    "${appointment.title}  ${Utils.convertDate(appointment.date)}"
                appointmentView.textSize = 20.0f
                itemView.appointments_holder.addView(appointmentView)
            }
        }
    }
}