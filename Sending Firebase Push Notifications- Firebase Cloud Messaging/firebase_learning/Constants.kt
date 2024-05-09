package com.example.firebase_learning

class Constants {
    companion object{
        const val BASE_URL = "https://fcm.googleapis.com" // Note  that this is definite
        const val SERVER_KEY = "AAAAEtNwEWc:APA91bEAN9qBcr3_AWgBmZuS1mllzZFc3m5TrHW1W_3vDCIMpJLBt4yMgbLPhaDhGJFk9Q6gYCGKJ4PpjL-b9rh5jUqbBEZZkf-1f8OisStUgTqdbSRagYjIlVEsUFHHoAOV_CSXyTkR" // This can be derived from the firebase console
        // Since this has been deprecated, we have to firstly add cloud messaging before it can work
        const val CONTENT_TYPE = "application/json"
    }
}