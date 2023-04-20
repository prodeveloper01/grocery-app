package com.grocery.app.model

class PinCodeModel {
    private var id: Int? = null

    private var strPinCode: String? = null

    private var isPinCode:Boolean=false
    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getPinCode(): String? {
        return strPinCode
    }

    fun setPinCode(strPinCode: String?) {
        this.strPinCode = strPinCode
    }

    fun isNeighborhoodSelected(): Boolean? {
        return isPinCode
    }

    fun setNeighborhoodSelected(isPinCode: Boolean) {
        this.isPinCode = isPinCode
    }

}