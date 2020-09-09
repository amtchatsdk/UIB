package com.uib

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.unified.inbox.beans.StartEndTime
import com.unified.inbox.beans.UIBConfig
import com.unified.inbox.beans.WorkingHours
import com.unified.inbox.beans.WorkingTime
import com.unified.inbox.ui.UIBChatFragment
import com.unified.inbox.utils.UIBConstants
import kotlinx.android.synthetic.main.activity_main.*
import java.security.AccessController.getContext
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private var chatPreview: Switch? = null
    private var chatPreviewValue: Boolean? = false
    private var workingHoursPreview: Switch? = null
    private var workingHoursPreviewValue: Boolean? = false
    private var startTime: EditText? = null
    private var endTime: EditText? = null
    private var fabContact: FloatingActionButton? = null
    private var userId:String?=null
    private var PREF_NAME:String="USER_PREFERENCE"
    private var PRIVATE_MODE:Int=0

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chatPreview = findViewById(R.id.preview)
        workingHoursPreview = findViewById(R.id.working_hours_preview)
        startTime = findViewById(R.id.et_start_time)
        endTime = findViewById(R.id.et_end_time)
        fabContact = findViewById(R.id.fab_contact)

        val sharedPreferences: SharedPreferences=getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val editor = sharedPreferences.edit()

        if (sharedPreferences.getString("UserId",null)!=null){
                userId= sharedPreferences.getString("UserId",null)
            }else{
                userId= Settings.Secure.getString(contentResolver,
                    Settings.Secure.ANDROID_ID)
                editor?.putString("UserId",userId)
                editor?.apply()
            }

        Log.d("USER_ID","$userId")


        // val
        supportActionBar?.hide()
        chatPreview?.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            chatPreviewValue = compoundButton.isChecked
        }
        workingHoursPreview?.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            workingHoursPreviewValue = compoundButton.isChecked
        }

        startTime?.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                this@MainActivity,
                OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                    startTime?.setText(
                        String.format("%02d.%02d", selectedHour, selectedMinute)
                    )
                }, hour, minute, true
            ) //Yes 24 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }

        endTime?.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                this@MainActivity,
                OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                    endTime?.setText(
                        String.format("%02d.%02d", selectedHour, selectedMinute)
                    )
                }, hour, minute, true
            ) //Yes 24 hour time

            mTimePicker.setTitle("Select Time")

            mTimePicker.show()
        }
        //supportActionBar?.title = "UIB"
        //supportActionBar?.setDisplayHomeAsUpEnabled(false)
        if (supportFragmentManager.findFragmentByTag("TAG") != null) {
            btnSupport.visibility = View.GONE
            val fragment: UIBChatFragment =
                supportFragmentManager.findFragmentByTag("TAG") as UIBChatFragment
            val fragmentTransaction: FragmentTransaction =
                supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.content,
                fragment,
                "TAG"
            )
            //if (backSack) {
            fragmentTransaction.addToBackStack("TAG")
            //}
            fragmentTransaction.commit()
        }
        fabContact?.setOnClickListener {
            fabContact?.visibility = View.GONE
            chatPreview?.visibility = View.GONE
            workingHoursPreview?.visibility = View.GONE
            startTime?.visibility = View.GONE

            endTime?.visibility = View.GONE
            val startEndTime = StartEndTime(startTime = "00.01", endTime = "23.59")
            val workingTime =
                WorkingTime(dayName = "tuesday", workingStatus = true, time = startEndTime)
            val listOfTIme: MutableList<WorkingTime> = ArrayList()
            listOfTIme.add(workingTime)
            val workingHours =
                WorkingHours(workingTimeStatus = true, timeZone = "IST", workingTime = listOfTIme)

            //supportActionBar?.hide()
            val uibConfig = UIBConfig(
                /*amk*/
                appId="88148051-9b63-46c3-9c8a-a7b25f5f8a1e",
                botId="6469c178-ff6f-4cf5-88f2-a8ac7ea56d4c",
                userId = userId,
                botName = "AMK",
                botIcon = "https://i.postimg.cc/HnNZPHS6/computer-icons-user-clip-art-user.jpg",
                chatPreview = chatPreviewValue!!,
                inactiveChatPreviewMsg = "z Z z …  ich bin gerade nicht erreichbar. Sie können mir aber eine E-Mail schreiben an uschi@schwaebisch-gmuend.de",
                //chatWorkingHours = workingHours,
                activeChatPreviewMsg = "Welcome to uib"
            )
            val mFragment = UIBChatFragment()
            val bundle = Bundle()
            bundle.putParcelable(UIBConstants.UIB_CONFIGURATION, uibConfig)
            mFragment.arguments = bundle
            mFragment.shouldShowErrorForEmptyMessage(value = true)
            mFragment.showErrorMessageForEmptyValue(
                "Type here..."
            )
            mFragment.retainInstance = true
            mFragment.trimTypedMessage(value = true)
            //mFragment.setNoDataTextVisibility(View.VISIBLE)
            mFragment.setUIBAttachmentMenu(
                camera = true,
                gallery = true,
                document = true,
                audio = true/*,
                location = false,
                contact = false*/
            )
            /*mFragment.setNoDataText(
                resources?.getString(R.string.no_data_found)!!,
                resources.getColor(R.color.gray),
                Typeface.NORMAL,
                20.0f
            )*/

            /*mFragment.setMarginForUserText(
                adjustMargin = true,
                left = 125,
                right = 125,
                top = 125,
                bottom = 125
            )
            mFragment.setPaddingForUserText(
                adjustPadding = true,
                left = 125,
                right = 125,
                top = 125,
                bottom = 125
            )*/
            //mFragment.setUserMessageTextColor(resources.getColor(R.color.colorPrimary))
            //mFragment.setSupportMessageTextBackground(resources.getDrawable(R.drawable.screen_background))
            //mFragment.setSupportMessageTextColor(resources.getColor(R.color.white))
            //mFragment.setUIBEditTextBackground(resources.getDrawable(R.drawable.edit_text_background_new_client))
            //mFragment.setUIBEditTextHintColor(resources.getColor(R.color.colorPrimary))
            //mFragment.setUIBSendButtonBackground(resources.getDrawable(R.drawable.screen_background))
            //mFragment.setUIBSendIcon(resources.getDrawable(R.drawable.ic_trending_up_black_24dp))
            //mFragment.setUserMessageTextSize(20.0f)
            //mFragment.setBackGroundForLayout(getDrawable(R.drawable.screen_background)!!)
            /* mFragment.setMarginForEditText(
             left = 25,
             right = 25,
             adjustMargin = true,
             top = 25,
             bottom = 50

         )*/
            //mFragment.setUIBEditTextHintColor(R.color.colorPrimary)
            //mFragment.showErrorMessageForEmptyValue(errorString = "Bhaskar Pasupula")
            // mFragment.setUIBEditTextHint("Hello type your message here")
            //mFragment.setPaddingForEditText(left = 10, right = 10, top = 10, bottom = 10)
            // mFragment.setUIBActionBarTitle("AMT")
            mFragment.setUserMessageTextStyle(Typeface.NORMAL)
            //mFragment.setUserRepliedMessageTextStyle(Typeface.ITALIC)

            val fragmentTransaction: FragmentTransaction =
                supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.content,
                mFragment,
                "TAG"
            )
            //if (backSack) {
            fragmentTransaction.addToBackStack("TAG")
            //}
            fragmentTransaction.commit()


        }

    }

    override fun onStart() {
        super.onStart()
        //btnSupport.visibility = View.VISIBLE
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)




    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (fabContact!=null&&fabContact?.visibility==View.VISIBLE&&this.supportFragmentManager.backStackEntryCount>0)
        {
            Log.d("onRestoreInstanceState","Called")
            fabContact?.visibility=View.GONE
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sample_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.change_language) {
            //var lang: String?
            val lang = if (resources.configuration.locale.language == "ar") {
                "en"
            } else {
                "ar"
            }
            val locale = Locale(lang)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            onConfigurationChanged(config)
            baseContext.resources.updateConfiguration(
                config,
                baseContext.resources.displayMetrics
            )
            this.finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        return true
    }


    override fun onBackPressed() {

        val fm: FragmentManager = this.supportFragmentManager
        if (fm.backStackEntryCount > 1) {

            fm.popBackStackImmediate()

        } else
            finish()
    }
}
