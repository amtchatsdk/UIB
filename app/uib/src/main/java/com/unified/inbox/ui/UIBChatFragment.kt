package com.unified.inbox.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.airbnb.lottie.LottieAnimationView
import com.google.android.libraries.places.api.Places
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.unified.inbox.R
import com.unified.inbox.adapters.UIBChatAdapter
import com.unified.inbox.beans.*
import com.unified.inbox.interfaces.*
import com.unified.inbox.network.ChatHistoryRepo
import com.unified.inbox.network.PostMessageRepo
import com.unified.inbox.network.UIBConnection
import com.unified.inbox.utils.*
import com.unified.inbox.views.MenuEditText
import com.unified.inbox.views.SoftKeyBoardPopup
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class UIBChatFragment() : Fragment(), UIBListener,
    UIBEditTextListener, View.OnClickListener,
    OnResponseListener, UIBEventListener, View.OnTouchListener, MenuEditText.PopupListener,
    SoftKeyBoardPopup.FileSelectListener,
    UIBAudioInterFace, UIBLocation,
    UIBContactBookInterface,

    CheckImageFileAvailability, PushImageToApi,
    UIBChatAdapter.CallBack,
    UserMsgReplyEditInterface {

    private var imageType: String?="image/png"
    private var sizeInMb: Long?=null
    private var sizeInKb: Long?=null
    private var fileErrorMessage: String? = null
    private var userReplyEdit: UserMsgReplyEditInterface? = null
    private var indexToBeUpdated: Int = -1
    private var tryingToEdit: Boolean = false
    private var tryingToReply: Boolean = false
    private var sendingNormalMsg: Boolean = true
    private var chatObjectToEditOrReply: Chat? = null
    private var contactID: String? = null
    private var contactUir: Uri? = null
    private var audioFile: File? = null
    private var documentUri: Uri? = null
    private var imageFromGallery: File? = null
    private var document: File? = null
    private var imageUri: Uri? = null
    private var imageBitmap: Bitmap? = null
    private var part: MultipartBody.Part? = null
    private var taskTimer: AudioTimerTask? = null
    private var photo: FileInputStream? = null
    private var mView: View? = null
    private var etMessage: EditText? = null
    private var tvNoData: TextView? = null
    private var hint: String? = null
    private var sendIcon: Drawable? = null
    private var sendButtonBackground: Drawable? = null
    private var editTextBackground: Drawable? = null
    private var layoutBackground: Drawable? = null
    private var editTextInputType: Int? = null
    private var editTextRightPadding: Int? = 0

    //private var editTextContainerRightPadding: Int? = 0
    private var editTextLeftPadding: Int? = 0

    //private var editTextContainerLeftPadding: Int? = 0
    private var editTextTopPadding: Int? = 0

    //private var editTextContainerTopPadding: Int? = 0
    private var editTextBottomPadding: Int? = 0

    //private var editTextContainerBottomPadding: Int? = 0
    private var editTextBottomMargin: Int? = 0

    //private var editTextContainerBottomMargin: Int? = 0
    private var editTextTopMargin: Int? = 0

    //private var editTextContainerTopMargin: Int? = 0
    private var editTextLeftMargin: Int? = 0

    //private var editTextContainerLeftMargin: Int? = 0
    private var editTextRightMargin: Int? = 0

    //private var editTextContainerRightMargin: Int? = 0
    private var actionBarTitle: String? = null
    private var tvUserMessage: TextView? = null
    private var tvBotMessage: TextView? = null
    private var tvReplyMessage: TextView? = null
    private var ivRemoveReply: ImageView? = null
    private var sendMessage: TextView? = null
    private var errorMessage: String? = null
    private var shouldShowErrorMessage: Boolean? = true
    private var trimTypedMessage: Boolean? = false
    private var rvChatList: RecyclerView? = null
    private var editTextHintColor: Int? = null
    private var adjustEditTextPadding: Boolean? = false
    private var adjustEditTextMargin: Boolean? = false
    private var etContainer: LinearLayout? = null
    private var parentContainer: RelativeLayout? = null
    private var uibConnection: UIBConnection? = null
    private var supportChatAdapter: UIBChatAdapter? = null
    private var userMessageTextSize: Float? = 0.0f
    private var botMessageTextSize: Float? = 0.0f
    private var botMessageTextStyle: Int? = null
    private var userMessageTextStyle: Int? = null
    private var userRepliedMessageTextStyle: Int? = null
    private var userMessageTextColor: Int? = null
    private var userMessageTextBackground: Drawable? = null
    private var botMessageTextColor: Int? = null
    private var botMessageTextBackground: Drawable? = null
    private var noDataTextVisibility: Int? = null
    private var noDataMessage: String? = null
    private var noDataMessageTextColor: Int? = null
    private var noDataMessageTextStyle: Int? = null
    private var noDataMessageTextSize: Float? = null
    private var adjustUserTextMargin: Boolean? = false
    private var adjustUserTextPadding: Boolean? = false
    private var adjustBotTextMargin: Boolean? = false
    private var adjustBotTextPadding: Boolean? = false
    private var userTextMarginRight: Int? = null
    private var botTextMarginRight: Int? = null
    private var userTextMarginLeft: Int? = null
    private var botTextMarginLeft: Int? = null
    private var userTextMarginTop: Int? = null
    private var botTextMarginTop: Int? = null
    private var userTextMarginBottom: Int? = null
    private var botTextMarginBottom: Int? = null
    private var userTextPaddingRight: Int? = null
    private var botTextPaddingRight: Int? = null
    private var userTextPaddingLeft: Int? = null
    private var botTextPaddingLeft: Int? = null
    private var userTextPaddingTop: Int? = null
    private var botTextPaddingTop: Int? = null
    private var userTextPaddingBottom: Int? = null
    private var botTextPaddingBottom: Int? = null

    /*private var appId: String? = null
    private var botId: String? = null
    private var userId: String? = null*/
    private var authToken: String? = null
    private var toolBar: RelativeLayout? = null

    private var state: Boolean = false
    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var popUpAudio: PopupWindow? = null
    private var timer: Timer? = null
    private var animationAudio: LottieAnimationView? = null
    private var timeEllapsed: TextView? = null
    private var minutes = 0
    private var seconds = 0
    private var hour = 0

    private var filePath: String? = null
    private var dirName: String? = null
    private var fileName: String? = null

    private var myList: ArrayList<UIBContactObject>? = null
    private lateinit var chatHistoryRepo: ChatHistoryRepo

    /*override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

}*/
    private var arrayByte: String? = null
    private var agentPic: ImageView? = null
    private var agentName: TextView? = null
    private var previewValidationNoDataValue: Boolean? = false
    private var uibConfig: UIBConfig? = null

    @ExperimentalStdlibApi
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_uib_chat, container, false)
        //activity?.window?.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE)
        etMessage = mView?.findViewById(R.id.et_message)
        tvUserMessage = mView?.findViewById(R.id.tv_message_user)
        tvBotMessage = mView?.findViewById(R.id.tv_message_bot)
        rvChatList = mView?.findViewById(R.id.chat_recycler_view)
        sendMessage = mView?.findViewById(R.id.send)
        etContainer = mView?.findViewById(R.id.et_container)
        parentContainer = mView?.findViewById(R.id.parent_container)
        tvNoData = mView?.findViewById(R.id.tv_no_data)
        toolBar = mView?.findViewById(R.id.toolbar)
        agentName = mView?.findViewById(R.id.agent_name)
        agentPic = mView?.findViewById(R.id.agent_pic_)
        tvReplyMessage = mView?.findViewById(R.id.tv_reply_msg)
        ivRemoveReply = mView?.findViewById(R.id.iv_remove_reply)
        ivRemoveReply?.setOnClickListener(this)
        val bundle = arguments
        uibConfig = bundle?.getParcelable(UIBConstants.UIB_CONFIGURATION)
        etMessage?.setOnTouchListener(this)
        //(activity as AppCompatActivity?)?.setSupportActionBar(toolBar)
        //toolBar?.logo = resources.getDrawable(R.drawable.placeholder)
        //toolBar?.setBackgroundColor(resources.getColor(R.color.white))
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        supportChatAdapter = UIBChatAdapter(
            activity!!,
            rvChatList!!,
            layoutManager,
            this
        )
        rvChatList?.adapter = supportChatAdapter
        layoutManager.stackFromEnd = true
        rvChatList?.layoutManager = layoutManager
        if (uibConfig?.chatPreview!!) {
            /*if (uibConfig?.chatWorkingHours?.workingTimeStatus!!) {
                if (isCurrentTimeInBetweenSlots(
                        uibConfig?.chatWorkingHours?.workingTime?.get(getDayOfWeekPosition())?.time?.startTime,
                        uibConfig?.chatWorkingHours?.workingTime?.get(getDayOfWeekPosition())?.time?.endTime,
                        uibConfig?.chatWorkingHours?.timeZone!!
                    )
                ) {
                    toolBar?.setBackgroundColor(resources.getColor(R.color.uib_red))
                    agentPic?.load(uibConfig?.botIcon)
                    agentName?.text = uibConfig?.botName
                } else {
                    toolBar?.setBackgroundColor(resources.getColor(R.color.grey_light))
                    agentName?.text = uibConfig?.botName
                    etContainer?.visibility = View.GONE
                    tvNoData?.visibility = View.GONE
                    previewValidationNoDataValue = true
                    val chat = Chat()
                    chat.msg = uibConfig?.inactiveChatPreviewMsg
                    chat.type = "text"
                    chat.from = 1
                    activity?.runOnUiThread {
                        supportChatAdapter?.addItem(chat = chat)
                        rvChatList?.smoothScrollToPosition(supportChatAdapter?.itemCount!!)
                    }
                }
            } else {*/
                toolBar?.setBackgroundColor(resources.getColor(R.color.uib_red))
                agentPic?.load(uibConfig?.botIcon)
                agentName?.text = uibConfig?.botName
           /* }*/
        } else {
            toolBar?.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            agentPic?.load(uibConfig?.botIcon)
            agentName?.text = uibConfig?.botName
        }
        //layoutManager.reverseLayout = true

        sendMessage?.setOnClickListener(this)
        uibConnection =
            UIBConnection(uibEventListener = this)
        chatHistoryRepo =
            ChatHistoryRepo(responseListener = this)
        arrayByte = getAuth(uibConfig?.appId!!, uibConfig?.botId!!, uibConfig?.userId!!)
        Log.d("auth", "$arrayByte")
       /* if ((uibConfig?.chatWorkingHours?.workingTimeStatus!! && isCurrentTimeInBetweenSlots(
                uibConfig?.chatWorkingHours?.workingTime?.get(getDayOfWeekPosition())?.time?.startTime,
                uibConfig?.chatWorkingHours?.workingTime?.get(getDayOfWeekPosition())?.time?.endTime,
                uibConfig?.chatWorkingHours?.timeZone!!
            )) || (!uibConfig?.chatWorkingHours?.workingTimeStatus!!)
        ) {*/
            chatHistoryRepo.getChats(
                appId = uibConfig?.appId!!,
                botId = uibConfig?.botId!!,
                userId = uibConfig?.userId!!,
                authToken = arrayByte!!, skip = 0, limit = 25
            )
            //this.userId = UUID.randomUUID().toString()
            uibConnection?.connect(
                appId = uibConfig?.appId!!,
                botId = uibConfig?.botId!!,
                userId = uibConfig?.userId!!
            )
       /* }*/
        Log.d("Package_name", activity?.applicationContext?.packageName!!)

        /*Log.d(
        "Android_ID",
        Settings.Secure.getString(activity?.contentResolver, Settings.Secure.ANDROID_ID)
    )*/
        popup = SoftKeyBoardPopup(
            activity!!,
            parentContainer!!,
            etMessage!!,
            sendMessage!!,
            etContainer!!,
            this@UIBChatFragment, getAttachmentMenu()
        )
        if (!Places.isInitialized()) {
            Places.initialize(
                activity?.applicationContext!!,
                getString(R.string.google_places_api_key),
                Locale.US
            );
        }
        applyUIChanges()
        requestRunTimePermissions()
        if (!needAttachMenu) {
            etMessage?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
        dirName = "/uib_audio_recordings"
        filePath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath + dirName
        fileName = "uib_audio.mp3"

        uibLocation = this

        myList = ArrayList()

        /* val pos: Int = layoutManager.findLastCompletelyVisibleItemPosition()

         if ( pos == rvChatList?.size!!-1) {
             hide=true
         }*/

        return mView


    }


    override fun onResume() {
        super.onResume()
        userReplyEdit = this
    }

    private fun getCurrentDay(): String {
        val sdf = SimpleDateFormat("EEEE")
        val d = Date()
        val dayOfTheWeek: String = sdf.format(d)
        Log.d("dayOfWeek", dayOfTheWeek)
        return dayOfTheWeek
    }


   /* private fun getDayOfWeekPosition(): Int {
        var day = 0
        val dayName: String = getCurrentDay()
        for (i in 0 until uibConfig?.chatWorkingHours?.workingTime?.size!!) {
            if (uibConfig?.chatWorkingHours?.workingTime?.get(i)?.dayName?.equals(
                    dayName,
                    true
                )!!
            ) {
                day = i
                break
            }
        }
        return day
    }*/

    override fun setUpMediaRecorder() {
        mediaRecorder = MediaRecorder()
        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver: ContentResolver = activity!!.contentResolver
            output = UUID.randomUUID().toString().subSequence(0, 5).toString() + fileName
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, output)
                put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
                put(
                    MediaStore.Files.FileColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_MUSIC + dirName + "/"
                )
                put(MediaStore.Files.FileColumns.IS_PENDING, 1)
            }
            val audioUri: Uri? =
                resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
            val parcelFileDescriptor = resolver.openFileDescriptor(audioUri!!, "rw", null)
            mediaRecorder?.setOutputFile(parcelFileDescriptor?.fileDescriptor)
            output =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath + dirName + "/" + output
            println("Mi A2 $output")

        } else {
            val fileA = File(filePath!!)
            if (!fileA.exists()) {
                fileA.mkdir()
            }
            val audioDirectory = File(filePath!!)
            audioDirectory.mkdirs()
            output =
                audioDirectory.absolutePath + "/" + UUID.randomUUID()
                    .toString().subSequence(0, 5) + fileName
            audioFile = File(audioDirectory, fileName!!)

            // now attach the OutputStream to the file object, instead of a String representation
            try {
                FileOutputStream(audioFile!!)

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            println("Audio File $audioFile")
            println("Audio Out $output")
            println("Audio Dire $audioDirectory")
            mediaRecorder?.setOutputFile(output)

        }
        println("AUDIO_PATH $output")
        showAudioPopup()


    }

    private fun requestRunTimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && activity?.checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED && activity?.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && activity?.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                && activity?.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_CONTACTS

                )
                ActivityCompat.requestPermissions(activity!!, permissions, 100)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return
            }
        }
    }

    override fun setUIBSendIcon(icon: Drawable): UIBChatFragment {
        sendIcon = icon
        return this
    }

    override fun setUIBSendButtonBackground(background: Drawable): UIBChatFragment {
        this.sendButtonBackground = background
        return this
    }

    override fun setUIBEditTextHint(hint: String): UIBChatFragment {
        this.hint = hint
        return this
    }

    override fun setUIBEditTextBackground(background: Drawable): UIBChatFragment {
        this.editTextBackground = background
        return this
    }

    override fun setUIBEditTextInputType(inputType: Int): UIBChatFragment {
        editTextInputType = inputType
        return this
    }

    override fun setPaddingForEditText(
        adjustPadding: Boolean,
        left: Int,
        right: Int,
        top: Int,
        bottom: Int
    ): UIBChatFragment {
        adjustEditTextPadding = adjustPadding
        editTextLeftPadding = left
        editTextRightPadding = right
        editTextTopPadding = top
        editTextBottomPadding = bottom
        return this
    }


    override fun setMarginForEditText(
        adjustMargin: Boolean,
        left: Int,
        right: Int,
        top: Int,
        bottom: Int
    ): UIBChatFragment {
        adjustEditTextMargin = adjustMargin
        editTextBottomMargin = bottom
        editTextLeftMargin = left
        editTextRightMargin = right
        editTextTopMargin = top
        return this
    }


    override fun showErrorMessageForEmptyValue(errorString: String): UIBChatFragment {
        this.errorMessage = errorString
        return this
    }

    override fun shouldShowErrorForEmptyMessage(value: Boolean): UIBChatFragment {
        this.shouldShowErrorMessage = value
        return this
    }

    override fun trimTypedMessage(value: Boolean): UIBChatFragment {
        this.trimTypedMessage = value
        return this
    }

    override fun setUIBEditTextHintColor(color: Int): UIBChatFragment {
        this.editTextHintColor = color
        return this
    }

    private fun applyUIChanges() {
        if (hint != null) {
            etMessage?.hint = hint
        }
        if (sendButtonBackground != null) {
            sendMessage?.background = sendButtonBackground

        }
        if (sendIcon != null) {
            sendMessage?.setCompoundDrawablesWithIntrinsicBounds(sendIcon, null, null, null)
        }
        if (editTextBackground != null) {
            etMessage?.background = editTextBackground
        }
        if (editTextInputType != null) {
            etMessage?.inputType = editTextInputType!!
        }
        if (errorMessage == null) {
            errorMessage = activity?.resources?.getString(R.string.error_msg_for_edit_text)
        }
        if (editTextHintColor != null) {
            etMessage?.setHintTextColor(editTextHintColor!!)
        }
        if (adjustEditTextPadding!!) {
            etMessage?.setPadding(
                editTextLeftPadding!!,
                editTextTopPadding!!,
                editTextRightPadding!!,
                editTextBottomPadding!!
            )
        }
        if (adjustEditTextMargin!!) {
            val params: ViewGroup.MarginLayoutParams =
                etContainer?.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(
                editTextLeftMargin!!,
                editTextTopMargin!!,
                editTextRightMargin!!,
                editTextBottomMargin!!
            )
            if (editTextBottomMargin != 0)
                etContainer?.translationY = (-editTextBottomMargin?.toFloat()!!)
        }

        if (layoutBackground != null) {
            //etContainer?.background = layoutBackground
            parentContainer?.background = layoutBackground
        }
        if (userMessageTextSize != null && userMessageTextSize != 0.0f) {
            supportChatAdapter?.setUserMessageTextSize(userMessageTextSize!!)
        }

        if (botMessageTextSize != null && botMessageTextSize != 0.0f) {
            supportChatAdapter?.setBotMessageTextSize(botMessageTextSize!!)
        }

        if (userMessageTextStyle != null) {
            supportChatAdapter?.setUserMessageTextStyle(userMessageTextStyle!!)
        }

        if (userRepliedMessageTextStyle != null) {
            supportChatAdapter?.setUserRepliedMessageTextStyle(userRepliedMessageTextStyle!!)
        }

        if (botMessageTextStyle != null) {
            supportChatAdapter?.setBotMessageTextStyle(botMessageTextStyle!!)
        }

        if (userMessageTextColor != null) {
            supportChatAdapter?.setUserMessageTextColor(userMessageTextColor!!)
        }

        if (botMessageTextColor != null) {
            supportChatAdapter?.setBotMessageTextColor(botMessageTextColor!!)
        }

        if (userMessageTextBackground != null) {
            supportChatAdapter?.setUserMessageTextBackground(userMessageTextBackground!!)
        }


        if (botMessageTextBackground != null) {
            supportChatAdapter?.setSupportMessageTextBackground(botMessageTextBackground!!)
        }
        if (noDataMessage != null) {
            tvNoData?.text = noDataMessage
        }
        if (noDataMessageTextColor != null) {
            tvNoData?.setTextColor(noDataMessageTextColor!!)
        }

        if (noDataMessageTextStyle != null) {
            tvNoData?.setTypeface(tvNoData?.typeface, noDataMessageTextStyle!!)
        }
        if (noDataMessageTextSize != null) {
            tvNoData?.textSize = noDataMessageTextSize!!
        }
        if (noDataTextVisibility != null) {
            if (previewValidationNoDataValue!!) {
                tvNoData?.visibility = View.GONE
            } else {
                tvNoData?.visibility = noDataTextVisibility!!
            }
        }
        if (adjustBotTextMargin != null && adjustBotTextMargin!!) {
            supportChatAdapter?.setMarginForBotText(
                adjustMargin = adjustBotTextMargin!!,
                left = botTextMarginLeft!!,
                right = botTextMarginRight!!,
                top = botTextMarginTop!!,
                bottom = botTextMarginBottom!!
            )
        }

        if (adjustUserTextMargin != null && adjustUserTextMargin!!) {
            supportChatAdapter?.setMarginForUserText(
                adjustMargin = adjustUserTextMargin!!,
                left = userTextMarginLeft!!,
                right = userTextMarginRight!!,
                top = userTextMarginTop!!,
                bottom = userTextMarginBottom!!
            )
        }

        if (adjustBotTextPadding != null && adjustBotTextPadding!!) {
            supportChatAdapter?.setPaddingForBotText(
                adjustPadding = adjustBotTextPadding!!,
                left = botTextPaddingLeft!!,
                right = botTextPaddingRight!!,
                top = botTextPaddingTop!!,
                bottom = botTextPaddingBottom!!
            )
        }

        if (adjustUserTextPadding != null && adjustUserTextPadding!!) {
            supportChatAdapter?.setPaddingForUserText(
                adjustPadding = adjustUserTextPadding!!,
                left = userTextPaddingLeft!!,
                right = userTextPaddingRight!!,
                top = userTextPaddingTop!!,
                bottom = userTextPaddingBottom!!
            )
        }

    }


    override fun onDestroy() {
        mView = null
        etMessage = null
        hint = null
        sendIcon = null
        editTextBackground = null
        editTextInputType = null
        editTextLeftPadding = null
        editTextTopPadding = null
        editTextRightPadding = null
        editTextBottomPadding = null
        editTextLeftMargin = null
        editTextTopMargin = null
        editTextRightMargin = null
        editTextBottomMargin = null
        actionBarTitle = null
        errorMessage = null
        sendMessage = null
        shouldShowErrorMessage = null
        trimTypedMessage = null
        editTextHintColor = null
        layoutBackground = null
        supportChatAdapter = null
        userMessageTextSize = null
        botMessageTextSize = null
        userMessageTextStyle = null
        botMessageTextStyle = null
        userMessageTextBackground = null
        botMessageTextBackground = null
        userMessageTextColor = null
        botMessageTextColor = null
        tvNoData = null
        noDataMessage = null
        noDataMessageTextColor = null
        noDataMessageTextSize = null
        noDataMessageTextStyle = null
        userTextMarginBottom = null
        userTextMarginLeft = null
        userTextMarginRight = null
        userTextMarginTop = null
        userTextPaddingBottom = null
        userTextPaddingLeft = null
        userTextPaddingRight = null
        userTextPaddingTop = null
        botTextMarginBottom = null
        botTextMarginLeft = null
        botTextMarginRight = null
        botTextMarginTop = null
        botTextPaddingBottom = null
        botTextPaddingLeft = null
        botTextPaddingRight = null
        botTextPaddingTop = null
        adjustBotTextMargin = null
        adjustBotTextPadding = null
        adjustUserTextMargin = null
        adjustUserTextPadding = null
        noDataTextVisibility = null
        uibConnection?.disposeEventSource()
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.send -> {
                if (etMessage?.text.toString().trim()
                        .isNotEmpty() && ((sendingNormalMsg) && (!tryingToEdit) && (!tryingToReply))
                ) {
                    tvNoData?.visibility = View.GONE
                    //val chatUser = DummyChat(etMessage?.text.toString().trim(), 0)
                    //val chatBot = DummyChat("${etMessage?.text.toString().trim()}    -- BOT", 1)
                    /*tvUserMessage?.text = messageFromUser()
                    tvBotMessage?.text = messageFromUser()*/
                    //supportChatAdapter?.addItem(chatUser)
                    /*val message = MessageBody()
                   val body = message.Message()
                    body.text = etMessage?.text.toString().trim()
                   body.type = "text"
                    message.message = body*/
                    val msgObject = JsonObject()
                    val dataObject = JsonObject()
                    val dataList = JsonArray()

                    dataObject.addProperty("text", etMessage?.text.toString().trim())
                    dataObject.addProperty("type", "text")
                    val uuid = GenerateUniqueId.getUniqueMessageId()
                    Log.d("UUID", uuid)
                    dataObject.addProperty("msg_id", uuid)
                    dataList.add(dataObject)
                    msgObject.add("message", dataList)
                    Log.d("message_body", msgObject.toString())
                    val repo = PostMessageRepo(
                        responseListener = this
                    )
                    repo.postMessage(
                        message = msgObject,
                        appId = uibConfig?.appId!!,
                        botId = uibConfig?.botId!!,
                        userId = uibConfig?.userId!!,
                        adapterPosition = supportChatAdapter?.itemCount!!
                    )

                    // val chat = Chat(etMessage?.text.toString().trim(),0)
                    val chat = Chat()
                    chat.msg = etMessage?.text.toString().trim()
                    chat.from = 0
                    chat.type = AppConstants.text
                    chat.bitmap = null
                    etMessage?.setText("")
                    supportChatAdapter?.addItem(chat = chat)
                    rvChatList?.smoothScrollToPosition(supportChatAdapter?.itemCount!!)
                } else if (etMessage?.text.toString().trim().isNotEmpty() && tryingToReply) {
                    tvNoData?.visibility = View.GONE
                    val msgObject = JsonObject()
                    val payLoadObject = JsonObject()
                    val dataObject = JsonObject()
                    val dataList = JsonArray()

                    payLoadObject.addProperty(
                        "message_id",
                        supportChatAdapter?.chatList?.get(indexToBeUpdated)?.msgID
                    )
                    payLoadObject.addProperty("message", etMessage?.text.toString().trim())

                    dataObject.addProperty("text", payLoadObject.toString())
                    dataObject.addProperty("type", "reply")
                    dataObject.addProperty("msg_id", GenerateUniqueId.getUniqueMessageId())
                    dataList.add(dataObject)
                    msgObject.add("message", dataList)
                    Log.d("message_body", msgObject.toString())
                    val repo = PostMessageRepo(
                        responseListener = this
                    )
                    repo.postMessage(
                        message = msgObject,
                        appId = uibConfig?.appId!!,
                        botId = uibConfig?.botId!!,
                        userId = uibConfig?.userId!!,
                        adapterPosition = indexToBeUpdated
                    )

                    // val chat = Chat(etMessage?.text.toString().trim(),0)
                    chatObjectToEditOrReply?.oldMsg = chatObjectToEditOrReply?.msg
                    chatObjectToEditOrReply?.repliedAtTimeStamp = TimeUtils.getCurrentTime()
                    chatObjectToEditOrReply?.msg = etMessage?.text.toString().trim()
                    chatObjectToEditOrReply?.from = 0
                    chatObjectToEditOrReply?.type = AppConstants.reply
                    chatObjectToEditOrReply?.bitmap = null
                    etMessage?.setText("")
                    supportChatAdapter?.editUpdate(
                        chat = chatObjectToEditOrReply,
                        position = indexToBeUpdated
                    )
                    rvChatList?.smoothScrollToPosition(supportChatAdapter?.itemCount!!)
                    tryingToEdit = false
                    tryingToReply = false
                    sendingNormalMsg = true
                    tvReplyMessage?.visibility = View.GONE
                    ivRemoveReply?.visibility = View.GONE

                } else if ((etMessage?.text.toString().trim().isNotEmpty() && tryingToEdit)) {
                    tvNoData?.visibility = View.GONE
                    val msgObject = JsonObject()
                    val dataObject = JsonObject()
                    val dataList = JsonArray()

                    dataObject.addProperty("text", etMessage?.text.toString().trim())
                    dataObject.addProperty("type", "text")

                    dataList.add(dataObject)
                    msgObject.add("message", dataList)
                    Log.d("message_body", msgObject.toString())
                    val repo = PostMessageRepo(
                        responseListener = this
                    )
                    repo.postMessage(
                        message = msgObject,
                        appId = uibConfig?.appId!!,
                        botId = uibConfig?.botId!!,
                        userId = uibConfig?.userId!!,
                        adapterPosition = indexToBeUpdated
                    )

                    // val chat = Chat(etMessage?.text.toString().trim(),0)
                    chatObjectToEditOrReply?.msg = etMessage?.text.toString().trim()
                    chatObjectToEditOrReply?.from = 0
                    chatObjectToEditOrReply?.type = AppConstants.text
                    chatObjectToEditOrReply?.bitmap = null
                    chatObjectToEditOrReply?.isEdited=true
                    etMessage?.setText("")
                    supportChatAdapter?.editUpdate(
                        chat = chatObjectToEditOrReply,
                        position = indexToBeUpdated
                    )
                    rvChatList?.smoothScrollToPosition(supportChatAdapter?.itemCount!!)
                    tryingToEdit = false
                    tryingToReply = false
                    sendingNormalMsg = true
                } else {
                    if (shouldShowErrorMessage!!) {
                        Toast.makeText(activity!!, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.iv_remove_reply -> {
                tvReplyMessage?.visibility = View.GONE
                ivRemoveReply?.visibility = View.GONE
                tryingToReply = false
                tryingToEdit = false
                sendingNormalMsg = true
            }
        }
    }

    /*private fun messageFromUser(): String? {
    return if (trimTypedMessage!!) {
        etMessage?.text.toString().trim()
    } else {
        etMessage?.text.toString()
    }
}*/

    override fun setBackGroundForLayout(background: Drawable) {
        this.layoutBackground = background
    }

    override fun setUserMessageTextSize(size: Float) {
        this.userMessageTextSize = size
    }


    override fun setUserMessageTextStyle(style: Int) {
        this.userMessageTextStyle = style
    }

    override fun setUserMessageTextColor(color: Int) {
        this.userMessageTextColor = color
    }

    override fun setUserMessageTextBackground(background: Drawable) {
        this.userMessageTextBackground = background
    }

    override fun setSupportMessageTextStyle(style: Int) {
        this.botMessageTextStyle = style
    }

    override fun setSupportMessageTextColor(color: Int) {
        this.botMessageTextColor = color
    }

    override fun setSupportMessageTextSize(size: Float) {
        this.botMessageTextSize = size
    }

    override fun setSupportMessageTextBackground(background: Drawable) {
        this.botMessageTextBackground = background
    }

    /*override fun setNoDataTextVisibility(visibility: Int) {
        this.noDataTextVisibility = visibility
    }

    override fun setNoDataText(value: String, textColor: Int, textStyle: Int, textSize: Float) {
        this.noDataMessage = value
        this.noDataMessageTextColor = textColor
        this.noDataMessageTextSize = textSize
        this.noDataMessageTextStyle = textStyle
    }*/

    override fun setMarginForUserText(
        adjustMargin: Boolean,
        left: Int,
        right: Int,
        top: Int,
        bottom: Int
    ) {
        this.adjustUserTextMargin = adjustMargin
        this.userTextMarginBottom = bottom
        this.userTextMarginLeft = left
        this.userTextMarginRight = right
        this.userTextMarginTop = top
    }

    override fun setPaddingForUserText(
        adjustPadding: Boolean,
        left: Int,
        right: Int,
        top: Int,
        bottom: Int
    ) {
        this.adjustUserTextPadding = adjustPadding
        this.userTextPaddingTop = top
        this.userTextPaddingRight = right
        this.userTextPaddingLeft = left
        this.userTextPaddingBottom = bottom
    }

    override fun setMarginForSupportText(
        adjustMargin: Boolean,
        left: Int,
        right: Int,
        top: Int,
        bottom: Int
    ) {
        this.adjustBotTextMargin = adjustMargin
        this.botTextMarginTop = top
        this.botTextMarginRight = right
        this.botTextMarginLeft = left
        this.botTextMarginBottom = bottom
    }

    override fun setPaddingForSupportText(
        adjustPadding: Boolean,
        left: Int,
        right: Int,
        top: Int,
        bottom: Int
    ) {
        this.adjustBotTextPadding = adjustPadding
        this.botTextPaddingBottom = bottom
        this.botTextPaddingLeft = left
        this.botTextPaddingRight = right
        this.botTextPaddingTop = top
    }

    /*override fun setAccountDetails(
        appId: String,
        botId: String,
        userId: String
    ) {
        this.appId = appId
        this.userId = userId
        this.botId = botId
    }*/

    @ExperimentalStdlibApi
    override fun getAuth(appId: String, botId: String, userId: String): String {
        return Base64.encodeToString("${appId}${botId}${userId}".toByteArray(), Base64.NO_WRAP)
    }

    override fun getAttachmentMenu(): ArrayList<UIBAttachmentMenu> {
        val menuList: ArrayList<UIBAttachmentMenu> = ArrayList()
        if (needAttachMenu) {
            if (attachFromCamera) {
                menuList.add(
                    UIBAttachmentMenu(
                        "Camera",
                        R.drawable.ic_camera
                    )
                )
            }
            if (attachFromGallery) {
                menuList.add(
                    UIBAttachmentMenu(
                        "Gallery",
                        R.drawable.ic_gallery
                    )
                )
            }
            if (attachAudio) {
                menuList.add(
                    UIBAttachmentMenu(
                        "Audio",
                        R.drawable.ic_volume
                    )
                )
            }
            if (attachDocument) {
                menuList.add(
                    UIBAttachmentMenu(
                        "Document",
                        R.drawable.ic_document
                    )
                )
            }

         /*   if (attachLocation) {
                menuList.add(
                    UIBAttachmentMenu(
                        "Location",
                        R.drawable.ic_location
                    )
                )
            }
            if (attachContact) {
                menuList.add(
                    UIBAttachmentMenu(
                        "Contact",
                        R.drawable.ic_contact
                    )
                )
            }*/
        }

        return menuList
    }

    private var needAttachMenu: Boolean = false
    private var attachFromCamera: Boolean = false
    private var attachFromGallery: Boolean = false
    private var attachDocument: Boolean = false
    private var attachAudio: Boolean = false
    private var attachLocation: Boolean = false
    private var attachContact: Boolean = false
    override fun setUIBAttachmentMenu(
        camera: Boolean,
        gallery: Boolean,
        document: Boolean,
        audio: Boolean/*,
        location: Boolean,
        contact: Boolean*/
    ) {
        attachFromCamera = camera
        attachFromGallery = gallery
        attachDocument = document
        attachAudio = audio
       /* attachLocation = location
        attachContact = contact*/
        needAttachMenu =
            attachFromCamera || attachFromGallery || attachDocument || attachAudio || attachLocation || attachContact


    }


    override fun <T> onSuccess(response: T, adapterPosition: Int) {
        if (response != null) {
            if (response is ChatHistoryResponse) {
                tvNoData?.visibility = View.GONE


                if (response.info?.size!! > 0) {
                    //Toast.makeText(activity, "Loaded", Toast.LENGTH_SHORT).show()
                    if (supportChatAdapter?.itemCount!! <= 0) {
                        for (items in response.info.asReversed()) {
                            supportChatAdapter?.addChatHistory(items!!)
                        }
                    } else {
                        for (items in response.info) {
                            supportChatAdapter?.addChatHistory(items!!)
                        }
                    }
                } else {
                    if (supportChatAdapter?.itemCount == 0) {
                      /*  if (uibConfig?.chatWorkingHours?.workingTimeStatus!! && isCurrentTimeInBetweenSlots(
                                uibConfig?.chatWorkingHours?.workingTime?.get(getDayOfWeekPosition())?.time?.startTime,
                                uibConfig?.chatWorkingHours?.workingTime?.get(getDayOfWeekPosition())?.time?.endTime,
                                uibConfig?.chatWorkingHours?.timeZone!!
                            )
                        ) {*/
                            val chat = Chat()
                            chat.msg = uibConfig?.activeChatPreviewMsg
                            chat.type = "text"
                            chat.from = 1
                            activity?.runOnUiThread {
                                supportChatAdapter?.addItem(chat = chat)
                                rvChatList?.smoothScrollToPosition(supportChatAdapter?.itemCount!!)
                            }
                      /*  }*/
                    }
                }
            } else {
                //println("Image Uploaded success")
                tvNoData?.visibility = View.GONE
                if (supportChatAdapter?.chatList!!.size > 0) {
                    supportChatAdapter?.chatList!![adapterPosition].sentStatus = 1
                }
                supportChatAdapter?.notifyItemChanged(adapterPosition)
                //supportChatAdapter?.hideUserImageProgressBar()
            }
            //hideProgress(supportChatAdapter?.itemCount!!-1)
        }
    }

    override fun onError(error: Throwable, adapterPosition: Int) {
        println("onError  ${error.message}")

    }

    override fun <T> onEventResponse(response: T, type: String) {
        if (response is String) {

            println("onEventResponse  $type $response")
            val chat = Chat()
            chat.msg = response
            //  val chat = Chat(response ,1)
            if (type == AppConstants.messageImageLink) {
                chat.type = type
            } else {
                chat.type = type
                chat.from = 1
                chat.bitmap = null

            }
            activity?.runOnUiThread {
                supportChatAdapter?.addItem(chat = chat)
                rvChatList?.smoothScrollToPosition(supportChatAdapter?.itemCount!!)
            }
        }
    }

    override fun onEventError(error: Exception) {

        Log.d("onEventError", error.message)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        //val DRAWABLE_LEFT = 0
        //val DRAWABLE_TOP = 1
        val drawableRight = 2
        //val DRAWABLE_BOTTOM = 3
        if (needAttachMenu) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (event.rawX >= etMessage?.right!!.minus(
                        etMessage?.compoundDrawables?.get(
                            drawableRight
                        )?.bounds?.width()!!.plus(100)
                    )
                ) {
                    if (popup.isShowing) {
                        popup.dismiss()
                    } else {
                        popup.show()
                    }
                    return true
                }
            }
        }
        return false
    }

    private lateinit var popup: SoftKeyBoardPopup
    override fun getPopup(): PopupWindow {
        return popup
    }

    override fun imageSelect(type: String) {
        when (type) {
            activity?.getString(R.string.document) -> {
                if (isPermissionGrantedForDocuments())
                    openDocuments()
                else
                    requestRunTimePermissionForDocuments()
            }
            activity?.getString(R.string.gallery) -> {
                if (isPermissionGrantedForDocuments())
                    openImages()
                else
                    requestRunTimePermissionForDocuments()
            }
            activity?.getString(R.string.contact) -> {
                if (isPermissionGrantedForContacts())
                    openContacts()
                else
                    requestRunTimePermissionForContacts()
            }
            activity?.getString(R.string.audio) -> {
                if (isPermissionGrantedForMediaRecording()
                ) {
                    setUpMediaRecorder()
                } else {
                    requestRunTimePermissionsForMediaRecording()
                }
            }
            activity?.getString(R.string.camera) -> {
                if (isPermissionGrantedForCamera())
                    dispatchTakePictureIntent()
                else
                    requestRunTimePermissionForCamera()
                //captureImage()
            }
            activity?.getString(R.string.location) -> {
                val fragment = UIBMapsFragment()
                val fragmentTransaction: FragmentTransaction =
                    activity?.supportFragmentManager!!.beginTransaction()
                fragmentTransaction.replace(
                    R.id.content,
                    fragment,
                    "UIBMAPS"
                )
                //if (backSack) {
                fragmentTransaction.addToBackStack("UIBMAPS")
                //}
                fragmentTransaction.commit()

//Places
            }
        }
       // Toast.makeText(activity, "$type selected", Toast.LENGTH_SHORT).show()
    }

    private fun requestRunTimePermissionForCamera() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA

        )
        ActivityCompat.requestPermissions(activity!!, permissions, 100)
    }

    private fun isPermissionGrantedForCamera(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (activity?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        }
        return true
    }

    private fun requestRunTimePermissionForContacts() {
        val permissions = arrayOf(
            Manifest.permission.READ_CONTACTS

        )
        ActivityCompat.requestPermissions(activity!!, permissions, 100)
    }

    private fun isPermissionGrantedForContacts(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (activity?.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
        }
        return true
    }

    private fun requestRunTimePermissionForDocuments() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(activity!!, permissions, 100)
    }

    private fun isPermissionGrantedForDocuments(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (activity?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && activity?.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
                    )
        }
        return true
    }

    private fun requestRunTimePermissionsForMediaRecording() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO

        )
        ActivityCompat.requestPermissions(activity!!, permissions, 100)
    }

    private fun isPermissionGrantedForMediaRecording(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (activity?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && activity?.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
                    && activity?.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
        }
        return true
    }

    private val SELECT_PICTURE = 2
    private val SELECT_DOCUMENT = 3
    private val SELECT_CONTACT_REQUEST = 4
    private val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun openImages() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Picture"
            ), SELECT_PICTURE
        )

    }

    private fun openDocuments() {
        val intent = Intent()
        /*Allowed mime types for documents selection*/
        val extraMimeType = arrayOf(
            "application/pdf",
            "application/docx",
            "application/msword",
            "application/vnd.google-apps.document",
            "application/vnd.google-apps.spreadsheet",
            "application/vnd.google-apps.file",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/doc"

        )
        intent.also {
            it.type = "file/*"
            it.action = Intent.ACTION_OPEN_DOCUMENT
            it.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            it.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeType)
        }

        startActivityForResult(Intent.createChooser(intent, "Select files"), SELECT_DOCUMENT)
    }


    override fun showAudioPopup() {

        val inflater: LayoutInflater =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val audioView = View.inflate(
            activity!!,
            R.layout.audio_popup_layout, null
        )

        animationAudio = audioView.findViewById<LottieAnimationView>(R.id.vi_audio_recorder)
        timeEllapsed = audioView.findViewById<TextView>(R.id.vt_time_lap)
        animationAudio?.visibility = View.VISIBLE
        animationAudio?.playAnimation()

        popUpAudio = PopupWindow(
            audioView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        popUpAudio?.isOutsideTouchable = true
        popUpAudio?.elevation = 1.0f
        popUpAudio?.showAtLocation(mView, Gravity.CENTER, 0, 0)
        popUpAudio?.setOnDismissListener {
            hour = 0
            minutes = 0
            seconds = 0
            timeEllapsed?.text = activity?.resources?.getString(R.string.default_start)
            stopRecording()
        }

        startRecording()
    }

    override fun stopRecording() {
        if (state) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            state = false
            hour = 0
            minutes = 0
            seconds = 0
            timeEllapsed?.text = activity?.resources?.getString(R.string.default_start)
            //  Toast.makeText(activity, "Recording stopped!", Toast.LENGTH_SHORT).show()

            askUserToUpload(activity)

        } else {
            // Toast.makeText(activity, "You are not recording right now!", Toast.LENGTH_SHORT).show()
            popUpAudio?.dismiss()
        }
    }

    private fun askUserToUpload(activity: FragmentActivity?) {
        val alertDialog = AlertDialog.Builder(activity!!)
        alertDialog.setMessage("Do you wish to upload the record?")
        alertDialog.setPositiveButton("Yes") { _, _ ->
            run {
                //implement api
                val audioFile = File(output!!)
                //  val reqFile = RequestBody.create("audio/mp3".toMediaTypeOrNull(), audioFile)
                val reqBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "message",
                        audioFile.path,
                        RequestBody.create(
                            "audio/mp3".toMediaTypeOrNull(),
                            audioFile
                        )
                    ).addFormDataPart("type", "audio").build()


                // part = MultipartBody.Part.createFormData("message", audioFile.name, reqBody)

                sizeInKb = (audioFile.length() / 1024)
                sizeInMb = (sizeInKb!! / 1024)
                Log.d("AUDIO_FILE_SIZE", "" + sizeInMb)
                if (sizeInMb!! <= 5) {
                    apiCallForAudio(reqBody, audioFile)
                } else {
                    fileErrorMessage = "Audio file should be less than 5MB"
                    showFileSizeAlert(fileErrorMessage!!)
                }



            }

        }

        alertDialog.setNegativeButton("No") { _, _ ->
            {

            }
        }
        val dialog = alertDialog.create()
        dialog.show()
    }


    private fun showFileSizeAlert(message: String) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Alert")
        builder.setMessage(message)

        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun apiCallForAudio(type: RequestBody, audioFile: File) {
        tvNoData?.visibility = View.GONE
        val repo =
            PostMessageRepo(responseListener = this)
        repo.postAudioMessage(
            type = type,
            appId = uibConfig?.appId!!,
            botId = uibConfig?.botId!!,
            userId = uibConfig?.userId!!, adapterPosition = supportChatAdapter?.itemCount!!
        )
        //  val chat = Chat("",0)
        val chat = Chat()
        chat.from = 0
        chat.bitmap = null
        chat.audioFile = audioFile
        etMessage?.setText("")
        supportChatAdapter?.addItem(chat = chat)
        rvChatList?.smoothScrollToPosition(supportChatAdapter?.itemCount!!)
    }

    override fun startRecording() {
        if (taskTimer != null) {
            taskTimer?.cancel()
            seconds = 0
            minutes = 0
            hour = 0
            timeEllapsed?.text = activity?.resources?.getString(R.string.default_start)
        }
        try {

            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            // Toast.makeText(activity, "Recording started!", Toast.LENGTH_SHORT).show()
            timer = Timer("Audio_Timer", true)
            taskTimer = AudioTimerTask()
            timer?.schedule(taskTimer, 0, 1000)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    inner class AudioTimerTask() : TimerTask() {
        override fun run() {
            timeEllapsed?.post {
                seconds++
                if (seconds == 60) {
                    seconds = 0
                    minutes++
                }
                if (minutes == 60) {
                    minutes = 0
                    hour++
                }
                if ((seconds <= 60) and (minutes == 0)) {
                    if (seconds < 10)
                        timeEllapsed?.text = context?.getString(
                            R.string.zero_minute_single_digit_seconds,
                            seconds
                        )
                    else
                        timeEllapsed?.text =
                            context?.getString(R.string.zero_minute_two_digit_seconds, seconds)
                }
                if ((minutes > 0) && (minutes < 60)) {
                    if (minutes < 10) {
                        if (seconds < 10)
                            timeEllapsed?.text = context?.getString(
                                R.string.single_digit_minute_single_digit_seconds,
                                minutes,
                                seconds
                            )
                        else
                            timeEllapsed?.text = context?.getString(
                                R.string.single_digit_minute_two_digit_seconds,
                                minutes,
                                seconds
                            )
                    } else {
                        if (seconds < 10)
                            timeEllapsed?.text = context?.getString(
                                R.string.two_digit_minute_single_digit_seconds,
                                minutes,
                                seconds
                            )
                        else
                            timeEllapsed?.text = context?.getString(
                                R.string.two_digit_minute_two_digit_seconds,
                                minutes,
                                seconds
                            )
                    }
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {

            requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK -> {
                dirName = "/.uib_snaps"
                filePath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + dirName
                imageType="image/png"
                Log.d("CAPTURE_TYPE",imageType)
                imageBitmap = data?.extras?.get("data") as Bitmap
                val mFile = StoreTheBitMapToFileTask(
                    imageBitmap!!,
                    this,
                    filePath!!
                ).execute().get()
                  val reqBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                      .addFormDataPart(
                          "message",
                          mFile?.path,
                          RequestBody.create(
                              "image/*".toMediaTypeOrNull(),
                              mFile!!
                          )
                      ).addFormDataPart("type", "image").build()
                 // apiCallForImage(reqBody, imageBitmap!!)

            }
            requestCode == SELECT_PICTURE && resultCode == RESULT_OK -> {
                dirName = "/.uib_snaps"
                filePath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + dirName
                imageUri = data?.data
                imageType=context?.contentResolver?.getType(imageUri!!)
                Log.d("IMAGE_TYPE",context?.contentResolver?.getType(imageUri!!))
                imageFromGallery = File(imageUri?.path!!)



                try {


                    imageBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source =
                            ImageDecoder.createSource(context?.contentResolver!!, imageUri!!)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val mFile = StoreTheBitMapToFileTask(
                    imageBitmap!!,
                    this,
                    filePath!!
                ).execute().get()
                val reqBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "message",
                        mFile?.path,
                        RequestBody.create(
                            imageType?.toMediaTypeOrNull(),
                            imageFromGallery!!
                        )
                    ).addFormDataPart("type", "image").build()
                //Toast.makeText(activity, "Chosen from gallery", Toast.LENGTH_SHORT).show()
                //println(" Width= ${imageBitmap?.width}  Height= ${imageBitmap?.height} ")


               // apiCallForImage(reqBody, imageBitmap!!)
            }
            requestCode == 436 -> {
                val lat = data?.getDoubleExtra(AppConstants.LAT, 0.0)
                val lang = data?.getDoubleExtra(AppConstants.LANG, 0.0)
                /*appId = data?.getStringExtra(AppConstants.APP_ID)
                botId = data?.getStringExtra(AppConstants.BOT_ID)
                userId = data?.getStringExtra(AppConstants.USER_ID)*/
            }
            requestCode == SELECT_CONTACT_REQUEST && resultCode == RESULT_OK -> {
                Log.d("Contacts", data.toString())
            }
            requestCode == SELECT_DOCUMENT && resultCode == RESULT_OK -> {
                documentUri = data?.data
                var documentPath: String? = null
                document = File(documentUri!!.toString())

                val path = document!!.absolutePath
                var displayName: String? = null

                if (documentUri.toString().startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor =
                            activity!!.contentResolver.query(documentUri!!, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                    } finally {
                        cursor!!.close()
                    }
                } else if (documentUri.toString().startsWith("file://")) {
                    displayName = document!!.name
                }
                val filePath =
                    arrayOf(MediaStore.Files.FileColumns.DATA)
                context?.contentResolver?.query(
                    MediaStore.Files.getContentUri("external"),
                    filePath,
                    null,
                    null,
                    null
                )?.use {
                    it.moveToFirst()
                    val columnIndex: Int = it.getColumnIndex(filePath[0])
                    documentPath = it.getString(columnIndex)
                }
                val reqFile = RequestBody.create("application/pdf".toMediaTypeOrNull(), path)
                part = MultipartBody.Part.createFormData(
                    "message",
                    document?.name,
                    reqFile
                )

                //val f0001 = FileUtils.getPath(activity!!, documentUri!!)
                val fileName = documentUri.toString().split("/")
                    .get(documentUri.toString().split("/").lastIndex).contains(".")
                var absPath: String? = null
                if (fileName) {
                    absPath = copyFile(
                        FileUtils.getPath(activity!!, documentUri!!),
                        displayName!!,
                        "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath}/${AppConstants.document_folder}"
                    )
                } else {
                    absPath = copyFileFromURI(documentUri!!, displayName!!)
                }
                val reqBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "message", absPath, RequestBody.create(
                            "application/pdf".toMediaTypeOrNull(),
                            File(absPath)
                        )
                    ).addFormDataPart("type", "document").build()

                sizeInKb = (document!!.length() / 1024)
                sizeInMb = (sizeInKb!! / 1024)
                Log.d("DOCUMENT_SIZE", "" + sizeInMb)
                if (sizeInMb!! <= 5) {
                    sendUIBDocument(reqBody, document!!, displayName, documentPath!!)
                } else {
                    fileErrorMessage = "Document size should be less than 5MB"
                    showFileSizeAlert(fileErrorMessage!!)
                }

            }
            requestCode == 1231 -> {
                var cursor: Cursor? = null
                try {

                    contactUir = data!!.data
                    //Query the content uri
                    cursor = requireActivity().contentResolver.query(
                        contactUir!!,
                        null,
                        null,
                        null,
                        null
                    )
                    getVcardString(cursor!!)

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun copyFileFromURI(uri: Uri, fileName: String): String {
        //val id = DocumentsContract.getDocumentId(uri)
        val inputStream = activity?.contentResolver?.openInputStream(uri)
        val file =
            File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath}/${AppConstants.document_folder}/${fileName}.pdf")
        writeFile(inputStream!!, file)
        val filePath: String = file.absolutePath
        return filePath
    }

    private fun writeFile(`in`: InputStream, file: File?) {
        var out: OutputStream? = null
        try {
            out = FileOutputStream(file!!)
            val buf = ByteArray(1024)
            var len: Int
            while (`in`.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                out?.close()
                `in`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun copyFile(
        inputPath: String,
        inputFile: String,
        outputPath: String
    ): String {
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {

            //create output directory if it doesn't exist
            val dir = File(outputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            `in` = FileInputStream(inputPath)
            out = FileOutputStream("${outputPath}/${inputFile}")
            val buffer = ByteArray(1024)
            var read: Int
            while (`in`.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            `in`.close()
            `in` = null

            // write the output file (You have now copied the file)
            out.flush()
            out.close()
            out = null
        } catch (fnfe1: FileNotFoundException) {

            Log.e("tag", fnfe1.message!!)
        } catch (e: java.lang.Exception) {
            Log.e("tag", e.message!!)
        }
        return "${outputPath}/${inputFile}"
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getFilePathFromContentUri(
        selectedVideoUri: Uri,
        contentResolver: ContentResolver
    ): String? {
        var filePath: String? = null
        val filePathColumn =
            arrayOf(MediaStore.MediaColumns.DATA)
        val cursor =
            contentResolver.query(selectedVideoUri, filePathColumn, null, null, null)
        cursor!!.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        filePath = cursor.getString(columnIndex)
        cursor.close()
        return URLDecoder.decode(filePath, "UTF-8")
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.e("Req Code", "" + requestCode)

        if (requestCode == 100) {

            if (grantResults.size == 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                // Success Stuff here
                setUpMediaRecorder()
            } else {
                activity?.supportFragmentManager?.popBackStackImmediate()
            }
        }
    }


    override fun shareContacts(uibContactObject: UIBContactObject) {
        Log.d("CONTACT_IS", uibContactObject.toString())
        // getVcardString(uibContactObject)
    }


    private var vfile: String? = "uibContact1.vcf"


    @Throws(IOException::class)
    private fun getVcardString(cursor: Cursor) {
        cursor.moveToFirst()

        val nameIndex = cursor.getString(
            cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )
        )
        val contactNumber = retrieveContactNumber(nameIndex)
        val vfile: String? = "${nameIndex.replace(" ", "")}.vcf"


        val storage_path = Environment.getExternalStorageDirectory()
            .toString() + File.separator + vfile
        val fw = FileWriter(storage_path)
        fw.write("BEGIN:VCARD\r\n")
        fw.write("VERSION:3.0\r\n")
        fw.write("N:${nameIndex}${nameIndex}" + "\r\n")
        fw.write("FN:${nameIndex} ${nameIndex}" + "\r\n")
        fw.write("ORG:${"AMT"}" + "\r\n")
        fw.write("TITLE:${"Br"}" + "\r\n")
        fw.write("TEL;TYPE=WORK,VOICE:${"+919441723805"}" + "\r\n")
        fw.write("TEL;TYPE=HOME,VOICE:${"+919551723805"}" + "\r\n")
        fw.write("ADR;TYPE=WORK:;;" + "MCP" + ";" + "Maruru" + ";" + "AP" + ";" + "515641" + ";" + "India" + "\r\n")
        fw.write("EMAIL;TYPE=PREF,INTERNET:${"bhaskar@amt.in"}" + "\r\n")
        fw.write("END:VCARD\r\n")
        fw.close()
        cursor.close()
        copyFile(
            Environment.getExternalStorageDirectory().toString(), vfile!!,
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath}/${AppConstants.contacts_folder}"
        )


        PostMessageRepo(this).postImageMessage(
            createPartBody(File(storage_path), "contacts"),
            uibConfig?.appId!!,
            uibConfig?.botId!!,
            uibConfig?.userId!!,
            "contacts",
            supportChatAdapter?.itemCount!!
        )

        val chat = Chat()
        chat.from = 0
        chat.bitmap = null
        chat.contactName = vfile
        val uibContactObject = UIBContactObject()
        uibContactObject.contactName = nameIndex
        uibContactObject.phoneNumber = contactNumber
        chat.uibContactObject = uibContactObject
        etMessage?.setText("")
        supportChatAdapter?.addItem(chat = chat)
        rvChatList?.smoothScrollToPosition(supportChatAdapter?.itemCount!!)
    }

    private fun retrieveContactNumber(contactName: String): String? {
        Log.d("UIB_FRAG", "Contact Name: $contactName")
        var contactNumber: String? = null
        val cursorPhone: Cursor? = context?.contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ? AND " +
                    ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
            arrayOf(contactName),
            null
        )

        if (cursorPhone?.moveToFirst()!!) {
            contactNumber =
                cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
        }

        cursorPhone.close()

        Log.d("UIB_FRAG", "Contact Phone Number: $contactNumber")

        return contactNumber

    }


    override fun openContacts() {
        /* val fragmentTransaction: FragmentTransaction =
             activity?.supportFragmentManager?.beginTransaction()!!
         fragmentTransaction.add(R.id.content, ContactFragment(this.requireActivity(), this))
         fragmentTransaction.hide(this@UIBChatFragment)
         fragmentTransaction.addToBackStack("CONTACT_FRAG")
         fragmentTransaction.commit()*/
        val contactPickerIntent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        )
        startActivityForResult(contactPickerIntent, 1231)
    }


    companion object {
        var uibLocation: UIBLocation? = null
    }

    override fun setLatLang(lat: Double, lang: Double) {
        //val lat1 = lat
        //val lang1 = lang
    }

    /*var client: OkHttpClient = OkHttpClient().newBuilder()
        .build()
    var mediaType: MediaType = MediaType.parse("text/plain")
    var body: RequestBody = Builder().setType(MultipartBody.FORM)
        .addFormDataPart(
            "message", "/C:/Users/bhaskar/Desktop/android-device-identifiers-featured.jpg",
            RequestBody.create(
                MediaType.parse("application/octet-stream"),
                File("/C:/Users/bhaskar/Desktop/android-device-identifiers-featured.jpg")
            )
        )
        .addFormDataPart("type", "image")
        .build()
    var request: Request = Builder()
        .url("https://chatbot-v2connector.unificationengine.com/app/c9e0faed-0d1c-4580-a118-ad017811688a/bot/123456/user/1ws2345sfgh67/messages")
        .method("POST", body)
        .build()
    var response: Response = client.newCall(request).execute()*/
    override fun fileImageIsReadyForUpload(file: File,imageSizeInMB:Double) {


        val reqFile = file.asRequestBody("image/png".toMediaTypeOrNull())
        part = MultipartBody.Part.createFormData("message", file.name, reqFile)
        val reqBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "message",
                file.path,
                file.asRequestBody("image/png".toMediaTypeOrNull())
            ).addFormDataPart("type", "image").build()


        Log.d("IMAGE_SIZE", "" + imageSizeInMB)
        if (imageSizeInMB >= 5.0) {
            fileErrorMessage = "Image size should be less than 5MB"
            showFileSizeAlert(fileErrorMessage!!)
            return
        }
        apiCallForImage(reqBody, imageBitmap!!)
    }

    override fun createPartBody(file: File, type: String): RequestBody {
        //val reqFile = file.asRequestBody("${type}/*".toMediaTypeOrNull())
        //val part = MultipartBody.Part.createFormData("message", file.name, reqFile)
        val reqBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "message",
                file.path,
                file.asRequestBody("text/x-vcard".toMediaTypeOrNull())
            ).addFormDataPart("type", type).build()
        return reqBody
    }

    override fun apiCallForImage(part: RequestBody, bitmap: Bitmap) {
        tvNoData?.visibility = View.GONE
        val repo =
            PostMessageRepo(responseListener = this)
        repo.postImageMessage(
            part = part,
            appId = uibConfig?.appId!!,
            botId = uibConfig?.botId!!,
            userId = uibConfig?.userId!!,
            adapterPosition = supportChatAdapter?.itemCount!!,
            type = "image"
        )
        //  val chat = Chat("",0)
        val chat = Chat()
        chat.from = 0
        chat.bitmap = bitmap
        chat.fileName = fileName
        etMessage?.setText("")
        supportChatAdapter?.addItem(chat = chat)
        rvChatList?.smoothScrollToPosition(supportChatAdapter?.itemCount!!)
    }


    override fun sendUIBDocument(
        part: RequestBody,
        document: File,
        fileName: String,
        filePath: String
    ) {
        val repo =
            PostMessageRepo(responseListener = this)
        repo.postImageMessage(
            part = part,
            appId = uibConfig?.appId!!,
            botId = uibConfig?.botId!!,
            userId = uibConfig?.userId!!,
            adapterPosition = supportChatAdapter?.itemCount!!,
            type = "document"
        )
        val chat = Chat()
        chat.from = 0
        chat.bitmap = null
        chat.document = document
        etMessage?.setText("")
        chat.fileName = fileName
        supportChatAdapter?.addItem(chat = chat)
        rvChatList?.smoothScrollToPosition(supportChatAdapter?.itemCount!!)
    }

    override fun CallPagination(pageNo: Int) {
        chatHistoryRepo.getChats(
            appId = uibConfig?.appId!!,
            botId = uibConfig?.botId!!,
            userId = uibConfig?.userId!!,
            authToken = arrayByte!!, skip = (pageNo - 1) * 25, limit = 25
        )
    }

    override fun textLongPressed(chat: Chat, position: Int) {
        showMessageActionSheet(chat, position)
    }


    private fun showMessageActionSheet(chat: Chat, position: Int) {
        val builder =
            AlertDialog.Builder(context!!)
        val features: Array<String>? = if (chat.from == 0) {
            arrayOf("Reply", "Edit")
        } else {
            arrayOf("Reply")
        }
        builder.setItems(features) { dialog, which ->
            when (which) {
                0 -> {
                    tvReplyMessage?.text = chat.msg
                    tvReplyMessage?.visibility = View.VISIBLE
                    ivRemoveReply?.visibility = View.VISIBLE
                    dialog?.dismiss()
                    userReplyEdit?.userIsEditing(false)
                    userReplyEdit?.userIsReplying(true)
                    chatObjectToEditOrReply = chat
                    indexToBeUpdated = position
                }
                1 -> {
                    etMessage?.setText(chat.msg)
                    userReplyEdit?.userIsEditing(true)
                    userReplyEdit?.userIsReplying(false)
                    chatObjectToEditOrReply = chat
                    indexToBeUpdated = position
                    dialog?.dismiss()
                }


            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun userIsEditing(userIsEditing: Boolean) {
        tryingToEdit = userIsEditing
    }

    override fun userIsReplying(userIsReplying: Boolean) {
        tryingToReply = userIsReplying
    }

    override fun userIsSendingNewOne(userIsSendingNewMsg: Boolean) {
        sendingNormalMsg = userIsSendingNewMsg
    }


}







