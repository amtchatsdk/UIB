package com.unified.inbox.interfaces

import com.unified.inbox.beans.UIBContactObject

interface UIBContactBookInterface {

    fun shareContacts(uibContactObject: UIBContactObject)
    fun openContacts()
}