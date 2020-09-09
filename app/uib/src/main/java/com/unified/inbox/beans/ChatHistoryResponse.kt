package com.unified.inbox.beans

data class ChatHistoryResponse(
	val totalCount: Int? = null,
	val status: Int? = null,
	val info: List<InfoItem?>? = null
)

data class InfoItem(
	val hasattachment: Boolean? = null,
	val newUserGroupRouting: Boolean? = null,
	val apiStat: String? = null,
	val attachmentType: String? = null,
	val inputMessage: String? = null,
	val msgId: String? = null,
	val userName: String? = null,
	val connectionType: String? = null,
	val phoneNo: String? = null,
	val contenttype: String? = null,
	val receiverAddress: String? = null,
	val newUser: String? = null,
	val createdDate: String? = null,
	val outputMessage: String? = null,
	val attachment: Any? = null,
	val modifiedDate: String? = null,
	val engageEnabled: Boolean? = null,
	val responses: List<Any?>? = null,
	val connectionName: String? = null,
	val id: String? = null,
	val status: String? = null
)

