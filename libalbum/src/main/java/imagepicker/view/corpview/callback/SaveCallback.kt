package imagepicker.view.corpview.callback

import android.net.Uri

interface SaveCallback : Callback {
    fun onSuccess(uri: Uri)
}


