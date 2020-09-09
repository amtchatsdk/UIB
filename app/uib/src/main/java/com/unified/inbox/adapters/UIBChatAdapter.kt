package com.unified.inbox.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.AudioAttributes.*
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.unified.inbox.R
import com.unified.inbox.beans.Chat
import com.unified.inbox.beans.InfoItem
import com.unified.inbox.interfaces.UIBChatAdapterListener
import com.unified.inbox.utils.AppConstants
import com.unified.inbox.utils.DownloadFileAsync
import com.unified.inbox.utils.StoreVcfContactAsync
import com.unified.inbox.utils.TimeUtils
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class UIBChatAdapter(
    private var mCtx: FragmentActivity,
    private var mRecyclerView: RecyclerView,
    private var mLinearLayoutManager: LinearLayoutManager,
    private var mCallBack: CallBack

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    UIBChatAdapterListener {

    private var ignoreView: Int = -1
    private val updateSeekBar: Int = 1455
    private var playingHolder: UserAudioViewHolder? = null
    private var uiUpdateHandler: Handler? = null
    private var audioSelectedPosition: Int = -1

    private var mediaPlayer: MediaPlayer? = null

    companion object {
        var hide: Boolean? = null
    }


    private val userMessageViewType: Int = 0
    private val botMessageViewType: Int = 1
    private val botImageViewType: Int = 2
    private val userImageMessageViewType: Int = 3
    private val userAudioMessageViewType: Int = 4
    private val userDocumentViewType: Int = 5
    private val userContactViewType: Int = 6
    private val userMessageViewTypeReply: Int = 7
    private var userMessageTextSize: Float? = 0.0f
    private var botMessageTextSize: Float? = 0.0f
    private var botMessageTextStyle: Int? = null
    private var userMessageTextStyle: Int? = null
    private var userRepliedMessageTextStyle: Int? = null
    private var userMessageTextColor: Int? = null
    private var userMessageTextBackground: Drawable? = null
    private var botMessageTextColor: Int? = null
    private var botMessageTextBackground: Drawable? = null
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

    var chatList: MutableList<Chat>? = null
    private lateinit var chatHistoryUser: Chat
    private lateinit var chatHistoryBot: Chat
    private var pastVisiblesItems = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var responseTotalCount: Int = 0
    private var pageNo = 1

    init {
        chatList = ArrayList()
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0) {
                    visibleItemCount = mLinearLayoutManager.childCount
                    totalItemCount = mLinearLayoutManager.itemCount
                    pastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition()
                    if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                        //loading = false;
                        Log.d(
                            "values",
                            visibleItemCount.toString() + " " + pastVisiblesItems + " " + totalItemCount + " - " + pageNo
                        )
                        pageNo++
                        mCallBack.CallPagination(pageNo)
                        //isLoading = true
                    }
                }
            }
        })
    }

    fun addItem(chat: Chat) {
        chatList?.add(chat)
        notifyDataSetChanged()
    }

    fun addChatHistory(item: InfoItem) {
        chatHistoryBot = Chat()
        chatHistoryUser = Chat()
        if (item.contenttype?.equals(AppConstants.image)!!) {
            chatHistoryUser.type = AppConstants.messageImageLink
        } else {
            chatHistoryUser.type = item.contenttype
        }
        chatHistoryUser.msgID = item.msgId
        chatHistoryUser.msg = item.inputMessage
        chatHistoryUser.from = 0
        if (chatHistoryUser.msg?.isNotEmpty()!!) {
            if (pageNo == 1) {
                chatList?.add(chatHistoryUser)
            } else {
                chatList?.add(0, chatHistoryUser)
            }
        }
        /*if (item.contenttype){

        }*/
        if (item.hasattachment!!) {
            if (item.attachmentType.equals(AppConstants.image)) {
                chatHistoryBot.type = AppConstants.messageImageLink
                chatHistoryBot.msg = item.attachment.toString()
            }
        } else {
            chatHistoryBot.type = "text"
            chatHistoryBot.msg = item.outputMessage
        }

        if (chatHistoryBot.msg != "") {
            chatHistoryBot.from = 1
            if (pageNo == 1) {
                chatList?.add(chatHistoryBot)
            } else {
                chatList?.add(0, chatHistoryBot)
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList?.get(position)?.from == 0 && chatList?.get(position)?.type == AppConstants.text) {
            userMessageViewType
        } else if (chatList?.get(position)?.from == 0 && chatList?.get(position)?.type == AppConstants.reply) {
            userMessageViewTypeReply
        } else if (chatList?.get(position)?.from == 0 && (chatList?.get(position)?.bitmap != null) || (chatList?.get(
                position
            )?.type == AppConstants.messageImageLink)
        ) {
            userImageMessageViewType
        } else if (chatList?.get(position)?.type == AppConstants.messageImageLink) {
            botImageViewType
        } else if (chatList?.get(position)?.from == 0 && (chatList?.get(position)?.audioFile != null || chatList?.get(
                position
            )?.type == AppConstants.voice)
        ) {
            userAudioMessageViewType
        } else if (chatList?.get(position)?.from == 0 && (chatList?.get(position)?.document != null || chatList?.get(
                position
            )?.type == AppConstants.document)
        ) {
            userDocumentViewType
        } else if (chatList?.get(position)?.from == 0 && (chatList?.get(position)?.contactName != null || chatList?.get(
                position
            )?.type == AppConstants.contacts)
        ) {
            userContactViewType
        } else if (chatList?.get(position)?.from == 1 && (chatList?.get(position)?.type == AppConstants.text)) {
            botMessageViewType

        } else {
            Log.d("BOT_REPLY_", chatList?.get(position)?.type)
            botMessageViewType

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        return when (viewType) {
            userMessageViewType -> {
                val mView: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.uib_chat_bubble_user, parent, false)
                UserMessageViewHolder(mView)
                /*val mView: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.uib_chat_bubble_user_image, parent, false)
                UserImageViewHolder(mView)*/
                /* val mView: View =
                     LayoutInflater.from(parent.context)
                         .inflate(R.layout.uib_chat_bubble_user_file, parent, false)
                 UserFileViewHolder(mView)*/
                /*  val mView: View =
                      LayoutInflater.from(parent.context)
                          .inflate(R.layout.uib_chat_bubble_user_contact, parent, false)
                  UserContactViewHolder(mView)*/
            }
            userMessageViewTypeReply -> {
                val mView: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.uib_chat_bubble_user_reply, parent, false)
                UserMessageReplyViewHolder(mView)

            }
            userImageMessageViewType -> {
                val mView: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.uib_chat_bubble_user_image, parent, false)
                UserImageViewHolder(mView)

            }
            botImageViewType -> {
                val mView: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.uib_chat_bubble_bot_image, parent, false)
                BotImageViewHolder(mView)

            }
            userAudioMessageViewType -> {
                val mView: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.uib_chat_bubble_user_audio, parent, false)
                UserAudioViewHolder(mView)
            }
            userDocumentViewType -> {
                val mView: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.uib_chat_bubble_user_file, parent, false)
                UserFileViewHolder(mView)
            }
            userContactViewType -> {
                val mView: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.uib_chat_bubble_user_contact, parent, false)
                UserContactViewHolder(mView)
            }
            botMessageViewType -> {
                val mView: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.uib_chat_bubble_bot, parent, false)
                BotMessageViewHolder(mView)
                /* val mView: View =
                     LayoutInflater.from(parent.context)
                         .inflate(R.layout.uib_chat_bubble_bot_image, parent, false)
                 BotImageViewHolder(mView)

                 val mView: View =
                     LayoutInflater.from(parent.context)
                         .inflate(R.layout.uib_chat_bubble_bot_audio, parent, false)
                 BotAudioViewHolder(mView)*/

            }
            else -> {
                val mView: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.uib_chat_bubble_bot, parent, false)
                BotMessageViewHolder(mView)
            }
        }
    }

    override fun getItemCount(): Int {
        return chatList?.size!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserMessageViewHolder -> {
                bindUserMessage(holder, position)

            }
            is UserMessageReplyViewHolder -> {
                bindUserMessageReply(holder, position)

            }
            is BotMessageViewHolder -> {
                bindBotMessage(holder, position)
            }
            is UserImageViewHolder -> {

                bindUserImageMessage(holder, position)
            }
            is BotImageViewHolder -> {
                bindBotImage(holder, position)
            }
            is UserAudioViewHolder -> {
                bindUserAudioMessage(holder, position)
            }
            is UserFileViewHolder -> {
                bindUserDocumentMessage(holder, position)
            }
            is UserContactViewHolder -> {
                bindUserContact(holder, position)
            }
        }
    }


    inner class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUserMsg: TextView? = null
        var vIEdit: ImageView? = null

        init {
            tvUserMsg = itemView.findViewById(R.id.tv_message_user)
            vIEdit = itemView.findViewById(R.id.vI_edit)
        }
    }

    inner class UserMessageReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUserMsgReply: TextView? = null
        var tvUserMsg: TextView? = null
        var tvUserReplyTimeStamp: TextView? = null
        var vLContainerUserReplied: LinearLayout? = null

        init {
            vLContainerUserReplied = itemView.findViewById(R.id.container_user_reply)
            tvUserMsgReply = itemView.findViewById(R.id.tv_message_user_reply)
            tvUserReplyTimeStamp = itemView.findViewById(R.id.vT_user_reply_stamp)
            tvUserMsg = itemView.findViewById(R.id.tv_message_user_oldMsg)
        }
    }

    inner class BotMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvBotMsg: TextView? = null

        init {
            tvBotMsg = itemView.findViewById(R.id.tv_message_bot)
        }
    }

    inner class UserImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageMsg: ImageView? = null
        var progressBar: ProgressBar? = null


        init {
            progressBar = itemView.findViewById(R.id.vP_ProgressBar)
            imageMsg = itemView.findViewById(R.id.vI_Image_By_User)

        }


    }

    inner class UserFileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fileMsg: TextView? = null
        var fileProgressUpdate: ProgressBar? = null
        var fileImage: ImageView? = null
        var fileType: TextView? = null
        var documentContainer: LinearLayout? = null

        init {
            fileMsg = itemView.findViewById(R.id.vT_User_File_Name)
            fileProgressUpdate = itemView.findViewById(R.id.progress_update)
            fileType = itemView.findViewById(R.id.vT_User_File_Type)
            fileImage = itemView.findViewById(R.id.iv_document_pic)
            documentContainer = itemView.findViewById(R.id.vL_User_File)
        }

    }

    inner class BotImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageMsg: ImageView? = null

        init {
            imageMsg = itemView.findViewById(R.id.vI_Image_By_Bot)
        }

    }

    inner class BotAudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var botPlayPause: ImageView? = null

        init {
            botPlayPause = itemView.findViewById(R.id.vI_Bot_PlayPause)
        }

    }

    inner class UserAudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        SeekBar.OnSeekBarChangeListener, Handler.Callback {
        var userPlayPause: ImageView? = null
        var seekBar: SeekBar? = null
        var duration: TextView? = null


        init {
            userPlayPause = itemView.findViewById(R.id.vI_User_PlayPause)
            seekBar = itemView.findViewById(R.id.vP_User_Audio_ProgressBar)
            duration = itemView.findViewById(R.id.vT_User_Audio_Clip_Length)
            uiUpdateHandler = Handler(this)


        }

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                mediaPlayer?.seekTo(progress)
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }

        override fun handleMessage(msg: Message): Boolean {
            when (msg.what) {
                updateSeekBar -> {
                    playingHolder?.duration?.text =
                        getTimeToBeDisplayed(mediaPlayer?.currentPosition!!)
                    playingHolder?.seekBar?.progress = mediaPlayer?.currentPosition!!
                    uiUpdateHandler!!.sendEmptyMessageDelayed(updateSeekBar, 100)
                    return true
                }
            }
            return false
        }

    }


    inner class UserContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userContactName: TextView? = null
        var sharedTime: TextView? = null
        var contactContainer: LinearLayout? = null

        init {
            userContactName = itemView.findViewById(R.id.vT_User_Shared_Contact_Name)
            sharedTime = itemView.findViewById(R.id.vT_User_Contact_Time)
            contactContainer = itemView.findViewById(R.id.vL_User_Shared_Contact_Card)
        }

    }

    override fun setUserMessageTextSize(size: Float) {
        this.userMessageTextSize = size
    }

    override fun setUserMessageTextStyle(style: Int) {
        this.userMessageTextStyle = style
    }

    override fun setUserRepliedMessageTextStyle(style: Int) {
        this.userRepliedMessageTextStyle = style
    }

    override fun setBotMessageTextSize(size: Float) {
        this.botMessageTextSize = size
    }

    override fun setBotMessageTextStyle(style: Int) {
        this.botMessageTextStyle = style
    }

    override fun setUserMessageTextColor(color: Int) {
        this.userMessageTextColor = color
    }

    override fun setBotMessageTextColor(color: Int) {
        this.botMessageTextColor = color
    }

    override fun setUserMessageTextBackground(background: Drawable) {
        this.userMessageTextBackground = background
    }

    override fun setSupportMessageTextBackground(background: Drawable) {
        this.botMessageTextBackground = background
    }

    override fun bindUserMessage(holder: UserMessageViewHolder, position: Int) {
        if (adjustUserTextMargin!!) {
            val params: ViewGroup.MarginLayoutParams =
                holder.tvUserMsg?.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(
                userTextMarginLeft!!,
                userTextMarginTop!!,
                userTextMarginRight!!,
                userTextMarginBottom!!
            )
        }

        if (adjustUserTextPadding!!) {
            holder.tvUserMsg?.setPadding(
                userTextPaddingLeft!!,
                userTextPaddingTop!!,
                userTextPaddingRight!!,
                userTextPaddingBottom!!
            )
        }
        if (userMessageTextSize != 0.0f) {
            holder.tvUserMsg?.textSize = userMessageTextSize!!
        }
        if (userMessageTextStyle != null) {
            holder.tvUserMsg?.setTypeface(holder.tvUserMsg!!.typeface, userMessageTextStyle!!)
        }
        if (userMessageTextColor != null) {
            holder.tvUserMsg?.setTextColor(userMessageTextColor!!)
        }

        if (userMessageTextBackground != null) {
            holder.tvUserMsg?.background = userMessageTextBackground
        }
        holder.tvUserMsg?.text = chatList?.get(position)?.msg
        if (chatList?.get(position)?.isEdited!!)
            holder.vIEdit?.visibility = View.VISIBLE
        else
            holder.vIEdit?.visibility = View.GONE

        holder.tvUserMsg?.setOnLongClickListener {
            mCallBack.textLongPressed(chatList?.get(position)!!, position)

            true
        }
    }

    override fun bindUserMessageReply(holder: UserMessageReplyViewHolder, position: Int) {
        if (adjustUserTextMargin!!) {
            val params: ViewGroup.MarginLayoutParams =
                holder.tvUserMsg?.layoutParams as ViewGroup.MarginLayoutParams
            val params1: ViewGroup.MarginLayoutParams =
                holder.tvUserMsgReply?.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(
                userTextMarginLeft!!,
                userTextMarginTop!!,
                userTextMarginRight!!,
                userTextMarginBottom!!
            )
            params1.setMargins(
                userTextMarginLeft!!,
                userTextMarginTop!!,
                userTextMarginRight!!,
                userTextMarginBottom!!
            )
        }

        if (adjustUserTextPadding!!) {
            holder.tvUserMsg?.setPadding(
                userTextPaddingLeft!!,
                userTextPaddingTop!!,
                userTextPaddingRight!!,
                userTextPaddingBottom!!
            )
            holder.tvUserMsgReply?.setPadding(
                userTextPaddingLeft!!,
                userTextPaddingTop!!,
                userTextPaddingRight!!,
                userTextPaddingBottom!!
            )
        }
        if (userMessageTextSize != 0.0f) {
            holder.tvUserMsg?.textSize = userMessageTextSize!!
            holder.tvUserMsgReply?.textSize = userMessageTextSize!!
        }
        if (userMessageTextStyle != null) {
            holder.tvUserMsg?.setTypeface(holder.tvUserMsg!!.typeface, userMessageTextStyle!!)
            holder.tvUserMsgReply?.setTypeface(
                holder.tvUserMsgReply!!.typeface,
                userMessageTextStyle!!
            )
        }
        if (userMessageTextColor != null) {
            holder.tvUserMsg?.setTextColor(userMessageTextColor!!)
            holder.tvUserMsgReply?.setTextColor(userMessageTextColor!!)
        }

        if (userMessageTextBackground != null) {
            holder.tvUserMsg?.background = userMessageTextBackground
            holder.tvUserMsgReply?.background = userMessageTextBackground
        }

        if (chatList?.get(position)?.repliedAtTimeStamp != null)
            holder.tvUserReplyTimeStamp?.text = chatList?.get(position)?.repliedAtTimeStamp
        else
            holder.tvUserReplyTimeStamp?.text = TimeUtils.getCurrentTime()
        holder.tvUserMsg?.text = "${chatList?.get(position)?.oldMsg}"
        holder.tvUserMsgReply?.text = "${chatList?.get(position)?.msg}"


    }


    override fun bindBotMessage(holder: BotMessageViewHolder, position: Int) {
        if (adjustBotTextMargin!!) {
            val params: ViewGroup.MarginLayoutParams =
                holder.tvBotMsg?.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(
                botTextMarginLeft!!,
                botTextMarginTop!!,
                botTextMarginRight!!,
                botTextMarginBottom!!
            )
        }

        if (adjustBotTextPadding!!) {
            holder.tvBotMsg?.setPadding(
                botTextPaddingLeft!!,
                botTextPaddingTop!!,
                botTextPaddingRight!!,
                botTextPaddingBottom!!
            )
        }
        if (botMessageTextSize != 0.0f) {
            holder.tvBotMsg?.textSize = botMessageTextSize!!
        }
        if (botMessageTextStyle != null) {
            holder.tvBotMsg?.setTypeface(holder.tvBotMsg!!.typeface, botMessageTextStyle!!)
        }
        if (botMessageTextColor != null) {
            holder.tvBotMsg?.setTextColor(botMessageTextColor!!)
        }
        if (botMessageTextBackground != null) {
            holder.tvBotMsg?.background = botMessageTextBackground
        }
        holder.tvBotMsg?.text = chatList?.get(position)?.msg
        holder.tvBotMsg?.setOnLongClickListener {
            mCallBack.textLongPressed(chatList?.get(position)!!, position)
            true
        }
    }

    override fun bindBotImage(holder: BotImageViewHolder, position: Int) {
        holder.imageMsg?.load(chatList?.get(position)?.msg!!) {
            crossfade(true)
            //placeholder(R.drawable.)
            placeholder(R.drawable.placeholder)
            transformations(CircleCropTransformation())
        }
    }

    override fun bindUserImageMessage(holder: UserImageViewHolder, position: Int) {
        if (chatList?.get(position)?.bitmap != null) {
            holder.imageMsg?.setImageBitmap(chatList?.get(position)?.bitmap)


            if (position == chatList?.size!! - 1) {
                if (chatList!![position].sentStatus == 1) {
                    holder.imageMsg?.alpha = 1f
                    holder.progressBar?.visibility = View.GONE
                    hide = false

                    hide = false
                }


            } else {
                holder.imageMsg?.alpha = 1f
                holder.progressBar?.visibility = View.GONE

            }
        } else {
            holder.imageMsg?.load(chatList?.get(position)?.msg!!) {
                //crossfade(true)
                holder.progressBar?.visibility = View.GONE
                //placeholder(R.drawable.)
                //placeholder(R.drawable.placeholder)
                //transformations(CircleCropTransformation())
            }
        }
    }

    override fun bindUserAudioMessage(holder: UserAudioViewHolder, position: Int) {
        if (chatList?.get(position)?.audioFile != null) {
            val retrieverP1 = MediaMetadataRetriever()
            retrieverP1.setDataSource(chatList?.get(position)?.audioFile?.absolutePath)
            holder.duration?.text = getTimeToBeDisplayed(
                retrieverP1.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    .toInt()
            )
            retrieverP1.release()
            if (position == audioSelectedPosition) {
                playingHolder = holder
                updatePlayingView()
            } else {
                updateNonPlayingView(holder)
            }
            holder.userPlayPause?.setOnClickListener {
                if (position == audioSelectedPosition) {
                    // toggle between play/pause of audio
                    if (mediaPlayer?.isPlaying!!) {
                        mediaPlayer?.pause()
                    } else {
                        mediaPlayer?.start()
                    }
                } else {
                    // start another audio playback
                    audioSelectedPosition = holder.adapterPosition
                    if (mediaPlayer != null) {
                        if (null != playingHolder) {
                            updateNonPlayingView(playingHolder!!);
                        }
                        mediaPlayer?.release();
                    }
                    playingHolder = holder;
                    startMediaPlayer(chatList?.get(position)?.audioFile, "null")
                }
                updatePlayingView()
            }
        } else {
            val uri = Uri.parse(chatList?.get(position)?.msg)


            SetDataSourceAsync(chatList?.get(position)?.msg, position, holder).execute()

            mediaPlayer = MediaPlayer()
            //mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer?.setAudioAttributes(
                Builder()
                    .setContentType(CONTENT_TYPE_MUSIC)
                    .setUsage(USAGE_MEDIA)
                    .build()
            )
            //mediaPlayer?.setDataSource(mCtx, uri)


            holder.userPlayPause?.setOnClickListener {
                if (position == audioSelectedPosition) {
                    // toggle between play/pause of audio
                    if (mediaPlayer?.isPlaying!!) {
                        mediaPlayer?.pause()
                    } else {
                        mediaPlayer?.start()
                    }
                } else {
                    // start another audio playback
                    audioSelectedPosition = holder.adapterPosition
                    if (mediaPlayer != null) {
                        if (null != playingHolder) {
                            updateNonPlayingView(playingHolder!!)
                        }
                        mediaPlayer?.release()
                    }
                    playingHolder = holder
                    startMediaPlayer(null, chatList?.get(position)?.msg!!)
                }
                updatePlayingView()
            }
        }

    }

    private fun startMediaPlayer(audioFile: File?, awsLink: String) {

        var uri: Uri? = null
        uri = if (audioFile != null) {
            Uri.fromFile(audioFile)!!
        } else {
            Uri.parse(awsLink)!!
        }

        mediaPlayer = MediaPlayer.create(playingHolder?.itemView?.context, uri)
        mediaPlayer!!.setOnCompletionListener { releaseMediaPlayer() }
        mediaPlayer!!.start()

    }

    override fun bindUserDocumentMessage(holder: UserFileViewHolder, position: Int) {
        Log.d("DOCUMENT", "" + chatList?.get(position)?.document);
        if (chatList?.get(position)?.document != null) {
            if (chatList?.get(position)?.sentStatus == 1) {
                holder.fileProgressUpdate?.visibility = View.GONE
                holder.fileImage?.visibility = View.VISIBLE
            }
            holder.fileMsg?.text = chatList?.get(position)?.fileName
            /*holder.fileType?.text = chatList?.get(position)?.fileName?.substring(
                chatList?.get(position)?.fileName!!.lastIndexOf(".")
            )*/
            holder.documentContainer?.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)


                val file01 =
                    File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath}/${AppConstants.document_folder}/${chatList!![position].fileName}")
                //File(chatList?.get(position)?.document?.parent!!)
                intent.setDataAndType(
                    FileProvider.getUriForFile(
                        mCtx,
                        mCtx.applicationContext.packageName + ".provider",
                        //chatList?.get(position)?.document!!
                        file01
                    ),
                    "application/pdf"
                )
                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                mCtx.startActivity(intent)
            }
        } else {
            holder.fileMsg?.text = URLUtil.guessFileName(chatList?.get(position)?.msg, null, null)
            holder.fileProgressUpdate?.visibility = View.GONE
            holder.fileImage?.visibility = View.VISIBLE

            holder.documentContainer?.setOnClickListener {
                Log.d("UIBChatAdapter", "No file found")
                //download store open pdf
                DownloadFileAsync(
                    mCtx, chatList?.get(position)?.msg!!, URLUtil.guessFileName(
                        chatList?.get(
                            position
                        )?.msg, null, null
                    )
                ).execute()

            }

        }
    }

    override fun bindUserContact(holder: UserContactViewHolder, position: Int) {
        if (chatList?.get(position)?.contactName != null) {
            holder.userContactName?.text = chatList?.get(position)?.contactName?.split(".")!![0]
            /*holder.fileType?.text = chatList?.get(position)?.fileName?.substring(
                    chatList?.get(position)?.fileName!!.lastIndexOf(".")
                )*/
            holder.contactContainer?.setOnClickListener {
                /*val intent = Intent(Intent.ACTION_VIEW)
                var uri: Uri? = null
                val fileContact=File(Environment.getExternalStorageDirectory()
                    .toString() + File.separator + chatList!![position].contactName)
                uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(
                        mCtx,
                        mCtx.getApplicationContext()?.getPackageName() + ".provider",
                        fileContact
                    );

                } else {
                    Uri.fromFile(fileContact)
                }

                Log.d("CONTACT_VCF",""+fileContact)
                Log.d("CONTACT_RES",""+chatList!![position].uibContactObject?.phoneNumber)

               // intent.data = uri
                intent.setDataAndType(intent.data, "text/x-vcard"); //storage path is path of your vcf file and vFile is name of that file.
                mCtx.startActivity(intent);*/
                StoreVcfContactAsync(
                    Environment.getExternalStorageDirectory()
                        .toString() + File.separator + chatList!!.get(position).contactName,
                    chatList!!.get(
                        position
                    ).uibContactObject!!,
                    mCtx
                ).execute()
            }
        } else {

        }
    }

    private fun updateNonPlayingView(holder: UserAudioViewHolder) {
        if (holder == playingHolder) {
            uiUpdateHandler?.removeMessages(updateSeekBar);
        }
        holder.seekBar?.isEnabled = false
        holder.seekBar?.progress = 0
        holder.userPlayPause?.setImageResource(R.drawable.ic_play_arrow)
    }

    private fun updatePlayingView() {
        playingHolder?.seekBar?.max = mediaPlayer?.duration!!
        playingHolder?.seekBar?.progress = mediaPlayer?.currentPosition!!
        playingHolder?.seekBar?.isEnabled = true;
        if (mediaPlayer?.isPlaying!!) {
            uiUpdateHandler?.sendEmptyMessageDelayed(updateSeekBar, 100)
            playingHolder?.userPlayPause?.setImageResource(R.drawable.ic_pause)
        } else {
            uiUpdateHandler?.removeMessages(updateSeekBar);
            playingHolder?.userPlayPause?.setImageResource(R.drawable.ic_play_arrow)
        }
    }

    fun stopMediaPlayer() {
        if (null != mediaPlayer) {
            releaseMediaPlayer()
        }
    }

    private fun releaseMediaPlayer() {
        if (null != playingHolder) {
            updateNonPlayingView(playingHolder!!);
        }
        mediaPlayer?.release();
        mediaPlayer = null;
        audioSelectedPosition = -1;
    }

    fun getTimeToBeDisplayed(progress: Int): CharSequence? {
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(progress.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(progress.toLong()) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progress.toLong()))
        )
    }


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

    override fun setMarginForBotText(
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

    override fun setPaddingForBotText(
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

    fun hideUserImageProgressBar() {
        hide = true
        notifyDataSetChanged()
    }

    fun editUpdate(chat: Chat?, position: Int) {
        if (chat != null) {
            chatList?.set(position, chat)
        }
    }

    interface CallBack {
        fun CallPagination(pageNo: Int)
        fun textLongPressed(chat: Chat, position: Int)
    }


}

class SetDataSourceAsync(

    msg: String?,
    position: Int,
    holder: UIBChatAdapter.UserAudioViewHolder
) : AsyncTask<Void, Void, String>() {

    private var retrieverP1: MediaMetadataRetriever? = null
    private var awsLink: String? = null
    private var adapterPos: Int? = null
    private var holder: UIBChatAdapter.UserAudioViewHolder? = null

    init {
        this.retrieverP1 = MediaMetadataRetriever()
        this.awsLink = msg
        this.adapterPos = position
        this.holder = holder
    }

    override fun doInBackground(vararg params: Void?): String {
        retrieverP1?.setDataSource(awsLink, HashMap())

        val progress = retrieverP1?.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toInt()

        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(progress?.toLong()!!),
            TimeUnit.MILLISECONDS.toSeconds(progress.toLong()) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progress.toLong()))
        )
    }

    override fun onPostExecute(result: String?) {
        holder?.duration?.text = result
        retrieverP1?.release()
    }

}
