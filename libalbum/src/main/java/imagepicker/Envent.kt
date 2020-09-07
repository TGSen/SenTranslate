package imagepicker

import java.io.Serializable

class OnMediaSeleted(images: ArrayList<MediaFile>?) : Serializable {
    var imagePath: ArrayList<MediaFile>? = null

    init {
        this.imagePath = images
    }
}

class OnAddPicture(file: String) {
    var imagePath: String? = null

    init {
        this.imagePath = file
    }
}