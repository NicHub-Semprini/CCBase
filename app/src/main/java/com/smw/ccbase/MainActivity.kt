package com.smw.ccbase

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.smw.ccbase.model.BaseCard
import com.smw.ccbase.util.SupabaseUtils
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Retrieve saved user
        SupabaseUtils.loginEmail = this.getPreferences(MODE_PRIVATE)
            .getString(getString(R.string.pref_user_key), SupabaseUtils.DEFAULT_USER)
            ?: SupabaseUtils.DEFAULT_USER

        // Retrieve saved pwd
        SupabaseUtils.loginPassword = this.getPreferences(MODE_PRIVATE)
            .getString(getString(R.string.pref_pwd_key), SupabaseUtils.DEFAULT_PWD)
            ?: SupabaseUtils.DEFAULT_PWD

        // Try first login
        runBlocking {
            SupabaseUtils.login()
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatusButtons()
    }

    // Buttons

    fun onClickSettings(v: View?) {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Settings")
        alertDialogBuilder.setView(R.layout.dialog_settings)
        alertDialog = alertDialogBuilder.show()
        alertDialog?.setContentView(R.layout.dialog_settings)
        val emailTextView = alertDialog?.findViewById<TextView>(R.id.edit_text_email)
        emailTextView?.text = SupabaseUtils.loginEmail
        val passwordTextView = alertDialog?.findViewById<TextView>(R.id.edit_text_password)
        passwordTextView?.text = SupabaseUtils.loginPassword
    }

    fun onClickSave(v: View?) {
        SupabaseUtils.loginEmail =
            alertDialog?.findViewById<TextView>(R.id.edit_text_email)?.text.toString()
        SupabaseUtils.loginPassword =
            alertDialog?.findViewById<TextView>(R.id.edit_text_password)?.text.toString()
        alertDialog?.cancel()
        saveCredentials(SupabaseUtils.loginEmail, SupabaseUtils.loginPassword)
        SupabaseUtils.login()
        updateStatusButtons()
    }

    fun onClickCancel(v: View?) {
        alertDialog?.cancel()
    }

    fun onClickSearch(v: View?) {
        // Clear previous search results
        val resultsLayout = this.findViewById<LinearLayout>(R.id.results_layout)
        resultsLayout.removeAllViews()
        // Search new results
        val name = this.findViewById<EditText>(R.id.edit_text_name).text.toString()
        val set_code = this.findViewById<EditText>(R.id.edit_text_set_code).text.toString()
        val set_number = this.findViewById<EditText>(R.id.edit_text_set_number).text.toString()
        val results: List<BaseCard> =
            runBlocking { SupabaseUtils.searchCards(name, set_code, set_number) }
        // Display results
        results.forEach {
            // Parent layout
            val linearLayout = LinearLayout(resultsLayout.context)
            resultsLayout.addView(linearLayout)

            // Name
            val nameLayout = createResultView(linearLayout.context, it.name(), 0.2f)
            linearLayout.addView(nameLayout)

            // Set
            val setLayout = createResultView(linearLayout.context, it.setCode(), 0.6f)
            linearLayout.addView(setLayout)

            // Count
            val countLayout = createResultView(
                linearLayout.context, "${it.count()}", 0.7f, View.TEXT_ALIGNMENT_TEXT_END
            )
            linearLayout.addView(countLayout)
        }
    }

    // Utils

    private fun createResultView(
        context: Context,
        text: String,
        weight: Float,
        textAlignment: Int = View.TEXT_ALIGNMENT_TEXT_START
    ): View {
        val layout = RelativeLayout(context)
        val params = LinearLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.weight = weight
        layout.layoutParams = params
        val textView = TextView(layout.context)
        textView.text = text
        textView.textAlignment = textAlignment
        layout.addView(textView)

        return layout
    }

    private fun saveCredentials(user: String, pwd: String) {
        val preferences = this.getPreferences(Context.MODE_PRIVATE)
        with(preferences.edit()) {
            putString(getString(R.string.pref_user_key), user)
            putString(getString(R.string.pref_pwd_key), pwd)
            apply()
        }
    }

    private fun updateStatusButtons() {
        this.findViewById<RadioButton>(R.id.radio_button_db).isChecked = SupabaseUtils.dbOnline
        this.findViewById<RadioButton>(R.id.radio_button_user).isChecked = SupabaseUtils.userLogged
    }
}